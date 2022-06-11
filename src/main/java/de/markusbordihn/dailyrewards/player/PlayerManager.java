/**
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

package de.markusbordihn.dailyrewards.player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;

@EventBusSubscriber
public class PlayerManager {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static int rewardTimePerDay = COMMON.rewardTimePerDay.get();
  private static int rewardTimePerDayTicks = rewardTimePerDay * 60 * 20;

  private static final short REWARD_CHECK_TICK = 20 * 60;

  private static short ticker = 0;
  private static Set<ServerPlayer> playerList = ConcurrentHashMap.newKeySet();

  protected PlayerManager() {}

  @SubscribeEvent
  public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    playerList = ConcurrentHashMap.newKeySet();

    rewardTimePerDay = COMMON.rewardTimePerDay.get();
    rewardTimePerDayTicks = rewardTimePerDay * 60 * 20;

    log.info("Daily rewards will be granted after {} min ({} ticks) a player is online.",
        rewardTimePerDay, rewardTimePerDayTicks);
  }

  @SubscribeEvent
  public static void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
    String username = event.getPlayer().getName().getString();
    if (username.isEmpty()) {
      return;
    }
    ServerPlayer player =
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(username);
    log.info("{} Player {} {} logged in.", Constants.LOG_NAME, username, player);
    playerList.add(player);
  }

  @SubscribeEvent
  public static void handlePlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
    String username = event.getPlayer().getName().getString();
    if (username.isEmpty()) {
      return;
    }
    ServerPlayer player =
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(username);
    log.debug("{} Player {} {} logged out.", Constants.LOG_NAME, username, player);
    playerList.remove(player);
  }

  @SubscribeEvent
  public static void handleServerTickEvent(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END || ticker++ < REWARD_CHECK_TICK
        || playerList.isEmpty()) {
      return;
    }
    for (ServerPlayer player : playerList) {
      log.info("Test {}", player.tickCount);
      if (player.tickCount > rewardTimePerDayTicks) {
        log.info("Reward player {} daily reward for today ...", player);
      }
    }
    ticker = 0;
  }

}
