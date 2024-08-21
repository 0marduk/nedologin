package ru.marduk.nedologin.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.marduk.nedologin.network.MessageChangePassword;
import ru.marduk.nedologin.network.NetworkLoader;

import static net.minecraft.commands.Commands.*;

@OnlyIn(Dist.CLIENT)
public final class ChangePasswordCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var theCommand =
                literal("nedologin")
                        .then(literal("change_password")
                                .then(argument("passwd", StringArgumentType.string())
                                        .executes(ChangePasswordCommand::changePassword)));
        dispatcher.register(theCommand);
    }

    private static int changePassword(CommandContext<CommandSourceStack> context) {
        final var to = StringArgumentType.getString(context, "passwd");
        var msg = new MessageChangePassword(PasswordHolder.instance().password(), to);
        PasswordHolder.instance().setPendingPassword(to);
        PacketDistributor.sendToServer(msg);
        return Command.SINGLE_SUCCESS;
    }
}
