package me.bebeli555.cookieclient.mods.render;

import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLineWidth;

import java.awt.Color;

import org.lwjgl.opengl.GL32;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.utils.EntityUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracers extends Mod {
	public static Setting lineWidth = new Setting(Mode.DOUBLE, "LineWidth", 0.5, "Width of the rendered lines");
	public static Setting alpha = new Setting(Mode.INTEGER, "Alpha", 150, "How bright the tracers are as alpha");
	public static Setting players = new Setting(Mode.BOOLEAN, "Players", true);
	public static Setting friends = new Setting(Mode.BOOLEAN, "Friends", true);
	public static Setting animals = new Setting(Mode.BOOLEAN, "Animals", false);
	public static Setting monsters = new Setting(Mode.BOOLEAN, "Monsters", false);
	public static Setting vehicles = new Setting(Mode.BOOLEAN, "Vehicles", false);
	public static Setting items = new Setting(Mode.BOOLEAN, "Items", false);
	public static Setting others = new Setting(Mode.BOOLEAN, "Others", false);
	
	public Tracers() {
		super(Group.RENDER, "Tracers", "Draws line to entities");
	}
	
	@SubscribeEvent
	public void onRender(RenderWorldLastEvent e) {
		if (mc.getRenderManager() == null) {
			return;
		}
		
		for (Entity entity : mc.world.loadedEntityList) {
			if (shouldRenderTracer(entity)) {
				Color c = getColor(entity);
				Vec3d entityPos = interpolateEntity(entity, e.getPartialTicks()).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
				entityPos = entityPos.add(0, entity.height / 2, 0);
				renderTracer(entityPos, c, (float)lineWidth.doubleValue(), e.getPartialTicks());
			}
		}
	}
	
	public static void renderTracer(Vec3d pos, Color c, float lineWidth, float partialTicks) {
		boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
        
		Vec3d pointer = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.renderViewEntity.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.renderViewEntity.rotationYaw));
		
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);
        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableDepth();
        glEnable(GL32.GL_DEPTH_CLAMP);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(pointer.x, pointer.y + mc.player.getEyeHeight(), pointer.z).color(c.getRed(), c.getGreen(), c.getBlue(), alpha.intValue()).endVertex();
        bufferbuilder.pos(pos.x, pos.y, pos.z).color(c.getRed(), c.getGreen(), c.getBlue(), alpha.intValue()).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableDepth();
        glDisable(GL32.GL_DEPTH_CLAMP);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        
        mc.gameSettings.viewBobbing = bobbing;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
	}
	
	//Checks if the tracers should be rendered for the given entity
    public static boolean shouldRenderTracer(Entity e) {
        if (!Freecam.isToggled && e.equals(mc.player)) {
            return false;
        }
        
        if (Freecam.isToggled && e.equals(mc.renderViewEntity)) {
        	return false;
        }
        
        if (e instanceof EntityPlayer) {
            if (Friends.isFriend(e)) {
            	return friends.booleanValue();
            } else {
            	return players.booleanValue();
            }
        }
            		
        if ((EntityUtil.isHostileMob(e) || EntityUtil.isNeutralMob(e))) {
            return monsters.booleanValue();
        }
            
        if (EntityUtil.isPassive(e)) {
            return animals.booleanValue();
        }
            
        if ((e instanceof EntityBoat || e instanceof EntityMinecart)) {
            return vehicles.booleanValue();
        }
        
        if (e instanceof EntityItem) {
            return items.booleanValue();
        }
        
        return others.booleanValue();
    }
    
    //Get color for the entity. Far away = green and then yellow and red and stuff
    public static Color getColor(Entity e) {
    	if (e instanceof EntityPlayer && Friends.isFriend(e)) {
    		return Color.CYAN;
    	}
    	
    	float distance = e.getDistance(mc.player);
    	if (distance <= 6) {
    		return Color.RED;
    	} else if (distance <= 23) {
    		return Color.ORANGE;
    	} else if (distance <= 45) {
    		return Color.YELLOW;
    	} else {
    		return Color.GREEN;
    	}
    }
    
    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
}
