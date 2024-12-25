package ru.marduk.nedologin.server.handler.plugins;

import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import ru.marduk.nedologin.platform.Service;
import ru.marduk.nedologin.server.handler.HandlerPlugin;
import ru.marduk.nedologin.server.handler.Login;
import ru.marduk.nedologin.server.storage.NLStorage;

public final class RestrictGameType implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayer player, Login login) {
        Service.PLATFORM.getServer().tell(new TickTask(1, () -> player.setGameMode(GameType.SPECTATOR)));
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        Service.PLATFORM.getServer().tell(new TickTask(1, () -> player.setGameMode(NLStorage.instance().storageProvider.gameType(player.getGameProfile().getName().toLowerCase()))));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        player.setGameMode(GameType.SPECTATOR);
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
