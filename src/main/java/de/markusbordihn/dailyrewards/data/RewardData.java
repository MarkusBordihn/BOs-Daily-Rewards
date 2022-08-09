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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.rewards.Rewards;

public class RewardData extends WorldSavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final String ITEMS_TAG = "RewardItems";
  public static final String ITEM_LIST_TAG = "ItemList";
  public static final String REWARDS_TAG = "Rewards";
  public static final String YEAR_MONTH_TAG = "YearMonth";

  private static final String FILE_ID = Constants.MOD_ID;

  private static MinecraftServer server;
  private static RewardData data;

  private static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
      new ConcurrentHashMap<>();

  public RewardData() {
    super(FILE_ID);
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
    RewardData.data = server.getLevel(World.OVERWORLD).getDataStorage()
        .computeIfAbsent(RewardData::new, RewardData.getFileId());
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
    return rewardItemsMap.computeIfAbsent(key, id -> Rewards.calculateRewardItemsForMonth(month));
  }

  public List<ItemStack> getRewardsForMonth(int month) {
    String key = getKeyId(Rewards.getCurrentYear(), month);
    return rewardItemsMap.computeIfAbsent(key, id -> Rewards.calculateRewardItemsForMonth(month));
  }

  public List<ItemStack> getRewardsForCurrentMonth() {
    return getRewardsFor(Rewards.getCurrentYear(), Rewards.getCurrentMonth());
  }

  public CompoundNBT getRewardsForCurrentMonthSyncData() {
    List<ItemStack> rewardItems = getRewardsForCurrentMonth();
    CompoundNBT syncData = new CompoundNBT();
    ListNBT itemListNBT = new ListNBT();
    for (int i = 0; i < rewardItems.size(); ++i) {
      ItemStack itemStack = rewardItems.get(i);
      CompoundNBT itemStackTag = new CompoundNBT();
      itemStack.save(itemStackTag);
      itemListNBT.add(itemStackTag);
    }
    syncData.put(ITEM_LIST_TAG, itemListNBT);
    return syncData;
  }

  public ItemStack getRewardForCurrentMonth(int day) {
    List<ItemStack> rewards = getRewardsForCurrentMonth();
    int rewardIndex = --day;
    if (rewardIndex >= 0 && rewards.size() > rewardIndex) {
      return rewards.get(rewardIndex).copy();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void load(CompoundNBT compoundTag) {
    log.info("{} loading reward data ... {}", Constants.LOG_NAME, compoundTag);

    // Restoring rewards items per year-month
    if (compoundTag.contains(REWARDS_TAG)) {
      ListNBT listTag = compoundTag.getList(REWARDS_TAG, 10);
      for (int i = 0; i < listTag.size(); ++i) {
        CompoundNBT rewardTag = listTag.getCompound(i);
        List<ItemStack> rewardItems = new ArrayList<>();
        ListNBT itemListNBT = rewardTag.getList(ITEMS_TAG, 10);
        String yearMonthKey = rewardTag.getString(YEAR_MONTH_TAG);
        for (int i2 = 0; i2 < itemListNBT.size(); ++i2) {
          ItemStack itemStack = ItemStack.of(itemListNBT.getCompound(i2));
          rewardItems.add(itemStack);
        }
        rewardItemsMap.put(yearMonthKey, rewardItems);
      }
    }
    log.debug("{} Loaded stored rewards data from disk: {}", Constants.LOG_NAME, rewardItemsMap);
  }

  @Override
  public CompoundNBT save(CompoundNBT compoundTag) {
    log.info("{} saving reward data ... {}", Constants.LOG_NAME, rewardItemsMap);

    ListNBT listTag = new ListNBT();
    for (Map.Entry<String, List<ItemStack>> reward : rewardItemsMap.entrySet()) {
      CompoundNBT rewardTag = new CompoundNBT();
      List<ItemStack> rewardItems = reward.getValue();

      // Storing rewards items per year-month
      ListNBT itemListNBT = new ListNBT();
      for (int i = 0; i < rewardItems.size(); ++i) {
        ItemStack itemStack = rewardItems.get(i);
        if (itemStack.isEmpty()) {

          log.error("Reward item for month {} and day {} is empty, will fill item!",
              reward.getKey(), i);
          itemStack = Rewards.getNormalFillItem();
        }
        CompoundNBT itemStackTag = new CompoundNBT();
        itemStack.save(itemStackTag);
        itemListNBT.add(itemStackTag);
      }
      if (!itemListNBT.isEmpty()) {
        rewardTag.putString(YEAR_MONTH_TAG, reward.getKey());
        rewardTag.put(ITEMS_TAG, itemListNBT);
        listTag.add(rewardTag);
      }
    }
    compoundTag.put(REWARDS_TAG, listTag);

    return compoundTag;
  }

}
