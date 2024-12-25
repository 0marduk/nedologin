package ru.marduk.nedologin.mixin;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;

import java.util.Arrays;

@SuppressWarnings("CancellableInjectionUsage") // IDE plugin bug ig, it gets cancelled on line 41
@Mixin(Commands.class)
public class MixinCommands {
    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void canUseCommand(ParseResults<CommandSourceStack> parseResults, String string, CallbackInfoReturnable<Integer> cir) {
        Nedologin.logger.debug("Checking command '{}'", string);

        CommandSource realSource = parseResults.getContext().getSource().source;

        if (!(realSource instanceof ServerPlayer cast)) {
            return;
        }
        if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
            return;
        }

        if (Arrays.asList(NLConfig.INSTANCE.whitelistCommands).contains(string)) {
            return;
        }

        Nedologin.logger.warn("Denied {} to execute command '{}' before login",
                string, string);

        cir.cancel();
    }
}