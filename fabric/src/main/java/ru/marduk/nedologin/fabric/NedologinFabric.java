package ru.marduk.nedologin.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.command.CommandLoader;

public class NedologinFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Nedologin.CommonInit();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandLoader.commandRegister(dispatcher));
    }
}