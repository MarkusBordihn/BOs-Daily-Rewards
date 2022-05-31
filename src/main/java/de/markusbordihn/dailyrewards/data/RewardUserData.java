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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.dailyrewards.Constants;

public class RewardUserData extends SavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static MinecraftServer server;
  private static RewardUserData data;

  private static final String FILE_ID = Constants.MOD_ID + "_user";

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

  public static RewardUserData load(CompoundTag compoundTag) {
    RewardUserData rewardData = new RewardUserData();
    log.info("{} loading reward user data ... {}", Constants.LOG_NAME, compoundTag);
    return rewardData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving reward user data ... {}", Constants.LOG_NAME, this);
    return compoundTag;
  }

}
