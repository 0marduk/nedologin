package ru.marduk.nedologin.server.handler.plugins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.marduk.nedologin.server.LastPosData;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.HandlerPlugin;
import ru.marduk.nedologin.server.handler.Login;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.server.storage.Position;

public final class ProtectCoord implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            Position lastPos = LastPosData.getLastPos(player);

            if (lastPos.equals(LastPosData.defaultPosition)) {
                player.setPos(login.posX, login.posY, login.posZ);
            } else {
                player.setPos(lastPos.getX(), lastPos.getY(), lastPos.getZ());
                player.connection.teleport(lastPos.getX(), lastPos.getY(), lastPos.getZ(), login.rotY, login.rotX);
            }
        }));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        try {
            if (PlayerLoginHandler.instance().hasPlayerLoggedIn(player.getGameProfile().getName())) {
                final Position pos = new Position(player.getX(), player.getY(), player.getZ());
                LastPosData.setLastPos(player, pos);
            }
            BlockPos spawnPoint = player.getLevel().getSharedSpawnPos();
            player.setPos(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
        } catch (Exception ex) {
            Nedologin.logger.error("Fail to set player position to spawn point when logging out.", ex);
        }
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
