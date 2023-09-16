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

  private static final String JANUARY = "January";
  private static final String FEBRUARY = "February";
  private static final String MARCH = "March";
  private static final String APRIL = "April";
  private static final String MAY = "May";
  private static final String JUNE = "June";
  private static final String JULY = "July";
  private static final String AUGUST = "August";
  private static final String SEPTEMBER = "September";
  private static final String OCTOBER = "October";
  private static final String NOVEMBER = "November";
  private static final String DECEMBER = "December";

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

    public final ForgeConfigSpec.BooleanValue automaticRewardPlayers;
    public final ForgeConfigSpec.BooleanValue automaticRewardSpecialPlayers;
    public final ForgeConfigSpec.IntValue rewardTimePerDay;
    public final ForgeConfigSpec.BooleanValue showRewardMenuOnPlayerJoin;
    public final ForgeConfigSpec.ConfigValue<String> rewardScreenType;

    public final ForgeConfigSpec.BooleanValue useFillItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> normalFillItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rareFillItems;
    public final ForgeConfigSpec.IntValue rareFillItemsChance;
    public final ForgeConfigSpec.ConfigValue<List<String>> lootBagFillItems;
    public final ForgeConfigSpec.IntValue lootBagFillItemsChance;

    public final ForgeConfigSpec.BooleanValue shuffleRewardsItems;
    public final ForgeConfigSpec.BooleanValue shuffleRewardsSpecialItems;
    public final ForgeConfigSpec.BooleanValue previewRewardsItems;
    public final ForgeConfigSpec.BooleanValue previewRewardsSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJanuaryItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJanuarySpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsFebruaryItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsFebruarySpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMarchItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMarchSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAprilItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAprilSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMayItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsMaySpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJuneItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJuneSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJulyItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsJulySpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAugustItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsAugustSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsSeptemberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsSeptemberSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsOctoberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsOctoberSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsNovemberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsNovemberSpecialItems;

    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsDecemberItems;
    public final ForgeConfigSpec.ConfigValue<List<String>> rewardsDecemberSpecialItems;

    private static final String getListOfRewardsItemsText(String month) {
      return "List of rewards items for " + month;
    }

    private static final String getSpecialRewardsItemsText(String month) {
      return "Single reward item or list of special streak rewards items for " + month
          + ". (Only used if rewards" + month + "SpecialItemsNeededDays is greater than 0)";
    }

    Config(ForgeConfigSpec.Builder builder) {

      builder.comment("Daily Rewards (General configuration)");

      builder.push("General");
      automaticRewardPlayers = builder.comment(
          "Automatically reward players after the rewardTimePerDay is reached. (e.g. 30 minutes). If disabled the reward command needs to be executed manually to reward the players.")
          .define("automaticRewardPlayers", true);
      automaticRewardSpecialPlayers = builder.comment(
          "Automatically reward players with specials after the rewardTimePerDay is reached. (e.g. 30 minutes). If disabled the reward command needs to be executed manually to reward the players.")
          .define("automaticRewardSpecialPlayers", true);
      rewardTimePerDay = builder.comment(
          "Time in minutes the players needs to be online on the server before receiving a reward for the day.")
          .defineInRange("rewardTimePerDay", 30, 1, 1440);
      showRewardMenuOnPlayerJoin = builder.comment(
          "Shows the rewards menu when a player joins the server (if there are unclaimed rewards).")
          .define("showRewardMenuOnPlayerJoin", false);
      rewardScreenType =
          builder.comment("Type of the reward screen (e.g. default, overview, compact)")
              .define("rewardScreenType", "default");
      builder.pop();

      builder.push("Fill Items");
      useFillItems = builder.comment(
          "Use fill items if there are not enough valid items for a month. (e.g. if there are only 2 items for a month and the player claims 3 rewards, the 3rd reward will be a fill item)")
          .define("useFillItems", true);
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
      rareFillItemsChance = builder.comment(
          "The chance to use a rare item instead of a regular one. e.g. 7 means every 7th items could be a rare item. (0 = disabled)")
          .defineInRange("rareFillItemsChance", 7, 0, 100);
      lootBagFillItems = builder.comment(
          "List of loot bag fill items which are used in the case we have not enough valid items for a month.")
          .define("lootBagFillItems", new ArrayList<String>(Arrays.asList("lootbagmod:lootbag")));
      lootBagFillItemsChance = builder.comment(
          "The chance to use a loot bag item instead of a regular one. e.g. 15 means every 15th items could be a loot bag item. (0 = disabled)")
          .defineInRange("lootBagFillItemsChance", 15, 0, 100);
      builder.pop();

      builder.push("Rewards Items");
      shuffleRewardsItems =
          builder.comment("Shuffle the rewards items instead of using the defined order.")
              .define("shuffleRewardsItems", true);
      shuffleRewardsSpecialItems =
          builder.comment("Shuffle the special rewards items instead of using the defined order.")
              .define("shuffleRewardsSpecialItems", false);
      previewRewardsItems = builder.comment("Preview the upcoming rewards items for the next days.")
          .define("previewRewardsItems", true);
      previewRewardsSpecialItems =
          builder.comment("Preview the upcoming special rewards items for the next days.")
              .define("previewRewardsSpecialItems", true);

      builder.push("January Rewards Items");
      rewardsJanuaryItems = builder.comment(getListOfRewardsItemsText(JANUARY))
          .define("rewardsJanuaryItems", new ArrayList<String>(Arrays.asList("minecraft:oak_log:32",
              "minecraft:cooked_salmon:16", "minecraft:white_wool:16", "minecraft:cake:1")));
      rewardsJanuarySpecialItems = builder.comment(getSpecialRewardsItemsText(JANUARY))
          .define("rewardsJanuarySpecialItems", new ArrayList<String>(Arrays
              .asList("minecraft:cooked_salmon:1", "minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("February Rewards Items");
      rewardsFebruaryItems = builder.comment(getListOfRewardsItemsText(FEBRUARY))
          .define("rewardsFebruaryItems", new ArrayList<String>(Arrays.asList()));
      rewardsFebruarySpecialItems = builder.comment(getSpecialRewardsItemsText(FEBRUARY)).define(
          "rewardsFebruarySpecialItems",
          new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("March Rewards Items");
      rewardsMarchItems = builder.comment(getListOfRewardsItemsText(MARCH))
          .define("rewardsMarchItems", new ArrayList<String>(Arrays.asList()));
      rewardsMarchSpecialItems =
          builder.comment(getSpecialRewardsItemsText(MARCH)).define("rewardsMarchSpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("April Rewards Items");
      rewardsAprilItems = builder.comment(getListOfRewardsItemsText(APRIL))
          .define("rewardsAprilItems", new ArrayList<String>(Arrays.asList()));
      rewardsAprilSpecialItems = builder.comment(getSpecialRewardsItemsText(APRIL))
          .define("rewardsAprilSpecialItems", new ArrayList<String>(
              Arrays.asList("minecraft:eggs:1", "minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("May Rewards Items");
      rewardsMayItems = builder.comment(getListOfRewardsItemsText(MAY)).define("rewardsMayItems",
          new ArrayList<String>(Arrays.asList("minecraft:egg:16")));
      rewardsMaySpecialItems =
          builder.comment(getSpecialRewardsItemsText(MAY)).define("rewardsMaySpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("June Rewards Items");
      rewardsJuneItems = builder.comment(getListOfRewardsItemsText(JUNE)).define("rewardsJuneItems",
          new ArrayList<String>(Arrays.asList()));
      rewardsJuneSpecialItems =
          builder.comment(getSpecialRewardsItemsText(JUNE)).define("rewardsJuneSpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("July Rewards Items");
      rewardsJulyItems = builder.comment(getListOfRewardsItemsText(JULY)).define("rewardsJulyItems",
          new ArrayList<String>(Arrays.asList()));
      rewardsJulySpecialItems =
          builder.comment(getSpecialRewardsItemsText(JULY)).define("rewardsJulySpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("August Rewards Items");
      rewardsAugustItems = builder.comment(getListOfRewardsItemsText(AUGUST))
          .define("rewardsAugustItems", new ArrayList<String>(Arrays.asList()));
      rewardsAugustSpecialItems =
          builder.comment(getSpecialRewardsItemsText(AUGUST)).define("rewardsAugustSpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("September Rewards Items");
      rewardsSeptemberItems = builder.comment(getListOfRewardsItemsText(SEPTEMBER))
          .define("rewardsSeptemberItems", new ArrayList<String>(Arrays.asList()));
      rewardsSeptemberSpecialItems = builder.comment(getSpecialRewardsItemsText(SEPTEMBER)).define(
          "rewardsSeptemberSpecialItems",
          new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("October Rewards Items");
      rewardsOctoberItems = builder.comment(getListOfRewardsItemsText(OCTOBER))
          .define("rewardsOctoberItems", new ArrayList<String>(Arrays.asList()));
      rewardsOctoberSpecialItems =
          builder.comment(getSpecialRewardsItemsText(OCTOBER)).define("rewardsOctoberSpecialItems",
              new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("November Rewards Items");
      rewardsNovemberItems = builder.comment(getListOfRewardsItemsText(NOVEMBER))
          .define("rewardsNovemberItems", new ArrayList<String>(Arrays.asList()));
      rewardsNovemberSpecialItems = builder.comment(getSpecialRewardsItemsText(NOVEMBER)).define(
          "rewardsNovemberSpecialItems",
          new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.push("December Rewards Items");
      rewardsDecemberItems =
          builder.comment(getListOfRewardsItemsText(DECEMBER)).define("rewardsDecemberItems",
              new ArrayList<String>(Arrays.asList("minecraft:firework_rocket:32")));
      rewardsDecemberSpecialItems = builder.comment(getSpecialRewardsItemsText(DECEMBER)).define(
          "rewardsDecemberSpecialItems",
          new ArrayList<String>(Arrays.asList("minecraft:cake:1", "minecraft:cookie:1")));
      builder.pop();

      builder.pop();
    }
  }


}
