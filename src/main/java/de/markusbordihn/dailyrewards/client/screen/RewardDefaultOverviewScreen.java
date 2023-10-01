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

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardScreenType;
import de.markusbordihn.dailyrewards.menu.RewardOverviewMenu;
import de.markusbordihn.dailyrewards.network.NetworkMessage;

@OnlyIn(Dist.CLIENT)
public class RewardDefaultOverviewScreen extends RewardOverviewScreen<RewardOverviewMenu> {

  // Button
  protected ImageButton openSpecialRewardsOverviewButton;

  public RewardDefaultOverviewScreen(RewardOverviewMenu menu, Inventory inventory,
      Component component) {
    super(menu, inventory, component);
  }

  @Override
  public void init() {
    super.init();

    if (this.hasSpecialReward) {
      this.openSpecialRewardsOverviewButton =
          this.addRenderableWidget(new ImageButton(this.leftPos - 28, this.topPos + 18, 32, 28, 64,
              64, 28, Constants.TEXTURE_TABS, 256, 256, button -> {
                NetworkMessage.openRewardScreen(RewardScreenType.SPECIAL_OVERVIEW);
              }));
      this.openSpecialRewardsOverviewButton.setTooltip(Tooltip.create(
          Component.translatable(Constants.TEXT_PREFIX + "open_special_reward_screen.info")));
    }

  }

  @Override
  protected void renderIcons(PoseStack poseStack, int x, int y) {
    super.renderIcons(poseStack, x, y);
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_ICONS);
    poseStack.pushPose();
    this.blit(poseStack, leftPos - 21, topPos + 20, 0, 40, 32, 32);
    poseStack.popPose();
  }

}
