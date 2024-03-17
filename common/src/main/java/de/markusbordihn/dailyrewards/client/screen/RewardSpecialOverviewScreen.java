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
import de.markusbordihn.dailyrewards.data.RewardScreenType;
import de.markusbordihn.dailyrewards.menu.RewardSpecialOverviewMenu;
import de.markusbordihn.dailyrewards.network.NetworkMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RewardSpecialOverviewScreen extends RewardOverviewScreen<RewardSpecialOverviewMenu> {

  // Button
  protected ImageButton openDefaultRewardsOverviewButton;

  public RewardSpecialOverviewScreen(
      RewardSpecialOverviewMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  @Override
  public void init() {
    super.init();

    if (this.hasSpecialReward) {
      this.openDefaultRewardsOverviewButton =
          this.addRenderableWidget(
              new ImageButton(
                  this.leftPos + 172,
                  this.topPos + 18,
                  32,
                  28,
                  96,
                  64,
                  28,
                  Constants.TEXTURE_TABS,
                  256,
                  256,
                  button -> NetworkMessage.openRewardScreen(RewardScreenType.DEFAULT_OVERVIEW)));
      this.openDefaultRewardsOverviewButton.setTooltip(
          Tooltip.create(
              Component.translatable(Constants.TEXT_PREFIX + "open_default_reward_screen.info")));
    }
  }

  @Override
  protected void renderIcons(GuiGraphics guiGraphics, int x, int y) {
    super.renderIcons(guiGraphics, x, y);
    guiGraphics.pose().pushPose();
    guiGraphics.blit(Constants.TEXTURE_ICONS, leftPos + 176, topPos + 20, 0, 40, 32, 32);
    guiGraphics.pose().popPose();
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

    // Render custom background for overview screen
    guiGraphics.pose().pushPose();
    guiGraphics.blit(
        Constants.TEXTURE_SPECIAL_OVERVIEW_SCREEN, leftPos + 1, topPos, 0, 0, 256, 256);
    guiGraphics.pose().popPose();
  }
}
