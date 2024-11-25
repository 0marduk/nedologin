package ru.marduk.nedologin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import ru.marduk.nedologin.command.arguments.ArgumentTypeEntryName;
import ru.marduk.nedologin.command.arguments.ArgumentTypeHandlerPlugin;

public final class CommandLoader {

    public static void argumentRegister() {
        registerByClass(ArgumentTypeEntryName.class, SingletonArgumentInfo.contextFree(ArgumentTypeEntryName::entryName));
        registerByClass(ArgumentTypeHandlerPlugin.class,
                SingletonArgumentInfo.contextFree(ArgumentTypeHandlerPlugin::allPlugins));
    }

    public static void commandRegister(CommandDispatcher<CommandSourceStack> event) {
        NLCommand.register(event);
    }

    // ripped from forge :P
    public static synchronized <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> void registerByClass(Class<A> infoClass, I argumentTypeInfo) {
        ArgumentTypeInfos.BY_CLASS.put(infoClass, argumentTypeInfo);
    }
}
