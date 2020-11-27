package net.mcreator.mmorpg.procedures;

@MmorpgModElements.ModElement.Tag
public class AbonnezvousProcedure extends MmorpgModElements.ModElement {

	public AbonnezvousProcedure(MmorpgModElements instance) {
		super(instance, 13);

	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			if (!dependencies.containsKey("entity"))
				System.err.println("Failed to load dependency entity for procedure Abonnezvous!");
			return;
		}

		Entity entity = (Entity) dependencies.get("entity");

		if (entity instanceof PlayerEntity && !entity.world.isRemote) {
			((PlayerEntity) entity).sendStatusMessage(new StringTextComponent("abonnez vous \u00E0 cornysneeze"), (false));
		}
		if (entity instanceof PlayerEntity && !entity.world.isRemote) {
			((PlayerEntity) entity).sendStatusMessage(new StringTextComponent("https://www.youtube.com/channel/UCjKaj-3KsN4CegFw7-nVA-w"), (true));
		}
		if (entity instanceof PlayerEntity && !entity.world.isRemote) {
			((PlayerEntity) entity).sendStatusMessage(new StringTextComponent("https://www.youtube.com/channel/UCjKaj-3KsN4CegFw7-nVA-w"), (false));
		}

	}

}
