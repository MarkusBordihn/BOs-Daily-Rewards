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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardMenu;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;
import de.markusbordihn.dailyrewards.rewards.Rewards;

@OnlyIn(Dist.CLIENT)
public class RewardScreen extends AbstractContainerScreen<RewardMenu> {

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private int rewardDaysForCurrentMonth = Rewards.getDaysCurrentMonth();
  private int rewardedDays = 0;
  private int rewardTimePerDay = 30;
  private int rewardTimePerDayInSeconds = rewardTimePerDay * 60 + 60;
  private int updateTicker = 0;
  private String nextRewardTimeString;
  private LocalPlayer localPlayer;
  private boolean reloadToClaim = false;

  private MutableComponent rewardScreenTitle;

  public RewardScreen(RewardMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  public void rendererTakeableRewardSlot(PoseStack poseStack, int x, int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_ICONS);
    poseStack.pushPose();
    this.blit(poseStack, x + 12, y - 5, 0, 0, 16, 16);
    poseStack.popPose();
  }

  public void renderRewardSlot(PoseStack poseStack, int x, int y) {
    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    fill(poseStack, x, y, x + 16, y + 18 + 8, 0x80AAAAAA);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }

  protected void renderNextTimeForReward(PoseStack poseStack, int x, int y) {
    // Early return if the user needs to reload to claim rewards.
    if (this.reloadToClaim) {
      MutableComponent component =
          Component.translatable(Constants.TEXT_PREFIX + "next_reward.reload");
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
    MutableComponent component =
        Component.translatable(Constants.TEXT_PREFIX + "next_reward.in", this.nextRewardTimeString);
    int componentWidth = this.font.width(component);
    this.font.draw(poseStack, component,
        x + (componentWidth < this.imageWidth ? ((this.imageWidth - componentWidth) / 2f) : 0), y,
        0x666666);
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageHeight = 242;

    // Set Title with already rewarded days.
    this.rewardedDays = this.menu.getRewardedDays();
    rewardScreenTitle =
        Component.translatable(Constants.TEXT_PREFIX + "reward_screen", this.rewardedDays);

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
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);

    // Additional styling for the different kind of slots and slot states.
    for (int k = 0; k < this.menu.slots.size(); ++k) {
      Slot slot = this.menu.slots.get(k);
      if (slot instanceof TakeableRewardSlot && !slot.getItem().is(ModItems.TAKEN_REWARD.get())) {
        rendererTakeableRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
      } else if (slot instanceof RewardSlot) {
        renderRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
      }
    }

    this.renderNextTimeForReward(poseStack, leftPos + 2, topPos + 140);

    this.renderTooltip(poseStack, x, y);
  }

  @Override
  protected void renderLabels(PoseStack poseStack, int x, int y) {
    this.font.draw(poseStack, rewardScreenTitle, this.titleLabelX, this.titleLabelY, 4210752);
    this.font.draw(poseStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
        4210752);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_GENERIC_54);

    // Main screen
    this.blit(poseStack, leftPos, topPos + 20, 0, 0, 176, 222);
    this.blit(poseStack, leftPos, topPos, 0, 0, 176, 139);
    blit(poseStack, leftPos + 5, topPos + 15, 3, 64, 165, 130, 255, 4096);

    // Render Rewards Slots
    int dayCounter = 1;
    for (int i = 0; i < 4; i++) {
      int slotTopPos = topPos + 2 + (i * 31);
      for (int i2 = 0; i2 < 8; i2++) {
        if (dayCounter <= rewardDaysForCurrentMonth) {
          int slotLeftPos = leftPos + 7 + Math.round(i2 * 20.5f);
          RenderSystem.setShaderTexture(0, Constants.TEXTURE_GENERIC_54);
          this.blit(poseStack, slotLeftPos, slotTopPos + 26, 7, 17, 18, 18);
          this.blit(poseStack, slotLeftPos, slotTopPos + 16, 7, 17, 18, 18);
          this.font.draw(poseStack, dayCounter + "", slotLeftPos + (dayCounter < 10 ? 6f : 4f),
              slotTopPos + 35f, 4210752);
          dayCounter++;
        }
      }
    }
  }

}
