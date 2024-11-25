package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import ru.marduk.nedologin.client.PasswordHolder;

public class MessageRequestLogin implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        FriendlyByteBuf buffer = MessageLogin.encode(PasswordHolder.instance().password());

        ClientPlayNetworking.send(NetworkConsts.MessageLogin, buffer);
    }
}
