package cursedflames.simplehearthud.client;

import java.util.Random;

import cursedflames.simplehearthud.ModConfig;
import cursedflames.simplehearthud.SHH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtraHeartRenderHandler {
	private static final ResourceLocation ICON_HEARTS = new ResourceLocation(SHH.MODID,
			"textures/gui/hearts.png");
	private static final ResourceLocation ICON_ABSORB = new ResourceLocation(SHH.MODID,
			"textures/gui/absorb.png");
	private static final ResourceLocation ICON_VANILLA = Gui.ICONS;

	private final Minecraft mc = Minecraft.getMinecraft();

	private int updateCounter = 0;
	private int playerHealth = 0;
	private int lastPlayerHealth = 0;
	private long healthUpdateCounter = 0;
	private long lastSystemTime = 0;
	private Random rand = new Random();

	private int height;
	private int width;
	private int regen;

	private static int left_height = 39;

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width,
			int height) {
		Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width,
				height);
	}

	private int[] offset = new int[10];

	/* HUD */
	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderHud(RenderGameOverlayEvent.Pre event) {
		if (event.getType()!=RenderGameOverlayEvent.ElementType.HEALTH||event.isCanceled()) {
			return;
		}
		// extra setup stuff from us
		left_height = 39;
		ScaledResolution resolution = event.getResolution();
		width = resolution.getScaledWidth();
		height = resolution.getScaledHeight();
		event.setCanceled(true);
		updateCounter = mc.ingameGUI.getUpdateCounter();

		// start default forge/mc rendering
		// changes are indicated by comment
		mc.mcProfiler.startSection("health");
		GlStateManager.enableBlend();

		EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
		int health = MathHelper.ceil(player.getHealth());
		boolean highlight = healthUpdateCounter>(long) updateCounter
				&&(healthUpdateCounter-(long) updateCounter)/3L%2L==1L;

		if (health<this.playerHealth&&player.hurtResistantTime>0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter+20);
		} else if (health>this.playerHealth&&player.hurtResistantTime>0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter+10);
		}

		if (Minecraft.getSystemTime()-this.lastSystemTime>1000L) {
			this.playerHealth = health;
			this.lastPlayerHealth = health;
			this.lastSystemTime = Minecraft.getSystemTime();
		}

		this.playerHealth = health;
		int healthLast = this.lastPlayerHealth;

		IAttributeInstance attrMaxHealth = player
				.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		float healthMax = (float) attrMaxHealth.getAttributeValue();
		float absorb = MathHelper.ceil(player.getAbsorptionAmount());

		// CHANGE: simulate 10 hearts max if there's more, so vanilla only
		// renders one row max
		healthMax = Math.min(healthMax, 20f);
		health = Math.min(health, 20);
		absorb = Math.min(absorb, 20);

		int healthRows = MathHelper.ceil((healthMax+absorb)/2.0F/10.0F);
		int rowHeight = Math.max(10-(healthRows-2), 3);

		this.rand.setSeed((long) (updateCounter*312871));

		int left = width/2-91;
		int top = height-left_height;
		left_height += (healthRows*rowHeight);
		if (rowHeight!=10)
			left_height += 10-rowHeight;

		regen = -1;
		if (player.isPotionActive(MobEffects.REGENERATION)) {
			regen = updateCounter%25;
		}

		final int TOP = 9*(mc.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
		final int BACKGROUND = (highlight ? 25 : 16);

		for (int i = MathHelper.ceil((healthMax+absorb)/2.0F)-1; i>=0; --i) {
			// int b0 = (highlight ? 1 : 0);
			int row = MathHelper.ceil((float) (i+1)/10.0F)-1;
			int x = left+i%10*8;
			int y = top-row*rowHeight;

			if (health<=4&&i<10) {
				offset[i] = rand.nextInt(2);
				y += offset[i];
			}
			if (i==regen)
				y -= 2;

			drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
		}

		renderExtraHearts(left, top, player);
		renderExtraAbsorption(left, top-rowHeight, player);

		this.mc.getTextureManager().bindTexture(ICON_VANILLA);
		GuiIngameForge.left_height += 10;
		if (absorb>0) {
			GuiIngameForge.left_height += 10;
		}

		event.setCanceled(true);

		GlStateManager.disableBlend();
		mc.mcProfiler.endSection();
	}

	private void renderExtraHearts(int xBasePos, int yBasePos, EntityPlayer player) {
		int potionOffset = getPotionOffset(player);

		// Extra hearts
		this.mc.getTextureManager().bindTexture(ICON_HEARTS);
		IAttributeInstance attrMaxHealth = player
				.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		float healthMax = (float) attrMaxHealth.getAttributeValue();
		int hp = Math.min(MathHelper.ceil(player.getHealth()*2), (int) Math.ceil(healthMax*2));
		renderCustomHearts(xBasePos, yBasePos, potionOffset, hp, false);
	}

	private void renderCustomHearts(int xBasePos, int yBasePos, int potionOffset, int count,
			boolean absorb) {
		int regenOffset = absorb ? 10 : 0;
		for (int iter = 0; iter-1<count/40; iter++) {
			int renderHearts = (count-40*(iter))/4;
			int heartIndex = iter%11;
			if (renderHearts>10) {
				renderHearts = 10;
			}
			for (int i = 0; i<renderHearts; i++) {
				int y = getYRegenOffset(i, regenOffset);
				if (count<=8)
					y += offset[i];
				if (absorb) {
					this.drawTexturedModalRect(xBasePos+8*i, yBasePos+y, 0, 54, 9, 9);
				}
				this.drawTexturedModalRect(xBasePos+8*i, yBasePos+y, 0+18*heartIndex, potionOffset,
						9, 9);
			}
			if (count%4!=0&&renderHearts<10) {
				int y = getYRegenOffset(renderHearts, regenOffset);
				if (count<=8)
					y += offset[renderHearts];
				if (absorb&&count<40) {
					this.drawTexturedModalRect(xBasePos+8*renderHearts, yBasePos+y, 0, 54, 9, 9);
				}
				int xOff = ((count%4)<3) ? 9 : 0;
				int yOff = (ModConfig.quarterHearts.getBoolean(false)&&(count%2)==1)
						? (absorb ? 63 : 54) : 0;
				this.drawTexturedModalRect(xBasePos+8*renderHearts, yBasePos+y, xOff+18*heartIndex,
						potionOffset+yOff, 9, 9);
			}
		}
	}

	private int getYRegenOffset(int i, int offset) {
		return i+offset==regen ? -2 : 0;
	}

	private int getPotionOffset(EntityPlayer player) {
		int potionOffset = 0;
		PotionEffect potion = player.getActivePotionEffect(MobEffects.WITHER);
		if (potion!=null) {
			potionOffset = 18;
		}
		potion = player.getActivePotionEffect(MobEffects.POISON);
		if (potion!=null) {
			potionOffset = 9;
		}
		if (mc.world.getWorldInfo().isHardcoreModeEnabled()) {
			potionOffset += 27;
		}
		return potionOffset;
	}

	private void renderExtraAbsorption(int xBasePos, int yBasePos, EntityPlayer player) {
		int potionOffset = getPotionOffset(player);

		// Extra hearts
		this.mc.getTextureManager().bindTexture(ICON_ABSORB);

		int absorb = MathHelper.ceil(player.getAbsorptionAmount()*2);
		renderCustomHearts(xBasePos, yBasePos, potionOffset, absorb, true);
	}
}
