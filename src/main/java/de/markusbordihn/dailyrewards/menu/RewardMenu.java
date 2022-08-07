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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardClientData;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;

public class RewardMenu extends Container {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Define Slot index for easier access

  public static final int PLAYER_SLOT_START = 9;
  public static final int PLAYER_INVENTORY_SLOT_START = PLAYER_SLOT_START;
  public static final int PLAYER_SLOT_STOP = 3 * 9 + PLAYER_INVENTORY_SLOT_START + 8;

  // Defining basic layout options
  private static int containerSize = 32;
  private static int slotSize = 18;
  private static int rewardSlotSizeX = 23;
  private static int rewardSlotSizeY = 28;

  // Container
  private Inventory rewardsContainer = new Inventory(containerSize);
  private Inventory rewardsUserContainer = new Inventory(containerSize);

  // Meta data
  private int rewardedDays = 0;

  // Misc
  protected final World level;
  protected final PlayerEntity player;

  public RewardMenu(final int windowId, final PlayerInventory playerInventory) {
    this(windowId, playerInventory, ModMenuTypes.REWARD_MENU.get());
  }

  public RewardMenu(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
    this(windowId, playerInventory);
  }

  public RewardMenu(final int windowId, final PlayerInventory playerInventory, ContainerType<?> menuType) {
    super(menuType, windowId);

    // Others
    this.player = playerInventory.player;
    this.level = this.player.level;

    // Sync rewarded days
    this.rewardedDays = this.level.isClientSide ? RewardClientData.getRewardedDaysForCurrentMonth()
        : RewardUserData.get().getRewardedDaysForCurrentMonth(this.player.getUUID());

    // Sync possible rewards items for current month
    List<ItemStack> rewardsForCurrentMonth =
        this.level.isClientSide ? RewardClientData.getGeneralRewardsForCurrentMonth()
            : RewardData.get().getRewardsForCurrentMonth();
    if (!rewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < rewardsForCurrentMonth.size(); index++) {
        this.rewardsContainer.setItem(index, rewardsForCurrentMonth.get(index));
      }
    }

    // Sync user rewarded items for current month
    List<ItemStack> userRewards =
        level.isClientSide ? RewardClientData.getUserRewardsForCurrentMonth()
            : RewardUserData.get().getRewardsForCurrentMonth(player.getUUID());
    if (!userRewards.isEmpty()) {
      for (int index = 0; index < userRewards.size(); index++) {
        this.rewardsUserContainer.setItem(index, userRewards.get(index));
      }
    }

    // Rewards Slots
    int rewardStartPositionY = 17;
    int rewardStartPositionX = 9;
    for (int rewardRow = 0; rewardRow < 5; ++rewardRow) {
      for (int rewardColumn = 0; rewardColumn < 7; ++rewardColumn) {
        int rewardSlotIndex = rewardColumn + rewardRow * 7;
        if (userRewards.size() > rewardSlotIndex && userRewards.get(rewardSlotIndex) != null
            && !userRewards.get(rewardSlotIndex).isEmpty()) {
          this.addSlot(new TakeableRewardSlot(this.rewardsUserContainer, rewardSlotIndex,
              rewardStartPositionX + rewardColumn * rewardSlotSizeX,
              rewardStartPositionY + rewardRow * rewardSlotSizeY, this));
        } else if (rewardsForCurrentMonth.size() > rewardSlotIndex) {
          this.addSlot(new RewardSlot(this.rewardsContainer, rewardSlotIndex,
              rewardStartPositionX + rewardColumn * rewardSlotSizeX,
              rewardStartPositionY + rewardRow * rewardSlotSizeY, this));
        }
      }
    }

    // Player Inventory Slots
    int playerInventoryStartPositionY = 168;
    int playerInventoryStartPositionX = 6;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
            playerInventoryStartPositionX + inventoryColumn * slotSize,
            playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar Slots
    int hotbarStartPositionY = 226;
    int hotbarStartPositionX = 6;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(new Slot(playerInventory, playerInventorySlot,
          hotbarStartPositionX + playerInventorySlot * slotSize, hotbarStartPositionY));
    }
  }

  public void syncRewardsUserContainer(PlayerEntity player) {
    List<ItemStack> userRewards = new ArrayList<>();
    for (int index = 0; index < this.rewardsUserContainer.getContainerSize(); index++) {
      ItemStack itemStack = this.rewardsUserContainer.getItem(index);
      if (itemStack != null && !itemStack.isEmpty()) {
        userRewards.add(itemStack);
      }
    }
    if (level.isClientSide) {
      RewardClientData.setUserRewardsForCurrentMonth(userRewards);
    } else {
      RewardUserData.get().setRewardsForCurrentMonth(player.getUUID(), userRewards);
    }
  }

  public int getRewardedDays() {
    return this.rewardedDays;
  }

  @Override
  public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
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
  public boolean stillValid(PlayerEntity player) {
    return true;
  }

}
