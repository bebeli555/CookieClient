package me.bebeli555.cookieclient.mods.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.render.RenderEntityNameEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.utils.ItemUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class NameTags extends Mod {
	private SPacketEntityStatus lastPacket;
	private int count;
	private HashMap<String, Integer> pops = new HashMap<String, Integer>();
	
	public static Setting scaling = new Setting(Mode.DOUBLE, "Scaling", 0.0, "How much bigger the nametags are rendered", "Higher = bigger");
	public static Setting yAdd = new Setting(Mode.DOUBLE, "YAdd", 0.0, "How much to add to the rendered y position", "Lower = lower");
	public static Setting scaleBigger = new Setting(Mode.BOOLEAN, "Scale", true, "Scales it bigger when u move further away from the player");
		public static Setting scaleBiggerAmount = new Setting(scaleBigger, Mode.INTEGER, "Amount", 325, "Lower = Scale gets more bigger when moving further away");
		public static Setting minScaleAmount = new Setting(scaleBigger, Mode.DOUBLE, "MinScale", 0.025);
	public static Setting armor = new Setting(Mode.BOOLEAN, "Armor", true, "Shows their armor");
		public static Setting enchantments = new Setting(armor, Mode.BOOLEAN, "Enchantments", true, "Shows what enchantments they got on", "Their armor and item");
		public static Setting durability = new Setting(armor, Mode.BOOLEAN, "Durability", true, "Shows their armor durability");
	public static Setting itemName = new Setting(Mode.BOOLEAN, "ItemName", true, "Shows the name of the item the", "Player has in their main hand");
	public static Setting health = new Setting(Mode.BOOLEAN, "Health", true, "Shows their health + absortion");
	public static Setting ping = new Setting(Mode.BOOLEAN, "Ping", true, "Shows their ping");
	public static Setting popCounter = new Setting(Mode.BOOLEAN, "PopCounter", true, "Displays how many times the people have", "Popped a totem next to their health");
		public static Setting popCounterReset = new Setting(popCounter, Mode.BOOLEAN, "Reset", true, "Resets the counter when the entity", "Goes out of render distance");
		
	public NameTags() {
		super(Group.RENDER, "NameTags", "Renders useful information about the player", "Above them");
	}
    
    @Override
    public void onRenderWorld(float partialTicks) {
        for (Entity entity : mc.world.loadedEntityList) {
        	if (entity instanceof EntityPlayer && !entity.equals(mc.renderViewEntity)) {                
        		renderNameTag((EntityPlayer)entity, partialTicks);
        	}
        }
	}
    
	public void renderNameTag(EntityPlayer player, float partialTicks) {	
		if (mc.getRenderManager().options == null || player.isDead || player.getHealth() + player.getAbsorptionAmount() == 0) {
			return;
		}
		
        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        double scale = 0;
        double minScale = minScaleAmount.doubleValue();
        if (scaleBigger.booleanValue()) {
            Vec3d cameraPos = Tracers.interpolateEntity(mc.renderViewEntity, partialTicks);
            Vec3d playerPos = Tracers.interpolateEntity(player, partialTicks);
            scale = (cameraPos.distanceTo(playerPos) / scaleBiggerAmount.intValue());
        } else {
        	scale = minScale;
        }
        if (scale < minScale) {
        	scale = minScale;
        }
    	scale += scaling.doubleValue();
		double playerX = (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - mc.getRenderManager().renderPosX);
		double playerY = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - mc.getRenderManager().renderPosY);
		double playerZ = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - mc.getRenderManager().renderPosZ);
        float viewerYaw = mc.getRenderManager().playerViewY;
        float viewerPitch = mc.getRenderManager().playerViewX;
        if (player.isSneaking()) {
    		playerY += 1.8 + yAdd.doubleValue();
        } else {
    		playerY += 2.1 + yAdd.doubleValue();
        }
    	
        GlStateManager.pushMatrix();
    	GlStateManager.translate(playerX, playerY, playerZ);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, -scale);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
		//Name
		String name = StringUtils.stripControlCodes(player.getName());
		if (Friends.isFriend(player)) {
			name = ChatFormatting.AQUA + name;
		}

		//Ping
		int responseTime = -1;

		if (ping.booleanValue()) {
			try {
				responseTime = mc.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime();
			} catch (NullPointerException e2) {

			}
		}

		//Health
		String playerName = name;
		if (ping.booleanValue() && responseTime != -1) playerName += " " + responseTime + "ms";
		if (health.booleanValue()) {
			int health = (int)(player.getHealth() + player.getAbsorptionAmount());
			ChatFormatting color2 = ChatFormatting.RED;
			if (health >= 20) {
				color2 = ChatFormatting.GREEN;
			} else if (health >= 14) {
				color2 = ChatFormatting.YELLOW;
			} else if (health >= 7) {
				color2 = ChatFormatting.GOLD;
			}
			
			playerName += color2 + " " + health;
		}
		
		//Totem pop counter
		if (popCounter.booleanValue() && pops.containsKey(name)) {
			int amount = pops.get(name);
			if (amount != 0) {
				ChatFormatting color = ChatFormatting.RED;
				if (amount < 4) {
					color = ChatFormatting.GREEN;
				} else if (amount < 8) {
					color = ChatFormatting.YELLOW;
				}
				
				playerName += " " + color + -amount;
			}
		}

		mc.fontRenderer.drawStringWithShadow(playerName, -mc.fontRenderer.getStringWidth(playerName) / 2, -9, -1);
		
		boolean renderedArmor = false;
		if (armor.booleanValue()) {
			Iterator<ItemStack> items = player.getArmorInventoryList().iterator();
			ArrayList<ItemStack> stacks = new ArrayList<>();

			stacks.add(player.getHeldItemOffhand());

			while (items.hasNext()) {
				final ItemStack stack = items.next();
				if (stack != null && stack.getItem() != Items.AIR) {
					stacks.add(stack);
				}
			}

			stacks.add(player.getHeldItemMainhand());
			Collections.reverse(stacks);
			int x = 0;

			for (ItemStack stack : stacks) {
				if (stack != null) {
					Item item = stack.getItem();
					if (item != Items.AIR) {
						//Render items and armor
						if (item instanceof ItemBlock) {
	                        GlStateManager.pushMatrix();
	                        GlStateManager.enableBlend();
	                        GlStateManager.disableDepth();
	                        RenderHelper.enableStandardItemLighting();
						} else {
					        GlStateManager.pushMatrix();
					        GlStateManager.depthMask((boolean)true);
					        GlStateManager.clear((int)256);
					        RenderHelper.enableStandardItemLighting();
					        mc.getRenderItem().zLevel = -150.0f;
					        GlStateManager.disableAlpha();
					        GlStateManager.enableDepth();
					        GlStateManager.disableCull();
						}
						//Oh man it took like forever to figure out this right scale for this.
						//It took more than time. Even a few brain cells were lost
						double itemScale = (double)mc.fontRenderer.FONT_HEIGHT / (double)9;
						GlStateManager.scale(itemScale, itemScale, 0);
						GlStateManager.translate(x - (16 * stacks.size() / 2), -mc.fontRenderer.FONT_HEIGHT - 23, 0);
						
						mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
						mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
						
						if (item instanceof ItemBlock) {
	                        RenderHelper.disableStandardItemLighting();
	                        GlStateManager.enableDepth();
	                        GlStateManager.disableBlend();
	                        GlStateManager.popMatrix();
						} else {
					        mc.getRenderItem().zLevel = 0.0f;
					        RenderHelper.disableStandardItemLighting();
					        GlStateManager.enableCull();
					        GlStateManager.enableAlpha();
					        GlStateManager.disableDepth();
					        GlStateManager.enableDepth();
					        GlStateManager.popMatrix();
						}
				        
						x += 16;
						renderedArmor = true;

						if (enchantments.booleanValue()) {
							int y = -2;
							ArrayList<String> stringsToDraw = new ArrayList<String>();

							if (stack.getEnchantmentTagList() != null) {
								NBTTagList tags = stack.getEnchantmentTagList();
								
								int count = 0;
								for (int i = 0; i < tags.tagCount(); i++) {
									if (count >= 5) {
										break;
									}
									
									NBTTagCompound tagCompound = tags.getCompoundTagAt(i);
									if (tagCompound != null && Enchantment.getEnchantmentByID(tagCompound.getByte("id")) != null) {
										Enchantment enchantment = Enchantment .getEnchantmentByID(tagCompound.getShort("id"));
										short lvl = tagCompound.getShort("lvl");
										
										if (enchantment != null) {
											if (enchantment.isCurse()) {
												continue;
											}
											
											String ench = "";
											if (lvl == 1) {
												ench = enchantment.getTranslatedName(lvl).substring(0, 3);
											} else {
												ench = enchantment.getTranslatedName(lvl).substring(0, 2) + lvl;
											}
											
											stringsToDraw.add(ench);
											count++;
										}
									}
								}
							}

							for (String string : stringsToDraw) {
								GlStateManager.pushMatrix();
								GlStateManager.disableDepth();
								GlStateManager.translate(x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (mc.fontRenderer.getStringWidth(string) / 4.0f), -mc.fontRenderer.FONT_HEIGHT - 23 - y, 0);
								GlStateManager.scale(0.5f, 0.5f, 0.5f);
								mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
								GlStateManager.scale(2, 2, 2);
								GlStateManager.enableDepth();
								GlStateManager.popMatrix();
								y += -4;
							}
						}
						
						if (durability.booleanValue()) {
							if (stack.getMaxDamage() != 0) {
								String string = "" + ItemUtil.getDurabilityColor(stack) + ItemUtil.getPercentageDurability(stack) + "%";
								GlStateManager.pushMatrix();
								GlStateManager.disableDepth();
								GlStateManager.translate(x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (mc.fontRenderer.getStringWidth(string) / 4.0f), -mc.fontRenderer.FONT_HEIGHT - 26, 0);
								GlStateManager.scale(0.5f, 0.5f, 0.5f);
								mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
								GlStateManager.scale(2, 2, 2);
								GlStateManager.enableDepth();
								GlStateManager.popMatrix();	
							}
						}
					}
				}
			}
		}
		
		if (itemName.booleanValue() && player.getHeldItemMainhand().getItem() != Items.AIR) {
			int y = 31;
			if (!renderedArmor)  {
				y = 5;
			}
			
			String string = player.getHeldItemMainhand().getDisplayName();
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.translate(-(mc.fontRenderer.getStringWidth(string) / 4.0f), -mc.fontRenderer.FONT_HEIGHT - y, 0);
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
			GlStateManager.scale(2, 2, 2);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();	
		}
        
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
		if (event.packet instanceof SPacketEntityStatus && mc.world != null) {
			SPacketEntityStatus packet = (SPacketEntityStatus)event.packet;
			
			if (packet.getOpCode() == 35 && !packet.equals(lastPacket)) {
				int amount = 1;
				Entity entity = packet.getEntity(mc.world);
				if (entity == null) {
					return;
				}
				
				if (pops.containsKey(entity.getName())) {
					amount += pops.get(entity.getName());
				}
				
				lastPacket = packet;
				pops.put(entity.getName(), amount);
			}
		}
	});
	
    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
    	if (popCounterReset.booleanValue() && mc.player != null) {
        	count++;
        	if (count >= 30) {
        		count = 0;
        		
        		for (String name : pops.keySet()) {
        			if (mc.world != null && mc.world.getPlayerEntityByName(name) == null || mc.world.getPlayerEntityByName(name).isDead) {
        				pops.put(name, 0);
        			}
        		}
        	}
    	}
    }
    
    @EventHandler
    private Listener<RenderEntityNameEvent> onRenderEntityName = new Listener<>(event -> {
        event.cancel();
    });
}
