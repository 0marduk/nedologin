package ru.marduk.nedologin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.utils.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class MessageLogin {
    private final String pwd;

    public MessageLogin(String pwd) {
        this.pwd = SHA256.getSHA256(pwd);
    }

    public static void encode(MessageLogin packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.pwd.length());
        buf.writeCharSequence(packet.pwd, StandardCharsets.UTF_8);
    }

    public static MessageLogin decode(FriendlyByteBuf buffer) {
        int len = buffer.readInt();
        return new MessageLogin(buffer.readCharSequence(len, StandardCharsets.UTF_8).toString());
    }

    public static void handle(MessageLogin message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            PlayerLoginHandler.instance().login(player.getGameProfile().getName(), message.pwd);
        }
        ctx.get().setPacketHandled(true);
    }
}
