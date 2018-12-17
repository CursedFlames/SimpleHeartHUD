package cursedflames.simplehearthud.client;

import cursedflames.simplehearthud.ModConfig;
import cursedflames.simplehearthud.SHH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorRenderHandler {
	private static final ResourceLocation ICON_ARMOR = new ResourceLocation(SHH.MODID,
			"textures/gui/armor.png");
	private static final ResourceLocation ICON_VANILLA = Gui.ICONS;

	private final Minecraft mc = Minecraft.getMinecraft();

//	private Random rand = new Random();

	private int height;
	private int width;

	private static int left_height = 39;

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width,
			int height) {
		Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width,
				height);
	}

//	private int[] offset = new int[10];

	/* HUD */
	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderHud(RenderGameOverlayEvent.Pre event) {
		if (event.getType()!=RenderGameOverlayEvent.ElementType.ARMOR||event.isCanceled()) {
			return;
		}
		// extra setup stuff from us
		left_height = GuiIngameForge.left_height;
		ScaledResolution resolution = event.getResolution();
		width = resolution.getScaledWidth();
		height = resolution.getScaledHeight();
		event.setCanceled(true);
//		updateCounter = mc.ingameGUI.getUpdateCounter();

		// start default forge/mc rendering
		// changes are indicated by comment
		mc.mcProfiler.startSection("armor");
		GlStateManager.enableBlend();

		EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
		int armor = player.getTotalArmorValue();
//		SHH.logger.info(armor);
//		int rowHeight = Math.max(10-(1-2), 3);

//		this.rand.setSeed((long) (updateCounter*312871));

		int left = width/2-91;
		int top = height-left_height;
		left_height += 10;
		this.mc.getTextureManager().bindTexture(ICON_ARMOR);

		for (int i = 0;; ++i) {
			if (armor>0) {
				int x = left+i%10*8;
				int y = top;// -row*11;

				if (i*2+1<armor) {
					this.drawTexturedModalRect(x, y, 9, (int) (9+Math.floor(i/10F)*9), 9, 9);
				} else if (i*2+1==armor) {
					this.drawTexturedModalRect(x, y, 0, (int) (9+Math.floor(i/10F)*9), 9, 9);
				} else {
					if (i>=10)
						break;
					this.drawTexturedModalRect(x, y, 0, 0, 9, 9);
				}
			} else
				break;
		}

		if (ModConfig.armorToughness.getBoolean(false)) {
			int armorToughness = (int) player
					.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
					.getAttributeValue();
			if (armorToughness>0) {
				for (int i = 0;; ++i) {
					int x = left+i%10*8;
					int y = top;// -row*11;

					if (i*2+1<armorToughness) {
						this.drawTexturedModalRect(x, y, 27, (int) (9+Math.floor(i/10F)*9), 9, 9);
					} else if (i*2+1==armorToughness) {
						this.drawTexturedModalRect(x, y, 18, (int) (9+Math.floor(i/10F)*9), 9, 9);
					} else
						break;

				}
			}
		}
		this.mc.getTextureManager().bindTexture(ICON_VANILLA);
		GuiIngameForge.left_height += 10;

		event.setCanceled(true);

		GlStateManager.disableBlend();
		mc.mcProfiler.endSection();
	}
}
