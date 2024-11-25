package ru.marduk.nedologin.forge;

import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.client.ClientEvents;
import ru.marduk.nedologin.command.CommandLoader;
import ru.marduk.nedologin.forge.network.NetworkLoader;
import ru.marduk.nedologin.server.ServerEvents;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;

import java.util.Arrays;

@Mod(NLConstants.MODID)
public class NedologinForge {
    public NedologinForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NedologinCommonEvents::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> NetworkLoader.registerPackets());
    }

    @Mod.EventBusSubscriber(modid = NLConstants.MODID, value = Dist.DEDICATED_SERVER)
    public static class NedologinServerEvents {
        @SubscribeEvent
        public static void serverStopped(ServerStoppedEvent e) {
            Nedologin.ServerStop();
        }

        @SubscribeEvent
        public static void serverStarted(ServerStartingEvent e) {
            Nedologin.ServerInit(e.getServer());
        }

        @SubscribeEvent
        public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            ServerEvents.playerLeave(event.getEntity());
        }

        @SubscribeEvent
        public static void onCommand(CommandEvent event) {
            String command = event.getParseResults().getReader().getString();
            if (command.startsWith("/")) command = command.substring(1);
            Nedologin.logger.debug("Checking command '{}'", command);
            CommandSource realSource = event.getParseResults().getContext().getSource().source;
            if (!(realSource instanceof ServerPlayer cast)) {
                return;
            }
            if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
                return;
            }

            if (Arrays.asList(NLConfig.INSTANCE.whitelistCommands).contains(command)) {
                return;
            }

            Nedologin.logger.warn("Denied {} to execute command '{}' before login",
                    event.getParseResults().getContext().getSource().getTextName(), command);
            event.setCanceled(true);
        }
    }

    @Mod.EventBusSubscriber(modid = NLConstants.MODID)
    public static class NedologinCommonEvents {
        public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
            CommandLoader.argumentRegister();
        }

        @SubscribeEvent
        public static void commandRegister(RegisterCommandsEvent event) {
            CommandLoader.commandRegister(event.getDispatcher());
        }
    }

    @Mod.EventBusSubscriber(modid = NLConstants.MODID, value = Dist.CLIENT)
    public static class NedologinClientEvents {
        @SubscribeEvent
        public static void joinServer(ClientPlayerNetworkEvent.LoggingIn event) {
            // if (event.getConnection().isMemoryConnection()) return;
            ClientEvents.onJoinServer();
        }

        @SubscribeEvent
        public static void onClientRegisterCommand(RegisterClientCommandsEvent event) {
            ClientEvents.onClientRegisterCommand(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onGuiOpen(ScreenEvent.Opening event) {
            ClientEvents.onGuiOpen(event.getScreen());
        }

        @SubscribeEvent
        public static void onGuiInit(ScreenEvent.Init event) {
            ClientEvents.onGuiInit(event.getScreen());
        }
    }
}
