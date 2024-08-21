package ru.marduk.nedologin.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.network.MessageLogin;
import ru.marduk.nedologin.network.NetworkLoader;

@EventBusSubscriber(modid = NLConstants.MODID, value = Dist.CLIENT)
public final class ClientLoader {

    @SubscribeEvent
    public static void joinServer(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getConnection().isMemoryConnection()) return;
        Nedologin.logger.debug("Sending login packet to the server...");
        PacketDistributor.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
    }

    @SubscribeEvent
    public static void onClientRegisterCommand(RegisterClientCommandsEvent event) {
        ChangePasswordCommand.register(event.getDispatcher());
    }
}
