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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.rewards.Rewards;

public class RewardUserData extends SavedData {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final String ITEMS_TAG = "RewardItems";
  public static final String ITEM_LIST_TAG = "ItemList";
  public static final String LAST_REWARDED_DAY_TAG = "LastRewardedDay";
  public static final String REWARDED_DAYS_TAG = "RewardedDays";
  public static final String USER_REWARDS_TAG = "UserRewards";
  public static final String YEAR_MONTH_TAG = "YearMonth";
  public static final String YEAR_MONTH_USER_TAG = "YearMonthUser";

  private static final String FILE_ID = Constants.MOD_ID + "_user";

  private static MinecraftServer server;
  private static RewardUserData data;

  private static ConcurrentHashMap<String, List<ItemStack>> rewardItemsMap =
      new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, Integer> rewardedDaysMap = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, String> lastRewardedDayMap = new ConcurrentHashMap<>();

  public RewardUserData() {
    this.setDirty();
  }

  public static void prepare(MinecraftServer server) {
    // Make sure we preparing the data only once for the same server!
    if (server == null || server == RewardUserData.server && RewardUserData.data != null) {
      return;
    }

    log.info("{} preparing reward user data for {}", Constants.LOG_NAME, server);
    RewardUserData.server = server;

    // Using a global approach and storing relevant data in the overworld only!
    ServerLevel serverLevel = server.getLevel(Level.OVERWORLD);
    if (serverLevel != null) {
      RewardUserData.data = serverLevel.getDataStorage().computeIfAbsent(RewardUserData::load,
          RewardUserData::new, RewardUserData.getFileId());
    } else {
      log.error("{} unable to get server level {} for storing data!", Constants.LOG_NAME,
          serverLevel);
    }
  }

  public static boolean available() {
    RewardUserData.get();
    return RewardUserData.data != null;
  }

  public static RewardUserData get() {
    if (RewardUserData.data == null) {
      prepare(ServerLifecycleHooks.getCurrentServer());
    }
    return RewardUserData.data;
  }

  public static String getFileId() {
    return FILE_ID;
  }

  public static String getKeyId(int year, int month, UUID uuid) {
    return year + "-" + month + ":" + uuid.toString();
  }


  public void addRewardFor(int year, int month, int day, UUID uuid, ItemStack itemStack) {
    List<ItemStack> rewards = getRewardsFor(year, month, uuid);
    int rewardIndex = --day;
    if (rewardIndex >= 0 && rewards.size() > rewardIndex) {
      rewards.add(rewardIndex, itemStack);
    } else {
      rewards.add(itemStack);
    }
    this.setDirty();
  }

  public void addRewardForCurrentMonth(int day, UUID uuid, ItemStack itemStack) {
    addRewardFor(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), day, uuid, itemStack);
  }

  public List<ItemStack> getRewardsFor(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
    return rewardItemsMap.computeIfAbsent(key, id -> new ArrayList<>());
  }

  public List<ItemStack> getRewardsForCurrentMonth(UUID uuid) {
    return getRewardsFor(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid);
  }

  public boolean hasUnclaimedRewardsForCurrentMonth(UUID uuid) {
    List<ItemStack> rewardItemStack = getRewardsForCurrentMonth(uuid);
    for (ItemStack itemStack : rewardItemStack) {
      if (!itemStack.isEmpty() && !itemStack.is(ModItems.TAKEN_REWARD.get())) {
        return true;
      }
    }
    return false;
  }

  public CompoundTag getRewardsForCurrentMonthSyncData(UUID uuid) {
    List<ItemStack> rewardItems = getRewardsForCurrentMonth(uuid);
    CompoundTag syncData = new CompoundTag();
    ListTag itemListTag = new ListTag();
    for (int i = 0; i < rewardItems.size(); ++i) {
      ItemStack itemStack = rewardItems.get(i);
      CompoundTag itemStackTag = new CompoundTag();
      itemStack.save(itemStackTag);
      itemListTag.add(itemStackTag);
    }
    syncData.put(ITEM_LIST_TAG, itemListTag);
    return syncData;
  }

  public void setRewardsForCurrentMonth(UUID uuid, List<ItemStack> rewardItems) {
    setRewardsFor(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid, rewardItems);
  }

  public void setRewardsFor(int year, int month, UUID uuid, List<ItemStack> rewardItems) {
    log.debug("Set rewards for {}-{} and player {} to: {}", year, month, uuid, rewardItems);
    String key = getKeyId(year, month, uuid);
    rewardItemsMap.put(key, rewardItems);
    this.setDirty();
  }

  public String getLastRewardedDay(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
    return lastRewardedDayMap.computeIfAbsent(key, id -> "");
  }

  public String getLastRewardedDayForCurrentMonth(UUID uuid) {
    return getLastRewardedDay(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid);
  }

  public void setLastRewardedDay(int year, int month, UUID uuid, String lastRewardedDay) {
    log.debug("Set last rewarded day for {}-{} and player {} to {}", year, month, uuid,
        lastRewardedDay);
    String key = getKeyId(year, month, uuid);
    lastRewardedDayMap.put(key, lastRewardedDay);
    this.setDirty();
  }

  public void setLastRewardedDayForCurrentMonth(UUID uuid) {
    setLastRewardedDay(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid,
        Rewards.getCurrentYearMonthDay());
  }

  public boolean hasRewardedToday(UUID uuid) {
    String lastRewardedDay = getLastRewardedDayForCurrentMonth(uuid);
    return Rewards.getCurrentYearMonthDay().equals(lastRewardedDay);
  }

  public int getRewardedDays(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
    return rewardedDaysMap.getOrDefault(key, 0);
  }

  public int getRewardedDaysForCurrentMonth(UUID uuid) {
    return getRewardedDays(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid);
  }

  public int increaseRewardedDays(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
    int rewardedDays = rewardedDaysMap.getOrDefault(key, 0);
    rewardedDaysMap.put(key, ++rewardedDays);
    this.setDirty();
    return rewardedDays;
  }

  public int increaseRewardedDaysForCurrentMonth(UUID uuid) {
    return increaseRewardedDays(Rewards.getCurrentYear(), Rewards.getCurrentMonth(), uuid);
  }

  public static RewardUserData load(CompoundTag compoundTag) {
    RewardUserData rewardData = new RewardUserData();
    log.info("{} loading reward user data ... {}", Constants.LOG_NAME, compoundTag);

    // Restoring rewards items per year-month:uuid
    if (compoundTag.contains(USER_REWARDS_TAG)) {
      ListTag listTag = compoundTag.getList(USER_REWARDS_TAG, 10);
      for (int i = 0; i < listTag.size(); ++i) {
        CompoundTag rewardUserTag = listTag.getCompound(i);

        // Get Reward key
        String rewardKey = rewardUserTag.getString(YEAR_MONTH_USER_TAG);

        // Restoring rewards items per year-month:uuid
        List<ItemStack> rewardItems = new ArrayList<>();
        ListTag itemListTag = rewardUserTag.getList(ITEMS_TAG, 10);
        for (int i2 = 0; i2 < itemListTag.size(); ++i2) {
          ItemStack itemStack = ItemStack.of(itemListTag.getCompound(i2));
          rewardItems.add(itemStack);
        }
        rewardItemsMap.put(rewardKey, rewardItems);

        // Restoring last rewarded day and totally rewarded days for the month.
        rewardedDaysMap.put(rewardKey, rewardUserTag.getInt(REWARDED_DAYS_TAG));
        lastRewardedDayMap.put(rewardKey, rewardUserTag.getString(LAST_REWARDED_DAY_TAG));
      }
    }
    log.debug("{} Loaded stored rewards user data from disk: {}", Constants.LOG_NAME, rewardData);

    return rewardData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    // Get a list of all keys which needs to be stored.
    Set<String> rewardKeys = new HashSet<>();
    rewardKeys.addAll(rewardItemsMap.keySet());
    rewardKeys.addAll(rewardedDaysMap.keySet());
    rewardKeys.addAll(lastRewardedDayMap.keySet());

    if (rewardKeys.isEmpty()) {
      log.warn("{} unable to save reward user data, because data are empty!", Constants.LOG_NAME);
      return compoundTag;
    }
    log.info("{} saving reward user data for {} ...", Constants.LOG_NAME, rewardKeys);

    // Iterate trough the stored keys to make sure we don't forget anything.
    ListTag listTag = new ListTag();

    compoundTag.put(USER_REWARDS_TAG, listTag);
    for (String rewardKey : rewardKeys) {
      CompoundTag rewardUserTag = new CompoundTag();

      // Using year month uuid as key
      rewardUserTag.putString(YEAR_MONTH_USER_TAG, rewardKey);

      // Storing rewards items per year-month:uuid
      ListTag itemListTag = new ListTag();
      List<ItemStack> rewardItems = rewardItemsMap.get(rewardKey);
      for (int i = 0; i < rewardItems.size(); ++i) {
        ItemStack itemStack = rewardItems.get(i);
        CompoundTag itemStackTag = new CompoundTag();
        itemStack.save(itemStackTag);
        itemListTag.add(itemStackTag);
      }
      rewardUserTag.put(ITEMS_TAG, itemListTag);

      // Adding last rewarded day and totally rewarded days for the month.
      rewardUserTag.putInt(REWARDED_DAYS_TAG, rewardedDaysMap.getOrDefault(rewardKey, 0));
      rewardUserTag.putString(LAST_REWARDED_DAY_TAG,
          lastRewardedDayMap.getOrDefault(rewardKey, ""));

      // Storing entry
      listTag.add(rewardUserTag);
    }

    return compoundTag;
  }

}
