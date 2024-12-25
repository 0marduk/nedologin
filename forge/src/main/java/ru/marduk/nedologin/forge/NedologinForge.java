package ru.marduk.nedologin.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.client.ClientEvents;
import ru.marduk.nedologin.command.CommandLoader;
import ru.marduk.nedologin.forge.network.NetworkLoader;
import ru.marduk.nedologin.server.ServerEvents;

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
        public static void onGuiInit(ScreenEvent.Init event) {
            ClientEvents.onGuiInit(event.getScreen());
        }
    }
}