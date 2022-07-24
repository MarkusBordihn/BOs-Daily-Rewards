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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import de.markusbordihn.dailyrewards.Constants;

public class RewardClientData {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static List<ItemStack> generalRewardItems = new ArrayList<>();
  private static List<ItemStack> userRewardItems = new ArrayList<>();
  private static int userRewardedDays = 0;

  protected RewardClientData() {

  }

  public static int getRewardedDaysForCurrentMonth() {
    return userRewardedDays;
  }

  public static void setRewardedDaysForCurrentMonth(int rewardedDays) {
    userRewardedDays = rewardedDays;
  }

  public static List<ItemStack> getGeneralRewardsForCurrentMonth() {
    return generalRewardItems;
  }

  public static void setGeneralRewardsForCurrentMonth(List<ItemStack> generalRewards) {
    generalRewardItems = generalRewards;
  }

  public static void setGeneralRewardsForCurrentMonth(String data) {
    CompoundNBT compoundTag;
    try {
      compoundTag = TagParser.parseTag(data);
    } catch (CommandSyntaxException commandSyntaxException) {
      throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
    }
    if (compoundTag != null) {
      setGeneralRewardsForCurrentMonth(compoundTag);
    }
  }

  public static void setGeneralRewardsForCurrentMonth(CompoundNBT compoundTag) {
    if (compoundTag.contains(RewardData.ITEM_LIST_TAG)) {
      ListNBT itemListNBT = compoundTag.getList(RewardData.ITEM_LIST_TAG, 10);
      generalRewardItems = new ArrayList<>();
      for (int i = 0; i < itemListNBT.size(); ++i) {
        generalRewardItems.add(ItemStack.of(itemListNBT.getCompound(i)));
      }
    } else {
      log.error("Unable to load general rewards for current month data from {}!", compoundTag);
    }
  }

  public static List<ItemStack> getUserRewardsForCurrentMonth() {
    return userRewardItems;
  }

  public static void setUserRewardsForCurrentMonth(List<ItemStack> userRewards) {
    userRewardItems = userRewards;
  }

  public static void setUserRewardsForCurrentMonth(String data) {
    CompoundNBT compoundTag;
    try {
      compoundTag = TagParser.parseTag(data);
    } catch (CommandSyntaxException commandSyntaxException) {
      throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
    }
    if (compoundTag != null) {
      setUserRewardsForCurrentMonth(compoundTag);
    }
  }

  public static void setUserRewardsForCurrentMonth(CompoundNBT compoundTag) {
    if (compoundTag.contains(RewardData.ITEM_LIST_TAG)) {
      ListNBT itemListNBT = compoundTag.getList(RewardData.ITEM_LIST_TAG, 10);
      userRewardItems = new ArrayList<>();
      for (int i = 0; i < itemListNBT.size(); ++i) {
        userRewardItems.add(ItemStack.of(itemListNBT.getCompound(i)));
      }
    } else {
      log.error("Unable to load user rewards for current month data from {}!", compoundTag);
    }
  }

}
