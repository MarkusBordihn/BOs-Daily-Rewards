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

package de.markusbordihn.dailyrewards.rewards;

import de.markusbordihn.dailyrewards.Constants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RewardsItems {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static List<ItemStack> parseConfigItems(List<String> configItems) {
    List<ItemStack> items = new ArrayList<>();

    for (String configItem : configItems) {
      String itemName = configItem;
      int itemCount = 1;

      // Check if we have a count for the item.
      if (configItem.chars().filter(delimiter -> delimiter == ':').count() == 2) {
        String[] itemParts = configItem.split(":");
        itemName = itemParts[0] + ":" + itemParts[1];
        itemCount = Integer.parseInt(itemParts[2]);
      }

      // Check if we have a short name for the item.
      if (!itemName.contains(":")) {
        if (itemName.equals("skip_day") || itemName.equals("lock_day")) {
          itemName = "daily_rewards:" + itemName;
        } else {
          itemName = "minecraft:" + itemName;
        }
      }

      // Verify that the item exists in the registry.
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
      if (item == null || item == Items.AIR) {
        log.error("Unable to find reward item {} in the registry!", itemName);
      } else {
        ItemStack itemStack = new ItemStack(item);
        itemStack.setCount(itemCount);
        items.add(itemStack);
      }
    }

    return items;
  }

}
