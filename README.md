# Daily Rewards

Daily rewards is a very lightweight and simple mod that rewards your players daily by simply running a command.

## Internal Data Structure

This section covers the internal data structure used inside the corresponding .dat files.

### Reward Data (daily_rewards.dat)

The reward data are separated by year month and will be calculated at the beginning of the month based on the provided config.

Data structure:

- - Year - Month
  - Rewards 1-31 [ItemStacks ...]

### Reward User Data (daily_rewards_user.dat)

The reward user data tracking the given rewards to the user, they have no direct relationship to the reward data to make sure updates will not make former rewards invalid and only relevant for further items.

Data structure:

- - Year - Month
    - UUID
      - Awards 1-31 (takeable) [ItemStacks ...]
      - Awards History 1-31 (locked) [ItemStacks ...]
      - Rewarded days [int]
      - Rewards days [Dates ...]
