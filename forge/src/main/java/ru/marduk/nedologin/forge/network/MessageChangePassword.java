package ru.marduk.nedologin.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.utils.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;

public class MessageChangePassword {
    private final String original, to;

    public MessageChangePassword(String original, String to) {
        this.original = SHA256.getSHA256(original);
        this.to = SHA256.getSHA256(to);
    }

    public static void encode(MessageChangePassword msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.original.length());
        buf.writeCharSequence(msg.original, StandardCharsets.UTF_8);
        buf.writeInt(msg.to.length());
        buf.writeCharSequence(msg.to, StandardCharsets.UTF_8);
    }

    public static MessageChangePassword decode(FriendlyByteBuf buf) {
        String original = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        String to = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        return new MessageChangePassword(original, to);
    }

    public static void handle(MessageChangePassword msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        assert context.getSender() != null;
        String username = Objects.requireNonNull(ctx.get().getSender()).getGameProfile().getName();

        if (!NLConfig.INSTANCE.enableChangePassword) {
            context.getSender().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_disabled"),
                    false
            );

            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new MessageChangePasswordResponse(false));

            context.setPacketHandled(true);

            return;
        }

        if (NLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            NLStorage.instance().storageProvider.changePassword(username, msg.to);
            context.getSender().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_successful"),
                    false
            );
            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender),
                    new MessageChangePasswordResponse(true));
        } else {
            // Should never happen though
            context.getSender().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_fail"),
                    false
            );
            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender),
                    new MessageChangePasswordResponse(false));
            Nedologin.logger.warn("Player {} tried to change password with a wrong password.", username);
        }
        context.setPacketHandled(true);
    }
}
