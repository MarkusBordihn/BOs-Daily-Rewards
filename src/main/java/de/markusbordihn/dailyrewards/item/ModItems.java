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

package de.markusbordihn.dailyrewards.item;

import de.markusbordihn.dailyrewards.Constants;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
  public static final RegistryObject<Item> HIDDEN_REWARD =
      ITEMS.register(
          "hidden_reward",
          () -> new RewardSlotItem(new Item.Properties(), "hidden_reward.description"));
  public static final RegistryObject<Item> TAKEN_REWARD =
      ITEMS.register(
          "taken_reward",
          () -> new RewardSlotItem(new Item.Properties(), "taken_reward.description"));
  public static final RegistryObject<Item> EMPTY_REWARD =
      ITEMS.register(
          "empty_reward",
          () -> new RewardSlotItem(new Item.Properties(), "empty_reward.description"));
  // Special reward items
  public static final RegistryObject<Item> SKIP_DAY =
      ITEMS.register(
          "skip_day", () -> new RewardSlotItem(new Item.Properties(), "skip_day.description"));
  public static final RegistryObject<Item> SKIPPED_DAY =
      ITEMS.register(
          "skipped_day",
          () -> new RewardSlotItem(new Item.Properties(), "skipped_day.description"));
  public static final RegistryObject<Item> LOCK_DAY =
      ITEMS.register(
          "lock_day", () -> new RewardSlotItem(new Item.Properties(), "lock_day.description"));
  public static final RegistryObject<Item> UNLOCK_DAY =
      ITEMS.register(
          "unlock_day", () -> new RewardSlotItem(new Item.Properties(), "unlock_day.description"));

  protected ModItems() {}
}
