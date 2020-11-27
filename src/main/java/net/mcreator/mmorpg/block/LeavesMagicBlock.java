
package net.mcreator.mmorpg.block;

@MmorpgModElements.ModElement.Tag
public class LeavesMagicBlock extends MmorpgModElements.ModElement {

	@ObjectHolder("mmorpg:leaves_magic")
	public static final Block block = null;

	public LeavesMagicBlock(MmorpgModElements instance) {
		super(instance, 12);

	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new CustomBlock());
		elements.items
				.add(() -> new BlockItem(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(block.getRegistryName()));
	}

	public static class CustomBlock extends Block {

		public CustomBlock() {
			super(

					Block.Properties.create(Material.LEAVES).sound(SoundType.PLANT).hardnessAndResistance(1f, 10f).lightValue(0));

			setRegistryName("leaves_magic");
		}

		@Override
		public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if (!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, 1));
		}

	}

}
