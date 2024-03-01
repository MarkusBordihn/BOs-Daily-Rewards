/*
 * Copyright 2022 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.dailyrewards;

import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSetup {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected ServerSetup() {
  }

  public static void handleServerStartingEvent(ServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    log.info("{} Server Starting setup on {} ...", Constants.LOG_REGISTER_PREFIX, server);
    RewardData.prepare(server);
    RewardUserData.prepare(server);
    SpecialRewardUserData.prepare(server);

    List<ItemStack> rewardItems = RewardData.get().getRewardsForCurrentMonth();
    if (rewardItems.isEmpty()) {
      log.warn("No rewards found for this month!");
    } else {
      log.info("Rewards for this Month: {}", rewardItems);
    }

    List<ItemStack> specialRewardItems = RewardData.get().getSpecialRewardsForCurrentMonth();
    if (specialRewardItems.isEmpty()) {
      log.info("No special rewards found for this month!");
    } else {
      log.info("Special Rewards for this Month: {}", specialRewardItems);
    }
  }

}
