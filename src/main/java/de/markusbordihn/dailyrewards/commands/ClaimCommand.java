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

import javax.annotation.Nullable;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fml.network.NetworkHooks;

import de.markusbordihn.dailyrewards.menu.RewardMenu;

public class ClaimCommand extends CustomCommand {
  private static final ClaimCommand command = new ClaimCommand();

  public static ArgumentBuilder<CommandSource, ?> register() {
    return Commands.literal("claim").requires(cs -> cs.hasPermission(0)).executes(command);
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    ServerPlayerEntity player = context.getSource().getPlayerOrException();
    openRewardMenuForPlayer(player);
    return 0;
  }

  public static void openRewardMenuForPlayer(ServerPlayerEntity player) {
    INamedContainerProvider provider = new INamedContainerProvider() {
      @Override
      public ITextComponent getDisplayName() {
        return new StringTextComponent("Rewards");
      }

      @Nullable
      @Override
      public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
        return new RewardMenu(windowId, inventory);
      }
    };

    NetworkHooks.openGui(player, provider, buffer -> {
    });
  }
}
