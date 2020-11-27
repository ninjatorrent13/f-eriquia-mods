
package net.mcreator.mmorpg.block;

@MmorpgModElements.ModElement.Tag
public class LiquideroseBlock extends MmorpgModElements.ModElement {

	@ObjectHolder("mmorpg:liquiderose")
	public static final FlowingFluidBlock block = null;

	@ObjectHolder("mmorpg:liquiderose_bucket")
	public static final Item bucket = null;

	public static FlowingFluid flowing = null;
	public static FlowingFluid still = null;

	private ForgeFlowingFluid.Properties fluidproperties = null;

	public LiquideroseBlock(MmorpgModElements instance) {
		super(instance, 7);

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void registerFluids(RegistryEvent.Register<Fluid> event) {
		event.getRegistry().register(still);
		event.getRegistry().register(flowing);
	}

	@Override
	public void initElements() {
		fluidproperties = new ForgeFlowingFluid.Properties(() -> still, () -> flowing,
				FluidAttributes.builder(new ResourceLocation("mmorpg:blocks/liquiderose"), new ResourceLocation("mmorpg:blocks/liquiderose"))
						.luminosity(0).density(1000).viscosity(1000)).bucket(() -> bucket).block(() -> block);

		still = (FlowingFluid) new ForgeFlowingFluid.Source(fluidproperties).setRegistryName("liquiderose");
		flowing = (FlowingFluid) new ForgeFlowingFluid.Flowing(fluidproperties).setRegistryName("liquiderose_flowing");

		elements.blocks.add(() -> new FlowingFluidBlock(still, Block.Properties.create(Material.WATER)) {

		}.setRegistryName("liquiderose"));

		elements.items.add(() -> new BucketItem(still, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC))
				.setRegistryName("liquiderose_bucket"));
	}

}
