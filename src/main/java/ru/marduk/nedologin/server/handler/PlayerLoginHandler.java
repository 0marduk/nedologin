package ru.marduk.nedologin.server.handler;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.server.NLRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class PlayerLoginHandler {
    private static PlayerLoginHandler INSTANCE;

    private final Set<Login> loginList = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder()
            .setNameFormat("Nedologin-Worker-%d")
            .build());
    private final Map<ResourceLocation, HandlerPlugin> plugins = new ConcurrentHashMap<>();

    private PlayerLoginHandler(Stream<ResourceLocation> plugins) {
        // Load plugins
        plugins.forEach(this::loadPlugin);
    }

    public void loadPlugin(ResourceLocation rl) {
        if (this.plugins.containsKey(rl)) return;
        Nedologin.logger.info("Loading plugin {}", rl.toString());
        HandlerPlugin plugin = NLRegistries.PLUGINS.get(rl).orElseThrow(() -> {
            return new IllegalArgumentException("No such plugin found: " + rl);
        }).get();

        // Should not be possible though
        Optional.ofNullable(this.plugins.put(rl, plugin)).ifPresent(HandlerPlugin::disable);
        plugin.enable(executor);
    }

    public void unloadPlugin(ResourceLocation rl) {
        Optional.ofNullable(plugins.remove(rl)).ifPresent(p -> {
            p.disable();
            Nedologin.logger.info("Unloaded plugin {}", rl.toString());
        });
    }

    public Collection<ResourceLocation> listPlugins() {
        return new ImmutableSet.Builder<ResourceLocation>().addAll(this.plugins.keySet()).build();
    }

    public static void initLoginHandler(Stream<ResourceLocation> pluginList) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new PlayerLoginHandler(pluginList);
    }

    // Singleton
    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        id = id.toLowerCase();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Login login = getLoginByName(id);
        ServerPlayer player = server.getPlayerList().getPlayerByName(id);

        // Though player shouldn't be null if login is not null
        if (login == null || player == null) {
            return;
        }

        loginList.remove(login);

        if (!NLStorage.instance().storageProvider.registered(id)) {
            NLStorage.instance().storageProvider.register(id, pwd);
            Nedologin.logger.info("Player {} has successfully registered.", id);
            postLogin(player, login);
        } else if (NLStorage.instance().storageProvider.checkPassword(id, pwd)) {
            Nedologin.logger.info("Player {} has successfully logged in.", id);
            postLogin(player, login);
        } else {
            Nedologin.logger.warn("Player {} tried to login with a wrong password.", id);
            player.connection.disconnect(Component.literal("Wrong Password."));
        }
    }

    public void playerJoin(final ServerPlayer player) {
        Login login = new Login(player);
        loginList.add(login);
        plugins.values().forEach(p -> p.preLogin(player, login));
    }

    public void playerLeave(final ServerPlayer player) {
        loginList.removeIf(l -> l.name.equals(player.getGameProfile().getName()));
        plugins.values().forEach(p -> p.preLogout(player));
    }

    public void postLogin(final ServerPlayer player, final Login login) {
        plugins.values().forEach(p -> p.postLogin(player, login));
    }

    public boolean hasPlayerLoggedIn(String id) {
        return loginList.stream().noneMatch(e -> e.name.equals(id.toLowerCase()));
    }

    public void stop() {
        Nedologin.logger.info("Shutting down player login handler");
        Nedologin.logger.info("Disabling all plugins");
        this.plugins.values().forEach(HandlerPlugin::disable);
        this.plugins.clear();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                Nedologin.logger.error("Timed out waiting player login handler to terminate.");
            }
        } catch (InterruptedException ignore) {
            Nedologin.logger.error("Interrupted when waiting player login handler to terminate.");
        }
    }

    @Nullable
    private Login getLoginByName(String name) {
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }
}
