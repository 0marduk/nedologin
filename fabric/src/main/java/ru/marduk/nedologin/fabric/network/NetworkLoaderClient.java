package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkLoaderClient {
    public static void registerS2CPackets() {
        registerS2CHandler(NetworkConsts.MessageRequestLogin, new MessageRequestLogin());
        registerS2CHandler(NetworkConsts.MessageChangePasswordResponse, new MessageChangePasswordResponse());
    }

    public static void registerS2CHandler(ResourceLocation identifier, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
    }
}
