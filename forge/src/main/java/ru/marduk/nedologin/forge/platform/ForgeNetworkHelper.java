package ru.marduk.nedologin.forge.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import ru.marduk.nedologin.forge.network.*;
import ru.marduk.nedologin.platform.IClientNetworkHelper;
import ru.marduk.nedologin.platform.INetworkHelper;

import java.util.function.Supplier;

public class ForgeNetworkHelper implements INetworkHelper, IClientNetworkHelper {
    @Override
    public void SendMessageRequestLogin(ServerPlayer player) {
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageRequestLogin());
    }

    @Override
    public void SendMessageLogin(String pwd) {
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(pwd));
    }

    @Override
    public void SendMessageChangePassword(String original, String to) {
        NetworkLoader.INSTANCE.sendToServer(new MessageChangePassword(original, to));
    }

    @Override
    public void SendMessageChangePasswordResponse(ServerPlayer player, boolean status) {
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with((Supplier<ServerPlayer>) player), new MessageChangePasswordResponse(status));
    }
}
