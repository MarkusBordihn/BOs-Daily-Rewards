package de.markusbordihn.dailyrewards.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.block.ModBlocks;
import de.markusbordihn.dailyrewards.Annotations.TemplateEntryPoint;

public class ModItems {

  protected ModItems() {

  }

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  @TemplateEntryPoint("Register Items")

  @TemplateEntryPoint("Register Block Items")

  private static final String MINECRAFT_FORGE_TEMPLATE =
      "https://github.com/MarkusBordihn/minecraft-forge-template";
}
