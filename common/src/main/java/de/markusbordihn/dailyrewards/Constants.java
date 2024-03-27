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

package de.markusbordihn.dailyrewards;

import net.minecraft.resources.ResourceLocation;

public final class Constants {

  // General Mod definitions
  public static final String LOG_NAME = "Daily Rewards";
  public static final String LOG_ICON = "💰";
  public static final String LOG_ICON_NAME = LOG_ICON + " " + LOG_NAME;
  public static final String LOG_REGISTER_PREFIX = LOG_ICON + " Register Daily Rewards";
  public static final String MOD_COMMAND = "DailyRewards";
  public static final String MOD_ID = "daily_rewards";
  public static final String MOD_NAME = "Daily Rewards";
  public static final String MOD_URL = "https://www.curseforge.com/minecraft/mc-mods/daily-rewards";
  // Prefixes
  public static final String MINECRAFT_PREFIX = "minecraft";
  public static final String TEXT_PREFIX = "text.daily_rewards.";
  // Colors
  public static final int FONT_COLOR_BLACK = 0;
  public static final int FONT_COLOR_DARK_GREEN = 43520;
  public static final int FONT_COLOR_GRAY = 11184810;
  public static final int FONT_COLOR_GREEN = 5635925;
  public static final int FONT_COLOR_RED = 16733525;
  public static final int FONT_COLOR_WARNING = FONT_COLOR_RED;
  public static final int FONT_COLOR_YELLOW = 16777045;
  public static final int FONT_COLOR_WHITE = 16777215;
  // Textures
  public static final ResourceLocation TEXTURE_COMPACT_SCREEN =
      new ResourceLocation(Constants.MOD_ID, "textures/container/compact_screen.png");
  public static final ResourceLocation TEXTURE_COMPACT_SCREEN_COMBINED =
      new ResourceLocation(Constants.MOD_ID, "textures/container/compact_screen_combined.png");
  public static final ResourceLocation TEXTURE_GENERIC_54 =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/container/generic_54.png");
  public static final ResourceLocation TEXTURE_TABS =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/advancements/tabs.png");
  public static final ResourceLocation TEXTURE_ICONS =
      new ResourceLocation(Constants.MOD_ID, "textures/container/icons.png");
  public static final ResourceLocation TEXTURE_OVERVIEW_SCREEN =
      new ResourceLocation(Constants.MOD_ID, "textures/container/overview_screen.png");
  public static final ResourceLocation TEXTURE_SPECIAL_OVERVIEW_SCREEN =
      new ResourceLocation(Constants.MOD_ID, "textures/container/special_overview_screen.png");

  private Constants() {}
}
