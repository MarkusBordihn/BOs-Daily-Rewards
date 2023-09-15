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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.slots.EmptyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.LockedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.SkipDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.SkippedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.UnlockedDaySlot;
import de.markusbordihn.dailyrewards.rewards.SpecialRewards;

public class RewardSpecialOverviewMenu extends RewardMenu {

  // Define Slot index for easier access
  public static final int PLAYER_SLOT_START = 9;
  public static final int PLAYER_INVENTORY_SLOT_START = PLAYER_SLOT_START;
  public static final int PLAYER_SLOT_STOP = 3 * 9 + PLAYER_INVENTORY_SLOT_START + 8;

  // Defining basic layout options
  private static int containerSize = 32;
  private static int slotSize = 18;

  // Container layout
  public static final float REWARD_SLOT_SIZE_X = 20.5f;
  public static final int REWARD_SLOT_SIZE_Y = 31;
  public static final int REWARD_SLOT_START_POSITION_X = 8;
  public static final int REWARD_SLOT_START_POSITION_Y = 18;

  // Container
  private Container rewardsContainer = new SimpleContainer(containerSize);
  private Container rewardsUserContainer = new SimpleContainer(containerSize);

  // Rewards Data
  private List<ItemStack> rewardsForCurrentMonth = new ArrayList<>();
  private List<ItemStack> userRewardsForCurrentMonth = new ArrayList<>();

  public RewardSpecialOverviewMenu(final int windowId, final Inventory playerInventory) {
    this(windowId, playerInventory, ModMenuTypes.REWARD_OVERVIEW_MENU.get(),
        playerInventory.player.getUUID(),
        SpecialRewardUserData.get().getRewardedDaysForCurrentMonth(playerInventory.player.getUUID()),
        SpecialRewardUserData.get().getLastRewardedDayForCurrentMonth(playerInventory.player.getUUID()),
        SpecialRewardUserData.get().getRewardsForCurrentMonthSyncData(playerInventory.player.getUUID()),
        RewardData.get().getSpecialRewardsForCurrentMonthSyncData());
  }

  public RewardSpecialOverviewMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, ModMenuTypes.REWARD_OVERVIEW_MENU.get(), data.readUUID(),
        data.readInt(), data.readUtf(), data.readNbt(), data.readNbt());
  }

  public RewardSpecialOverviewMenu(final int windowId, final Inventory playerInventory,
      MenuType<?> menuType, UUID playerUUID, int rewardedDays, String lastRewardedDay,
      CompoundTag userRewardsForCurrentMonth, CompoundTag rewardsForCurrentMonth) {
    super(menuType, windowId, playerInventory);

    // Storing Data
    this.playerUUID = playerUUID;
    this.rewardedDays = rewardedDays;
    this.lastRewardedDay = lastRewardedDay;

    // Check if we have a valid player with the given UUID
    if (this.playerUUID == null || this.player.getUUID() == null
        || !this.playerUUID.equals(this.player.getUUID())) {
      log.error("{} Unable to verify player {} with UUID {}!", Constants.LOG_NAME, this.player,
          this.playerUUID);
      return;
    }

    // Sync possible rewards items for current month.
    this.rewardsForCurrentMonth =
        RewardData.getRewardsForCurrentMonthSyncData(rewardsForCurrentMonth);
    if (!rewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < this.rewardsForCurrentMonth.size(); index++) {
        this.rewardsContainer.setItem(index, this.rewardsForCurrentMonth.get(index));
      }
    }

    // Sync user rewarded items for current month.
    this.userRewardsForCurrentMonth =
        SpecialRewardUserData.getRewardsForCurrentMonthSyncData(userRewardsForCurrentMonth);
    if (!this.userRewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < this.userRewardsForCurrentMonth.size(); index++) {
        this.rewardsUserContainer.setItem(index, this.userRewardsForCurrentMonth.get(index));
      }
    }

    // Define Rewards Slots and render takeable rewards, upcoming rewards and empty rewards.
    int numberOfDays = SpecialRewards.getDaysCurrentMonth();
    for (int rewardRow = 0; rewardRow < 4; ++rewardRow) {
      for (int rewardColumn = 0; rewardColumn < 8; ++rewardColumn) {
        int rewardSlotIndex = rewardColumn + rewardRow * 8;
        if (this.userRewardsForCurrentMonth.size() > rewardSlotIndex
            && this.userRewardsForCurrentMonth.get(rewardSlotIndex) != null
            && !this.userRewardsForCurrentMonth.get(rewardSlotIndex).isEmpty()) {
          ItemStack itemStack = this.userRewardsForCurrentMonth.get(rewardSlotIndex);
          if (itemStack.is(ModItems.SKIP_DAY.get())) {
            // Check if we have already skipped this day and show the skipped day slot.
            if (rewardSlotIndex < this.rewardedDays) {
              this.addSlot(new SkippedDaySlot(this.rewardsContainer, rewardSlotIndex,
                  REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
                  REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
            } else {
              this.addSlot(new SkipDaySlot(this.rewardsContainer, rewardSlotIndex,
                  REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
                  REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
            }
          } else if (itemStack.is(ModItems.LOCK_DAY.get())) {
            // Check if we have already unlocked this day and show the unlocked day slot.
            if (rewardSlotIndex < this.rewardedDays) {
              this.addSlot(new UnlockedDaySlot(this.rewardsContainer, rewardSlotIndex,
                  REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
                  REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
            } else {
              this.addSlot(new LockedDaySlot(this.rewardsContainer, rewardSlotIndex,
                  REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
                  REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
            }
          } else {
            // If the reward is takeable show the takeable reward slot.
            this.addSlot(new TakeableRewardSlot(this.rewardsUserContainer, rewardSlotIndex,
                REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
                REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
          }
        } else if (this.rewardsForCurrentMonth.size() > rewardSlotIndex) {
          // If the reward was not taken yet show the reward slot.
          this.addSlot(new RewardSlot(this.rewardsContainer, rewardSlotIndex,
              REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
              REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
        } else if (numberOfDays > rewardSlotIndex) {
          // If there is no reward show the empty reward slot.
          this.addSlot(new EmptyRewardSlot(this.rewardsContainer, rewardSlotIndex,
              REWARD_SLOT_START_POSITION_X + Math.round(rewardColumn * REWARD_SLOT_SIZE_X),
              REWARD_SLOT_START_POSITION_Y + rewardRow * REWARD_SLOT_SIZE_Y, this));
        }
      }
    }

    // Player Inventory Slots
    int playerInventoryStartPositionY = 160;
    int playerInventoryStartPositionX = 8;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
            playerInventoryStartPositionX + inventoryColumn * slotSize,
            playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar Slots
    int hotbarStartPositionY = 218;
    int hotbarStartPositionX = 8;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(new Slot(playerInventory, playerInventorySlot,
          hotbarStartPositionX + playerInventorySlot * slotSize, hotbarStartPositionY));
    }
  }

  @Override
  public Container getRewardsContainer() {
    return this.rewardsContainer;
  }

  @Override
  public Container getRewardsUserContainer() {
    return this.rewardsUserContainer;
  }

  @Override
  public List<ItemStack> getRewardsForCurrentMonth() {
    return this.rewardsForCurrentMonth;
  }

  @Override
  public List<ItemStack> getUserRewardsForCurrentMonth() {
    return this.userRewardsForCurrentMonth;
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
