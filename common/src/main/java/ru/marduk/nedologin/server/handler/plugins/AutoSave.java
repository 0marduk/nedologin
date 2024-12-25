package ru.marduk.nedologin.server.handler.plugins;

import net.minecraft.server.level.ServerPlayer;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.Login;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.server.handler.HandlerPlugin;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class AutoSave implements HandlerPlugin {
    private ScheduledFuture<?> future;

    @Override
    public void enable(ScheduledExecutorService executor) {
        this.future = executor.scheduleAtFixedRate(() -> {
            if (NLStorage.instance().storageProvider.dirty()) {
                Nedologin.logger.info("Auto saving entries");
                long start = System.currentTimeMillis();
                try {
                    NLStorage.instance().storageProvider.save();
                } catch (IOException e) {
                    Nedologin.logger.error("Failed saving nedologin entries", e);
                }
                Nedologin.logger.info("Done! Took {} ms.", System.currentTimeMillis() - start);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void preLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void preLogout(ServerPlayer player) {
        // NO-OP
    }

    @Override
    public void disable() {
        future.cancel(true);
    }
}
