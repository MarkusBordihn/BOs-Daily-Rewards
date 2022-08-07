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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.RewardMenu;
import de.markusbordihn.dailyrewards.menu.slots.RewardSlot;
import de.markusbordihn.dailyrewards.menu.slots.TakeableRewardSlot;
import de.markusbordihn.dailyrewards.rewards.Rewards;

public class RewardScreen extends ContainerScreen<RewardMenu> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ResourceLocation texture =
      new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen.png");

  private TranslationTextComponent rewardScreenTitle;

  public RewardScreen(RewardMenu menu, PlayerInventory inventory, ITextComponent component) {
    super(menu, inventory, component);
  }

  public void rendererTakeableRewardSlot(MatrixStack poseStack, int x, int y) {
    this.minecraft.getTextureManager().bind(this.texture);
    poseStack.pushPose();
    this.blit(poseStack, x + 11, y - 4, 430, 17, 16, 16);
    poseStack.popPose();
  }

  public void renderRewardSlot(MatrixStack poseStack, int x, int y, int blitOffset) {
    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    this.fillGradient(poseStack, x, y, x + 16, y + 16, -2130706433, 0);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageWidth = 171;
    this.imageHeight = 247;

    // Set Title with already rewarded days.
    int rewardedDays = this.menu.getRewardedDays();
    rewardScreenTitle =
        new TranslationTextComponent(Constants.TEXT_PREFIX + "reward_screen", rewardedDays);

    // Set background according the number or days for the current month.
    switch (Rewards.getDaysCurrentMonth()) {
      case 28:
        texture =
            new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen_28_days.png");
        break;
      case 29:
        texture =
            new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen_29_days.png");
        break;
      case 30:
        texture =
            new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen_30_days.png");
        break;
      case 31:
        texture =
            new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen_31_days.png");
        break;
      default:
        texture = new ResourceLocation(Constants.MOD_ID, "textures/container/reward_screen.png");
    }

    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.inventoryLabelX = 6;
    this.inventoryLabelY = this.imageHeight - 90;
  }

  @Override
  public void render(MatrixStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);

    // Additional styling for the different kind of slots and slot states.
    for (int k = 0; k < this.menu.slots.size(); ++k) {
      Slot slot = this.menu.slots.get(k);
      if (slot instanceof TakeableRewardSlot
          && !slot.getItem().getItem().equals(ModItems.TAKEN_REWARD.get())) {
        rendererTakeableRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
      } else if (slot instanceof RewardSlot) {
        renderRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y, this.getBlitOffset());
      }
    }

    this.renderTooltip(poseStack, x, y);
  }

  @Override
  protected void renderLabels(MatrixStack poseStack, int x, int y) {
    this.font.draw(poseStack, this.rewardScreenTitle, this.titleLabelX, this.titleLabelY, 4210752);
    this.font.draw(poseStack, this.title, this.inventoryLabelX, this.inventoryLabelY, 4210752);
  }

  @Override
  protected void renderBg(MatrixStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bind(this.texture);

    // Main screen
    this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
  }

}
