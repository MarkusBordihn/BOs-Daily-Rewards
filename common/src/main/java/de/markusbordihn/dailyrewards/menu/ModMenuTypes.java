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

package de.markusbordihn.dailyrewards.menu;

import de.markusbordihn.dailyrewards.Constants;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(Constants.MOD_ID, Registries.MENU);
  public static final RegistrySupplier<MenuType<RewardCompactMenu>> REWARD_COMPACT_MENU =
      MENU_TYPES.register(
          "reward_compact_menu", () -> MenuRegistry.ofExtended(RewardCompactMenu::new));
  public static final RegistrySupplier<MenuType<RewardOverviewMenu>> REWARD_OVERVIEW_MENU =
      MENU_TYPES.register(
          "reward_overview_menu", () -> MenuRegistry.ofExtended(RewardOverviewMenu::new));
  public static final RegistrySupplier<MenuType<RewardSpecialOverviewMenu>>
      REWARD_SPECIAL_OVERVIEW_MENU =
          MENU_TYPES.register(
              "reward_special_overview_menu",
              () -> MenuRegistry.ofExtended(RewardSpecialOverviewMenu::new));

  protected ModMenuTypes() {}
}
