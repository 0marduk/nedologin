package ru.marduk.nedologin.server.storage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.NLRegistries;

public class NLStorage {
    public final StorageProvider storageProvider;
    private static NLStorage INSTANCE;

    public static NLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String provider, MinecraftServer server) {
        if (INSTANCE == null) {
            try {
                INSTANCE = new NLStorage(provider);
            } catch (Exception e) {
                Nedologin.logger.fatal("Failed to initialize login provider '{}': {}", provider, e.getMessage());
                server.halt(false);
            }
        }
    }

    private NLStorage(String provider) {
        storageProvider = NLRegistries.STORAGE_PROVIDERS.get(new ResourceLocation(provider))
                .orElseThrow(() -> new RuntimeException("Storage provider not found: " + provider))
                .get();
    }
}
