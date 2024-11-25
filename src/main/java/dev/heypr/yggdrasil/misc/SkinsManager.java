package dev.heypr.yggdrasil.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.heypr.yggdrasil.Yggdrasil;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class SkinsManager {
    private static final String MINESKIN_DOMAIN = "api.mineskin.org";

    private final Yggdrasil plugin;

    private GameProfile getProfile(final Player player) throws Exception {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer serverPlayer = craftPlayer.getHandle();

        return serverPlayer.getGameProfile();
    }

    public SkinsManager(final Yggdrasil plugin) {
        this.plugin = plugin;
    }

    public record SkinData(String skinValue, String skinSignature) implements ConfigurationSerializable {
        @Override
        public Map<String, Object> serialize() {
            final Map<String, Object> data = new HashMap<>();

            data.put("skinValue", this.skinValue);
            data.put("skinSignature", this.skinSignature);

            return data;
        }

        public static SkinData deserialize(final Map<String, Object> args) {
            return new SkinData((String) args.get("skinValue"), (String) args.get("skinSignature"));
        }
    }

    private JSONObject extractData(final JSONObject obj) {
        final JSONObject skin = obj.getJSONObject("skin");
        final JSONObject texture = skin.getJSONObject("texture");
        final JSONObject data = texture.getJSONObject("data");

        return data;
    }

    /**
     * Returns a job link
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadSkin(final File file) throws IOException {
        final String url = String.format("https://%s/v2/queue", MINESKIN_DOMAIN);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(file));

            HttpEntity entity = builder.build();
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                final InputStream inputStream = response.getEntity().getContent();
                final JSONObject obj = new JSONObject(new JSONTokener(inputStream));

                if (obj.getBoolean("success"))
                    return extractData(obj).toString(4);

                final JSONObject links = obj.getJSONObject("links");
                final String endpoint = links.getString("job");

                return String.format("https://%s%s", MINESKIN_DOMAIN, endpoint);
            }
        }
    }

    private void resolveJob(final String jobUrl, final Consumer<JSONObject> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    try (CloseableHttpClient client = HttpClients.createDefault()) {
                        HttpGet get = new HttpGet(jobUrl);

                        try (CloseableHttpResponse response = client.execute(get)) {
                            final InputStream inputStream = response.getEntity().getContent();
                            final JSONObject obj = new JSONObject(new JSONTokener(inputStream));

                            if (!obj.getBoolean("success"))
                                return; // Not done yet

                            final JSONObject data = extractData(obj);

                            callback.accept(data);
                            super.cancel();
                        }
                    }
                } catch (final Exception exception) {
                    exception.printStackTrace();
                    super.cancel();
                    callback.accept(null);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L); // Check every 20 seconds until its done
    }

    private SkinData getSavedSkinData(final String value) {
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("skins.stored");

        if (!section.contains(value))
            return null;

        final SkinData skinData = (SkinData) section.get(value);
        return skinData;
    }

    private SkinData getSkinData(final File file, final Consumer<SkinData> callback) {
        if (!file.exists())
            return null;

        try {
            String value = encodeSkinToBase64(file);

            final SkinData saved = this.getSavedSkinData(value);

            if (saved != null)
                return saved;

            new Thread(() -> {
                try {
                    final Consumer<JSONObject> apply = dataObj -> {
                        final String newValue = dataObj.getString("value");
                        final String signature = dataObj.getString("signature");
                        final SkinData data = new SkinData(newValue, signature);

                        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("skins.stored");

                        section.set(value, data);
                        plugin.saveConfig();

                        callback.accept(data);
                    };

                    final String jobUrl = this.uploadSkin(file);

                    try {
                        final JSONObject response = new JSONObject(jobUrl);
                        apply.accept(response);
                    } catch (final JSONException ignored) {
                        this.resolveJob(jobUrl, apply);
                    }
                } catch (final Exception ignored) {
                    callback.accept(null);
                }
            }).start();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private String encodeSkinToBase64(final File skinFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(skinFile)) {
            byte[] fileBytes = new byte[(int) skinFile.length()];
            fis.read(fileBytes);

            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }

    private void refreshPlayer(final Player p) {
        Bukkit.getOnlinePlayers().stream()
                .filter(ps -> ps.getUniqueId() != p.getUniqueId())
                .forEach(ps -> {
                    ps.hidePlayer(p);

                    if (!p.getMetadata("vanished").stream().anyMatch(v -> true)) // Vanish check
                        plugin.getScheduler().runTaskLater(plugin, () -> ps.showPlayer(p), 2L);
                });
    }

    private GameProfile updateSkin(final Player player, final SkinData skinData) throws Exception {
        final GameProfile profile = this.getProfile(player);

        if (skinData != null) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", skinData.skinValue(), skinData.skinSignature()));
        }

        return profile;
    }

    private void updateSkinViaPackets(final Player player) throws Exception {
        final CraftPlayer craftPlayer = (CraftPlayer) player;

        ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(
                Collections.singletonList(craftPlayer.getHandle().getUUID())
        );

        craftPlayer.getHandle().connection.send(removePacket);

        ClientboundPlayerInfoUpdatePacket addPacket = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                craftPlayer.getHandle()
        );

        craftPlayer.getHandle().connection.send(addPacket);

        ClientboundPlayerInfoUpdatePacket updatePacket = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                craftPlayer.getHandle()
        );

        craftPlayer.getHandle().connection.send(updatePacket);

        try {
            Method refreshMethod = CraftPlayer.class.getDeclaredMethod("refreshPlayer");
            refreshMethod.setAccessible(true);
            refreshMethod.invoke(craftPlayer);
        } catch (final Exception exception){
            exception.printStackTrace();
        }

        craftPlayer.updateScaledHealth();
        craftPlayer.updateInventory();
    }

    private void skinInternal(final Player player, final SkinData skinData) {
        plugin.getScheduler().runTask(plugin, () -> {
            try {
                this.refreshPlayer(player);

                this.updateSkin(player, skinData);

                this.updateSkinViaPackets(player);

                this.refreshPlayer(player);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void skin(final Player player, final File file) {
        final SkinData skinData = this.getSkinData(file, data -> {
            if (data != null) {
                this.skinInternal(player, data);
            }
        });

        if (skinData != null)
            this.skinInternal(player, skinData);
    }
}