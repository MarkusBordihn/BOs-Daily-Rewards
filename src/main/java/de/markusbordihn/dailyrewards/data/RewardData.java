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

package de.markusbordihn.dailyrewards.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.rewards.Rewards;

public class RewardData extends SavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  public static final String ITEMS_TAG = "RewardItems";
  public static final String REWARDS_TAG = "Rewards";
  public static final String YEAR_MONTH_TAG = "Year-Month";

  private static final String FILE_ID = Constants.MOD_ID;

  private static MinecraftServer server;
  private static RewardData data;

  private static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
      new ConcurrentHashMap<>();

  public RewardData() {
    this.setDirty();
  }

  public static void prepare(MinecraftServer server) {
    // Make sure we preparing the data only once for the same server!
    if (server == null || server == RewardData.server && RewardData.data != null) {
      return;
    }

    log.info("{} preparing reward data for {}", Constants.LOG_NAME, server);
    RewardData.server = server;

    // Using a global approach and storing relevant data in the overworld only!
    RewardData.data = server.getLevel(Level.OVERWORLD).getDataStorage()
        .computeIfAbsent(RewardData::load, RewardData::new, RewardData.getFileId());
  }

  public static boolean available() {
    RewardData.get();
    return RewardData.data != null;
  }

  public static RewardData get() {
    if (RewardData.data == null) {
      prepare(ServerLifecycleHooks.getCurrentServer());
    }
    return RewardData.data;
  }

  public static String getFileId() {
    return FILE_ID;
  }

  public static String getKeyId(int year, int month) {
    return year + "-" + month;
  }

  public List<ItemStack> getRewardsFor(int year, int month) {
    String key = getKeyId(year, month);
    return rewardItemsMap.computeIfAbsent(key, id -> {
      return Rewards.calculateRewardItemsForMonth(month);
    });
  }

  public List<ItemStack> getRewardsForCurrentMonth() {
    return getRewardsFor(Rewards.getCurrentYear(), Rewards.getCurrentMonth());
  }

  public static RewardData load(CompoundTag compoundTag) {
    RewardData rewardData = new RewardData();
    log.info("{} loading reward data ... {}", Constants.LOG_NAME, compoundTag);

    // Restoring rewards items per year-month
    if (compoundTag.contains(REWARDS_TAG)) {
      ListTag listTag = compoundTag.getList(REWARDS_TAG, 10);
      for (int i = 0; i < listTag.size(); ++i) {
        CompoundTag rewardTag = listTag.getCompound(i);
        List<ItemStack> rewardItems = new ArrayList<>();
        ListTag itemListTag = rewardTag.getList(ITEMS_TAG, 10);
        String yearMonthKey = rewardTag.getString(YEAR_MONTH_TAG);
        for (int i2 = 0; i2 < itemListTag.size(); ++i2) {
          ItemStack itemStack = ItemStack.of(itemListTag.getCompound(i2));
          rewardItems.add(itemStack);
        }
        rewardItemsMap.put(yearMonthKey, rewardItems);
      }
    }
    log.debug("{} Loaded following stored rewards data from disk: {}", Constants.LOG_NAME,
        rewardItemsMap);

    return rewardData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving reward data ... {}", Constants.LOG_NAME, rewardItemsMap);

    ListTag listTag = new ListTag();
    for (Map.Entry<String, List<ItemStack>> reward : rewardItemsMap.entrySet()) {
      CompoundTag rewardTag = new CompoundTag();
      List<ItemStack> rewardItems = reward.getValue();

      // Storing rewards items per year-month
      ListTag itemListTag = new ListTag();
      for (int i = 0; i < rewardItems.size(); ++i) {
        ItemStack itemStack = rewardItems.get(i);
        if (itemStack.isEmpty()) {
          log.error("Reward item for month {} and day {} is empty!", reward.getKey(), i);
        }
        CompoundTag itemStackTag = new CompoundTag();
        itemStack.save(itemStackTag);
        itemListTag.add(itemStackTag);
      }
      if (!itemListTag.isEmpty()) {
        rewardTag.putString(YEAR_MONTH_TAG, reward.getKey());
        rewardTag.put(ITEMS_TAG, itemListTag);
        listTag.add(rewardTag);
      }
    }
    compoundTag.put(REWARDS_TAG, listTag);

    return compoundTag;
  }

}
