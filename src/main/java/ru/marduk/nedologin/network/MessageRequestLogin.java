package ru.marduk.nedologin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.marduk.nedologin.client.PasswordHolder;

import java.util.function.Supplier;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
public class MessageRequestLogin {

    public MessageRequestLogin() {
    }

    public static void encode(MessageRequestLogin msg, FriendlyByteBuf buffer) {
        // NO-OP
    }

    public static MessageRequestLogin decode(FriendlyByteBuf buffer) {
        return new MessageRequestLogin();
    }

    public static void handle(MessageRequestLogin message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkLoader.INSTANCE.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
        });
        ctx.get().setPacketHandled(true);
    }
}
