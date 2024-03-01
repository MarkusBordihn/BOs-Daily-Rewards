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
import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.config.CommonConfig;
import de.markusbordihn.dailyrewards.menu.RewardMenu;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
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
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_GENERIC_54);

    // Main screen
    this.blit(poseStack, leftPos, topPos + 20, 0, 0, 176, 222);
    this.blit(poseStack, leftPos, topPos + 8, 0, 0, 176, 139);
    blit(poseStack, leftPos + 5, topPos + 15, 3, 64, 165, 130, 255, 4096);
  }

}
