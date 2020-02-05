package top.seraphjack.simplelogin.server.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SimpleLogin;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SimpleLogin.MODID)
public class CapabilityLoader {
    @CapabilityInject(ISLEntry.class)
    public static Capability<ISLEntry> CAPABILITY_SL_ENTRY;

    @CapabilityInject(ILastPos.class)
    public static Capability<ILastPos> CAPABILITY_LAST_POS;

    @CapabilityInject(IRegisteredPlayers.class)
    public static Capability<IRegisteredPlayers> CAPABILITY_REGISTERED_PLAYERS;

    public CapabilityLoader() {
        CapabilityManager.INSTANCE.register(ISLEntry.class, new CapabilitySLEntry.Storage(), CapabilitySLEntry.Implementation::new);
        CapabilityManager.INSTANCE.register(ILastPos.class, new CapabilityLastPos.Storage(), CapabilityLastPos.Implementation::new);
        CapabilityManager.INSTANCE.register(IRegisteredPlayers.class, new CapabilityRegisteredPlayers.Storage(), CapabilityRegisteredPlayers.Implementation::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(SimpleLogin.MODID, "sl_password"),
                    new CapabilitySLEntry.Provider());

            event.addCapability(new ResourceLocation(SimpleLogin.MODID, "sl_lastPos"),
                    new CapabilityLastPos.Provider());
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<World> event) {
        if (event.getObject().dimension instanceof OverworldDimension) {
            event.addCapability(new ResourceLocation(SimpleLogin.MODID, "sl_registered_players"),
                    new CapabilityRegisteredPlayers.Provider());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        Capability[] capabilities = new Capability[]{CAPABILITY_LAST_POS, CAPABILITY_SL_ENTRY};
        for (Capability capability : capabilities) {
            Capability.IStorage storage = capability.getStorage();

            if (event.getOriginal().getCapability(capability, null).isPresent() && event.getPlayer().getCapability(capability, null).isPresent()) {
                INBT nbt = storage.writeNBT(capability, event.getOriginal().getCapability(capability, null), null);
                storage.readNBT(capability, event.getEntityPlayer().getCapability(capability, null), null, nbt);
            }
        }
    }
}
