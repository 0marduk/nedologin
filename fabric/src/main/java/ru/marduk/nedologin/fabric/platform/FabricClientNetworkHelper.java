package ru.marduk.nedologin.fabric.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import ru.marduk.nedologin.fabric.network.MessageChangePassword;
import ru.marduk.nedologin.fabric.network.MessageLogin;
import ru.marduk.nedologin.fabric.network.NetworkConsts;
import ru.marduk.nedologin.platform.IClientNetworkHelper;

public class FabricClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public void SendMessageLogin(String pwd) {
        FriendlyByteBuf buffer = MessageLogin.encode(pwd);

        ClientPlayNetworking.send(NetworkConsts.MessageLogin, buffer);
    }

    @Override
    public void SendMessageChangePassword(String original, String to) {
        FriendlyByteBuf buffer = MessageChangePassword.encode(original, to);

        ClientPlayNetworking.send(NetworkConsts.MessageChangePassword, buffer);
    }
}
