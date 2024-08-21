package ru.marduk.nedologin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;

public record MessageLogin(String pwd) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageLogin> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NLConstants.MODID, "m_login"));
    public static final StreamCodec<ByteBuf, MessageLogin> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MessageLogin::pwd,
            MessageLogin::new
    );

    public static void handle(final MessageLogin message, final IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        PlayerLoginHandler.instance().login(player.getGameProfile().getName(), message.pwd);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
