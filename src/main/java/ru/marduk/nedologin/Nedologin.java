package ru.marduk.nedologin;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.marduk.nedologin.command.CommandLoader;
import ru.marduk.nedologin.server.ServerLoader;
import ru.marduk.nedologin.network.NetworkLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(NLConstants.MODID)
@ParametersAreNonnullByDefault
public final class Nedologin {
    public static Logger logger = LogManager.getLogger(NLConstants.MODID);

    public Nedologin(ModContainer container) {
        container.getEventBus().addListener(CommandLoader::commonSetup);
        container.getEventBus().addListener(ServerLoader::serverSetup);
        // container.getEventBus().addListener((FMLCommonSetupEvent e) -> NetworkLoader.registerPackets());

        container.registerConfig(ModConfig.Type.SERVER, NLConfig.SERVER_SPEC);
    }
}
