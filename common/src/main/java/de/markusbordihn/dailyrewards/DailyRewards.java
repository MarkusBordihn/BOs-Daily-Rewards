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

package de.markusbordihn.dailyrewards;

import de.markusbordihn.dailyrewards.client.screen.ClientScreens;
import de.markusbordihn.dailyrewards.commands.CommandManager;
import de.markusbordihn.dailyrewards.config.ModConfigs;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.ModMenuTypes;
import de.markusbordihn.dailyrewards.network.NetworkHandler;
import de.markusbordihn.dailyrewards.player.PlayerRewardManager;
import de.markusbordihn.dailyrewards.rewards.Rewards;
import de.markusbordihn.dailyrewards.utils.StopModReposts;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DailyRewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static void init() {
    StopModReposts.checkStopModReposts();

    log.info("Register Items ...");
    ModItems.ITEMS.register();
    log.info("{} Menu Types ...", Constants.LOG_REGISTER_PREFIX);
    ModMenuTypes.MENU_TYPES.register();

    if(Platform.getEnvironment() == Env.CLIENT) {
      ClientScreens.registerScreens();
    }

    NetworkHandler.registerNetworkHandler();
    ModConfigs.registerConfigs();

    CommandRegistrationEvent.EVENT.register(CommandManager::handleRegisterCommandsEvent);
    LifecycleEvent.SERVER_STARTING.register(ServerSetup::handleServerStartingEvent);

    LifecycleEvent.SERVER_BEFORE_START.register(PlayerRewardManager::onServerAboutToStartEvent);
    PlayerEvent.PLAYER_JOIN.register(PlayerRewardManager::handlePlayerLoggedInEvent);
    PlayerEvent.PLAYER_QUIT.register(PlayerRewardManager::handlePlayerLoggedOutEvent);
    TickEvent.SERVER_PRE.register(PlayerRewardManager::handleServerTickEvent);

    LifecycleEvent.SERVER_BEFORE_START.register(Rewards::handleServerAboutToStartEvent);
  }

}
