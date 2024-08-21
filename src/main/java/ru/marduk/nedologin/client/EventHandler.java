package ru.marduk.nedologin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import ru.marduk.nedologin.NLConstants;
import net.minecraft.client.gui.components.Button;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = NLConstants.MODID, value = Dist.CLIENT)
public final class EventHandler {

    @SubscribeEvent
    public static void onGuiOpen(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof SetPasswordScreen) && !PasswordHolder.instance().initialized()) {
            Screen prev = event.getScreen();
            event.setNewScreen(new SetPasswordScreen(prev));
        }
    }

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Pre event) {
        Screen gui = event.getScreen();

        if (gui instanceof TitleScreen) {
            Button buttonSetPassword;

            buttonSetPassword = Button.builder(Component.literal("P"),
                    btn -> Minecraft.getInstance().setScreen(new SetPasswordScreen(gui)))
                    .bounds(gui.width / 2 - 124, gui.height / 4 + 56, 20, 20).build();

            event.addListener(buttonSetPassword);
        }
    }
}