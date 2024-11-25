package ru.marduk.nedologin.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.marduk.nedologin.client.PasswordHolder;

import java.util.function.Supplier;

public record MessageChangePasswordResponse(boolean success) {

    public static void encode(MessageChangePasswordResponse msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.success);
    }

    public static MessageChangePasswordResponse decode(FriendlyByteBuf buf) {
        return new MessageChangePasswordResponse(buf.readBoolean());
    }

    public static void handle(MessageChangePasswordResponse message, Supplier<NetworkEvent.Context> ctx) {
        if (message.success()) {
            PasswordHolder.instance().applyPending();
        } else {
            PasswordHolder.instance().dropPending();
        }
        ctx.get().setPacketHandled(true);
    }
}
