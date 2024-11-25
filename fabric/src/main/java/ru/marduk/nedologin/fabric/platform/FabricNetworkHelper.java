package ru.marduk.nedologin.fabric.platform;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ru.marduk.nedologin.fabric.network.MessageChangePasswordResponse;
import ru.marduk.nedologin.fabric.network.NetworkConsts;
import ru.marduk.nedologin.platform.INetworkHelper;

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public void SendMessageRequestLogin(ServerPlayer player) {
        ServerPlayNetworking.send(player, NetworkConsts.MessageRequestLogin, PacketByteBufs.empty());
    }

    @Override
    public void SendMessageChangePasswordResponse(ServerPlayer player, boolean status) {
        FriendlyByteBuf buffer = MessageChangePasswordResponse.encode(status);

        ServerPlayNetworking.send(player, NetworkConsts.MessageChangePasswordResponse, buffer);
    }
}
