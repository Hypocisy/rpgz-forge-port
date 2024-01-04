package net.rpgz.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;


@OnlyIn(Dist.CLIENT)
public class ModMenuIntegration {

    public ConfigScreenHandler.ConfigScreenFactory getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(RpgzConfig.class, parent).get();
    }
}
