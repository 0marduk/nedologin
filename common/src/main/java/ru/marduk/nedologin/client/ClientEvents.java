package ru.marduk.nedologin.client;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.platform.Service;

public class ClientEvents {
    public static void onGuiOpen(Screen screen) {
        if (!(screen instanceof SetPasswordScreen) && !PasswordHolder.instance().initialized()) {
            Minecraft.getInstance().setScreen(new SetPasswordScreen(screen));
        }
    }

    public static void onJoinServer() {
        Nedologin.logger.debug("Sending login packet to the server...");
        Service.CLIENT_NETWORK.SendMessageLogin(PasswordHolder.instance().password());
    }

    public static void onClientRegisterCommand(CommandDispatcher dispatcher) {
        ChangePasswordCommand.register(dispatcher);
    }

    public static void onGuiInit(Screen gui) {
        if (gui instanceof TitleScreen) {
            Button buttonSetPassword;

            buttonSetPassword = Button.builder(Component.literal("P"),
                            btn -> Minecraft.getInstance().setScreen(new SetPasswordScreen(gui)))
                    .bounds(gui.width / 2 - 124, gui.height / 4 + 48, 20, 20).build();
            gui.addRenderableWidget(buttonSetPassword);
        }
    }
}
