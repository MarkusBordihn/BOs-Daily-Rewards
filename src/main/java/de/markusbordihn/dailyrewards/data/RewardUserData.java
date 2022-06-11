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

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

public class RewardUserData extends SavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  public static final String ITEMS_TAG = "RewardItems";
  public static final String USER_REWARDS_TAG = "UserRewards";
  public static final String YEAR_MONTH_TAG = "Year-Month";

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
    RewardUserData.data = server.getLevel(Level.OVERWORLD).getDataStorage()
        .computeIfAbsent(RewardUserData::load, RewardUserData::new, RewardUserData.getFileId());
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


  public void addRewardFor(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
  }

  public List<ItemStack> getRewardsFor(int year, int month, UUID uuid) {
    String key = getKeyId(year, month, uuid);
    return rewardItemsMap.computeIfAbsent(key, id -> {
      return null;
      // return Rewards.calculateRewardItemsForMonth(month);
    });
  }

  public static RewardUserData load(CompoundTag compoundTag) {
    RewardUserData rewardData = new RewardUserData();
    log.info("{} loading reward user data ... {}", Constants.LOG_NAME, compoundTag);
    return rewardData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving reward user data ... {}", Constants.LOG_NAME, this);

    ListTag listTag = new ListTag();

    compoundTag.put(USER_REWARDS_TAG, listTag);
    for (Map.Entry<String, List<ItemStack>> reward : rewardItemsMap.entrySet()) {

      CompoundTag rewardTag = new CompoundTag();
      List<ItemStack> rewardItems = reward.getValue();

      // Storing rewards items per year-month
      ListTag itemListTag = new ListTag();
      for (int i = 0; i < rewardItems.size(); ++i) {
        ItemStack itemStack = rewardItems.get(i);
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

    return compoundTag;
  }

}
