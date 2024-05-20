package ru.marduk.nedologin;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

    public Nedologin() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommandLoader::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerLoader::serverSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> NetworkLoader.registerPackets());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, NLConfig.SERVER_SPEC);
    }
}
