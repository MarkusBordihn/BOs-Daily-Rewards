package de.markusbordihn.dailyrewards.config;

public class ModConfigs {

  public static CommonConfig COMMON;

  public static void registerConfigs() {
    COMMON = new CommonConfig().read();
  }
}
