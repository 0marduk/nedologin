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
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.storage.NLStorage;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Unique
    private final PlayerList playerList = (PlayerList) (Object) this;

    @Inject(method = "canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private void canPlayerLogin(SocketAddress pSocketAddress, GameProfile pGameProfile, CallbackInfoReturnable<Component> cir) {
        ServerPlayer onlinePlayer = playerList.getPlayerByName(pGameProfile.getName());

        if (onlinePlayer != null) {
            cir.setReturnValue(Component.literal("Someone is already playing with that nickname."));
            Nedologin.logger.warn("Someone tried to log in as an already present player {}", pGameProfile.getName());
        }

        if (!NLStorage.instance().storageProvider.registered(pGameProfile.getName()) && !NLConfig.SERVER.autoRegister.get()) {
            // this sounds like that one ""
            Nedologin.logger.warn("Player {} tried to register (automatic registration is disabled)", pGameProfile.getName());
            cir.setReturnValue(Component.literal("Automatic registration is disabled on this server."));
        }
    }
}
