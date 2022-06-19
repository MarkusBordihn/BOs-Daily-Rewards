# Daily Rewards

[![Daily Rewards Downloads](http://cf.way2muchnoise.eu/full_628798_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/daily-rewards)
[![Daily Rewards Versions](http://cf.way2muchnoise.eu/versions/Minecraft_628798_all.svg)](https://www.curseforge.com/minecraft/mc-mods/daily-rewards)

Daily rewards is a very lightweight and simple Forge mod that rewards players daily.

![Daily Rewards Screenshot](examples/daily_rewards_screen.png)

## Features

- Server and client friendly
- Easy Customization over configuration file
- Extra UI to collect rewarded items and to see upcoming rewards
- Grant daily rewards after some minutes online and not immediately
- Supports mod items

## Beta Note

This version is currently in beta and is used for testing.

## Report Issues

Please report issues over the issue link above.

## Internal Data Structure

This section covers the internal data structure used inside the corresponding .dat files.

### Reward Data (daily_rewards.dat)

The reward data are separated by year month and will be calculated at the beginning of the month based on the provided config.

Data structure:

- Rewards
  - Year - Month (key)
  - Rewards 1-31 [ItemStack ...]

### Reward User Data (daily_rewards_user.dat)

The reward user data tracking the given rewards to the user, they have no direct relationship to the reward data to make sure updates will not make former rewards invalid and are only relevant for further items.

Data structure:

- Rewards User
  - Year - Month - UUID (key)
  - Rewards 1-31 (takeable) [ItemStack ...]
  - Last rewarded day (String)
  - Number of rewarded days (int)

## Note

Please only download the mod from the official CurseForge page or with the official CurseForge launcher like:

🚀 <https://www.curseforge.com/minecraft/mc-mods/daily-rewards>

If you are downloading this mod from other sources we could not make sure that it works as expected or does not includes any unwanted modification (e.g. adware, malware, ...).
