package ru.marduk.nedologin.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import ru.marduk.nedologin.server.storage.NLStorage;
import ru.marduk.nedologin.platform.Service;

import java.util.concurrent.CompletableFuture;

public final class ArgumentTypeEntryName implements ArgumentType<EntryNameInput> {

    private static final DynamicCommandExceptionType ENTRY_NOT_EXIST = new DynamicCommandExceptionType((o -> Component.translatable("nedologin.command.error.entry_not_found")));

    private ArgumentTypeEntryName() {

    }

    public static ArgumentTypeEntryName entryName() {
        return new ArgumentTypeEntryName();
    }

    @Override
    public EntryNameInput parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();

        if (Service.PLATFORM.isDedicated() && !NLStorage.instance().storageProvider.registered(name)) {
            throw ENTRY_NOT_EXIST.create(name);
        }
        return EntryNameInput.of(name);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSourceStack) {
            return SharedSuggestionProvider.suggest(NLStorage.instance().storageProvider.getAllRegisteredUsername(), builder);
        } else if (context.getSource() instanceof ClientSuggestionProvider src) {
            return src.customSuggestion(context);
        }
        return Suggestions.empty();
    }

    public static <S> String getEntryName(CommandContext<S> ctx, String name) {
        return ctx.getArgument(name, EntryNameInput.class).getName();
    }
}
