package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.utils.SHA256;

import java.nio.charset.StandardCharsets;

public class MessageLogin implements ServerPlayNetworking.PlayChannelHandler {
    public static FriendlyByteBuf encode(String pwd) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        String hashed = SHA256.getSHA256(SHA256.getSHA256(pwd));

        buffer.writeInt(hashed.length());
        buffer.writeCharSequence(hashed, StandardCharsets.UTF_8);

        return buffer;
    }

    public static String decode(FriendlyByteBuf buffer) {
        int len = buffer.readInt();

        return buffer.readCharSequence(len, StandardCharsets.UTF_8).toString();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String password = decode(buf);

        PlayerLoginHandler.instance().login(player.getGameProfile().getName(), password);
    }
}
