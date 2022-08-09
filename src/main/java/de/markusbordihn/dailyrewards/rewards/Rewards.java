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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;

@EventBusSubscriber
public class Rewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Config values
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected static final Random random = new Random();

  protected Rewards() {}

  @SubscribeEvent
  public static void handleFMLServerAboutToStartEvent(FMLServerAboutToStartEvent event) {
    log.info("Will use the following normal fill items: {}", getNormalFillItems());
    log.info("Will use the following rare fill items: {}", getRareFillItems());
  }

  public static List<ItemStack> calculateRewardItemsForMonth(int month) {
    YearMonth yearMonth = YearMonth.of(getCurrentYear(), month);
    int numberOfDays = yearMonth.lengthOfMonth();
    log.info("Calculate Reward items for month {} with {} days ...", month, numberOfDays);
    List<ItemStack> rewardItemsForMonth = getRewardItemForMonth(month);

    // Early return if we have matching items without shuffle.
    if (rewardItemsForMonth.size() >= numberOfDays) {
      return rewardItemsForMonth.stream().limit(numberOfDays).collect(Collectors.toList());
    }

    // Fill missing days with fill items.
    int numRewardItems = rewardItemsForMonth.size();
    int numMissingRewardItems = numberOfDays - numRewardItems;
    List<ItemStack> normalFillItems = getNormalFillItems();
    List<ItemStack> rareFillItems = getRareFillItems();
    List<ItemStack> lootBagFillItems = getLootBagFillItems();
    Set<ItemStack> rareDuplicates = new HashSet<>();
    Set<ItemStack> lootBagDuplicates = new HashSet<>();

    // Chances for different items types
    int rareFillItemsChance = rareFillItems.isEmpty() ? 0 : COMMON.rareFillItemsChance.get();
    int lootBackFillItemChance =
        lootBagFillItems.isEmpty() ? 0 : COMMON.lootBagFillItemsChance.get();

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

      // There is a 1:x (1:15) chance to get an loot bag item instead of an normal item.
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
        fillItem = normalFillItems.get(random.nextInt(normalFillItems.size()));
      }

      rewardItemsForMonth.add(fillItem);
    }

    // Shuffle items before returning
    Collections.shuffle(rewardItemsForMonth);
    return rewardItemsForMonth;
  }

  public static List<ItemStack> getRewardItemForMonth(int month) {
    switch (month) {
      case 1:
        return parseConfigItems(COMMON.rewardsJanuaryItems.get());
      case 2:
        return parseConfigItems(COMMON.rewardsFebruaryItems.get());
      case 3:
        return parseConfigItems(COMMON.rewardsMarchItems.get());
      case 4:
        return parseConfigItems(COMMON.rewardsAprilItems.get());
      case 5:
        return parseConfigItems(COMMON.rewardsMayItems.get());
      case 6:
        return parseConfigItems(COMMON.rewardsJuneItems.get());
      case 7:
        return parseConfigItems(COMMON.rewardsJulyItems.get());
      case 8:
        return parseConfigItems(COMMON.rewardsAugustItems.get());
      case 9:
        return parseConfigItems(COMMON.rewardsSeptemberItems.get());
      case 10:
        return parseConfigItems(COMMON.rewardsOctoberItems.get());
      case 11:
        return parseConfigItems(COMMON.rewardsNovemberItems.get());
      case 12:
        return parseConfigItems(COMMON.rewardsDecemberItems.get());
      default:
        return new ArrayList<>();
    }
  }

  public static List<ItemStack> getNormalFillItems() {
    return parseConfigItems(COMMON.normalFillItems.get());
  }

  public static ItemStack getNormalFillItem() {
    List<ItemStack> normalFillItems = getNormalFillItems();
    return normalFillItems.get(random.nextInt(normalFillItems.size()));
  }

  public static List<ItemStack> getRareFillItems() {
    return parseConfigItems(COMMON.rareFillItems.get());
  }

  public static ItemStack getRareFillItem() {
    List<ItemStack> rareFillItems = getRareFillItems();
    return rareFillItems.get(random.nextInt(rareFillItems.size()));
  }

  public static List<ItemStack> getLootBagFillItems() {
    return parseConfigItems(COMMON.lootBagFillItems.get());
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

  public static int getDaysCurrentMonth() {
    YearMonth yearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth());
    return yearMonth.lengthOfMonth();
  }

  public static List<ItemStack> parseConfigItems(List<String> configItems) {
    List<ItemStack> items = new ArrayList<>();

    for (String configItem : configItems) {
      String itemName = configItem;
      int itemCount = 1;
      if (configItem.chars().filter(delimiter -> delimiter == ':').count() == 2) {
        String[] itemParts = configItem.split(":");
        itemName = itemParts[0] + ":" + itemParts[1];
        itemCount = Integer.parseInt(itemParts[2]);
      }
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
      if (item == null || item == Items.AIR) {
        log.warn("Unable to find reward item {} in the registry!", itemName);
      } else {
        ItemStack itemStack = new ItemStack(item);
        itemStack.setCount(itemCount);
        items.add(itemStack);
      }
    }

    return items;
  }

}
