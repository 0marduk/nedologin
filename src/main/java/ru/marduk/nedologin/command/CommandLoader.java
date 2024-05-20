package ru.marduk.nedologin.command;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.command.arguments.ArgumentTypeEntryName;
import ru.marduk.nedologin.command.arguments.ArgumentTypeHandlerPlugin;

@Mod.EventBusSubscriber(modid = NLConstants.MODID)
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
