package dev.heypr.yggdrasil;

import dev.heypr.yggdrasil.commands.*;
import dev.heypr.yggdrasil.data.PlayerData;
import dev.heypr.yggdrasil.events.PlayerDeathListener;
import dev.heypr.yggdrasil.events.PlayerJoinListener;
import dev.heypr.yggdrasil.events.PlayerLeaveListener;
import dev.heypr.yggdrasil.misc.SkinsManager;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class Yggdrasil extends JavaPlugin {
    public static Yggdrasil plugin;

    Map<UUID, PlayerData> playerData = new HashMap<>();
    List<Player> deadPlayers = new ArrayList<>();
    public boolean isSessionRunning = false;
    public boolean isGameRunning = false;

    public SkinsManager skinsManager;
    private FileConfiguration config;

    private void initConfig() {
        this.saveDefaultConfig();
        this.config = super.getConfig();

        ConfigurationSection section;

        if (!this.config.contains("skins")) {
            section = this.config.createSection("skins");

            if (!section.contains("stored"))
                section.createSection("stored");
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        ConfigurationSerialization.registerClass(SkinsManager.SkinData.class);

        this.initConfig();

        this.skinsManager = new SkinsManager(this);

        registerEvent(new PlayerJoinListener(this));
        registerEvent(new PlayerDeathListener(this));
        registerEvent(new PlayerLeaveListener(this));

        registerCommand("givelife", new GiveLifeCommand(this));
        registerCommand("setlives", new SetLivesCommand(this));
        registerCommand("lives", new LivesCommand(this));
        registerCommand("removeboogeyman", new RemoveBoogeymanCommand(this));
        registerCommand("setboogeyman", new SetBoogeymanCommand(this));
        registerCommand("randomizeboogeyman", new RandomizeBoogeymanCommand(this));
        registerCommand("startsession", new StartSessionCommand(this));
        registerCommand("stopsession", new StopSessionCommand(this));
        registerCommand("addplayer", new AddPlayerCommand(this));
        registerCommand("skin", new SkinCommand(this));

        this.initPlaceholders();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    private void initPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            return;

        // Have to use reflection just in case PlaceholderAPI is not on the server
        try {
            final Class<?> clazz = Class.forName("dev.heypr.yggdrasil.misc.papi.YggdrasilExpansion");
            final Object instance = clazz.getDeclaredConstructor(Yggdrasil.class).newInstance(this.plugin);
            final Method method = clazz.getSuperclass().getDeclaredMethod("register");

            method.invoke(instance);
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                       InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    public BukkitScheduler getScheduler() {
        return this.plugin.getServer().getScheduler();
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return playerData;
    }

    public List<Player> getDeadPlayers() {
        return deadPlayers;
    }

    public void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerCommand(String command, CommandExecutor executor) {
        getCommand(command).setExecutor(executor);
    }

    public int randomNumber(int lower, int upper) {
        return (int) (Math.random() * (upper - lower + 1)) + lower;
    }

    public TextComponent deserializeText(String text) {
        LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
        MiniMessage mm = MiniMessage.miniMessage();
        return legacy.deserialize(legacy.serialize(mm.deserialize(text).asComponent()));
    }
}
