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
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import de.markusbordihn.dailyrewards.menu.slots.EmptyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.HiddenRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.markusbordihn.dailyrewards.config.ModConfigs.COMMON;

public class RewardCompactMenu extends RewardMenu {

  // Define Slot index for easier access
  public static final int PLAYER_SLOT_START = 9;
  public static final int PLAYER_INVENTORY_SLOT_START = PLAYER_SLOT_START;
  public static final int PLAYER_SLOT_STOP = 3 * 9 + PLAYER_INVENTORY_SLOT_START + 8;
  // Container layout
  public static final float REWARD_SLOT_SIZE_X = 20.5f;
  public static final int REWARD_SLOT_SIZE_Y = 31;
  public static final int REWARD_SLOT_START_POSITION_X = 10;
  public static final int REWARD_SLOT_START_POSITION_Y = 45;
  public static final int REWARD_SLOT_SPACE_X = 2;
  // Defining basic layout options
  private static int containerSize = 32;
  private static int slotSize = 18;
  // Container
  private Container rewardsContainer = new SimpleContainer(containerSize);
  private Container rewardsUserContainer = new SimpleContainer(containerSize);
  private Container specialRewardsContainer = new SimpleContainer(containerSize);
  private Container specialRewardsUserContainer = new SimpleContainer(containerSize);

  // Rewards Data
  private List<ItemStack> rewardsForCurrentMonth = new ArrayList<>();
  private List<ItemStack> userRewardsForCurrentMonth = new ArrayList<>();
  private List<ItemStack> specialRewardsForCurrentMonth = new ArrayList<>();
  private List<ItemStack> userSpecialRewardsForCurrentMonth = new ArrayList<>();

  public RewardCompactMenu(final int windowId, final Inventory playerInventory) {
    this(
        windowId,
        playerInventory,
        ModMenuTypes.REWARD_COMPACT_MENU.get(),
        playerInventory.player.getUUID(),
        RewardUserData.get().getRewardedDaysForCurrentMonth(playerInventory.player.getUUID()),
        RewardUserData.get().getLastRewardedDayForCurrentMonth(playerInventory.player.getUUID()),
        RewardUserData.get().getRewardsForCurrentMonthSyncData(playerInventory.player.getUUID()),
        RewardData.get().getRewardsForCurrentMonthSyncData(),
        SpecialRewardUserData.get()
            .getRewardedDaysForCurrentMonth(playerInventory.player.getUUID()),
        SpecialRewardUserData.get()
            .getLastRewardedDayForCurrentMonth(playerInventory.player.getUUID()),
        SpecialRewardUserData.get()
            .getRewardsForCurrentMonthSyncData(playerInventory.player.getUUID()),
        RewardData.get().getSpecialRewardsForCurrentMonthSyncData());
  }

  public RewardCompactMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(
        windowId,
        playerInventory,
        ModMenuTypes.REWARD_COMPACT_MENU.get(),
        data.readUUID(),
        data.readInt(),
        data.readUtf(),
        data.readNbt(),
        data.readNbt(),
        data.readInt(),
        data.readUtf(),
        data.readNbt(),
        data.readNbt());
  }

  public RewardCompactMenu(
      final int windowId,
      final Inventory playerInventory,
      MenuType<?> menuType,
      UUID playerUUID,
      int rewardedDays,
      String lastRewardedDay,
      CompoundTag userRewardsForCurrentMonth,
      CompoundTag rewardsForCurrentMonth,
      int specialRewardedDays,
      String lastSpecialRewardedDay,
      CompoundTag specialUserRewardsForCurrentMonth,
      CompoundTag specialRewardsForCurrentMonth) {
    super(menuType, windowId, playerInventory);

    // Check if we have a valid player with the given UUID
    this.playerUUID = playerUUID;
    if (this.playerUUID == null
        || this.player.getUUID() == null
        || !this.playerUUID.equals(this.player.getUUID())) {
      log.error(
          "{} Unable to verify player {} with UUID {}!",
          Constants.LOG_NAME,
          this.player,
          this.playerUUID);
      return;
    }

    // Store rewards data
    this.rewardedDays = rewardedDays;
    this.lastRewardedDay = lastRewardedDay;

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
        RewardUserData.getRewardsForCurrentMonthSyncData(userRewardsForCurrentMonth);
    if (!this.userRewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < this.userRewardsForCurrentMonth.size(); index++) {
        this.rewardsUserContainer.setItem(index, this.userRewardsForCurrentMonth.get(index));
      }
    }

    // Store special rewards data
    this.rewardedSpecialDays = specialRewardedDays;
    this.lastRewardedSpecialDay = lastSpecialRewardedDay;

    // Sync possible special rewards items for current month.
    this.specialRewardsForCurrentMonth =
        RewardData.getSpecialRewardsForCurrentMonthSyncData(specialRewardsForCurrentMonth);
    if (!specialRewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < this.specialRewardsForCurrentMonth.size(); index++) {
        this.specialRewardsContainer.setItem(index, this.specialRewardsForCurrentMonth.get(index));
      }
    }

    // Sync user rewarded special items for current month.
    this.userSpecialRewardsForCurrentMonth =
        SpecialRewardUserData.getRewardsForCurrentMonthSyncData(specialUserRewardsForCurrentMonth);
    if (!this.userSpecialRewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < this.userSpecialRewardsForCurrentMonth.size(); index++) {
        this.specialRewardsUserContainer.setItem(
            index, this.userSpecialRewardsForCurrentMonth.get(index));
      }
    }

    // Define slot layout
    int numberOfDays = Rewards.getDaysCurrentMonth();
    int slotStartPositionX = REWARD_SLOT_START_POSITION_X;
    int slotStartPositionY = REWARD_SLOT_START_POSITION_Y;
    int specialSlotStartPositionX = REWARD_SLOT_START_POSITION_X;
    int specialSlotStartPositionY = REWARD_SLOT_START_POSITION_Y + REWARD_SLOT_SIZE_Y + 34;

    // Calculate slot index for rewards. By using -4 and +2 we align the slot index.
    int rewardIndexStart = Math.max(0, rewardedDays - 4);
    int rewardIndexStop = Math.min(numberOfDays, rewardedDays + 2);
    int specialRewardIndexStart = Math.max(0, specialRewardedDays - 4);
    int specialRewardIndexStop = Math.min(numberOfDays, specialRewardedDays + 2);

    // Shift slot position if we have no special rewards to show.
    if (!this.specialRewardAvailable) {
      slotStartPositionY += 30;
    }

    // Move slot position if we have less than 3 rewards.
    if (rewardIndexStop - rewardIndexStart == 5) {
      slotStartPositionX =
          (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 1);
    } else if (rewardIndexStop - rewardIndexStart == 4) {
      slotStartPositionX =
          (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 2);
    } else if (rewardIndexStop - rewardIndexStart == 3) {
      slotStartPositionX =
          (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 3);
    } else if (rewardIndexStop - rewardIndexStart == 2) {
      slotStartPositionX =
          (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 4);
    } else if (rewardIndexStop - rewardIndexStart == 1) {
      slotStartPositionX =
          (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 5);
    }

    // User Reward Slot
    for (int slotIndex = rewardIndexStart; slotIndex <= rewardIndexStop; slotIndex++) {
      if (this.userRewardsForCurrentMonth.size() > slotIndex) {
        ItemStack itemStack;
        try {
          itemStack = this.userRewardsForCurrentMonth.get(slotIndex);
        } catch (Exception e) {
          itemStack = ItemStack.EMPTY;
        }
        this.addSlot(
            this.createRewardSlot(
                itemStack,
                rewardedDays,
                this.rewardsUserContainer,
                slotIndex,
                slotStartPositionX,
                slotStartPositionY));
      } else if (this.rewardsForCurrentMonth.size() > slotIndex) {
        if (Boolean.TRUE.equals(COMMON.previewRewardsItems)) {
          // If the reward was not taken yet preview the reward slot.
          this.addSlot(
              new RewardSlot(
                  this.rewardsContainer, slotIndex, slotStartPositionX, slotStartPositionY));
        } else {
          // Hide the reward slot.
          this.addSlot(
              new HiddenRewardSlot(
                  this.rewardsContainer, slotIndex, slotStartPositionX, slotStartPositionY));
        }
      } else if (numberOfDays > slotIndex) {
        // If there is no reward show the empty reward slot.
        this.addSlot(
            new EmptyRewardSlot(
                this.rewardsContainer, slotIndex, slotStartPositionX, slotStartPositionY));
      }
      slotStartPositionX += REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X;
    }

    // Only show special rewards if we have any.
    if (this.specialRewardAvailable) {

      // Move special slot position if we have less than 3 rewards.
      if (specialRewardIndexStop - specialRewardIndexStart == 5) {
        specialSlotStartPositionX =
            (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 1);
      } else if (specialRewardIndexStop - specialRewardIndexStart == 4) {
        specialSlotStartPositionX =
            (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 2);
      } else if (specialRewardIndexStop - specialRewardIndexStart == 3) {
        specialSlotStartPositionX =
            (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 3);
      } else if (specialRewardIndexStop - specialRewardIndexStart == 2) {
        specialSlotStartPositionX =
            (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 4);
      } else if (specialRewardIndexStop - specialRewardIndexStart == 1) {
        specialSlotStartPositionX =
            (int) (REWARD_SLOT_START_POSITION_X + (REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X) * 5);
      }

      // Special User Reward Slot
      for (int slotIndex = specialRewardIndexStart;
          slotIndex <= specialRewardIndexStop;
          slotIndex++) {
        if (this.userSpecialRewardsForCurrentMonth.size() > slotIndex) {
          ItemStack itemStack;
          try {
            itemStack = this.userSpecialRewardsForCurrentMonth.get(slotIndex);
          } catch (Exception e) {
            itemStack = ItemStack.EMPTY;
          }
          this.addSlot(
              this.createRewardSlot(
                  itemStack,
                  rewardedDays,
                  this.specialRewardsUserContainer,
                  slotIndex,
                  specialSlotStartPositionX,
                  specialSlotStartPositionY));
        } else if (this.rewardsForCurrentMonth.size() > slotIndex) {
          if (Boolean.TRUE.equals(COMMON.previewRewardsSpecialItems)) {
            // If the reward was not taken yet preview the reward slot.
            this.addSlot(
                new RewardSlot(
                    this.specialRewardsContainer,
                    slotIndex,
                    specialSlotStartPositionX,
                    specialSlotStartPositionY));
          } else {
            // Hide the reward slot.
            this.addSlot(
                new HiddenRewardSlot(
                    this.specialRewardsContainer,
                    slotIndex,
                    specialSlotStartPositionX,
                    specialSlotStartPositionY));
          }
        } else if (numberOfDays > slotIndex) {
          // If there is no reward show the empty reward slot.
          this.addSlot(
              new EmptyRewardSlot(
                  this.specialRewardsContainer,
                  slotIndex,
                  specialSlotStartPositionX,
                  specialSlotStartPositionY));
        }
        specialSlotStartPositionX += REWARD_SLOT_SIZE_X + REWARD_SLOT_SPACE_X;
      }
    }

    // Player Inventory Slots
    int playerInventoryStartPositionY = 160;
    int playerInventoryStartPositionX = 8;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(
            new Slot(
                playerInventory,
                inventoryColumn + inventoryRow * 9 + 9,
                playerInventoryStartPositionX + inventoryColumn * slotSize,
                playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar Slots
    int hotbarStartPositionY = 218;
    int hotbarStartPositionX = 8;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(
          new Slot(
              playerInventory,
              playerInventorySlot,
              hotbarStartPositionX + playerInventorySlot * slotSize,
              hotbarStartPositionY));
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
  public Container getSpecialRewardsContainer() {
    return this.specialRewardsContainer;
  }

  @Override
  public Container getSpecialRewardsUserContainer() {
    return this.specialRewardsUserContainer;
  }

  @Override
  public List<ItemStack> getSpecialRewardsForCurrentMonth() {
    return this.specialRewardsForCurrentMonth;
  }

  @Override
  public List<ItemStack> getUserSpecialRewardsForCurrentMonth() {
    return this.userSpecialRewardsForCurrentMonth;
  }
}
