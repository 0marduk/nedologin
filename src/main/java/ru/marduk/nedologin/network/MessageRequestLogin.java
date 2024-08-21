package ru.marduk.nedologin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.client.PasswordHolder;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
public record MessageRequestLogin() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageRequestLogin> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NLConstants.MODID, "m_req_login"));
    public static final MessageRequestLogin INSTANCE = new MessageRequestLogin();
    public static final StreamCodec<ByteBuf, MessageRequestLogin> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void handle(final MessageRequestLogin data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            PacketDistributor.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
