package ru.marduk.nedologin.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.client.SetPasswordScreen;

/*
this exists because neoforge access transformers stink
smh
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Inject(method = "init", at = @At("RETURN"))
    private void addPasswordButton(CallbackInfo ci) {
        Nedologin.logger.info("WHERE'S THE FUCKING MONEY LEBOWSKI");

        /*Button buttonSetPassword;

        buttonSetPassword = Button.builder(Component.literal("P"),
                        btn -> Minecraft.getInstance().setScreen(new SetPasswordScreen(this)))
                .bounds(this.width / 2 - 124, this.height / 4 + 48, 20, 20).build();

        this.addRenderableWidget(buttonSetPassword);*/
    }
}
