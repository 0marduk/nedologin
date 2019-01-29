package top.seraphjack.simplelogin;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = SimpleLogin.MODID,acceptableRemoteVersions = "*")
public class SimpleLogin {

    public static final String MODID = "simplelogin";

    @SidedProxy(serverSide = "top.seraphjack.simplelogin.server.ServerProxy", clientSide = "top.seraphjack.simplelogin.client.ClientProxy")
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        proxy.serverStarting(e);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent e) {
        proxy.serverStopping(e);
    }

}