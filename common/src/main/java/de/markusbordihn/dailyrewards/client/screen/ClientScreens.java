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

package de.markusbordihn.dailyrewards.client.screen;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.menu.ModMenuTypes;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.menu.MenuRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientScreens {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected ClientScreens() {}

  public static void registerScreens() {
    ClientLifecycleEvent.CLIENT_SETUP.register(
        minecraft -> {
          log.info("{} Client Screens ...", Constants.LOG_REGISTER_PREFIX);
          MenuRegistry.registerScreenFactory(
              ModMenuTypes.REWARD_COMPACT_MENU.get(), RewardCompactScreen::new);
          MenuRegistry.registerScreenFactory(
              ModMenuTypes.REWARD_OVERVIEW_MENU.get(), RewardDefaultOverviewScreen::new);
          MenuRegistry.registerScreenFactory(
              ModMenuTypes.REWARD_SPECIAL_OVERVIEW_MENU.get(), RewardSpecialOverviewScreen::new);
        });
  }
}
