package ru.marduk.nedologin.server.handler.plugins;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.marduk.nedologin.server.handler.Login;
import ru.marduk.nedologin.network.MessageRequestLogin;
import ru.marduk.nedologin.server.handler.HandlerPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class ResendRequest implements HandlerPlugin {
    private ScheduledExecutorService executor;
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    @Override
    public void enable(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void preLogin(ServerPlayer player, Login login) {
        ScheduledFuture<?> future = executor.scheduleWithFixedDelay(() -> {
            PacketDistributor.sendToPlayer(player, new MessageRequestLogin());
        }, 0, 5, TimeUnit.SECONDS);
        Optional.ofNullable(futures.put(login.name, future)).ifPresent(f -> f.cancel(true));
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        Optional.ofNullable(futures.remove(login.name)).ifPresent(f -> f.cancel(true));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        Optional.ofNullable(futures.remove(player.getGameProfile().getName().toLowerCase()))
                .ifPresent(f -> f.cancel(true));
    }

    @Override
    public void disable() {
        futures.values().forEach(f -> f.cancel(true));
    }
}
