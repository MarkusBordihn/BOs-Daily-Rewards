package de.markusbordihn.dailyrewards.fabric;

import de.markusbordihn.dailyrewards.DailyRewards;
import net.fabricmc.api.ModInitializer;

public class DailyRewardsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DailyRewards.init();
    }

}