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

package de.markusbordihn.dailyrewards.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardUserData;

public class RewardMenu extends AbstractContainerMenu {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Container
  private static final Container REWARD_CONTAINER = new SimpleContainer(0);
  private static final Container REWARD_USER_CONTAINER = new SimpleContainer(0);

  // Reward Data
  protected UUID playerUUID;
  protected int rewardedDays = 0;
  protected String lastRewardedDay;
  private static final List<ItemStack> REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();
  private static final List<ItemStack> USER_REWARDS_FOR_CURRENT_MONTH = new ArrayList<>();

  // Misc
  protected final Level level;
  protected final Player player;

  public RewardMenu(MenuType<?> menuType, int windowId, Inventory playerInventory) {
    super(menuType, windowId);
    this.player = playerInventory.player;
    this.level = this.player.getLevel();
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
    return true;
  }

}
