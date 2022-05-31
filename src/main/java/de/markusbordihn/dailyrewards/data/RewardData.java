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

  private static MinecraftServer server;
  private static RewardData data;

  private static final String FILE_ID = Constants.MOD_ID;

  private static ConcurrentHashMap<String, List<ItemStack>> rewardsMap = new ConcurrentHashMap<>();

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

  public List<ItemStack> getRewardsFor(int year, int month) {
    String key = year + "-" + month;
    return rewardsMap.computeIfAbsent(key, id -> {
      return Rewards.calculateRewardItemsForMonth(month);
    });
  }

  public static RewardData load(CompoundTag compoundTag) {
    RewardData rewardData = new RewardData();
    log.info("{} loading reward data ... {}", Constants.LOG_NAME, compoundTag);
    return rewardData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving reward data ... {}", Constants.LOG_NAME, this);

    ListTag listTag = new ListTag();
    for (Map.Entry<String, List<ItemStack>> reward : rewardsMap.entrySet()) {
      CompoundTag rewardTag = new CompoundTag();
      List<ItemStack> rewardItems = reward.getValue();

      // Storing rewards items
      ListTag itemListTag = new ListTag();
      for (int i = 0; i < rewardItems.size(); ++i) {
        ItemStack itemStack = rewardItems.get(i);
        if (!itemStack.isEmpty()) {
          CompoundTag itemStackTag = new CompoundTag();
          itemStack.save(itemStackTag);
          itemListTag.add(itemStackTag);
        }
      }
      if (!itemListTag.isEmpty()) {
        rewardTag.putString("Year-Month", reward.getKey());
        rewardTag.put("Items", itemListTag);
        listTag.add(rewardTag);
      }
    }
    compoundTag.put("Rewards", listTag);

    return compoundTag;
  }

}
