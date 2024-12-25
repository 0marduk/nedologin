package ru.marduk.nedologin.fabric.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.platform.IPlatformHelper;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FabricPlatformHelper implements IPlatformHelper {
    public static MinecraftServer minecraftServer;

    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer(NLConstants.MODID).get().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public MinecraftServer getServer() {
        return minecraftServer;
    }

    @Override
    public boolean isDedicated() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public Path getConfigPath() {
        return Paths.get(".");
    }
}
