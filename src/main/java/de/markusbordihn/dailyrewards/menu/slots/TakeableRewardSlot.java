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

package de.markusbordihn.dailyrewards.menu.slots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardMenu;

public class TakeableRewardSlot extends Slot {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private RewardMenu menu;

  public TakeableRewardSlot(IInventory container, int index, int x, int y) {
    super(container, index, x, y);
  }

  public TakeableRewardSlot(IInventory container, int index, int x, int y, RewardMenu menu) {
    super(container, index, x, y);
    this.menu = menu;
  }

  @Override
  public ItemStack onTake(PlayerEntity player, ItemStack itemStack) {
    if (getItem().getItem() != ModItems.TAKEN_REWARD.get()) {
      set(new ItemStack(ModItems.TAKEN_REWARD.get()));
      this.menu.syncRewardsUserContainer(player);
    } else {
      this.setChanged();
    }
    return getItem();
  }

  @Override
  public boolean mayPickup(PlayerEntity player) {
    return getItem().getItem() != ModItems.TAKEN_REWARD.get();
  }

  @Override
  public boolean mayPlace(ItemStack itemStack) {
    return false;
  }

}
