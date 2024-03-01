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
import de.markusbordihn.dailyrewards.config.CommonConfig;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class Rewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Config values
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected static final Random random = new Random();

  protected Rewards() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    if (Boolean.TRUE.equals(COMMON.useFillItems.get())) {
      log.info("Will use the following normal fill items: {}", getNormalFillItems());
      log.info("Will use the following rare fill items: {}", getRareFillItems());
    } else {
      log.info("Fill items are disabled, will use only reward items.");
    }
  }

  public static List<ItemStack> calculateRewardItemsForMonth(int month) {
    YearMonth yearMonth = YearMonth.of(getCurrentYear(), month);
    int numberOfDays = yearMonth.lengthOfMonth();
    log.info("Calculate Reward items for month {} with {} days ...", month, numberOfDays);
    List<ItemStack> rewardItemsForMonth = getRewardItemForMonth(month);

    // Early return if we have matching items without shuffle.
    if (Boolean.TRUE.equals(!COMMON.useFillItems.get())
        || rewardItemsForMonth.size() >= numberOfDays) {
      if (Boolean.FALSE.equals(COMMON.useFillItems.get())) {
        log.info(
            "Fill items are disabled, will use {} reward items for month {} with {} days ...",
            rewardItemsForMonth.size(),
            month,
            numberOfDays);
      } else {
        log.info(
            "Found {} reward items for month {} with {} days ...",
            rewardItemsForMonth.size(),
            month,
            numberOfDays);
      }
      List<ItemStack> rewardItems =
          rewardItemsForMonth.stream().limit(numberOfDays).collect(Collectors.toList());
      if (Boolean.TRUE.equals(COMMON.shuffleRewardsItems.get())) {
        log.info("Shuffle reward items for month {} ...", month);
        Collections.shuffle(rewardItems);
      }
      return rewardItems;
    }

    // Fill missing days with fill items.
    int numRewardItems = rewardItemsForMonth.size();
    int numMissingRewardItems = numberOfDays - numRewardItems;
    List<ItemStack> normalFillItems = getNormalFillItems();
    List<ItemStack> rareFillItems = getRareFillItems();
    List<ItemStack> lootBagFillItems = getLootBagFillItems();
    Set<ItemStack> rareDuplicates = new HashSet<>();
    Set<ItemStack> lootBagDuplicates = new HashSet<>();

    // Chances for different items types.
    int rareFillItemsChance = rareFillItems.isEmpty() ? 0 : COMMON.rareFillItemsChance.get();
    int lootBackFillItemChance =
        lootBagFillItems.isEmpty() ? 0 : COMMON.lootBagFillItemsChance.get();

    // Fill missing reward items.
    log.warn(
        "Found {} missing days without any items, will try to use fill items ...",
        numMissingRewardItems);
    for (int i = 0; i < numMissingRewardItems; i++) {
      ItemStack fillItem = null;

      // There is a 1:x (1:7) chance to get an rare item instead of an normal item.
      if (rareFillItemsChance > 0 && random.nextInt(rareFillItemsChance) == 0) {
        ItemStack rareFillItem = rareFillItems.get(random.nextInt(rareFillItems.size()));
        // Make sure we avoid duplicates of rare fill items.
        if (!rareDuplicates.contains(rareFillItem)) {
          fillItem = rareFillItem;
          rareDuplicates.add(rareFillItem);
        }
      }

      // There is a 1:x (1:15) chance to get an loot bag item instead of an normal
      // item.
      else if (lootBackFillItemChance > 0 && random.nextInt(lootBackFillItemChance) == 0) {
        ItemStack lootBagFillItem = lootBagFillItems.get(random.nextInt(lootBagFillItems.size()));
        // Make sure we avoid duplicates of lootBag fill items.
        if (!lootBagDuplicates.contains(lootBagFillItem)) {
          fillItem = lootBagFillItem;
          lootBagDuplicates.add(lootBagFillItem);
        }
      }

      // Make sure we have filled something.
      if (fillItem == null) {
        if (!normalFillItems.isEmpty()) {
          fillItem = normalFillItems.get(random.nextInt(normalFillItems.size()));
        } else {
          log.error(
              "Unable to find any fill item for {} of {} missing days, will use {} instead!",
              i + 1,
              numMissingRewardItems,
              Items.DIRT);
          fillItem = new ItemStack(Items.DIRT);
        }
      }

      rewardItemsForMonth.add(fillItem);
    }

    // Shuffle items before returning.
    if (Boolean.TRUE.equals(COMMON.shuffleRewardsItems.get())) {
      log.info("Shuffle reward items for month {} ...", month);
      Collections.shuffle(rewardItemsForMonth);
    }
    return rewardItemsForMonth;
  }

  public static List<ItemStack> getRewardItemForMonth(int month) {
    return switch (month) {
      case 1 -> RewardsItems.parseConfigItems(COMMON.rewardsJanuaryItems.get());
      case 2 -> RewardsItems.parseConfigItems(COMMON.rewardsFebruaryItems.get());
      case 3 -> RewardsItems.parseConfigItems(COMMON.rewardsMarchItems.get());
      case 4 -> RewardsItems.parseConfigItems(COMMON.rewardsAprilItems.get());
      case 5 -> RewardsItems.parseConfigItems(COMMON.rewardsMayItems.get());
      case 6 -> RewardsItems.parseConfigItems(COMMON.rewardsJuneItems.get());
      case 7 -> RewardsItems.parseConfigItems(COMMON.rewardsJulyItems.get());
      case 8 -> RewardsItems.parseConfigItems(COMMON.rewardsAugustItems.get());
      case 9 -> RewardsItems.parseConfigItems(COMMON.rewardsSeptemberItems.get());
      case 10 -> RewardsItems.parseConfigItems(COMMON.rewardsOctoberItems.get());
      case 11 -> RewardsItems.parseConfigItems(COMMON.rewardsNovemberItems.get());
      case 12 -> RewardsItems.parseConfigItems(COMMON.rewardsDecemberItems.get());
      default -> new ArrayList<>();
    };
  }

  public static List<ItemStack> getNormalFillItems() {
    return RewardsItems.parseConfigItems(COMMON.normalFillItems.get());
  }

  public static ItemStack getNormalFillItem() {
    List<ItemStack> normalFillItems = getNormalFillItems();
    return normalFillItems.get(random.nextInt(normalFillItems.size()));
  }

  public static List<ItemStack> getRareFillItems() {
    return RewardsItems.parseConfigItems(COMMON.rareFillItems.get());
  }

  public static ItemStack getRareFillItem() {
    List<ItemStack> rareFillItems = getRareFillItems();
    return rareFillItems.get(random.nextInt(rareFillItems.size()));
  }

  public static List<ItemStack> getLootBagFillItems() {
    return RewardsItems.parseConfigItems(COMMON.lootBagFillItems.get());
  }

  public static ItemStack getLootBagFillItem() {
    List<ItemStack> lootBagFillItems = getRareFillItems();
    return lootBagFillItems.get(random.nextInt(lootBagFillItems.size()));
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

  public static int getDaysPerMonth(int year, int month) {
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.lengthOfMonth();
  }

  public static int getDaysCurrentMonth() {
    return getDaysPerMonth(getCurrentYear(), getCurrentMonth());
  }

  public static int getDaysLeftCurrentMonth() {
    return getDaysCurrentMonth() - getCurrentDay();
  }
}
