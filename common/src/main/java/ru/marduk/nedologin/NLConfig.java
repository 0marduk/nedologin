package ru.marduk.nedologin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.marduk.nedologin.platform.Service;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class NLConfig {
    public static NLConfig INSTANCE;
    public int secs;
    public String[] whitelistCommands;
    public boolean autoRegister;
    public String storageProvider;
    public boolean enableChangePassword;
    public String[] plugins;
    public int defaultGameType;

    public NLConfig defaultOptions() {
        secs = 5;
        autoRegister = true;
        storageProvider = "nedologin:file";
        enableChangePassword = true;
        whitelistCommands = new String[] {};
        plugins = new String[] {
                "nedologin:auto_save",
                "nedologin:resend_request",
                "nedologin:restrict_game_type",
                "nedologin:restrict_movement",
                "nedologin:timeout"
        };
        defaultGameType = 0;

        return this;
    }

    public static void fetchConfig() throws IOException {
        Path path = Service.PLATFORM.getConfigPath().resolve(Paths.get("nedologin.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!Files.exists(path)) {
            try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson(new NLConfig().defaultOptions(), w);
            } catch (Throwable t) {
                Nedologin.logger.error("Failed to write default mariadb config file.", t);
            }
        }

        INSTANCE = gson.fromJson(Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.joining(System.lineSeparator())), NLConfig.class);
    }
}