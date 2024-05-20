package ru.marduk.nedologin.server.storage;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.marduk.nedologin.server.NLRegistries;

@OnlyIn(Dist.DEDICATED_SERVER)
public class NLStorage {
    public final StorageProvider storageProvider;
    private static NLStorage INSTANCE;

    public static NLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String provider) {
        if (INSTANCE == null) {
            INSTANCE = new NLStorage(provider);
        }
    }

    private NLStorage(String provider) {
        storageProvider = NLRegistries.STORAGE_PROVIDERS.get(new ResourceLocation(provider))
                .orElseThrow(() -> new RuntimeException("Storage provider not found: " + provider))
                .get();
    }
}
