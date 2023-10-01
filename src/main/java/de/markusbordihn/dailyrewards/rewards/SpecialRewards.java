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

package de.markusbordihn.dailyrewards.rewards;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.ItemStack;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;

public class SpecialRewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Config values
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;

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
      log.info("Found {} special reward items for month {} with {} days ...",
          rewardItemsForMonth.size(), month, numberOfDays);
      List<ItemStack> rewardItems =
          rewardItemsForMonth.stream().limit(numberOfDays).collect(Collectors.toList());
      if (Boolean.TRUE.equals(COMMON.shuffleRewardsSpecialItems.get())) {
        log.info("Shuffle special reward items for month {} ...", month);
        Collections.shuffle(rewardItems);
      }
      return rewardItems;
    }

    // If we have only one item for the month we will use it for all days.
    // No need to shuffle here with only one item.
    if (rewardItemsForMonth.size() == 1) {
      log.info("Found only one special reward item for month {} with {} days ...", month,
          numberOfDays);
      List<ItemStack> rewardItems = new ArrayList<>();
      for (int i = 0; i < numberOfDays; i++) {
        rewardItems.add(rewardItemsForMonth.get(0));
      }
      return rewardItems;
    }

    // If we have not enough items for the month we will use them in the defined order.
    log.info("Found only {} special reward items for month {} with {} days ...",
        rewardItemsForMonth.size(), month, numberOfDays);
    List<ItemStack> rewardItems = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      rewardItems.add(rewardItemsForMonth.get(i % rewardItemsForMonth.size()));
    }

    // Shuffle items before returning.
    if (Boolean.TRUE.equals(COMMON.shuffleRewardsSpecialItems.get())) {
      log.info("Shuffle special reward items for month {} ...", month);
      Collections.shuffle(rewardItems);
    }

    return rewardItems;
  }

  public static List<ItemStack> getSpecialRewardItemForMonth(int month) {
    switch (month) {
      case 1:
        return RewardsItems.parseConfigItems(COMMON.rewardsJanuarySpecialItems.get());
      case 2:
        return RewardsItems.parseConfigItems(COMMON.rewardsFebruarySpecialItems.get());
      case 3:
        return RewardsItems.parseConfigItems(COMMON.rewardsMarchSpecialItems.get());
      case 4:
        return RewardsItems.parseConfigItems(COMMON.rewardsAprilSpecialItems.get());
      case 5:
        return RewardsItems.parseConfigItems(COMMON.rewardsMaySpecialItems.get());
      case 6:
        return RewardsItems.parseConfigItems(COMMON.rewardsJuneSpecialItems.get());
      case 7:
        return RewardsItems.parseConfigItems(COMMON.rewardsJulySpecialItems.get());
      case 8:
        return RewardsItems.parseConfigItems(COMMON.rewardsAugustSpecialItems.get());
      case 9:
        return RewardsItems.parseConfigItems(COMMON.rewardsSeptemberSpecialItems.get());
      case 10:
        return RewardsItems.parseConfigItems(COMMON.rewardsOctoberSpecialItems.get());
      case 11:
        return RewardsItems.parseConfigItems(COMMON.rewardsNovemberSpecialItems.get());
      case 12:
        return RewardsItems.parseConfigItems(COMMON.rewardsDecemberSpecialItems.get());
      default:
        return new ArrayList<>();
    }
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
    switch (month) {
      case 1:
        return COMMON.rewardsJanuarySpecialUsers.get();
      case 2:
        return COMMON.rewardsFebruarySpecialUsers.get();
      case 3:
        return COMMON.rewardsMarchSpecialUsers.get();
      case 4:
        return COMMON.rewardsAprilSpecialUsers.get();
      case 5:
        return COMMON.rewardsMaySpecialUsers.get();
      case 6:
        return COMMON.rewardsJuneSpecialUsers.get();
      case 7:
        return COMMON.rewardsJulySpecialUsers.get();
      case 8:
        return COMMON.rewardsAugustSpecialUsers.get();
      case 9:
        return COMMON.rewardsSeptemberSpecialUsers.get();
      case 10:
        return COMMON.rewardsOctoberSpecialUsers.get();
      case 11:
        return COMMON.rewardsNovemberSpecialUsers.get();
      case 12:
        return COMMON.rewardsDecemberSpecialUsers.get();
      default:
        return new ArrayList<>();
    }
  }

  public static List<String> getSpecialRewardUsersForCurrentMonth() {
    return getSpecialRewardUsersForMonth(getCurrentMonth());
  }

  public static boolean isSpecialRewardUserForCurrentMonth(String playerName) {
    List<String> specialRewardUsersForCurrentMonth = getSpecialRewardUsersForCurrentMonth();
    return specialRewardUsersForCurrentMonth == null || specialRewardUsersForCurrentMonth.isEmpty()
        || (getSpecialRewardUsersForCurrentMonth().size() == 1
            && specialRewardUsersForCurrentMonth.get(0).isEmpty())
        || specialRewardUsersForCurrentMonth.contains(playerName);
  }

}
