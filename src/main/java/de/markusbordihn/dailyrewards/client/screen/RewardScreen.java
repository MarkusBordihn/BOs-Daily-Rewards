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

package de.markusbordihn.dailyrewards.client.screen;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardMenu;
import de.markusbordihn.dailyrewards.menu.slots.DailyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.EmptyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.LockedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.SkippedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.UnlockedDaySlot;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RewardScreen<T extends RewardMenu> extends AbstractContainerScreen<T> {

  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final boolean hasSpecialReward;
  protected final int rewardedDays;
  protected final int rewardedSpecialDays;
  protected final int currentDay = Rewards.getCurrentDay();
  protected final int daysCurrentMonth = Rewards.getDaysCurrentMonth();
  protected final int rewardDaysForCurrentMonth = Rewards.getDaysCurrentMonth();

  protected boolean automaticRewardPlayers = true;
  protected boolean automaticRewardSpecialPlayers = true;
  protected int rewardTimePerDay = 30;
  protected int rewardTimePerDayInSeconds = rewardTimePerDay * 60;
  protected LocalPlayer localPlayer;

  public RewardScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);

    // Set already rewarded days.
    this.rewardedDays = this.menu.getRewardedDays();
    this.rewardedSpecialDays = this.menu.getRewardedSpecialDays();

    // Set special reward flag.
    this.hasSpecialReward = this.menu.isSpecialRewardAvailable();
  }

  public static String mergeComponentStyleCodeWithText(Component component, String text) {
    if (component == null || component.getString().length() < 2 || text == null || text.isEmpty()) {
      return text;
    }
    String styleString = component.getString(2);
    if (!styleString.isEmpty() && styleString.charAt(0) == '§') {
      return component.getString(2) + text;
    }
    return text;
  }

  public void close() {
    if (this.minecraft != null) {
      this.minecraft.setScreen(null);
    }
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageHeight = 242;

    // Automatic reward players
    this.automaticRewardPlayers = COMMON.automaticRewardPlayers.get();
    this.automaticRewardSpecialPlayers = COMMON.automaticRewardSpecialPlayers.get();

    // Calculations for next reward
    localPlayer = Minecraft.getInstance() != null ? Minecraft.getInstance().player : null;
    rewardTimePerDay = COMMON.rewardTimePerDay.get();
    rewardTimePerDayInSeconds = rewardTimePerDay * 60;

    // Basic Position
    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.inventoryLabelX = 8;
    this.inventoryLabelY = this.imageHeight - 92;
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, x, y, partialTicks);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    // Main screen
    guiGraphics.blit(Constants.TEXTURE_GENERIC_54, leftPos, topPos + 20, 0, 0, 176, 222);
    guiGraphics.blit(Constants.TEXTURE_GENERIC_54, leftPos, topPos + 8, 0, 0, 176, 139);
    guiGraphics.blit(
        Constants.TEXTURE_GENERIC_54, leftPos + 5, topPos + 15, 3, 64, 165, 130, 255, 4096);
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    // Handle tooltip for the different kind of slots and slot states.
    Slot tooltipHoverSlot = this.hoveredSlot;
    if (!(tooltipHoverSlot instanceof DailyRewardSlot) || !tooltipHoverSlot.hasItem()) {
      super.renderTooltip(guiGraphics, x, y);
      return;
    }

    ItemStack itemStack = this.hoveredSlot.getItem();
    List<Component> itemTooltip = this.getTooltipFromContainerItem(itemStack);
    int slotIndex = tooltipHoverSlot.getSlotIndex() + 1;
    // Check if the reward could be claimed based on the current day and the already rewarded
    // days.
    if (slotIndex > (daysCurrentMonth - currentDay) + rewardedDays) {
      itemTooltip.add(
          Component.translatable(Constants.TEXT_PREFIX + "not_claimable_for_this_month.info")
              .withStyle(ChatFormatting.RED));
    } else if (slotIndex == rewardedDays) {
      if (tooltipHoverSlot instanceof LockedDaySlot
          || tooltipHoverSlot.getItem().is(ModItems.LOCK_DAY.get())) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "lock_today.info")
                .withStyle(ChatFormatting.GREEN));
      } else if (tooltipHoverSlot instanceof SkippedDaySlot
          || tooltipHoverSlot.getItem().is(ModItems.SKIP_DAY.get())) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "skip_today.info")
                .withStyle(ChatFormatting.GREEN));
      } else {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "claim_today.info")
                .withStyle(ChatFormatting.GREEN));
      }
    } else if (slotIndex > rewardedDays) {
      itemTooltip.add(
          Component.translatable(
                  Constants.TEXT_PREFIX + "claimable_in_days.info", slotIndex - rewardedDays)
              .withStyle(ChatFormatting.GREEN));
    } else {
      if (tooltipHoverSlot instanceof UnlockedDaySlot) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "unlocked_day.info")
                .withStyle(ChatFormatting.DARK_GREEN));
      } else if (tooltipHoverSlot instanceof TakeableRewardSlot
          && !tooltipHoverSlot.getItem().is(ModItems.TAKEN_REWARD.get())) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "claimable_reward.info")
                .withStyle(ChatFormatting.GREEN));
      } else if (tooltipHoverSlot instanceof SkippedDaySlot) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "skipped_day.info")
                .withStyle(ChatFormatting.DARK_GREEN));
      } else if (tooltipHoverSlot instanceof EmptyRewardSlot) {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "empty_reward.info")
                .withStyle(ChatFormatting.DARK_GREEN));
      } else {
        itemTooltip.add(
            Component.translatable(Constants.TEXT_PREFIX + "claimed_reward.info")
                .withStyle(ChatFormatting.DARK_GREEN));
      }
    }
    guiGraphics.renderTooltip(this.font, itemTooltip, itemStack.getTooltipImage(), x, y);
  }
}
