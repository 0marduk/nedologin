package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import ru.marduk.nedologin.client.PasswordHolder;

public class MessageChangePasswordResponse implements ClientPlayNetworking.PlayChannelHandler {
    public static FriendlyByteBuf encode(boolean status) {
        FriendlyByteBuf buffer = PacketByteBufs.create();

        buffer.writeBoolean(status);

        return buffer;
    }

    public static boolean decode(FriendlyByteBuf buffer) {
        return buffer.readBoolean();
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        boolean status = decode(buf);

        if (status) {
            PasswordHolder.instance().applyPending();
        } else {
            PasswordHolder.instance().dropPending();
        }
    }
}
