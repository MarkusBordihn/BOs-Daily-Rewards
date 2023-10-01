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

package de.markusbordihn.dailyrewards.client.screen;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardMenu;
import de.markusbordihn.dailyrewards.menu.slots.DailyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.EmptyRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.HiddenRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.LockedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.SkippedDaySlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.UnlockedDaySlot;
import de.markusbordihn.dailyrewards.rewards.Rewards;

@OnlyIn(Dist.CLIENT)
public class RewardOverviewScreen<T extends RewardMenu> extends RewardScreen<T> {

  private final MutableComponent rewardScreenTitle;

  private int updateTicker = 0;
  private String nextRewardTimeString;
  private boolean reloadToClaim = false;

  public RewardOverviewScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);

    // Set Title with already rewarded days.
    if (this.rewardedDays > 0) {
      rewardScreenTitle =
          Component.translatable(Constants.TEXT_PREFIX + "reward_screen", this.rewardedDays);
    } else {
      rewardScreenTitle = Component.translatable(Constants.TEXT_PREFIX + "reward_screen_none",
          this.rewardedDays);
    }
  }

  public void rendererTakeableRewardSlot(PoseStack poseStack, int x, int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_ICONS);
    poseStack.pushPose();
    this.blit(poseStack, x + 12, y - 5, 0, 0, 9, 9);
    poseStack.popPose();
  }

  public void renderRewardSlot(PoseStack poseStack, int x, int y) {
    fill(poseStack, x - 1, y - 1, x + 17, y + 19 + 8, 0x80AAAAAA);
  }

  protected void renderNextTimeForReward(PoseStack poseStack, int x, int y) {
    // Early return if the user needs to reload to claim rewards.
    if (this.reloadToClaim) {
      Component component = Component.translatable(Constants.TEXT_PREFIX + "next_reward.reload");
      int componentWidth = this.font.width(component);
      this.font.draw(poseStack, component,
          x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2f) : 0), y,
          0xFF0000);
      return;
    }

    // Update data cache only every 20 ticks to avoid expensive operations on higher fps.
    if ((this.updateTicker++ & (20 - 1)) == 0) {
      int localPlayerTickCount = localPlayer != null ? localPlayer.tickCount : 0;
      String lastRewardedDay = this.menu.getLastRewardedDay();
      long nextRewardTime = Rewards.getCurrentYearMonthDay().equals(lastRewardedDay)
          ? Duration
              .between(LocalDateTime.now(),
                  LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))
              .toSeconds() + this.rewardTimePerDayInSeconds
          : this.rewardTimePerDayInSeconds - (localPlayerTickCount / 20);
      // Adding 60 seconds, because the server is only checking every 60 seconds for rewards.
      this.nextRewardTimeString = LocalTime.MIN.plusSeconds(nextRewardTime + 60).toString();
      if (nextRewardTimeString.length() == 5) {
        this.nextRewardTimeString += ":00";
      }
      if ("00:00:00".equals(nextRewardTimeString)) {
        log.debug("Reload screen to be able to claim for day {} ...", rewardedDays + 1);
        this.reloadToClaim = true;
      }
      if (this.updateTicker >= 20) {
        this.updateTicker = 0;
      }
    }

    // Display next reward time.
    Component component = Component.translatable(Constants.TEXT_PREFIX + "next_reward.in",
        this.nextRewardTimeString);
    int componentWidth = this.font.width(component);
    this.font.draw(poseStack, component,
        x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2f) : 0), y,
        0x666666);
  }

  protected void renderIcons(PoseStack poseStack, int x, int y) {}

  @Override
  protected void renderTooltip(PoseStack poseStack, ItemStack itemStack, int x, int y) {
    // Handle tooltip for the different kind of slots and slot states.
    Slot tooltipHoverSlot = this.hoveredSlot;
    if (!(tooltipHoverSlot instanceof DailyRewardSlot)) {
      super.renderTooltip(poseStack, itemStack, x, y);
      return;
    }

    List<Component> itemTooltip = this.getTooltipFromItem(itemStack);
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
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "lock_today.info")
            .withStyle(ChatFormatting.GREEN));
      } else if (tooltipHoverSlot instanceof SkippedDaySlot
          || tooltipHoverSlot.getItem().is(ModItems.SKIP_DAY.get())) {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "skip_today.info")
            .withStyle(ChatFormatting.GREEN));
      } else {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "claim_today.info")
            .withStyle(ChatFormatting.GREEN));
      }
    } else if (slotIndex > rewardedDays) {
      itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "claimable_in_days.info",
          slotIndex - rewardedDays).withStyle(ChatFormatting.GREEN));
    } else {
      if (tooltipHoverSlot instanceof UnlockedDaySlot) {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "unlocked_day.info")
            .withStyle(ChatFormatting.DARK_GREEN));
      } else if (tooltipHoverSlot instanceof TakeableRewardSlot
          && !tooltipHoverSlot.getItem().is(ModItems.TAKEN_REWARD.get())) {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "claimable_reward.info")
            .withStyle(ChatFormatting.GREEN));
      } else if (tooltipHoverSlot instanceof SkippedDaySlot) {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "skipped_day.info")
            .withStyle(ChatFormatting.DARK_GREEN));
      } else if (tooltipHoverSlot instanceof EmptyRewardSlot) {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "empty_reward.info")
            .withStyle(ChatFormatting.DARK_GREEN));
      } else {
        itemTooltip.add(Component.translatable(Constants.TEXT_PREFIX + "claimed_reward.info")
            .withStyle(ChatFormatting.DARK_GREEN));
      }
    }
    this.renderTooltip(poseStack, itemTooltip, itemStack.getTooltipImage(), x, y);
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    super.render(poseStack, x, y, partialTicks);

    // Additional styling for the different kind of slots and slot states.
    for (int k = 0; k < this.menu.slots.size(); ++k) {
      Slot slot = this.menu.slots.get(k);
      if (slot instanceof DailyRewardSlot) {
        if (slot instanceof TakeableRewardSlot && !slot.getItem().is(ModItems.TAKEN_REWARD.get())) {
          rendererTakeableRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
        } else if (slot instanceof RewardSlot || slot instanceof EmptyRewardSlot
            || slot instanceof HiddenRewardSlot) {
          renderRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
        }
      }
    }

    // Render additional icons.
    this.renderIcons(poseStack, x, y);

    // Render next reward time, if automatic reward is enabled.
    if (this.automaticRewardPlayers) {
      this.renderNextTimeForReward(poseStack, leftPos + 2, topPos + 140);
    }

    this.renderTooltip(poseStack, x, y);
  }

  @Override
  protected void renderLabels(PoseStack poseStack, int x, int y) {
    this.font.drawShadow(poseStack, rewardScreenTitle, this.titleLabelX + 5f, this.titleLabelY - 1f,
        Constants.FONT_COLOR_WHITE);
    this.font.draw(poseStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
        4210752);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    super.renderBg(poseStack, partialTicks, mouseX, mouseY);

    // Render custom background for overview screen
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_OVERVIEW_SCREEN);
    poseStack.pushPose();
    this.blit(poseStack, leftPos + 1, topPos, 0, 0, 256, 256);
    poseStack.popPose();

    // Render Rewards Slots boxes
    int dayCounter = 1;
    for (int i = 0; i < 4; i++) {
      int slotTopPos = topPos + (i * 30) + 2;
      for (int i2 = 0; i2 < 8; i2++) {
        if (dayCounter <= rewardDaysForCurrentMonth) {
          int slotLeftPos = leftPos + 7 + Math.round(i2 * 20.5f);
          RenderSystem.setShaderTexture(0, Constants.TEXTURE_ICONS);
          this.blit(poseStack, slotLeftPos - 1, slotTopPos + 17, 0, 11, 20, 29);
          this.font.draw(poseStack, dayCounter + "", slotLeftPos + (dayCounter < 10 ? 6f : 4f),
              slotTopPos + 36f, 4210752);
          dayCounter++;
        }
      }
    }

  }

}
