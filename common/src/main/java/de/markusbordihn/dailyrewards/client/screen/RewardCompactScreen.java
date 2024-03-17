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

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardCompactMenu;
import de.markusbordihn.dailyrewards.menu.slots.*;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RewardCompactScreen extends RewardScreen<RewardCompactMenu> {

  private final MutableComponent rewardScreenTitle;
  private final ResourceLocation rewardScreenBackground;

  private int updateTicker = 0;
  private int updateSpecialTicker = 0;
  private String nextRewardTimeString;
  private String nextRewardSpecialTimeString;
  private boolean reloadToClaim = false;

  public RewardCompactScreen(RewardCompactMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.rewardScreenTitle =
        Component.translatable(Constants.TEXT_PREFIX + "reward_screen_none", this.rewardedDays);
    this.rewardScreenBackground =
        hasSpecialReward
            ? Constants.TEXTURE_COMPACT_SCREEN_COMBINED
            : Constants.TEXTURE_COMPACT_SCREEN;
  }

  public void rendererTakeableRewardSlot(GuiGraphics guiGraphics, int x, int y) {
    guiGraphics.pose().pushPose();
    guiGraphics.blit(Constants.TEXTURE_GENERIC_54, x - 1, y - 1, 7, 17, 18, 18);
    guiGraphics.pose().popPose();

    guiGraphics.pose().pushPose();
    guiGraphics.blit(Constants.TEXTURE_ICONS, x + 12, y - 5, 0, 0, 8, 8);
    guiGraphics.pose().popPose();
  }

  public void renderRewardSlot(GuiGraphics guiGraphics, int x, int y) {
    guiGraphics.pose().pushPose();
    guiGraphics.blit(Constants.TEXTURE_GENERIC_54, x - 1, y - 1, 7, 17, 18, 18);
    guiGraphics.pose().popPose();

    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    guiGraphics.fill(RenderType.guiOverlay(), x - 1, y - 1, x + 17, y + 17, 0x80AAAAAA);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }

  protected void renderNextTimeForReward(GuiGraphics guiGraphics, int x, int y) {
    // Early return if the user needs to reload to claim rewards.
    if (this.reloadToClaim) {
      Component component = Component.translatable(Constants.TEXT_PREFIX + "next_reward.reload");
      int componentWidth = this.font.width(component);
      guiGraphics.drawString(
          this.font,
          component,
          x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2) : 0),
          y,
          0xFF0000,
          false);
      return;
    }

    // Update data cache only every 20 ticks to avoid expensive operations on higher fps.
    if ((this.updateTicker++ & (20 - 1)) == 0) {
      int localPlayerTickCount = localPlayer != null ? localPlayer.tickCount : 0;
      String lastRewardedDay = this.menu.getLastRewardedDay();
      long nextRewardTime =
          Rewards.getCurrentYearMonthDay().equals(lastRewardedDay)
              ? Duration.between(
                          LocalDateTime.now(),
                          LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))
                      .toSeconds()
                  + this.rewardTimePerDayInSeconds
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

    // Merge color codes for the next reward time, if any.
    this.nextRewardTimeString =
        mergeComponentStyleCodeWithText(
            Component.translatable(Constants.TEXT_PREFIX + "next_reward.in"),
            this.nextRewardTimeString);

    // Display next reward time.
    Component component =
        Component.translatable(Constants.TEXT_PREFIX + "next_reward.in", this.nextRewardTimeString);
    int componentWidth = this.font.width(component);
    guiGraphics.drawString(
        this.font,
        component,
        x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2) : 0),
        y,
        0x666666,
        false);
  }

  protected void renderNextTimeForSpecialReward(GuiGraphics guiGraphics, int x, int y) {
    // Early return if the user needs to reload to claim rewards.
    if (this.reloadToClaim) {
      Component component =
          Component.translatable(Constants.TEXT_PREFIX + "next_special_reward.reload");
      int componentWidth = this.font.width(component);
      guiGraphics.drawString(
          this.font,
          component,
          x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2) : 0),
          y,
          0xFF0000,
          false);
      return;
    }

    // Update data cache only every 20 ticks to avoid expensive operations on higher fps.
    if ((this.updateSpecialTicker++ & (20 - 1)) == 0) {
      int localPlayerTickCount = localPlayer != null ? localPlayer.tickCount : 0;
      String lastRewardedSpecialDay = this.menu.getLastRewardedSpecialDay();
      long nextRewardTime =
          Rewards.getCurrentYearMonthDay().equals(lastRewardedSpecialDay)
              ? Duration.between(
                          LocalDateTime.now(),
                          LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))
                      .toSeconds()
                  + this.rewardTimePerDayInSeconds
              : this.rewardTimePerDayInSeconds - (localPlayerTickCount / 20);
      // Adding 60 seconds, because the server is only checking every 60 seconds for rewards.
      this.nextRewardSpecialTimeString = LocalTime.MIN.plusSeconds(nextRewardTime + 60).toString();
      if (nextRewardSpecialTimeString.length() == 5) {
        this.nextRewardSpecialTimeString += ":00";
      }
      if ("00:00:00".equals(nextRewardSpecialTimeString)) {
        log.debug("Reload screen to be able to claim for day {} ...", rewardedDays + 1);
        this.reloadToClaim = true;
      }
      if (this.updateSpecialTicker >= 20) {
        this.updateSpecialTicker = 0;
      }
    }

    // Merge color codes for the next reward time, if any.
    this.nextRewardSpecialTimeString =
        mergeComponentStyleCodeWithText(
            Component.translatable(Constants.TEXT_PREFIX + "next_special_reward.in"),
            this.nextRewardSpecialTimeString);

    // Display next reward time.
    Component component =
        Component.translatable(
            Constants.TEXT_PREFIX + "next_special_reward.in", this.nextRewardSpecialTimeString);
    int componentWidth = this.font.width(component);
    guiGraphics.drawString(
        this.font,
        component,
        x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2) : 0),
        y,
        0x666666,
        false);
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    // Shift content if special rewards are available.
    int shiftTopPos = this.hasSpecialReward ? 0 : 30;

    // Additional styling for the different kind of slots and slot states.
    for (int k = 0; k < this.menu.slots.size(); ++k) {
      Slot slot = this.menu.slots.get(k);
      if (slot instanceof DailyRewardSlot) {
        if (slot instanceof TakeableRewardSlot) {
          if (!slot.getItem().is(ModItems.TAKEN_REWARD.get())) {
            rendererTakeableRewardSlot(guiGraphics, leftPos + slot.x, topPos + slot.y);
          }
        } else if (slot instanceof RewardSlot
            || slot instanceof EmptyRewardSlot
            || slot instanceof HiddenRewardSlot) {
          renderRewardSlot(guiGraphics, leftPos + slot.x, topPos + slot.y);
        }
      }
    }

    // Render sub-titles
    guiGraphics.drawString(
        this.font,
        Component.translatable(Constants.TEXT_PREFIX + "daily_rewards.title", this.rewardedDays),
        leftPos + 50,
        topPos + 29 + shiftTopPos,
        4210752,
        false);

    // Render special rewards sub-title, if available.
    if (this.hasSpecialReward) {
      guiGraphics.drawString(
          this.font,
          Component.translatable(
              Constants.TEXT_PREFIX + "special_rewards.title", this.rewardedSpecialDays),
          leftPos + 30,
          topPos + 94,
          4210752,
          false);
    }

    // Render next reward time, if automatic reward is enabled.
    if (this.automaticRewardPlayers) {
      this.renderNextTimeForReward(guiGraphics, leftPos + 2, topPos + 67 + shiftTopPos);
    }

    // Render next special reward time, if automatic reward is enabled and available.
    if (this.automaticRewardSpecialPlayers && this.hasSpecialReward) {
      this.renderNextTimeForSpecialReward(guiGraphics, leftPos, topPos + 133);
    }

    this.renderTooltip(guiGraphics, x, y);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
    guiGraphics.drawString(
        this.font,
        rewardScreenTitle,
        this.titleLabelX + 42,
        this.titleLabelY - 1,
        Constants.FONT_COLOR_WHITE,
        true);
    guiGraphics.drawString(
        this.font,
        this.playerInventoryTitle,
        this.inventoryLabelX,
        this.inventoryLabelY,
        4210752,
        false);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

    // Render custom background for compact screen
    guiGraphics.pose().pushPose();
    guiGraphics.blit(this.rewardScreenBackground, leftPos + 1, topPos, 0, 0, 256, 256);
    guiGraphics.pose().popPose();
  }
}
