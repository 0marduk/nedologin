package ru.marduk.nedologin.platform;

import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public interface IPlatformHelper {
    Path getConfigPath();
    boolean isDedicated();
    String getVersion();
    MinecraftServer getServer();
}