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

package de.markusbordihn.dailyrewards.network.message;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardScreenType;
import de.markusbordihn.dailyrewards.rewards.RewardsScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageOpenRewardScreen extends ModMessage<ServerGamePacketListenerImpl> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected RewardScreenType rewardScreenType;

  public MessageOpenRewardScreen() {}

  public MessageOpenRewardScreen(RewardScreenType rewardScreenType) {
    this.rewardScreenType = rewardScreenType;
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeEnum(this.rewardScreenType);
  }

  @Override
  public void read(FriendlyByteBuf buffer) {
    this.rewardScreenType = buffer.readEnum(RewardScreenType.class);
  }

  @Override
  public void onReceive(ServerGamePacketListenerImpl listener) {
    ServerPlayer player = listener.getPlayer();

    // Validate reward screen type
    RewardScreenType rewardScreenType = this.rewardScreenType;
    if (rewardScreenType == null) {
      log.warn(
          "Unable to open reward screen for player {} due to missing reward screen type!", player);
      return;
    }

    // Open reward screen
    log.debug("Opening reward screen for player {} with type {} ...", player, rewardScreenType);

    switch (rewardScreenType) {
      case COMPACT -> RewardsScreen.openRewardCompactMenuForPlayer(player);
      case DEFAULT_OVERVIEW -> RewardsScreen.openRewardOverviewMenuForPlayer(player);
      case SPECIAL_OVERVIEW -> RewardsScreen.openRewardSpecialOverviewMenuForPlayer(player);
      default ->
          log.warn(
              "Unable to open reward screen for player {} due to unknown reward screen type {}!",
              player,
              rewardScreenType);
    }
  }
}
