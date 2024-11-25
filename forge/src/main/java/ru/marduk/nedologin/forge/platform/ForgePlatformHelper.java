package ru.marduk.nedologin.forge.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.platform.IPlatformHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigPath() {
        return Paths.get(".");
    }

    @Override
    public boolean isDedicated() {
        return false;
    }

    @Override
    public String getVersion() {
        ModInfo info = FMLLoader.getLoadingModList().getMods().stream()
                .filter(modInfo -> modInfo.getModId().equals(NLConstants.MODID))
                .findAny().get();

        return info.getVersion().toString();
    }

    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
