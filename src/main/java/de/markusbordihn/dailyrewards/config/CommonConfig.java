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

package de.markusbordihn.dailyrewards.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

import de.markusbordihn.dailyrewards.Constants;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final ForgeConfigSpec commonSpec;
  public static final Config COMMON;

  protected CommonConfig() {}

  static {
    com.electronwill.nightconfig.core.Config.setInsertionOrderPreserved(true);
    final Pair<Config, ForgeConfigSpec> specPair =
        new ForgeConfigSpec.Builder().configure(Config::new);
    commonSpec = specPair.getRight();
    COMMON = specPair.getLeft();
    log.info("{} Common config ...", Constants.LOG_REGISTER_PREFIX);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
  }

  public static class Config {


    public final ForgeConfigSpec.IntValue rewardTimePerDay;

    public final ForgeConfigSpec.ConfigValue<List<String>> normalFillItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rareFillItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJanuaryItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsFebruaryItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMarchItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAprilItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMayItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJuneItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJulyItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAugustItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsSeptemberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsOctoberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsNovemberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsDecemberItems;

    Config(ForgeConfigSpec.Builder builder) {
      builder.comment("Daily Rewards (General configuration)");

      builder.push("General");
      rewardTimePerDay = builder.comment(
          "Time in minutes the players needs to be online on the server before receiving a reward for the day.")
          .defineInRange("rewardTimePerDay", 30, 1, 1440);
      builder.pop();

      builder.push("Fill Items");
      normalFillItems = builder.comment(
          "List of normal fill items which are used in the case we have not enough valid items for a month.")
          .define("normalFillItems", new ArrayList<String>(Arrays.asList("minecraft:cooked_beef:16",
              "minecraft:iron_ingot:8", "minecraft:oak_log:16", "minecraft:white_wool:4",
              "minecraft:gold_ingot:8", "minecraft:wheat_seeds:16", "minecraft:pumpkin_seeds:16",
              "minecraft:melon_seeds:16", "minecraft:beetroot_seeds:16", "minecraft:arrow:32")));

      rareFillItems = builder.comment(
          "List of rare fill items which are used in the case we have not enough valid items for a month.")
          .define("rareFillItems", new ArrayList<String>(
              Arrays.asList("minecraft:diamond", "minecraft:quartz:3", "minecraft:spyglass")));
      builder.pop();

      builder.push("Rewards Items");
      rewardsJanuaryItems = builder.comment("List of rewards items for January.")
          .define("rewardsJanuaryItems", new ArrayList<String>(Arrays.asList()));

      rewardsFebruaryItems = builder.comment("List of rewards items for February.")
          .define("rewardsFebruaryItems", new ArrayList<String>(Arrays.asList()));

      rewardsMarchItems = builder.comment("List of rewards items for March.")
          .define("rewardsMarchItems", new ArrayList<String>(Arrays.asList()));

      rewardsAprilItems = builder.comment("List of rewards items for April.")
          .define("rewardsAprilItems", new ArrayList<String>(Arrays.asList()));

      rewardsMayItems = builder.comment("List of rewards items for May.").define("rewardsMayItems",
          new ArrayList<String>(Arrays.asList("minecraft:egg:16")));

      rewardsJuneItems = builder.comment("List of rewards items for June.")
          .define("rewardsJuneItems", new ArrayList<String>(Arrays.asList()));

      rewardsJulyItems = builder.comment("List of rewards items for July.")
          .define("rewardsJulyItems", new ArrayList<String>(Arrays.asList()));

      rewardsAugustItems = builder.comment("List of rewards items for August.")
          .define("rewardsAugustItems", new ArrayList<String>(Arrays.asList()));

      rewardsSeptemberItems = builder.comment("List of rewards items for September.")
          .define("rewardsSeptemberItems", new ArrayList<String>(Arrays.asList()));

      rewardsOctoberItems = builder.comment("List of rewards items for October.")
          .define("rewardsOctoberItems", new ArrayList<String>(Arrays.asList()));

      rewardsNovemberItems = builder.comment("List of rewards items for November.")
          .define("rewardsNovemberItems", new ArrayList<String>(Arrays.asList()));

      rewardsDecemberItems = builder.comment("List of rewards items for December.")
          .define("rewardsDecemberItems", new ArrayList<String>(Arrays.asList()));

      builder.pop();
    }
  }

}
