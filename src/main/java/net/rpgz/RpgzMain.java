package net.rpgz;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;
import net.rpgz.screen.ModMenuTypes;

@Mod(RpgzMain.MOD_ID)
public class RpgzMain {
    public static final String MOD_ID = "rpgz";
    public static final String INVENTORY_KEY = "UnionInventory";
    private static final String NETWORK_PROTOCOL_VERSION = "1";
    public final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID,"main"), () -> NETWORK_PROTOCOL_VERSION, NETWORK_PROTOCOL_VERSION::equals, NETWORK_PROTOCOL_VERSION::equals);

    public RpgzMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ConfigInit.init();
        SoundInit.register(modEventBus);
        TagInit.init();
        ModMenuTypes.register(modEventBus);
    }

}
