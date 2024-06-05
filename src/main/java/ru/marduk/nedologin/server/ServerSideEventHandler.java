package ru.marduk.nedologin.server;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.network.MessageRequestLogin;
import ru.marduk.nedologin.network.NetworkLoader;

import java.lang.reflect.Field;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = NLConstants.MODID)
public class ServerSideEventHandler {

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().playerJoin((ServerPlayer) event.getEntity());
        // noinspection InstantiationOfUtilityClass
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new MessageRequestLogin());
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerLoginHandler.instance().playerLeave((ServerPlayer) event.getEntity());
    }

    // Block command usage from unauthenticated players
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        if (command.startsWith("/")) command = command.substring(1);
        Nedologin.logger.debug("Checking command '{}'", command);
        CommandSource realSource = getRealSource(event.getParseResults().getContext().getSource());
        if (realSource == null) {
            return;
        }
        if (!(realSource instanceof ServerPlayer cast)) {
            return;
        }
        if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
            return;
        }
        if (NLConfig.SERVER.whitelistCommands.get().contains(command)) {
            return;
        }
        Nedologin.logger.warn("Denied {} to execute command '{}' before login",
                event.getParseResults().getContext().getSource().getTextName(), command);
        event.setCanceled(true);
    }

    private static final Field COMMAND_SOURCE_FIELD;

    static {
        Field f;
        try {
            f = ObfuscationReflectionHelper.findField(CommandSourceStack.class, "f_81288_");
            f.setAccessible(true);
        } catch (Exception ex) {
            Nedologin.logger.error("Failed to get command source field", ex);
            f = null;
        }
        COMMAND_SOURCE_FIELD = f;
    }

    private static CommandSource getRealSource(CommandSourceStack sourceStack) {
        if (COMMAND_SOURCE_FIELD == null) return null;
        try {
            return (CommandSource) COMMAND_SOURCE_FIELD.get(sourceStack);
        } catch (IllegalAccessException e) {
            Nedologin.logger.error("Failed to get real command source", e);
            return null;
        }
    }
}