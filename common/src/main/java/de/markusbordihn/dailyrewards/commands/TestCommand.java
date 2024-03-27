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

package de.markusbordihn.dailyrewards.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.markusbordihn.dailyrewards.rewards.RewardsScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class TestCommand extends CustomCommand {
  private static final TestCommand command = new TestCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("test")
        .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
        .executes(command)
        .then(Commands.literal("overview").executes(command::showOverview))
        .then(Commands.literal("special_overview").executes(command::showSpecialOverview))
        .then(Commands.literal("compact").executes(command::showCompact));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "This command allows you to test the different kind of reward screens.");
    return 0;
  }

  public int showCompact(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    sendFeedback(context, "Open Compact Reward Screen for " + player);
    RewardsScreen.openRewardCompactMenuForPlayer(player);
    return 0;
  }

  public int showOverview(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    sendFeedback(context, "Open Overview Reward Screen for " + player);
    RewardsScreen.openRewardOverviewMenuForPlayer(player);
    return 0;
  }

  public int showSpecialOverview(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    sendFeedback(context, "Open Overview Special Reward Screen for " + player);
    RewardsScreen.openRewardSpecialOverviewMenuForPlayer(player);
    return 0;
  }
}
