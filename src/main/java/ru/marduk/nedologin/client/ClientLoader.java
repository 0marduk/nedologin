package ru.marduk.nedologin.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.network.MessageLogin;
import ru.marduk.nedologin.network.NetworkLoader;

@Mod.EventBusSubscriber(modid = NLConstants.MODID, value = Dist.CLIENT)
public final class ClientLoader {

    @SubscribeEvent
    public static void joinServer(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getConnection().isMemoryConnection()) return;
        Nedologin.logger.debug("Sending login packet to the server...");
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
    }

    @SubscribeEvent
    public static void onClientRegisterCommand(RegisterClientCommandsEvent event) {
        ChangePasswordCommand.register(event.getDispatcher());
    }
}
