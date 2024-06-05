package ru.marduk.nedologin.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;

import java.io.IOException;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = NLConstants.MODID)
public final class ServerLoader {

    public static void serverSetup(@SuppressWarnings("unused") FMLDedicatedServerSetupEvent event) {
        // NO-OP
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent e) throws RuntimeException {
        NLStorage.initialize(NLConfig.SERVER.storageProvider.get(), e);

        PlayerLoginHandler.initLoginHandler(NLConfig.SERVER.plugins.get().stream().map(ResourceLocation::new));
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent e) throws IOException {
        PlayerLoginHandler.instance().stop();

        Nedologin.logger.info("Saving all entries");
        if (NLStorage.instance() != null)
            NLStorage.instance().storageProvider.save();
    }
}
