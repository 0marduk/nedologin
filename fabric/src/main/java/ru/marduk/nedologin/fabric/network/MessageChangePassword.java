package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.platform.Service;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.utils.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MessageChangePassword implements ServerPlayNetworking.PlayChannelHandler {
    public static FriendlyByteBuf encode(String original, String to) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        String origHashed = SHA256.getSHA256(SHA256.getSHA256(original)), toHashed = SHA256.getSHA256(SHA256.getSHA256(to));

        buffer.writeInt(origHashed.length());
        buffer.writeCharSequence(origHashed, StandardCharsets.UTF_8);
        buffer.writeInt(toHashed.length());
        buffer.writeCharSequence(toHashed, StandardCharsets.UTF_8);

        return buffer;
    }

    public static String[] decode(FriendlyByteBuf buffer) {
        String original = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();
        String to = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();

        return new String[] { original, to };
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String username = Objects.requireNonNull(player).getGameProfile().getName();
        String[] decoded = decode(buf);

        if (!NLConfig.INSTANCE.enableChangePassword) {
            player.displayClientMessage(
                    Component.translatable("nedologin.info.password_change_disabled"),
                    false
            );

            Service.NETWORK.SendMessageChangePasswordResponse(player, false);
            return;
        }

        if (NLStorage.instance().storageProvider.checkPassword(username, decoded[0])) {
            NLStorage.instance().storageProvider.changePassword(username, decoded[1]);

            player.displayClientMessage(
                    Component.translatable("nedologin.info.password_change_successful"),
                    false
            );

            Service.NETWORK.SendMessageChangePasswordResponse(player, true);
        } else {
            // Should never happen though
            player.displayClientMessage(
                    Component.translatable("nedologin.info.password_change_fail"),
                    false
            );

            Service.NETWORK.SendMessageChangePasswordResponse(player, false);
            Nedologin.logger.warn("Player {} tried to change password with a wrong password.", username);
        }
    }
}
