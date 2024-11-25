package ru.marduk.nedologin.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import ru.marduk.nedologin.client.ClientEvents;
import ru.marduk.nedologin.fabric.network.NetworkLoaderClient;

public class NedologinFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkLoaderClient.registerS2CPackets();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientEvents.onJoinServer());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ClientEvents.onClientRegisterCommand(dispatcher));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> ClientEvents.onGuiOpen(screen));
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ClientEvents.onGuiInit(screen));
    }
}
