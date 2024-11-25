package ru.marduk.nedologin.fabric.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkLoaderServer {
    public static void registerC2SPackets() {
        registerC2SHandler(NetworkConsts.MessageLogin, new MessageLogin());
        registerC2SHandler(NetworkConsts.MessageChangePassword, new MessageChangePassword());
    }

    public static void registerC2SHandler(ResourceLocation identifier, ServerPlayNetworking.PlayChannelHandler handler) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, handler);
    }
}
