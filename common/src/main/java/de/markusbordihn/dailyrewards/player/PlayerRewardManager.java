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

package de.markusbordihn.dailyrewards.player;

import static de.markusbordihn.dailyrewards.config.ModConfigs.COMMON;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import de.markusbordihn.dailyrewards.rewards.RewardsScreen;
import de.markusbordihn.dailyrewards.rewards.SpecialRewards;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerRewardManager {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final short REWARD_CHECK_TICK = 20 * 60; // every 1 Minute
  private static final MutableComponent claimCommand =
      Component.literal("/DailyRewards claim")
          .setStyle(
              Style.EMPTY
                  .withColor(ChatFormatting.GREEN)
                  .withClickEvent(
                      new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/DailyRewards claim")));

  private static short ticker = 0;
  private static Set<ServerPlayer> playerList = ConcurrentHashMap.newKeySet();

  protected PlayerRewardManager() {}

  public static void onServerAboutToStartEvent(MinecraftServer server) {
    playerList = ConcurrentHashMap.newKeySet();
    int rewardTimePerDay = COMMON.rewardTimePerDay;
    int rewardTimePerDayTicks = rewardTimePerDay * 60 * 20;

    if (Boolean.TRUE.equals(COMMON.automaticRewardPlayers)) {
      log.info(
          "Daily rewards will be automatically granted after {} min ({} ticks) a player is online.",
          rewardTimePerDay,
          rewardTimePerDayTicks);
    } else {
      log.warn(
          "Daily rewards will not be automatically granted. Use `/DailyRewards reward today <player>` to manually grant rewards.");
    }

    if (Boolean.TRUE.equals(COMMON.automaticRewardSpecialPlayers)) {
      log.info(
          "Special daily rewards will be automatically granted after {} min ({} ticks) a player is online.",
          rewardTimePerDay,
          rewardTimePerDayTicks);
    } else {
      log.warn(
          "Special daily rewards will not be automatically granted. Use `/DailyRewards reward_special today <player>` to manually grant rewards.");
    }
  }

  public static void handlePlayerLoggedInEvent(ServerPlayer player) {
    // Track currently logged in player.
    playerList.add(player);

    // Track unclaimed rewards for player.
    boolean hasUnclaimedRewards = false;

    // Get player UUID
    UUID uuid = player.getUUID();
    if (uuid == null) {
      log.error("{} Unable to get UUID for player {}", Constants.LOG_NAME, player);
      return;
    }

    // Days left for current month to claim rewards.
    int daysLeftCurrentMonth = Rewards.getDaysLeftCurrentMonth();

    // Check if player has any unclaimed rewards.
    if (COMMON.showUnclaimedRewardsOnPlayerJoin
        && RewardUserData.get().hasUnclaimedRewardsForCurrentMonth(uuid)) {
      if (daysLeftCurrentMonth > 0) {
        player.sendSystemMessage(
            Component.translatable(
                Constants.TEXT_PREFIX + "unclaimed_rewarded_item",
                player.getName(),
                daysLeftCurrentMonth));
      } else {
        player.sendSystemMessage(
            Component.translatable(
                    Constants.TEXT_PREFIX + "unclaimed_rewarded_item_today", player.getName())
                .withStyle(ChatFormatting.RED));
      }
      hasUnclaimedRewards = true;
    }

    // Check if player has any unclaimed special rewards.
    if (COMMON.showUnclaimedRewardsSpecialOnPlayerJoin
        && SpecialRewardUserData.get().hasUnclaimedRewardsForCurrentMonth(uuid)) {
      if (daysLeftCurrentMonth > 0) {
        player.sendSystemMessage(
            Component.translatable(
                Constants.TEXT_PREFIX + "unclaimed_special_rewarded_item",
                player.getName(),
                daysLeftCurrentMonth));
      } else {
        player.sendSystemMessage(
            Component.translatable(
                    Constants.TEXT_PREFIX + "unclaimed_special_rewarded_item_today",
                    player.getName())
                .withStyle(ChatFormatting.RED));
      }
      hasUnclaimedRewards = true;
    }

    // Open reward overview menu or show claim command, if player has any unclaimed rewards.
    if (hasUnclaimedRewards && Boolean.TRUE.equals(COMMON.showRewardMenuOnPlayerJoin)) {
      switch (COMMON.rewardScreenType) {
        case COMPACT:
          RewardsScreen.openRewardCompactMenuForPlayer(player);
          break;
        case DEFAULT_OVERVIEW:
          RewardsScreen.openRewardOverviewMenuForPlayer(player);
          break;
        case SPECIAL_OVERVIEW:
          RewardsScreen.openRewardSpecialOverviewMenuForPlayer(player);
          break;
        default:
          RewardsScreen.openRewardOverviewMenuForPlayer(player);
          break;
      }
    } else if ((COMMON.showUnclaimedRewardsOnPlayerJoin
            || COMMON.showUnclaimedRewardsSpecialOnPlayerJoin)
        && COMMON.showRewardClaimCommandMessage
        && hasUnclaimedRewards) {
      player.sendSystemMessage(
          Component.translatable(Constants.TEXT_PREFIX + "claim_rewards", claimCommand));
    }
  }

  public static void handlePlayerLoggedOutEvent(ServerPlayer player) {
    if (playerList.isEmpty()) {
      return;
    }

    if (player != null) {
      playerList.remove(player);
    }
  }

  public static void handleServerTickEvent(MinecraftServer server) {
    // Early return if we should not reward player automatically or
    // we have no players online.
    if ((!COMMON.automaticRewardPlayers && !COMMON.automaticRewardSpecialPlayers)
        || ticker++ < REWARD_CHECK_TICK
        || playerList.isEmpty()) {
      return;
    }

    // Check if we have any players online which should be rewarded.
    for (ServerPlayer player : playerList) {
      if (player.tickCount > COMMON.rewardTimePerDay * 60 * 20) {
        UUID uuid = player.getUUID();
        String username = player.getName().getString();
        boolean showRewardClaimCommand = false;

        // Reward player if he has not been rewarded today.
        RewardUserData rewardUserData = RewardUserData.get();
        if (Boolean.FALSE.equals(COMMON.automaticRewardPlayers)) {
          log.debug("Player {} will not be automatically rewarded.", player);
        } else if (!rewardUserData.hasRewardedToday(uuid)) {
          // Update stored data
          rewardUserData.setLastRewardedDayForCurrentMonth(uuid);
          int rewardedDays = rewardUserData.increaseRewardedDaysForCurrentMonth(uuid);

          // Add reward for rewarded Days.
          ItemStack itemStack = RewardData.get().getRewardForCurrentMonth(rewardedDays);
          if (itemStack.isEmpty()) {
            log.error("Reward {} for day {} for current month was empty!", itemStack, rewardedDays);
          } else {
            rewardUserData.addRewardForCurrentMonth(rewardedDays, uuid, itemStack);
            if (COMMON.showReceivedRewardMessage) {
              player.sendSystemMessage(
                  Component.translatable(
                      Constants.TEXT_PREFIX + "rewarded_item",
                      player.getName(),
                      itemStack,
                      rewardedDays));
            }
            showRewardClaimCommand = true;
          }

          log.info(
              "Reward player {} daily reward for {} days with {} ...",
              player,
              rewardedDays,
              itemStack);
        }

        // Special Reward for player if he has not been rewarded today.
        SpecialRewardUserData specialRewardUserData = SpecialRewardUserData.get();
        if (Boolean.FALSE.equals(COMMON.automaticRewardSpecialPlayers)) {
          log.debug("Player {} will not be automatically special rewarded for today.", player);
        } else if (!specialRewardUserData.hasRewardedToday(uuid)
            && SpecialRewards.isSpecialRewardUserForCurrentMonth(username)) {
          // Update stored data
          specialRewardUserData.setLastRewardedDayForCurrentMonth(uuid);
          int rewardedDays = specialRewardUserData.increaseRewardedDaysForCurrentMonth(uuid);

          // Add reward for rewarded Days.
          ItemStack itemStack = RewardData.get().getSpecialRewardForCurrentMonth(rewardedDays);
          if (itemStack.isEmpty()) {
            log.error(
                "Special Reward {} for day {} for current month was empty!",
                itemStack,
                rewardedDays);
          } else {
            specialRewardUserData.addRewardForCurrentMonth(rewardedDays, uuid, itemStack);
            if (COMMON.showReceivedRewardSpecialMessage) {
              player.sendSystemMessage(
                  Component.translatable(
                      Constants.TEXT_PREFIX + "rewarded_item",
                      player.getName(),
                      itemStack,
                      rewardedDays));
            }
            showRewardClaimCommand = true;
          }

          log.info(
              "Special Reward player {} daily reward for {} days with {} ...",
              player,
              rewardedDays,
              itemStack);
        }

        // Show reward command if player has any unclaimed rewards.
        if (COMMON.showRewardClaimCommandMessage && showRewardClaimCommand) {
          player.sendSystemMessage(
              Component.translatable(Constants.TEXT_PREFIX + "claim_rewards", claimCommand));
        }
      }
    }

    ticker = 0;
  }
}
