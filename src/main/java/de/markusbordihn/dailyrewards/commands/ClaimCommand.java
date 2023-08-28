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

import java.util.UUID;
import javax.annotation.Nullable;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraftforge.network.NetworkHooks;

import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.menu.RewardOverviewMenu;

public class ClaimCommand extends CustomCommand {
  private static final ClaimCommand command = new ClaimCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("claim").requires(cs -> cs.hasPermission(Commands.LEVEL_ALL))
        .executes(command);
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    openRewardMenuForPlayer(player);
    return 0;
  }

  public static void openRewardMenuForPlayer(ServerPlayer player) {
    MenuProvider provider = new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return new TextComponent("Rewards");
      }

      @Nullable
      @Override
      public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new RewardOverviewMenu(windowId, inventory);
      }
    };

    NetworkHooks.openGui(player, provider, buffer -> {
      UUID uuid = player.getUUID();
      int rewardedDays = RewardUserData.get().getRewardedDaysForCurrentMonth(uuid);
      String lastRewardedDay = RewardUserData.get().getLastRewardedDayForCurrentMonth(uuid);
      CompoundTag userRewardsForCurrentMonth =
          RewardUserData.get().getRewardsForCurrentMonthSyncData(uuid);
      CompoundTag rewardsForCurrentMonth = RewardData.get().getRewardsForCurrentMonthSyncData();
      buffer.writeUUID(uuid);
      buffer.writeInt(rewardedDays);
      buffer.writeUtf(lastRewardedDay);
      buffer.writeNbt(userRewardsForCurrentMonth);
      buffer.writeNbt(rewardsForCurrentMonth);
    });
  }
}
