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

package de.markusbordihn.dailyrewards.menu;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.slots.*;
import de.markusbordihn.dailyrewards.rewards.SpecialRewards;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardMenu extends AbstractContainerMenu {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Container
  private static final Container REWARD_CONTAINER = new SimpleContainer(0);
  private static final Container REWARD_USER_CONTAINER = new SimpleContainer(0);
  private static final Container SPECIAL_REWARD_CONTAINER = new SimpleContainer(0);
  private static final Container SPECIAL_REWARD_USER_CONTAINER = new SimpleContainer(0);
  private static final List<ItemStack> REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();
  private static final List<ItemStack> USER_REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();
  private static final List<ItemStack> SPECIAL_REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();
  private static final List<ItemStack> USER_SPECIAL_REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();
  protected final Level level;
  protected final Player player;
  // Reward Data
  protected int rewardedDays = 0;
  protected String lastRewardedDay;
  // Special Reward Data
  protected int rewardedSpecialDays = 0;
  protected String lastRewardedSpecialDay;
  protected boolean specialRewardAvailable = true;
  // Misc
  protected UUID playerUUID;

  public RewardMenu(MenuType<?> menuType, int windowId, Inventory playerInventory) {
    super(menuType, windowId);
    this.player = playerInventory.player;
    this.level = this.player.level();
    this.specialRewardAvailable =
        SpecialRewards.hasSpecialRewardItemsForCurrentMonth()
            && SpecialRewards.isSpecialRewardUserForCurrentMonth(this.player.getName().getString());
  }

  public void syncRewardContainer(Player player) {
    if (getRewardsForCurrentMonth() != null && !getRewardsForCurrentMonth().isEmpty()) {
      syncRewardsUserContainer(player);
    }
    if (getSpecialRewardsForCurrentMonth() != null
        && !getSpecialRewardsForCurrentMonth().isEmpty()) {
      syncSpecialRewardsUserContainer(player);
    }
  }

  public void syncRewardsUserContainer(Player player) {
    if (level.isClientSide) {
      return;
    }
    List<ItemStack> userRewards = new ArrayList<>();
    for (int index = 0; index < this.getRewardsUserContainer().getContainerSize(); index++) {
      ItemStack itemStack = this.getRewardsUserContainer().getItem(index);
      if (itemStack != null && !itemStack.isEmpty()) {
        userRewards.add(itemStack);
      }
    }
    RewardUserData.get().setRewardsForCurrentMonth(player.getUUID(), userRewards);
  }

  public void syncSpecialRewardsUserContainer(Player player) {
    if (level.isClientSide) {
      return;
    }
    List<ItemStack> userRewards = new ArrayList<>();
    for (int index = 0; index < this.getSpecialRewardsUserContainer().getContainerSize(); index++) {
      ItemStack itemStack = this.getSpecialRewardsUserContainer().getItem(index);
      if (itemStack != null && !itemStack.isEmpty()) {
        userRewards.add(itemStack);
      }
    }
    SpecialRewardUserData.get().setRewardsForCurrentMonth(player.getUUID(), userRewards);
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public int getRewardedDays() {
    return this.rewardedDays;
  }

  public String getLastRewardedDay() {
    return this.lastRewardedDay;
  }

  public Container getRewardsContainer() {
    return REWARD_CONTAINER;
  }

  public Container getRewardsUserContainer() {
    return REWARD_USER_CONTAINER;
  }

  public List<ItemStack> getRewardsForCurrentMonth() {
    return REWARDS_FOR_CURRENT_MONTH;
  }

  public List<ItemStack> getUserRewardsForCurrentMonth() {
    return USER_REWARDS_FOR_CURRENT_MONTH;
  }

  public int getRewardedSpecialDays() {
    return this.rewardedSpecialDays;
  }

  public String getLastRewardedSpecialDay() {
    return this.lastRewardedSpecialDay;
  }

  public boolean isSpecialRewardAvailable() {
    return this.specialRewardAvailable;
  }

  public Container getSpecialRewardsContainer() {
    return SPECIAL_REWARD_CONTAINER;
  }

  public Container getSpecialRewardsUserContainer() {
    return SPECIAL_REWARD_USER_CONTAINER;
  }

  public List<ItemStack> getSpecialRewardsForCurrentMonth() {
    return SPECIAL_REWARDS_FOR_CURRENT_MONTH;
  }

  public List<ItemStack> getUserSpecialRewardsForCurrentMonth() {
    return USER_SPECIAL_REWARDS_FOR_CURRENT_MONTH;
  }

  public DailyRewardSlot createRewardSlot(
      ItemStack itemStack, int rewardedDays, Container container, int index, int x, int y) {
    if (itemStack.is(ModItems.SKIP_DAY.get())) {
      // Check if we have already skipped this day and show the skipped day slot.
      if (index < rewardedDays) {
        return new SkippedDaySlot(container, index, x, y, this);
      } else {
        return new SkipDaySlot(container, index, x, y, this);
      }
    } else if (itemStack.is(ModItems.SKIPPED_DAY.get())) {
      return new SkippedDaySlot(container, index, x, y, this);
    } else if (itemStack.is(ModItems.LOCK_DAY.get())) {
      // Check if we have already unlocked this day and show the unlocked day slot.
      if (index < rewardedDays) {
        return new UnlockedDaySlot(container, index, x, y, this);
      } else {
        return new LockedDaySlot(container, index, x, y, this);
      }
    } else if (itemStack.is(ModItems.UNLOCK_DAY.get())) {
      return new LockedDaySlot(container, index, x, y, this);
    } else {
      // If the reward is takeable show the takeable reward slot.
      return new TakeableRewardSlot(container, index, x, y, this);
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    Slot slot = this.slots.get(slotIndex);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack itemStack = slot.getItem();

    // Store changes if itemStack is not empty.
    if (itemStack.isEmpty()) {
      slot.set(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return player != null && player.isAlive();
  }
}
