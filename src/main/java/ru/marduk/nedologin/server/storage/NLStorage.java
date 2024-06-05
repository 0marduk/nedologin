package ru.marduk.nedologin.server.storage;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.server.ServerStartingEvent;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.NLRegistries;

@OnlyIn(Dist.DEDICATED_SERVER)
public class NLStorage {
    public final StorageProvider storageProvider;
    private static NLStorage INSTANCE;

    public static NLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String provider, ServerStartingEvent event) {
        if (INSTANCE == null) {
            try {
                 INSTANCE = new NLStorage(provider);
            } catch (Exception e) {
                Nedologin.logger.fatal("Failed to initialize login provider '{}': {}", provider, e.getMessage());
                event.getServer().halt(false);
            }
        }
    }

    private NLStorage(String provider) {
        storageProvider = NLRegistries.STORAGE_PROVIDERS.get(new ResourceLocation(provider))
                .orElseThrow(() -> new RuntimeException("Storage provider not found: " + provider))
                .get();
    }
}
