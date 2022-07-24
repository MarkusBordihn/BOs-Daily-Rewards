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

package de.markusbordihn.dailyrewards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import de.markusbordihn.dailyrewards.client.screen.ClientScreens;
import de.markusbordihn.dailyrewards.item.ModItems;
import de.markusbordihn.dailyrewards.menu.ModMenuTypes;
import de.markusbordihn.dailyrewards.network.NetworkHandler;
import de.markusbordihn.dailyrewards.utils.StopModReposts;

@Mod(Constants.MOD_ID)
public class DailyRewards {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public DailyRewards() {
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

    StopModReposts.checkStopModReposts();

    modEventBus.addListener(NetworkHandler::registerNetworkHandler);

    log.info("Register Items ...");
    ModItems.ITEMS.register(modEventBus);

    log.info("{} Menu Types ...", Constants.LOG_REGISTER_PREFIX);
    ModMenuTypes.MENU_TYPES.register(modEventBus);

    forgeEventBus.addListener(ServerSetup::handleFMLServerStartingEvent);

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> modEventBus.addListener(ClientScreens::registerScreens));
  }
}
