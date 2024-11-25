package ru.marduk.nedologin.platform;

import net.minecraft.server.level.ServerPlayer;

public interface IClientNetworkHelper {
    void SendMessageLogin(String pwd);
    void SendMessageChangePassword(String original, String to);
}
