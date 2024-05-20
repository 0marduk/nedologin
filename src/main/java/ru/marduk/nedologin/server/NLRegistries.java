package ru.marduk.nedologin.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.marduk.nedologin.server.handler.HandlerPlugin;
import ru.marduk.nedologin.server.handler.plugins.*;
import ru.marduk.nedologin.server.storage.StorageProvider;
import ru.marduk.nedologin.server.storage.StorageProviderFile;
import ru.marduk.nedologin.server.storage.StorageProviderMariaDB;
import ru.marduk.nedologin.server.storage.StorageProviderSQLite;
import ru.marduk.nedologin.NLConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class NLRegistries<S> {
    private final Map<ResourceLocation, Supplier<? extends S>> plugins = new HashMap<>();

    public synchronized void register(ResourceLocation rl, Supplier<? extends S> plugin) {
        if (plugins.containsKey(rl)) {
            throw new IllegalArgumentException("Resource location " + rl.toString() + " already exists.");
        }
        plugins.put(rl, plugin);
    }

    public Optional<Supplier<? extends S>> get(ResourceLocation rl) {
        return Optional.ofNullable(plugins.get(rl));
    }

    public Set<ResourceLocation> list() {
        return plugins.keySet();
    }

    private NLRegistries() {
    }

    public static final NLRegistries<HandlerPlugin> PLUGINS = new NLRegistries<>();
    public static final NLRegistries<StorageProvider> STORAGE_PROVIDERS = new NLRegistries<>();

    static {
        // Default plugins
        PLUGINS.register(new ResourceLocation("nedologin", "auto_save"), AutoSave::new);
        PLUGINS.register(new ResourceLocation("nedologin", "protect_coord"), ProtectCoord::new);
        PLUGINS.register(new ResourceLocation("nedologin", "resend_request"), ResendRequest::new);
        PLUGINS.register(new ResourceLocation("nedologin", "restrict_game_type"), RestrictGameType::new);
        PLUGINS.register(new ResourceLocation("nedologin", "restrict_movement"), RestrictMovement::new);
        PLUGINS.register(new ResourceLocation("nedologin", "timeout"), Timeout::new);

        // Default storage providers
        STORAGE_PROVIDERS.register(new ResourceLocation("nedologin", "file"),
                () -> mustCall(() -> new StorageProviderFile(ServerLifecycleHooks.getCurrentServer().getWorldPath(NLConstants.NL_ENTRY))));
        STORAGE_PROVIDERS.register(new ResourceLocation("nedologin", "sqlite"),
                () -> mustCall((Callable<StorageProvider>) StorageProviderSQLite::new));
        STORAGE_PROVIDERS.register(new ResourceLocation("nedologin", "mariadb"),
                () -> mustCall((Callable<StorageProvider>) StorageProviderMariaDB::new));
    }

    private static <S> S mustCall(Callable<S> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
