package ru.marduk.nedologin.client;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableObject;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.mixin.TitleScreenAccessor;
import ru.marduk.nedologin.platform.Service;

public class ClientEvents {
    public static void onJoinServer() {
        Nedologin.logger.debug("Sending login packet to the server...");
        Service.CLIENT_NETWORK.SendMessageLogin(PasswordHolder.instance().password());
    }

    // Regarding the warning: refer to ChangePasswordCommand
    public static void onClientRegisterCommand(CommandDispatcher dispatcher) {
        ChangePasswordCommand.register(dispatcher);
    }

    public static void onGuiInit(Screen gui) {
        if (!(gui instanceof SetPasswordScreen) && !PasswordHolder.instance().initialized() ) {
            Minecraft.getInstance().setScreen(new SetPasswordScreen(gui));
        }

        if (gui instanceof TitleScreen) {

            MutableObject<Button> addable = new MutableObject<>(null);

            ((TitleScreenAccessor)gui).getChildren().stream()
                    .filter(w -> w instanceof AbstractWidget)
                    .map(w -> (AbstractWidget)w)
                    .filter(w -> w.getMessage()
                            .getString()
                            .equals(I18n.get("menu.multiplayer")))
                    .findFirst()
                    .ifPresent(w -> addable
                            .setValue(net.minecraft.client.gui.components.Button.builder(Component.literal("P"),
                                            btn -> Minecraft.getInstance().setScreen(new SetPasswordScreen(gui)))
                                    .bounds(w.getX() + w.getWidth() + 5, w.getY(), 20, 20).build()));

            if (addable.getValue() != null)
                gui.addRenderableWidget(addable.getValue());
        }
    }
}
