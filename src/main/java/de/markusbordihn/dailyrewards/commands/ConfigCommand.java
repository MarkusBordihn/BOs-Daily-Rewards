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

import de.markusbordihn.dailyrewards.config.CommonConfig;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;

public class ConfigCommand extends CustomCommand {
  private static final ConfigCommand command = new ConfigCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("config").requires(cs -> cs.hasPermission(Commands.LEVEL_ADMINS))
        .executes(command).then(Commands.literal("reload").executes(command::reloadConfig))
        .then(Commands.literal("reset").executes(command::resetConfig))
        .then(Commands.literal("clear").executes(command::clearConfig));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    sendFeedback(context,
        "Usage: /dailyrewards config [OPTION]\n\n" + "- reload   Reloads the config file!\n"
            + "- reset    Resets the config file and clean the data!!.\n"
            + "- clear    Cleans the config file and clear all data!!!\n");
    return 0;
  }

  public int reloadConfig(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Reset caching after reload config ...\n"
        + "If your changes are not visible, please try to save the file 2-3 times again to trigger the automatic reload!\n"
        + "If this is still not working, please restart the game / server instead.");
    CommonConfig.commonSpec.afterReload();
    return 0;
  }

  public int resetConfig(CommandContext<CommandSourceStack> context) {

    sendFeedback(context, "Reset reward and reward user config for current month!");
    RewardData.get().resetRewardDataForCurrentMonth();
    RewardUserData.get().resetRewardUserDataForCurrentMonth();
    SpecialRewardUserData.get().resetRewardUserDataForCurrentMonth();

    sendFeedback(context, "Regenerate reward for current month!");
    List<ItemStack> rewardsForCurrentMonth = RewardData.get().getRewardsForCurrentMonth();
    List<ItemStack> specialRewardsForCurrentMonth =
        RewardData.get().getSpecialRewardsForCurrentMonth();

    if (!rewardsForCurrentMonth.isEmpty()) {
      sendFeedback(context, "Rewards for this Month: " + rewardsForCurrentMonth);
    }
    if (!specialRewardsForCurrentMonth.isEmpty()) {
      sendFeedback(context, "Special Rewards for this Month: " + specialRewardsForCurrentMonth);
    }

    return 0;
  }

  public int clearConfig(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Reset all reward and reward user data!");
    RewardData.get().clearRewardData();
    RewardUserData.get().clearRewardUserData();
    SpecialRewardUserData.get().clearRewardUserData();

    sendFeedback(context, "Regenerate reward for current month!");
    List<ItemStack> rewardsForCurrentMonth = RewardData.get().getRewardsForCurrentMonth();
    List<ItemStack> specialRewardsForCurrentMonth =
        RewardData.get().getSpecialRewardsForCurrentMonth();

    if (!rewardsForCurrentMonth.isEmpty()) {
      sendFeedback(context, "Rewards for this Month: " + rewardsForCurrentMonth);
    }
    if (!specialRewardsForCurrentMonth.isEmpty()) {
      sendFeedback(context, "Special Rewards for this Month: " + specialRewardsForCurrentMonth);
    }

    return 0;
  }

}
