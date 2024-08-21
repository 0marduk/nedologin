package ru.marduk.nedologin.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.marduk.nedologin.NLConstants;

@EventBusSubscriber(modid = NLConstants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkLoader {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.1");
        registrar.playToClient(
                MessageRequestLogin.TYPE,
                MessageRequestLogin.STREAM_CODEC,
                MessageRequestLogin::handle);
        registrar.playToServer(
                MessageLogin.TYPE,
                MessageLogin.STREAM_CODEC,
                MessageLogin::handle);
        registrar.playToServer(
                MessageChangePassword.TYPE,
                MessageChangePassword.STREAM_CODEC,
                MessageChangePassword::handle);
        registrar.playToClient(
                MessageChangePasswordResponse.TYPE,
                MessageChangePasswordResponse.STREAM_CODEC,
                MessageChangePasswordResponse::handle);
    }

    private NetworkLoader() {
        throw new UnsupportedOperationException("No instance");
    }
}
