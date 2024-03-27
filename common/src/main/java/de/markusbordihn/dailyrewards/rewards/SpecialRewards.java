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

package de.markusbordihn.dailyrewards.rewards;

import de.markusbordihn.dailyrewards.Constants;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static de.markusbordihn.dailyrewards.config.ModConfigs.COMMON;

public class SpecialRewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected static final Random random = new Random();

  protected SpecialRewards() {}

  public static List<ItemStack> calculateSpecialRewardItemsForMonth(int month) {
    YearMonth yearMonth = YearMonth.of(getCurrentYear(), month);
    int numberOfDays = yearMonth.lengthOfMonth();
    List<ItemStack> rewardItemsForMonth = getSpecialRewardItemForMonth(month);

    // Early return if we have no items.
    if (rewardItemsForMonth.isEmpty()) {
      log.info("No special reward items for month {} with {} days ...", month, numberOfDays);
      return new ArrayList<>();
    }

    // Early return if we have matching items.
    if (rewardItemsForMonth.size() >= numberOfDays) {
      log.info(
          "Found {} special reward items for month {} with {} days ...",
          rewardItemsForMonth.size(),
          month,
          numberOfDays);
      List<ItemStack> rewardItems =
          rewardItemsForMonth.stream().limit(numberOfDays).collect(Collectors.toList());
      if (Boolean.TRUE.equals(COMMON.shuffleRewardsSpecialItems)) {
        log.info("Shuffle special reward items for month {} ...", month);
        Collections.shuffle(rewardItems);
      }
      return rewardItems;
    }

    // If we have only one item for the month we will use it for all days.
    // No need to shuffle here with only one item.
    if (rewardItemsForMonth.size() == 1) {
      log.info(
          "Found only one special reward item for month {} with {} days ...", month, numberOfDays);
      List<ItemStack> rewardItems = new ArrayList<>();
      for (int i = 0; i < numberOfDays; i++) {
        rewardItems.add(rewardItemsForMonth.get(0));
      }
      return rewardItems;
    }

    // If we have not enough items for the month we will use them in the defined order.
    log.info(
        "Found only {} special reward items for month {} with {} days ...",
        rewardItemsForMonth.size(),
        month,
        numberOfDays);
    List<ItemStack> rewardItems = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      rewardItems.add(rewardItemsForMonth.get(i % rewardItemsForMonth.size()));
    }

    // Shuffle items before returning.
    if (Boolean.TRUE.equals(COMMON.shuffleRewardsSpecialItems)) {
      log.info("Shuffle special reward items for month {} ...", month);
      Collections.shuffle(rewardItems);
    }

    return rewardItems;
  }

  public static List<ItemStack> getSpecialRewardItemForMonth(int month) {
    return switch (month) {
      case 1 -> RewardsItems.parseConfigItems(COMMON.rewardsJanuarySpecialItems);
      case 2 -> RewardsItems.parseConfigItems(COMMON.rewardsFebruarySpecialItems);
      case 3 -> RewardsItems.parseConfigItems(COMMON.rewardsMarchSpecialItems);
      case 4 -> RewardsItems.parseConfigItems(COMMON.rewardsAprilSpecialItems);
      case 5 -> RewardsItems.parseConfigItems(COMMON.rewardsMaySpecialItems);
      case 6 -> RewardsItems.parseConfigItems(COMMON.rewardsJuneSpecialItems);
      case 7 -> RewardsItems.parseConfigItems(COMMON.rewardsJulySpecialItems);
      case 8 -> RewardsItems.parseConfigItems(COMMON.rewardsAugustSpecialItems);
      case 9 -> RewardsItems.parseConfigItems(COMMON.rewardsSeptemberSpecialItems);
      case 10 -> RewardsItems.parseConfigItems(COMMON.rewardsOctoberSpecialItems);
      case 11 -> RewardsItems.parseConfigItems(COMMON.rewardsNovemberSpecialItems);
      case 12 -> RewardsItems.parseConfigItems(COMMON.rewardsDecemberSpecialItems);
      default -> new ArrayList<>();
    };
  }

  public static boolean hasSpecialRewardItemsForMonth(int month) {
    return !getSpecialRewardItemForMonth(month).isEmpty();
  }

  public static boolean hasSpecialRewardItemsForCurrentMonth() {
    return hasSpecialRewardItemsForMonth(getCurrentMonth());
  }

  public static int getCurrentDay() {
    return LocalDate.now().getDayOfMonth();
  }

  public static int getCurrentMonth() {
    return LocalDate.now().getMonthValue();
  }

  public static int getCurrentYear() {
    return LocalDate.now().getYear();
  }

  public static String getCurrentYearMonthDay() {
    return getCurrentYear() + "-" + getCurrentMonth() + "-" + getCurrentDay();
  }

  public static int getDaysCurrentMonth() {
    YearMonth yearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth());
    return yearMonth.lengthOfMonth();
  }

  public static int getDaysLeftCurrentMonth() {
    return getDaysCurrentMonth() - getCurrentDay();
  }

  public static List<String> getSpecialRewardUsersForMonth(int month) {
    return switch (month) {
      case 1 -> COMMON.rewardsJanuarySpecialUsers;
      case 2 -> COMMON.rewardsFebruarySpecialUsers;
      case 3 -> COMMON.rewardsMarchSpecialUsers;
      case 4 -> COMMON.rewardsAprilSpecialUsers;
      case 5 -> COMMON.rewardsMaySpecialUsers;
      case 6 -> COMMON.rewardsJuneSpecialUsers;
      case 7 -> COMMON.rewardsJulySpecialUsers;
      case 8 -> COMMON.rewardsAugustSpecialUsers;
      case 9 -> COMMON.rewardsSeptemberSpecialUsers;
      case 10 -> COMMON.rewardsOctoberSpecialUsers;
      case 11 -> COMMON.rewardsNovemberSpecialUsers;
      case 12 -> COMMON.rewardsDecemberSpecialUsers;
      default -> new ArrayList<>();
    };
  }

  public static List<String> getSpecialRewardUsersForCurrentMonth() {
    return getSpecialRewardUsersForMonth(getCurrentMonth());
  }

  public static boolean isSpecialRewardUserForCurrentMonth(String playerName) {
    List<String> specialRewardUsersForCurrentMonth = getSpecialRewardUsersForCurrentMonth();
    return specialRewardUsersForCurrentMonth == null
        || specialRewardUsersForCurrentMonth.isEmpty()
        || (getSpecialRewardUsersForCurrentMonth().size() == 1
            && specialRewardUsersForCurrentMonth.get(0).isEmpty())
        || specialRewardUsersForCurrentMonth.contains(playerName);
  }
}
