package de.markusbordihn.dailyrewards.forge;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.DailyRewards;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class DailyRewardsForge {

    public DailyRewardsForge() {
        EventBuses.registerModEventBus(Constants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        DailyRewards.init();
    }

}