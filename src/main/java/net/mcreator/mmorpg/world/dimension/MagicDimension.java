
package net.mcreator.mmorpg.world.dimension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

@MmorpgModElements.ModElement.Tag
public class MagicDimension extends MmorpgModElements.ModElement {

	@ObjectHolder("mmorpg:magic")
	public static final ModDimension dimension = null;

	@ObjectHolder("mmorpg:magic_portal")
	public static final CustomPortalBlock portal = null;

	public static DimensionType type = null;

	private static Biome[] dimensionBiomes;

	public MagicDimension(MmorpgModElements instance) {
		super(instance, 10);

		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void registerDimension(RegistryEvent.Register<ModDimension> event) {
		event.getRegistry().register(new CustomModDimension().setRegistryName("magic"));
	}

	@SubscribeEvent
	public void onRegisterDimensionsEvent(RegisterDimensionsEvent event) {
		if (DimensionType.byName(new ResourceLocation("mmorpg:magic")) == null) {
			DimensionManager.registerDimension(new ResourceLocation("mmorpg:magic"), dimension, null, false);
		}

		type = DimensionType.byName(new ResourceLocation("mmorpg:magic"));
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		dimensionBiomes = new Biome[]{ForgeRegistries.BIOMES.getValue(new ResourceLocation("mmorpg:magicbiome")),};
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new CustomPortalBlock());
		elements.items.add(() -> new MagicItem().setRegistryName("magic"));
	}

	public static class CustomPortalBlock extends NetherPortalBlock {

		public CustomPortalBlock() {
			super(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().tickRandomly().hardnessAndResistance(-1.0F).sound(SoundType.GLASS)
					.lightValue(0).noDrops());
			setRegistryName("magic_portal");
		}

		@Override
		public void tick(BlockState state, World world, BlockPos pos, Random random) {
		}

		public void portalSpawn(World world, BlockPos pos) {
			CustomPortalBlock.Size portalsize = this.isValid(world, pos);
			if (portalsize != null)
				portalsize.placePortalBlocks();
		}

		/* failed to load code for net.minecraft.block.NetherPortalBlock */

		@Override /* failed to load code for net.minecraft.block.NetherPortalBlock */

		@Override /* failed to load code for net.minecraft.block.NetherPortalBlock */

		@OnlyIn(Dist.CLIENT)
		@Override
		public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
			for (int i = 0; i < 4; i++) {
				double px = pos.getX() + random.nextFloat();
				double py = pos.getY() + random.nextFloat();
				double pz = pos.getZ() + random.nextFloat();
				double vx = (random.nextFloat() - 0.5) / 2f;
				double vy = (random.nextFloat() - 0.5) / 2f;
				double vz = (random.nextFloat() - 0.5) / 2f;
				int j = random.nextInt(4) - 1;
				if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
					px = pos.getX() + 0.5 + 0.25 * j;
					vx = random.nextFloat() * 2 * j;
				} else {
					pz = pos.getZ() + 0.5 + 0.25 * j;
					vz = random.nextFloat() * 2 * j;
				}
				world.addParticle(ParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
			}

			if (random.nextInt(110) == 0)
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						(net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(("block.portal.ambient"))),
						SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4F + 0.8F, false);
		}

		public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
			if (!world.isRemote && !entity.isPassenger() && !entity.isBeingRidden() && entity instanceof ServerPlayerEntity && true) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				if (player.timeUntilPortal > 0) {
					player.timeUntilPortal = 10;
				} else if (player.dimension != type) {
					player.timeUntilPortal = 10;
					teleportToDimension(player, type);
				} else {
					player.timeUntilPortal = 10;
					teleportToDimension(player, DimensionType.OVERWORLD);
				}

			}
		}

		private void teleportToDimension(ServerPlayerEntity player, DimensionType destinationType) {
			ObfuscationReflectionHelper.setPrivateValue(ServerPlayerEntity.class, player, true, "field_184851_cj");

			ServerWorld nextWorld = player.getServer().getWorld(destinationType);

			TeleporterDimensionMod teleporter = getTeleporterForDimension(player, player.getPosition(), nextWorld);

			player.connection.sendPacket(new SChangeGameStatePacket(4, 0));

			if (!teleporter.func_222268_a(player, player.rotationYaw)) {
				teleporter.makePortal(player);
				teleporter.func_222268_a(player, player.rotationYaw);
			}

			player.teleport(nextWorld, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), player.rotationYaw,
					player.rotationPitch);

			player.connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
			for (EffectInstance effectinstance : player.getActivePotionEffects()) {
				player.connection.sendPacket(new SPlayEntityEffectPacket(player.getEntityId(), effectinstance));
			}

			player.connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
		}

		private TeleporterDimensionMod getTeleporterForDimension(Entity entity, BlockPos pos, ServerWorld nextWorld) {
			BlockPattern.PatternHelper bph = portal.createPatternHelper(entity.world, new BlockPos(pos));
			double d0 = bph.getForwards().getAxis() == Direction.Axis.X
					? (double) bph.getFrontTopLeft().getZ()
					: (double) bph.getFrontTopLeft().getX();
			double d1 = bph.getForwards().getAxis() == Direction.Axis.X ? entity.posZ : entity.posX;
			d1 = Math.abs(MathHelper.pct(d1 - (double) (bph.getForwards().rotateY().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0),
					d0, d0 - (double) bph.getWidth()));
			double d2 = MathHelper.pct(entity.posY - 1, (double) bph.getFrontTopLeft().getY(),
					(double) (bph.getFrontTopLeft().getY() - bph.getHeight()));
			return new TeleporterDimensionMod(nextWorld, new Vec3d(d1, d2, 0), bph.getForwards());
		}

	public static class Size /* failed to load code for net.minecraft.block.NetherPortalBlock */

}

		public static class TeleporterDimensionMod extends Teleporter {

			private static final Logger LOGGER = LogManager.getLogger();

			private Vec3d lastPortalVec;
			private Direction teleportDirection;

			protected final ServerWorld world;
			protected final Random random;
			protected final Map<ColumnPos, TeleporterDimensionMod.PortalPosition> destinationCoordinateCache = Maps.newHashMapWithExpectedSize(4096);
			private final Object2LongMap<ColumnPos> field_222275_f = new Object2LongOpenHashMap();

			public TeleporterDimensionMod(ServerWorld worldServer, Vec3d lastPortalVec, Direction teleportDirection) {
				super(worldServer);

				this.world = worldServer;
				this.random = new Random(worldServer.getSeed());
				this.lastPortalVec = lastPortalVec;
				this.teleportDirection = teleportDirection;

				worldServer.customTeleporters.add(this);
			}

	@Override /* failed to load code for net.minecraft.world.Teleporter */

	@Override /* failed to load code for net.minecraft.world.Teleporter */

	@Override /* failed to load code for net.minecraft.world.Teleporter */

	public static class PortalPosition /* failed to load code for net.minecraft.world.Teleporter */

}

			public static class CustomModDimension extends ModDimension {

				@Override
				public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
					return CustomDimension::new;
				}

			}

	public static class CustomDimension extends Dimension {

		private BiomeProviderCustom biomeProviderCustom = null;

		public CustomDimension(World world, DimensionType type) {
			super(world, type);
			this.nether = false;
		}


		@Override @OnlyIn(Dist.CLIENT) public Vec3d getFogColor(float cangle, float ticks) {
			return new Vec3d(1,0.6,1);
		}

		@Override public ChunkGenerator<?> createChunkGenerator() {
			if(this.biomeProviderCustom == null) {
				this.biomeProviderCustom = new BiomeProviderCustom(this.world);
			}
			return new ChunkProviderModded(this.world, this.biomeProviderCustom);
		}

		@Override public boolean isSurfaceWorld() {
			return false;
		}

		@Override public boolean canRespawnHere() {
			return false;
		}

		@OnlyIn(Dist.CLIENT) @Override public boolean doesXZShowFog(int x, int z) {
			return false;
		}

		@Override public SleepResult canSleepAt(PlayerEntity player, BlockPos pos){
        	return SleepResult.ALLOW;
		}

		@Nullable public BlockPos findSpawn(ChunkPos chunkPos, boolean checkValid) {
   		   return null;
   		}

   		@Nullable public BlockPos findSpawn(int x, int z, boolean checkValid) {
   		   return null;
   		}

		@Override protected void generateLightBrightnessTable() {
			float f = 0.5f;
			for (int i = 0; i <= 15; ++i) {
				float f1 = 1 - (float) i / 15f;
				this.lightBrightnessTable[i] = (1 - f1) / (f1 * 3 + 1) * (1 - f) + f;
			}
		}

		@Override public boolean doesWaterVaporize() {
      		return false;
   		}

		@Override /* failed to load code for net.minecraft.world.dimension.OverworldDimension */

	}

			public static class ChunkProviderModded extends OverworldChunkGenerator {

				public ChunkProviderModded(IWorld world, BiomeProvider provider) {
					super(world, provider, new OverworldGenSettings() {
						public BlockState getDefaultBlock() {
							return DirtmagicBlock.block.getDefaultState();
						}

						public BlockState getDefaultFluid() {
							return LiquideroseBlock.block.getDefaultState();
						}
					});
					this.randomSeed.skip(5349);
				}

				@Override
				public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
				}

			}

			public static class BiomeLayerCustom implements IC0Transformer {

				@Override
				public int apply(INoiseRandom context, int value) {
					return Registry.BIOME.getId(dimensionBiomes[context.random(dimensionBiomes.length)]);
				}

			}

public static class BiomeProviderCustom extends BiomeProvider {

	private final Layer genBiomes;
	private final Layer biomeFactoryLayer;
	private final Biome[] biomes;

	private static boolean biomesPatched = false;

	public BiomeProviderCustom(World world) {
		Layer[] aLayer = makeTheWorld(world.getSeed());
		this.genBiomes = aLayer[0];
		this.biomeFactoryLayer = aLayer[1];
		this.biomes = dimensionBiomes;


		if(!biomesPatched) {
			for (Biome biome : this.biomes) {
				biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(new CaveWorldCarver(ProbabilityConfig::deserialize, 256) {

					{
						carvableBlocks = ImmutableSet.of(
							DirtmagicBlock.block.getDefaultState().getBlock(),
							biome.getSurfaceBuilder().getConfig().getTop().getBlock(),
							biome.getSurfaceBuilder().getConfig().getUnder().getBlock()
						);
					}

				}, new ProbabilityConfig(0.14285715f)));
			}
			biomesPatched = true;
		}
	}

	private Layer[] makeTheWorld(long seed) {
		LongFunction<IExtendedNoiseRandom<LazyArea>> contextFactory = l -> new LazyAreaLayerContext(25, seed, l);

		IAreaFactory<LazyArea> parentLayer = IslandLayer.INSTANCE.apply(contextFactory.apply(1));
		IAreaFactory<LazyArea> biomeLayer = (new BiomeLayerCustom()).apply(contextFactory.apply(200), parentLayer);

		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1000), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1002), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1004), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1005), biomeLayer);

		IAreaFactory<LazyArea> voronoizoom = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(10), biomeLayer);

		return new Layer[] { new Layer(biomeLayer), new Layer(voronoizoom) };
	}

	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */
	@Override /* failed to load code for net.minecraft.world.biome.provider.OverworldBiomeProvider */


}

}
