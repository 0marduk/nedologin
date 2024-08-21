package ru.marduk.nedologin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.storage.NLStorage;

import java.util.Objects;

public record MessageChangePassword(String original, String to) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageChangePassword> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NLConstants.MODID, "m_change_pwd"));

    public static final StreamCodec<ByteBuf, MessageChangePassword> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MessageChangePassword::original,
            ByteBufCodecs.STRING_UTF8,
            MessageChangePassword::to,
            MessageChangePassword::new
    );

    public static void handle(final MessageChangePassword msg, final IPayloadContext ctx) {
        String username = Objects.requireNonNull(ctx.player()).getGameProfile().getName();

        if (!NLConfig.SERVER.enableChangePassword.get()) {
            ctx.player().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_disabled"),
                    false
            );

            PacketDistributor.sendToPlayer((ServerPlayer) ctx.player(),
                    new MessageChangePasswordResponse(false));

            return;
        }

        if (NLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            NLStorage.instance().storageProvider.changePassword(username, msg.to);
            ctx.player().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_successful"),
                    false
            );
            PacketDistributor.sendToPlayer((ServerPlayer) ctx.player(),
                    new MessageChangePasswordResponse(true));
        } else {
            // Should never happen though
            ctx.player().displayClientMessage(
                    Component.translatable("nedologin.info.password_change_fail"),
                    false
            );
            PacketDistributor.sendToPlayer((ServerPlayer) ctx.player(),
                    new MessageChangePasswordResponse(false));
            Nedologin.logger.warn("Player {} tried to change password with a wrong password.", username);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
