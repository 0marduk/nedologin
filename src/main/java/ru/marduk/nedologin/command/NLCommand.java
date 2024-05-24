package ru.marduk.nedologin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import ru.marduk.nedologin.command.arguments.ArgumentTypeHandlerPlugin;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.command.arguments.ArgumentTypeEntryName;
import ru.marduk.nedologin.server.NLRegistries;
import ru.marduk.nedologin.server.handler.PlayerLoginHandler;
import ru.marduk.nedologin.server.storage.NLStorage;

import java.io.IOException;
import java.util.Collection;


public class NLCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("nedologin")
                .then(Commands.literal("save").requires((c) -> c.hasPermission(3))
                        .executes(NLCommand::save))
                .then(Commands.literal("unregister").requires((c) -> c.hasPermission(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .executes(NLCommand::unregister)))
                .then(Commands.literal("setDefaultGameType").requires((c) -> c.hasPermission(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
                                        .executes(NLCommand::setDefaultGamemode))))
                .then(Commands.literal("plugin").requires((c) -> c.hasPermission(3))
                        .then(Commands.literal("load")
                                .then(Commands.argument("plugin", ArgumentTypeHandlerPlugin.unloadedPlugins())
                                        .executes(NLCommand::loadPlugin)))
                        .then(Commands.literal("unload")
                                .then(Commands.argument("plugin", ArgumentTypeHandlerPlugin.loadedPlugins())
                                        .executes(NLCommand::unloadPlugin)))
                        .then(Commands.literal("available")
                                .executes(NLCommand::listAvailablePlugins))
                        .then(Commands.literal("loaded")
                                .executes(NLCommand::listLoadedPlugins)))
                .then(Commands.literal("about").executes(NLCommand::about));
        dispatcher.register(command);
    }

    private static int save(CommandContext<CommandSourceStack> ctx) {
        try {
            long start = System.currentTimeMillis();
            NLStorage.instance().storageProvider.save();
            long cost = System.currentTimeMillis() - start;
            ctx.getSource().sendSuccess(Component.literal("Done. Took " + cost + " ms."), true);
        } catch (IOException e) {
            ctx.getSource().sendSuccess(Component.literal("Error during saving entries, see log for details"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int unregister(CommandContext<CommandSourceStack> ctx) {
        NLStorage.instance().storageProvider.unregister(ArgumentTypeEntryName.getEntryName(ctx, "entry"));
        ctx.getSource().sendSuccess(
                Component.literal("Successfully unregistered."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setDefaultGamemode(CommandContext<CommandSourceStack> ctx) {
        GameType gameType = GameType.byId(ctx.getArgument("mode", Integer.class));
        NLStorage.instance().storageProvider.setGameType(ArgumentTypeEntryName.getEntryName(ctx, "entry"), gameType);
        ctx.getSource().sendSuccess(
                Component.literal("Successfully set entry default game type to " + gameType.getName() + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int about(CommandContext<CommandSourceStack> ctx) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        ModInfo info = FMLLoader.getLoadingModList().getMods().stream()
                .filter(modInfo -> modInfo.getModId().equals(NLConstants.MODID))
                .findAny().get();
        ctx.getSource().sendSuccess(
                Component.translatable("nedologin.command.about.info", info.getVersion().toString()),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int loadPlugin(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (NLRegistries.PLUGINS.get(plugin).isEmpty()) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.already_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().loadPlugin(plugin);

        ctx.getSource().sendSuccess(
                Component.translatable("nedologin.command.plugin.load_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int unloadPlugin(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (NLRegistries.PLUGINS.get(plugin).isEmpty()) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (!PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.not_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().unloadPlugin(plugin);
        ctx.getSource().sendSuccess(
                Component.translatable("nedologin.command.plugin.unload_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int listAvailablePlugins(CommandContext<CommandSourceStack> ctx) {
        Collection<ResourceLocation> plugins = NLRegistries.PLUGINS.list();
        ctx.getSource().sendSuccess(
                Component.translatable("nedologin.command.plugin.available_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int listLoadedPlugins(CommandContext<CommandSourceStack> ctx) {
        Collection<ResourceLocation> plugins = PlayerLoginHandler.instance().listPlugins();
        ctx.getSource().sendSuccess(
                Component.translatable("nedologin.command.plugin.loaded_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendSuccess(
                    Component.translatable("nedologin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

}
