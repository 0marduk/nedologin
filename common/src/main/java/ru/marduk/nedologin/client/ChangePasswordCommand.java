package ru.marduk.nedologin.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import ru.marduk.nedologin.platform.Service;

import static net.minecraft.commands.Commands.*;

public final class ChangePasswordCommand {
    public static void register(CommandDispatcher dispatcher) {
        var theCommand =
                literal("nedologin")
                        .then(literal("change_password")
                                .then(argument("passwd", StringArgumentType.string())
                                        .executes(ChangePasswordCommand::changePassword)));
        dispatcher.register(theCommand);
    }

    private static int changePassword(CommandContext<CommandSourceStack> context) {
        final var to = StringArgumentType.getString(context, "passwd");

        PasswordHolder.instance().setPendingPassword(to);
        Service.CLIENT_NETWORK.SendMessageChangePassword(PasswordHolder.instance().password(), to);
        return Command.SINGLE_SUCCESS;
    }
}
