package net.rpgz;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;

@Mod(RpgzMain.MOD_ID)
public class RpgzMain {
    public static final String MOD_ID = "rpgz";

    public RpgzMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ConfigInit.init();
        SoundInit.register(modEventBus);
        TagInit.init();
    }
}
