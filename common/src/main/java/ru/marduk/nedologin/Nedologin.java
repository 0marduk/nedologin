package ru.marduk.nedologin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.marduk.nedologin.command.CommandLoader;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.server.storage.NLStorage;

import java.util.Arrays;

public final class Nedologin {
    public static final Logger logger = LogManager.getLogger(NLConstants.MODID);

    public static void CommonInit() {
        CommandLoader.argumentRegister();
    }

    public static void ServerInit(MinecraftServer server) {
        try {
            NLConfig.fetchConfig();
        } catch (Exception e) {
            logger.error("Failed to fetch config: ", e);
        }

        NLStorage.initialize(NLConfig.INSTANCE.storageProvider, server);
        PlayerLoginHandler.initLoginHandler(Arrays.stream(NLConfig.INSTANCE.plugins).map(ResourceLocation::new));
    }

    public static void ServerStop() {
        PlayerLoginHandler.instance().stop();

        try {
            Nedologin.logger.info("Saving all entries");
            if (NLStorage.instance() != null)
                NLStorage.instance().storageProvider.save();
        } catch (Exception e) {
            Nedologin.logger.error("Failed to save entries: ", e);
        }
    }
}
