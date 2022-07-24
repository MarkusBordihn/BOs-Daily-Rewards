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

package de.markusbordihn.dailyrewards.network;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardData;
import de.markusbordihn.dailyrewards.data.RewardUserData;
import de.markusbordihn.dailyrewards.network.message.MessageGeneralRewardsForCurrentMonth;
import de.markusbordihn.dailyrewards.network.message.MessageUserRewardsForCurrentMonth;

@EventBusSubscriber
public class NetworkHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "network"),
          () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
  private static int id = 0;

  protected NetworkHandler() {}

  public static void registerNetworkHandler(final FMLCommonSetupEvent event) {

    log.info("{} Network Handler for {} with version {} ...", Constants.LOG_REGISTER_PREFIX,
        INSTANCE, PROTOCOL_VERSION);

    event.enqueueWork(() -> {

      // Sync General Reward Data: Server -> Client
      INSTANCE.registerMessage(id++, MessageGeneralRewardsForCurrentMonth.class,
          (message, buffer) -> buffer.writeNbt(message.getData()),
          buffer -> new MessageGeneralRewardsForCurrentMonth(buffer.readNbt()),
          MessageGeneralRewardsForCurrentMonth::handle);

      // Sync User Reward Data: Server -> Client
      INSTANCE.registerMessage(id++, MessageUserRewardsForCurrentMonth.class, (message, buffer) -> {
        buffer.writeNbt(message.getData());
        buffer.writeInt(message.getRewardedDays());
      }, buffer -> new MessageUserRewardsForCurrentMonth(buffer.readNbt(), buffer.readInt()),
          MessageUserRewardsForCurrentMonth::handle);
    });
  }

  /** Send general rewards for current month to player. */
  public static void syncGeneralRewardForCurrentMonth(ServerPlayerEntity serverPlayer) {
    CompoundNBT data = RewardData.get().getRewardsForCurrentMonthSyncData();
    if (serverPlayer != null && serverPlayer.getUUID() != null && data != null && !data.isEmpty()) {
      log.debug("Sending general reward for current month to {}: {}", serverPlayer, data);
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessageGeneralRewardsForCurrentMonth(data));
    }
  }

  /** Send user rewards for current month to player. */
  public static void syncUserRewardForCurrentMonth(ServerPlayerEntity serverPlayer) {
    if (serverPlayer == null || serverPlayer.getUUID() == null) {
      return;
    }
    UUID uuid = serverPlayer.getUUID();
    CompoundNBT data = RewardUserData.get().getRewardsForCurrentMonthSyncData(uuid);
    int rewardedDays = RewardUserData.get().getRewardedDaysForCurrentMonth(uuid);
    if (data != null && !data.isEmpty()) {
      log.debug("Sending user reward for current month to {}: {}", serverPlayer, data);
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessageUserRewardsForCurrentMonth(data, rewardedDays));
    }
  }

}
