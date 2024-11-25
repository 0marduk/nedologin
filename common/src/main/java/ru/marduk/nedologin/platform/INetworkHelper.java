package ru.marduk.nedologin.platform;

import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper {
    void SendMessageRequestLogin(ServerPlayer player);
    void SendMessageChangePasswordResponse(ServerPlayer player, boolean status);
}
