package cursedflames.simplehearthud;

import cursedflames.simplehearthud.Config.EnumPropSide;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
	public static Configuration configuration;

	public static Property extraHearts;
	public static Property quarterHearts;
	public static Property extraArmor;
	public static Property armorToughness;

	public static void initConfig() {
		extraHearts = SHH.config.addPropBoolean("extraHearts", "General",
				"If enabled, display extra hearts as different colors on top of the first 10 hearts.",
				true, EnumPropSide.CLIENT);
		quarterHearts = SHH.config
				.addPropBoolean("quarterHearts", "General",
						"If enabled, display quarter hearts instead of half hearts.\n"
								+"Requires colored extra hearts to be enabled",
						false, EnumPropSide.CLIENT);
		extraArmor = SHH.config.addPropBoolean("extraArmor", "General",
				"If enabled, display extra armor as different colors on top of the first 10 armor icons.",
				true, EnumPropSide.CLIENT);
		armorToughness = SHH.config
				.addPropBoolean("armorToughness", "General",
						"If enabled, display armor toughness as a border around armor point icons\n"
								+"Requires extra armor icons to be enabled",
						false, EnumPropSide.CLIENT);
	}
}
