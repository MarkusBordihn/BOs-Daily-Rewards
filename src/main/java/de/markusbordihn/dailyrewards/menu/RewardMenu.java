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

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
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
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;

public class RewardMenu extends AbstractContainerMenu {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Define Slot index for easier access

  public static final int PLAYER_SLOT_START = 9;
  public static final int PLAYER_INVENTORY_SLOT_START = PLAYER_SLOT_START;
  public static final int PLAYER_SLOT_STOP = 3 * 9 + PLAYER_INVENTORY_SLOT_START + 8;

  // Defining basic layout options
  private static int containerSize = 32;
  private static int slotSize = 18;
  private static int rewardSlotSize = 23;

  // Define containers
  private final Container container;

  // Misc
  protected final Level level;
  protected final Player player;

  public RewardMenu(int windowIdIn, Inventory inventory) {
    this(windowIdIn, inventory, new SimpleContainer(containerSize), ModMenuTypes.REWARD_MENU.get());
  }

  public RewardMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory);
  }

  public RewardMenu(final int windowId, final Inventory playerInventory,
      final Container container) {
    this(windowId, playerInventory, container, ModMenuTypes.REWARD_MENU.get());
  }

  public RewardMenu(final int windowId, final Inventory playerInventory, final Container container,
      MenuType<?> menuType) {
    super(menuType, windowId);

    // Make sure the passed container matched the expected sizes
    checkContainerSize(container, containerSize);

    // Container
    this.container = container;

    // Other
    this.player = playerInventory.player;
    this.level = this.player.getLevel();

    // Rewards Items for the month
    List<ItemStack> rewardsForCurrentMonth = RewardData.get().getRewardsForCurrentMonth();
    if (!rewardsForCurrentMonth.isEmpty()) {
      for (int index = 0; index < rewardsForCurrentMonth.size(); index++) {
        this.container.setItem(index, rewardsForCurrentMonth.get(index));
      }
    }

    // Rewards Slots
    int rewardStartPositionY = 22;
    int rewardStartPositionX = 9;
    for (int rewardRow = 0; rewardRow < 5; ++rewardRow) {
      for (int rewardColumn = 0; rewardColumn < 7; ++rewardColumn) {
        int rewardSlotIndex = rewardColumn + rewardRow * 7;
        if (rewardSlotIndex < this.container.getContainerSize()) {
          this.addSlot(new RewardSlot(container, rewardSlotIndex,
              rewardStartPositionX + rewardColumn * rewardSlotSize,
              rewardStartPositionY + rewardRow * rewardSlotSize));
        }
      }
    }

    // Player Inventory Slots
    int playerInventoryStartPositionY = 151;
    int playerInventoryStartPositionX = 6;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
            playerInventoryStartPositionX + inventoryColumn * slotSize,
            playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar Slots
    int hotbarStartPositionY = 209;
    int hotbarStartPositionX = 6;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(new Slot(playerInventory, playerInventorySlot,
          hotbarStartPositionX + playerInventorySlot * slotSize, hotbarStartPositionY));
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return this.container.stillValid(player);
  }

}
