package dev.heypr.yggdrasil.misc.discord.command;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Response {
    public static final class ResponseBuilder {
        private String response;
        private final List<FileUpload> uploads = new ArrayList<>();
        private final List<Button> buttons = new ArrayList<>();

        private ResponseType type = ResponseType.SUCCESS;
        private boolean nonEmbed = false;
        private boolean ephemeral = true;

        public String getResponse() {
            return this.response;
        }

        public ResponseBuilder setResponse(final String response) {
            this.response = response;
            return this;
        }

        public ResponseType getType() {
            return this.type;
        }

        public ResponseBuilder setType(final ResponseType type) {
            this.type = type;
            return this;
        }

        public boolean isNonEmbed() {
            return this.nonEmbed;
        }

        public ResponseBuilder setNonEmbed(final boolean nonEmbed) {
            this.nonEmbed = nonEmbed;
            return this;
        }

        public boolean isEphemeral() {
            return this.ephemeral;
        }

        public ResponseBuilder setEphemeral(final boolean ephemeral) {
            this.ephemeral = ephemeral;
            return this;
        }

        public static ResponseBuilder response(final String response) {
            final ResponseBuilder builder = new ResponseBuilder();
            builder.response = response;
            return builder;
        }

        public static ResponseBuilder response(final FileUpload upload) {
            return new ResponseBuilder().addFile(upload);
        }

        public ResponseBuilder addFile(final FileUpload upload) {
            this.uploads.add(upload);
            return this;
        }

        public ResponseBuilder addFiles(final FileUpload... uploads) {
            this.uploads.addAll(Arrays.asList(uploads));
            return this;
        }

        public ResponseBuilder addButton(final Button button) {
            this.buttons.add(button);
            return this;
        }

        public Response build() {
            final Response responseObj = new Response(this.response, this.uploads, this.buttons, this.type, this.nonEmbed, this.ephemeral);
            return responseObj;
        }
    }

    private final String response;
    private final List<FileUpload> uploads;
    private final List<Button> buttons;
    private final ResponseType type;
    private final boolean nonEmbed;
    private final boolean ephemeral;

    private Response(final String response, final List<FileUpload> uploads, final List<Button> buttons, final ResponseType type, final boolean nonEmbed, final boolean ephemeral) {
        this.response = response;
        this.uploads = uploads;
        this.buttons = buttons;
        this.type = type;
        this.nonEmbed = nonEmbed;
        this.ephemeral = ephemeral;
    }

    public String getResponse() {
        return this.response;
    }

    public List<FileUpload> getUploads() {
        return this.uploads;
    }

    public List<Button> getButtons() {
        return this.buttons;
    }

    public ResponseType getType() {
        return this.type;
    }

    public boolean isNonEmbed() {
        return this.nonEmbed;
    }

    public boolean isEphemeral() {
        return this.ephemeral;
    }
}