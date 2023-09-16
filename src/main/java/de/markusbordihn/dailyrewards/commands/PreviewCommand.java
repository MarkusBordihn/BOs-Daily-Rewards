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

package de.markusbordihn.dailyrewards.commands;

import java.util.List;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.dailyrewards.rewards.Rewards;

public class PreviewCommand extends CustomCommand {
  private static final PreviewCommand command = new PreviewCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("preview").requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
        .executes(command).then(Commands.literal("January").executes(command::showJanuary))
        .then(Commands.literal("February").executes(command::showFebruary))
        .then(Commands.literal("March").executes(command::showMarch))
        .then(Commands.literal("April").executes(command::showApril))
        .then(Commands.literal("May").executes(command::showMay))
        .then(Commands.literal("June").executes(command::showJune))
        .then(Commands.literal("July").executes(command::showJuly))
        .then(Commands.literal("August").executes(command::showAugust))
        .then(Commands.literal("September").executes(command::showSeptember))
        .then(Commands.literal("October").executes(command::showOctober))
        .then(Commands.literal("November").executes(command::showNovember))
        .then(Commands.literal("December").executes(command::showDecember));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    sendFeedback(context, "...");
    return 0;
  }

  public int showJanuary(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for January");
    previewRewardsItemsForMonth(context, 1);
    return 0;
  }

  public int showFebruary(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for February");
    previewRewardsItemsForMonth(context, 2);
    return 0;
  }

  public int showMarch(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for March");
    previewRewardsItemsForMonth(context, 3);
    return 0;
  }

  public int showApril(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for April");
    previewRewardsItemsForMonth(context, 4);
    return 0;
  }

  public int showMay(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for May");
    previewRewardsItemsForMonth(context, 5);
    return 0;
  }

  public int showJune(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for June");
    previewRewardsItemsForMonth(context, 6);
    return 0;
  }

  public int showJuly(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for July");
    previewRewardsItemsForMonth(context, 7);
    return 0;
  }

  public int showAugust(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for August");
    previewRewardsItemsForMonth(context, 8);
    return 0;
  }

  public int showSeptember(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for September");
    previewRewardsItemsForMonth(context, 9);
    return 0;
  }

  public int showOctober(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for October");
    previewRewardsItemsForMonth(context, 10);
    return 0;
  }

  public int showNovember(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for November");
    previewRewardsItemsForMonth(context, 11);
    return 0;
  }

  public int showDecember(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Preview for December");
    previewRewardsItemsForMonth(context, 12);
    return 0;
  }

  private void previewRewardsItemsForMonth(CommandContext<CommandSourceStack> context, int month) {
    List<ItemStack> rewardItems = Rewards.calculateRewardItemsForMonth(month);
    if (rewardItems.isEmpty()) {
      sendFeedback(context, String.format("Found not items for %s month!", month));
      return;
    }
    sendFeedback(context, "Note: The preview could be different from the actually result.");
    int day = 1;
    for (ItemStack rewardItem : rewardItems) {
      sendFeedback(context, String.format("Day %s: %sx%s (%s)", day++, rewardItem.getCount(),
          rewardItem.getDisplayName().getString(), rewardItem.getItem()));
    }
  }
}
