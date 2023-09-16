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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.server.level.ServerPlayer;

import de.markusbordihn.dailyrewards.rewards.RewardsScreen;

public class ClaimCommand extends CustomCommand {
  private static final ClaimCommand command = new ClaimCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("claim").requires(cs -> cs.hasPermission(Commands.LEVEL_ALL))
        .executes(command);
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();

    // Open reward screen for player depending on the configuration
    switch (COMMON.rewardScreenType.get()) {
      case "overview":
        RewardsScreen.openRewardOverviewMenuForPlayer(player);
        break;
      case "compact":
        RewardsScreen.openRewardCompactMenuForPlayer(player);
        break;
      case "special":
        RewardsScreen.openRewardSpecialOverviewMenuForPlayer(player);
        break;
      default:
        RewardsScreen.openRewardOverviewMenuForPlayer(player);
        break;
    }
    return 0;
  }

}
