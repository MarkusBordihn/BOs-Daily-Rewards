package de.markusbordihn.dailyrewards.config;

import de.markusbordihn.dailyrewards.data.RewardScreenType;
import java.util.List;

public class CommonConfig extends Config {

  public boolean automaticRewardPlayers;
  public boolean automaticRewardSpecialPlayers;
  public int rewardTimePerDay;
  public boolean showUnclaimedRewardsOnPlayerJoin;
  public boolean showUnclaimedRewardsSpecialOnPlayerJoin;
  public boolean showReceivedRewardMessage;
  public boolean showReceivedRewardSpecialMessage;
  public boolean showRewardClaimCommandMessage;
  public boolean showRewardMenuOnPlayerJoin;
  public RewardScreenType rewardScreenType;

  public boolean useFillItems;
  public List<String> normalFillItems;
  public List<String> rareFillItems;
  public int rareFillItemsChance;
  public List<String> lootBagFillItems;
  public int lootBagFillItemsChance;

  public boolean shuffleRewardsItems;
  public boolean shuffleRewardsSpecialItems;
  public boolean previewRewardsItems;
  public boolean previewRewardsSpecialItems;

  public List<String> rewardsJanuaryItems;
  public List<String> rewardsJanuarySpecialItems;
  public List<String> rewardsJanuarySpecialUsers;

  public List<String> rewardsFebruaryItems;
  public List<String> rewardsFebruarySpecialItems;
  public List<String> rewardsFebruarySpecialUsers;

  public List<String> rewardsMarchItems;
  public List<String> rewardsMarchSpecialItems;
  public List<String> rewardsMarchSpecialUsers;

  public List<String> rewardsAprilItems;
  public List<String> rewardsAprilSpecialItems;
  public List<String> rewardsAprilSpecialUsers;

  public List<String> rewardsMayItems;
  public List<String> rewardsMaySpecialItems;
  public List<String> rewardsMaySpecialUsers;

  public List<String> rewardsJuneItems;
  public List<String> rewardsJuneSpecialItems;
  public List<String> rewardsJuneSpecialUsers;

  public List<String> rewardsJulyItems;
  public List<String> rewardsJulySpecialItems;
  public List<String> rewardsJulySpecialUsers;

  public List<String> rewardsAugustItems;
  public List<String> rewardsAugustSpecialItems;
  public List<String> rewardsAugustSpecialUsers;

  public List<String> rewardsSeptemberItems;
  public List<String> rewardsSeptemberSpecialItems;
  public List<String> rewardsSeptemberSpecialUsers;

  public List<String> rewardsOctoberItems;
  public List<String> rewardsOctoberSpecialItems;
  public List<String> rewardsOctoberSpecialUsers;

  public List<String> rewardsNovemberItems;
  public List<String> rewardsNovemberSpecialItems;
  public List<String> rewardsNovemberSpecialUsers;

  public List<String> rewardsDecemberItems;
  public List<String> rewardsDecemberSpecialItems;
  public List<String> rewardsDecemberSpecialUsers;

  @Override
  public String getPath() {
    return "common";
  }

  @Override
  protected void reset() {
    this.automaticRewardPlayers = true;
    this.automaticRewardSpecialPlayers = true;
    this.rewardTimePerDay = 30;
    this.showUnclaimedRewardsOnPlayerJoin = true;
    this.showUnclaimedRewardsSpecialOnPlayerJoin = true;
    this.showReceivedRewardMessage = true;
    this.showReceivedRewardSpecialMessage = true;
    this.showRewardClaimCommandMessage = true;
    this.showRewardMenuOnPlayerJoin = false;
    this.rewardScreenType = RewardScreenType.COMPACT;

    this.useFillItems = true;
    this.normalFillItems =
        List.of(
            "minecraft:cooked_beef:16",
            "minecraft:iron_ingot:8",
            "minecraft:oak_log:16",
            "minecraft:white_wool:4",
            "minecraft:gold_ingot:8",
            "minecraft:wheat_seeds:16",
            "minecraft:pumpkin_seeds:16",
            "minecraft:melon_seeds:16",
            "minecraft:beetroot_seeds:16",
            "minecraft:arrow:32");
    this.rareFillItems = List.of("minecraft:diamond", "minecraft:quartz:3", "minecraft:spyglass");
    this.rareFillItemsChance = 7;
    this.lootBagFillItems = List.of("lootbagmod:lootbag");
    this.lootBagFillItemsChance = 15;

    this.shuffleRewardsItems = true;
    this.shuffleRewardsSpecialItems = false;
    this.previewRewardsItems = true;
    this.previewRewardsSpecialItems = true;

    this.rewardsJanuaryItems =
        List.of(
            "minecraft:oak_log:32",
            "minecraft:cooked_salmon:16",
            "minecraft:white_wool:16",
            "minecraft:cake:1");
    this.rewardsJanuarySpecialItems =
        List.of("minecraft:cooked_salmon:1", "minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsJanuarySpecialUsers = List.of("");

    this.rewardsFebruaryItems = List.of();
    this.rewardsFebruarySpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsFebruarySpecialUsers = List.of("");

    this.rewardsMarchItems = List.of();
    this.rewardsMarchSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsMarchSpecialUsers = List.of("");

    this.rewardsAprilItems = List.of();
    this.rewardsAprilSpecialItems =
        List.of("minecraft:eggs:1", "minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsAprilSpecialUsers = List.of("");

    this.rewardsMayItems = List.of("minecraft:egg:16");
    this.rewardsMaySpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsMaySpecialUsers = List.of("");

    this.rewardsJuneItems = List.of();
    this.rewardsJuneSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsJuneSpecialUsers = List.of("");

    this.rewardsJulyItems = List.of();
    this.rewardsJulySpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsJulySpecialUsers = List.of("");

    this.rewardsAugustItems = List.of();
    this.rewardsAugustSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsAugustSpecialUsers = List.of("");

    this.rewardsSeptemberItems = List.of();
    this.rewardsSeptemberSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsSeptemberSpecialUsers = List.of("");

    this.rewardsOctoberItems = List.of();
    this.rewardsOctoberSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsOctoberSpecialUsers = List.of("");

    this.rewardsNovemberItems = List.of();
    this.rewardsNovemberSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsNovemberSpecialUsers = List.of("");

    this.rewardsDecemberItems = List.of("minecraft:firework_rocket:32");
    this.rewardsDecemberSpecialItems = List.of("minecraft:cake:1", "minecraft:cookie:1");
    this.rewardsDecemberSpecialUsers = List.of("");
  }
}
