package ru.marduk.nedologin.command;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.command.arguments.ArgumentTypeEntryName;
import ru.marduk.nedologin.command.arguments.ArgumentTypeHandlerPlugin;

@EventBusSubscriber(modid = NLConstants.MODID)
public final class CommandLoader {

    public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
        ArgumentTypeInfos.registerByClass(ArgumentTypeEntryName.class, SingletonArgumentInfo.contextFree(ArgumentTypeEntryName::entryName));
        ArgumentTypeInfos.registerByClass(ArgumentTypeHandlerPlugin.class,
                SingletonArgumentInfo.contextFree(ArgumentTypeHandlerPlugin::allPlugins));
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        NLCommand.register(event.getDispatcher());
    }
}
