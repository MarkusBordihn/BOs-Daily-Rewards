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
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

  protected ModMenuTypes() {}

  public static final RegistryObject<MenuType<RewardCompactMenu>> REWARD_COMPACT_MENU =
      MENU_TYPES.register(
          "reward_compact_menu", () -> IForgeMenuType.create(RewardCompactMenu::new));

  public static final RegistryObject<MenuType<RewardOverviewMenu>> REWARD_OVERVIEW_MENU =
      MENU_TYPES.register(
          "reward_overview_menu", () -> IForgeMenuType.create(RewardOverviewMenu::new));

  public static final RegistryObject<MenuType<RewardSpecialOverviewMenu>>
      REWARD_SPECIAL_OVERVIEW_MENU =
          MENU_TYPES.register(
              "reward_special_overview_menu",
              () -> IForgeMenuType.create(RewardSpecialOverviewMenu::new));
}
