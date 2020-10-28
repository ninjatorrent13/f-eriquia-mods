
package net.mcreator.mmorpg.item;

import net.minecraftforge.registries.ObjectHolder;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.IItemTier;

import net.mcreator.mmorpg.MmorpgModElements;

@MmorpgModElements.ModElement.Tag
public class EpeecommunItem extends MmorpgModElements.ModElement {
	@ObjectHolder("mmorpg:epeecommun")
	public static final Item block = null;
	public EpeecommunItem(MmorpgModElements instance) {
		super(instance, 5);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new SwordItem(new IItemTier() {
			public int getMaxUses() {
				return 700;
			}

			public float getEfficiency() {
				return 7.5f;
			}

			public float getAttackDamage() {
				return 3.3999999999999995f;
			}

			public int getHarvestLevel() {
				return 1;
			}

			public int getEnchantability() {
				return 12;
			}

			public Ingredient getRepairMaterial() {
				return Ingredient.EMPTY;
			}
		}, 3, -2.5f, new Item.Properties().group(ItemGroup.TOOLS)) {
		}.setRegistryName("epeecommun"));
	}
}
