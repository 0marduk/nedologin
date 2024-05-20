package ru.marduk.nedologin.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Unique
    private final PlayerList playerList = (PlayerList) (Object) this;

    @Inject(method = "canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private void canPlayerLogin(SocketAddress pSocketAddress, GameProfile pGameProfile, CallbackInfoReturnable<Component> cir) {
        ServerPlayer onlinePlayer = playerList.getPlayerByName(pGameProfile.getName());

        if (onlinePlayer != null) {
            cir.setReturnValue(Component.literal("Выпей йаду придурок"));
        }
    }
}
