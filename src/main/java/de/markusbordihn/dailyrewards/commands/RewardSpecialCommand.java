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
import java.util.UUID;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.SpecialRewardUserData;
import de.markusbordihn.dailyrewards.rewards.Rewards;

public class RewardSpecialCommand extends CustomCommand {

  private static final RewardSpecialCommand command = new RewardSpecialCommand();
  private static final String PLAYER_ARGUMENT = "player";

  private static final MutableComponent claimCommand = new TextComponent("/DailyRewards claim")
      .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withClickEvent(
          new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/DailyRewards claim")));

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("special_reward").requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
        .executes(command).then(Commands.literal("today")
            .then(Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> {
              return rewardPlayerToday(context.getSource(),
                  EntityArgument.getPlayer(context, PLAYER_ARGUMENT));
            })))
        .executes(command).then(Commands.literal("add")
            .then(Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> {
              return addRewardForPlayer(context.getSource(),
                  EntityArgument.getPlayer(context, PLAYER_ARGUMENT));
            })))
        .executes(command).then(Commands.literal("list")
            .then(Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> {
              return listRewardForPlayer(context.getSource(),
                  EntityArgument.getPlayer(context, PLAYER_ARGUMENT));
            })))
        .executes(command).then(Commands.literal("remove")
            .then(Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> {
              return removeRewardForPlayer(context.getSource(),
                  EntityArgument.getPlayer(context, PLAYER_ARGUMENT));
            })))
        .executes(command).then(Commands.literal("clear")
            .then(Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> {
              return clearRewardForPlayer(context.getSource(),
                  EntityArgument.getPlayer(context, PLAYER_ARGUMENT));
            })));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    return 0;
  }

  private static int listRewardForPlayer(CommandSourceStack context, ServerPlayer player) {
    RewardData rewardData = RewardData.get();
    SpecialRewardUserData rewardUserData = SpecialRewardUserData.get();
    UUID playerUUID = player.getUUID();
    int daysCurrentMonth = Rewards.getDaysCurrentMonth();
    int rewardedDays = rewardUserData.getRewardedDaysForCurrentMonth(playerUUID);
    String lastRewardedDay = rewardUserData.getLastRewardedDayForCurrentMonth(playerUUID);
    List<ItemStack> rewards = rewardUserData.getRewardsForCurrentMonth(playerUUID);

    // Display basic reward information
    MutableComponent textComponent = new TextComponent("Special Rewards for ").append(player.getName())
        .append(" (").append("" + rewardedDays + " of " + daysCurrentMonth + " days").append(" / ")
        .append(lastRewardedDay).append(")");
    context.sendSuccess(textComponent, true);

    // Display reward items
    MutableComponent rewardComponent = new TextComponent("");
    int rewardDay = 0;
    for (ItemStack reward : rewards) {
      rewardComponent = rewardComponent
          .append(new TextComponent("[" + ++rewardDay + "] ").withStyle(Style.EMPTY
              .withColor(rewardDay == rewards.size() ? ChatFormatting.RED : ChatFormatting.GOLD)))
          .append(
              new TextComponent("" + reward).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)))
          .append(new TextComponent(" (" + reward.getItem().getRegistryName() + ")\n")
              .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    // Preview next 3 days
    List<ItemStack> previewRewards = rewardData.getSpecialRewardsForTheNextDays(rewardedDays, 3);
    for (ItemStack previewReward : previewRewards) {
      rewardComponent = rewardComponent
          .append(new TextComponent("[" + ++rewardDay + "] ")
              .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
          .append(new TextComponent("" + previewReward)
              .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
          .append(new TextComponent(" (" + previewReward.getItem().getRegistryName() + ")\n")
              .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }
    rewardComponent = rewardComponent.append(new TextComponent("..."));
    context.sendSuccess(rewardComponent, true);

    return 0;
  }

  private static int rewardPlayerToday(CommandSourceStack context, ServerPlayer player) {
    SpecialRewardUserData rewardUserData = SpecialRewardUserData.get();
    UUID playerUUID = player.getUUID();
    if (rewardUserData.hasRewardedToday(playerUUID)) {
      context.sendFailure(new TranslatableComponent(
          Constants.TEXT_PREFIX + "player_already_rewarded_today", player.getName()));
      return 0;
    }
    return addRewardForPlayer(context, player);
  }

  private static int addRewardForPlayer(CommandSourceStack context, ServerPlayer player) {
    SpecialRewardUserData rewardUserData = SpecialRewardUserData.get();
    UUID playerUUID = player.getUUID();
    if (!rewardUserData.hasRewardedToday(playerUUID)) {
      rewardUserData.setLastRewardedDayForCurrentMonth(playerUUID);
    }

    // Update stored reward data
    int rewardedDays = rewardUserData.increaseRewardedDaysForCurrentMonth(playerUUID);
    ItemStack itemStack = RewardData.get().getSpecialRewardForCurrentMonth(rewardedDays);
    if (itemStack.isEmpty()) {
      context.sendFailure(new TranslatableComponent(Constants.TEXT_PREFIX + "rewarded_item_empty",
          player.getName(), rewardedDays, itemStack));
    } else {
      rewardUserData.addRewardForCurrentMonth(rewardedDays, playerUUID, itemStack);

      // Inform player about new reward
      player.sendMessage(new TranslatableComponent(Constants.TEXT_PREFIX + "rewarded_item",
          player.getName(), itemStack, rewardedDays), Util.NIL_UUID);
      player.sendMessage(
          new TranslatableComponent(Constants.TEXT_PREFIX + "claim_rewards", claimCommand),
          Util.NIL_UUID);
    }
    context.sendSuccess(
        new TextComponent("Special Reward granted for ").append(player.getName()).append(" (")
            .append("" + rewardedDays).append(" / ")
            .append(rewardUserData.getLastRewardedDayForCurrentMonth(playerUUID)).append(")"),
        true);

    return 0;
  }

  private static int removeRewardForPlayer(CommandSourceStack context, ServerPlayer player) {
    SpecialRewardUserData rewardUserData = SpecialRewardUserData.get();
    UUID playerUUID = player.getUUID();
    if (!rewardUserData.hasRewardedToday(playerUUID)) {
      rewardUserData.setLastRewardedDayForCurrentMonth(playerUUID);
    }

    // Update stored reward data
    int rewardedDays = rewardUserData.decreaseRewardedDaysForCurrentMonth(playerUUID);
    ItemStack itemStack = RewardData.get().getSpecialRewardForCurrentMonth(rewardedDays);
    if (itemStack.isEmpty()) {
      context.sendFailure(new TranslatableComponent(Constants.TEXT_PREFIX + "rewarded_item_empty",
          player.getName(), rewardedDays, itemStack));
    } else if (rewardedDays >= 0) {
      rewardUserData.removeRewardForCurrentMonth(++rewardedDays, playerUUID);
    }
    context.sendSuccess(
        new TextComponent("Special Reward removed for ").append(player.getName()).append(" (")
            .append("" + rewardedDays).append(" / ")
            .append(rewardUserData.getLastRewardedDayForCurrentMonth(playerUUID)).append(")"),
        true);

    return 0;
  }

  private static int clearRewardForPlayer(CommandSourceStack context, ServerPlayer player) {
    SpecialRewardUserData rewardUserData = SpecialRewardUserData.get();
    UUID playerUUID = player.getUUID();

    // Clear stored reward data
    rewardUserData.clearRewardsForCurrentMonth(playerUUID);

    context.sendSuccess(new TextComponent("Rewards cleared for ").append(player.getName()), true);

    return 0;
  }

}