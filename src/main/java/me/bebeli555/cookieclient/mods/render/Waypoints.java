package me.bebeli555.cookieclient.mods.render;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.GLUProjection;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Waypoints extends Mod {
	public static boolean loaded;
	public static ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	private static ArrayList<String> onlinePlayers = new ArrayList<String>();
	private static ArrayList<NamePos> loadedPlayers = new ArrayList<NamePos>();
	private static Timer timer = new Timer();
	
	public static Setting logoutSpots = new Setting(Mode.BOOLEAN, "LogoutSpots", true, "Sets a waypoint when someone logs off");
	public static Setting deathSpots = new Setting(Mode.BOOLEAN, "DeathSpots", true, "Sets a waypoint to ur death location");
	public static Setting tracers = new Setting(Mode.BOOLEAN, "Tracers", false, "Renders tracers to the waypoints");
		public static Setting tracersAlpha = new Setting(tracers, Mode.INTEGER, "Alpha", 200, "Rbg");
		public static Setting tracersWidth = new Setting(tracers, Mode.DOUBLE, "Width", 1.5, "Line width");
	public static Setting boundingBox = new Setting(Mode.BOOLEAN, "BoundingBox", true, "Renders a box around the waypoint");
		public static Setting size = new Setting(boundingBox, Mode.DOUBLE, "Size", 0.65, "Size in width of rendered box");
		public static Setting ySize = new Setting(boundingBox, Mode.DOUBLE, "YSize", 2, "YSize of the box");
		public static Setting width = new Setting(boundingBox, Mode.DOUBLE, "Width", 1, "How thicc the lines are");
		public static Setting red = new Setting(boundingBox, Mode.INTEGER, "Red", 66, "Rbg color");
		public static Setting green = new Setting(boundingBox, Mode.INTEGER, "Green", 245, "Rbg color");
		public static Setting blue = new Setting(boundingBox, Mode.INTEGER, "Blue", 218, "Rbg color");
		public static Setting alpha = new Setting(boundingBox, Mode.INTEGER, "Alpha", 180, "Rbg color");
	public static Setting name = new Setting(Mode.BOOLEAN, "Name", true, "Renders the name above the box");
	
	public Waypoints() {
		super(Group.RENDER, "Waypoints", "Renders waypoints", "You can add a waypoint with \"waypoint add name dimension\"", "And delete one with \"waypoint remove name\"", "Example: \"waypoint add test Overworld\" Default dimension is the one ur in", "Also you can specify the coords like \"waypoint add name x y z dimension\"", "Also \"waypoint list\" will give u a list of waypoints for current server", "The waypoints are per server");
	}
	
	@Override
	public void onEnabled() {
		//Loads the waypoints from file
		if (!loaded) {
			loaded = true;
			
			try {
				File file = new File(Settings.path + "/Waypoints.txt");
				if (file.exists()) {
					Scanner s = new Scanner(file);
					while(s.hasNextLine()) {
						String line = s.nextLine();
						if (!line.isEmpty()) {
							try {
								String[] split = line.split(",");
								Vec3d pos = new Vec3d(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
								Waypoint waypoint = new Waypoint(split[0], pos);
								waypoint.server = split[4];
								waypoint.originalDimension = Dimension.getDimensionFromId(Integer.parseInt(split[5]));
								waypoints.add(waypoint);
							} catch (Exception e) {
								System.out.println(NAME + " - Something is wrong with ur Waypoints.txt file. Couldnt load all waypoints");
								e.printStackTrace();
							}
						}
					}
					
					s.close();
				}
			} catch (Exception e) {
				System.out.println(NAME + " - Error loading Waypoints from file");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDisabled() {
		ArrayList<Waypoint> temp = new ArrayList<Waypoint>();
		temp.addAll(waypoints);
		
		for (Waypoint waypoint : temp) {
			if (waypoint.isTemp) {
				waypoints.remove(waypoint);
			}
		}
	}
	
	@Override
	public void onRenderWorld(float partialTicks) {
		for (Waypoint waypoint : waypoints) {
			if (!waypoint.server.equals(PlayerUtil.getServerIp()) || waypoint.originalDimension == Dimension.END && Dimension.getCurrentDimension() != Dimension.END) {
				continue;
			}
			
			//Render bounding box
			if (boundingBox.booleanValue()) {
				if (waypoint.getPos().distanceTo(mc.renderViewEntity.getPositionVector()) < 1000) {
					RenderUtil.drawBoundingBox(RenderUtil.getBB(waypoint.getPos(), size.doubleValue(), ySize.doubleValue()), (float)width.doubleValue(), red.intValue() / 255.0f, green.intValue() / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f);
				}
			}
			
			//Render tracer
			if (tracers.booleanValue()) {
				Vec3d vec = new Vec3d(waypoint.getPos().x, waypoint.getPos().y, waypoint.getPos().z).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
				Tracers.renderTracer(vec, new Color(82, 81, 79, tracersAlpha.intValue()), (float)tracersWidth.doubleValue(), partialTicks);
			}
			
			//Render name. This is rendered here if ur close and on the onRenderGameOverlay when ur far because the 3d text dissapears if it gets outside the render distance
			//Also its really hard to scale this to be the same scale so the other thing has allways same scale so its good
			if (name.booleanValue() && mc.getRenderManager().options != null && mc.renderViewEntity.getPositionVector().distanceTo(waypoint.getPos()) <= 15) {
		        double scale = 0.03;
				double posX = (waypoint.getPos().x + (0) * partialTicks - mc.getRenderManager().renderPosX) + size.doubleValue() / 2;
				double posY = (waypoint.getPos().y + (0) * partialTicks - mc.getRenderManager().renderPosY) + ySize.doubleValue() + 0.5;
				double posZ = (waypoint.getPos().z + (0) * partialTicks - mc.getRenderManager().renderPosZ) + size.doubleValue() / 2;
		        GlStateManager.pushMatrix();
		    	GlStateManager.translate(posX, posY, posZ);
		        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate((float) (mc.getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(-scale, -scale, -scale);
		        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		        GlStateManager.disableDepth();
		        
		        mc.fontRenderer.drawStringWithShadow(ChatFormatting.GRAY + waypoint.name, -mc.fontRenderer.getStringWidth(waypoint.name) / 2, 0, -1);
		        
		        GlStateManager.enableDepth();
		        GlStateManager.popMatrix();
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if (event.getType() == ElementType.TEXT) {
			GlStateManager.pushMatrix();
			float scale = 0.35f + (110 / mc.gameSettings.fovSetting) - 1;
			GlStateManager.scale(scale, scale, scale);
			
			for (Waypoint waypoint : waypoints) {
				if (mc.renderViewEntity.getPositionVector().distanceTo(waypoint.getPos()) > 15) {
					if (!waypoint.server.equals(PlayerUtil.getServerIp()) || waypoint.originalDimension == Dimension.END && Dimension.getCurrentDimension() != Dimension.END) {
						continue;
					}
					
			        float[] bounds = GLUProjection.convertBounds(waypoint.getPos().add(size.doubleValue() / 2, ySize.doubleValue() + 0.25, size.doubleValue() / 2), event.getPartialTicks(), event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());			        
			        if (bounds != null) {
				        for (int i = 0; i < bounds.length; i++) {
				        	bounds[i] = bounds[i] / scale;
				        }
			        	
				        String text = ChatFormatting.GRAY + waypoint.name + " " + (int)waypoint.getPos().distanceTo(mc.renderViewEntity.getPositionVector()) + "m";
			            mc.fontRenderer.drawStringWithShadow(text, bounds[0] + (bounds[2] - bounds[0]) / 2 - mc.fontRenderer.getStringWidth(text) / 2, bounds[1] + (bounds[3] - bounds[1]) - 8 - 1, -1);
			        }
				}
			}
			
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
		//Clear nametags
		onDisabled();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (logoutSpots.booleanValue() && mc.player != null && mc.player.ticksExisted > 20 && !mc.player.isDead && timer.hasPassed(500)) {
			timer.reset();
			onlinePlayers.clear();
			for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
				onlinePlayers.add(player.getGameProfile().getName());
			}
			
			for (NamePos player : loadedPlayers) {
				if (!onlinePlayers.contains(player.name)) {
					ArrayList<Waypoint> temp = new ArrayList<Waypoint>();
					temp.addAll(waypoints);
					for (Waypoint waypoint : temp) {
						if (waypoint.logoutName.equals(player.name)) {
							waypoints.remove(waypoint);
							break;
						}
					}
					
                	Waypoint waypoint = new Waypoint(player.name + "'s logout spot", player.pos, true);
                	waypoint.isTemp = true;
                	waypoint.logoutName = player.name;
                	waypoint.server = PlayerUtil.getServerIp();
                	waypoints.add(waypoint);
				}
			}
			
			//Delete them if the player is online
			ArrayList<Waypoint> remove = new ArrayList<Waypoint>();
			for (Waypoint waypoint : waypoints) {
				if (waypoint.isTemp && !waypoint.logoutName.isEmpty() && onlinePlayers.contains(waypoint.logoutName)) {
					remove.add(waypoint);
				}
			}
			waypoints.removeAll(remove);
			
			loadedPlayers.clear();
			for (EntityPlayer player : mc.world.playerEntities) {
				if (player.getEntityId() != -100) {
					loadedPlayers.add(new NamePos(player.getName(), new Vec3d(player.posX, player.posY, player.posZ)));
				}
			}
		}
	}
    
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if (mc.player != null && event.getEntity().equals(mc.player) && deathSpots.booleanValue()) {
			Waypoint waypoint = new Waypoint("Death spot", new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ));
			waypoint.isTemp = true;
			waypoint.server = PlayerUtil.getServerIp();
			waypoints.add(waypoint);
		}
	}
	
	//Adds a waypoint to list and file
	public static void addWaypoint(Waypoint waypoint) {
		waypoints.add(waypoint);
		updateFile();
	}
	
	//Removes a waypoint from the list and file
	public static void removeWaypoint(Waypoint waypoint) {
		waypoints.remove(waypoint);
		updateFile();
	}
	
	//Updates the file
	//Name,X,Y,Z,Server,DimensionID
	public static void updateFile() {
		try {
			File file = new File(Settings.path + "/Waypoints.txt");
			file.delete();
			file.createNewFile();
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (Waypoint waypoint : waypoints) {
				if (!waypoint.isTemp) {
					bw.write(waypoint.name + "," + waypoint.getPos().x + "," + waypoint.getPos().y + "," + waypoint.getPos().z + "," + waypoint.server + "," + waypoint.originalDimension.id);
					bw.newLine();
				}
			}
			
			bw.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error updating Waypoints file");
			e.printStackTrace();
		}
	}
	
	public static class Waypoint {
		private Vec3d originalPos;
		public Dimension originalDimension;
		public String name;
		public String logoutName = "";
		public String server = "";
		public boolean isTemp;
		
		public Waypoint(String name, Vec3d pos) {
			//Center pos
			this.originalPos = pos.add(-size.doubleValue() / 2, 0, -size.doubleValue() / 2);
			this.originalDimension = Dimension.getCurrentDimension();
			this.name = name;
		}
		
		public Vec3d getPos() {
			if (Dimension.getCurrentDimension() == Dimension.NETHER && originalDimension == Dimension.OVERWORLD) {
				return new Vec3d(originalPos.x / 8, originalPos.y, originalPos.z / 8);
			} else if (Dimension.getCurrentDimension() == Dimension.OVERWORLD && originalDimension == Dimension.NETHER) {
				return new Vec3d(originalPos.x * 8, originalPos.y, originalPos.z * 8);
			} else {
				return originalPos;
			}
		}
		
		public Vec3d getOriginalPos() {
			return originalPos;
		}
		
		public Waypoint(String name, Vec3d pos, boolean isTemp) {
			this(name, pos);
			this.isTemp = isTemp;
		}
		
		public static Waypoint getWaypointFromName(String name) {
			for (Waypoint waypoint : waypoints) {
				if (waypoint.name.equals(name)) {
					return waypoint;
				}
			}
			
			return null;
		}
	}
	
	public static enum Dimension {
		OVERWORLD(0, "Overworld"),
		NETHER(-1, "Nether"),
		END(1, "End");
		
		public int id;
		public String name;
		Dimension(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public static Dimension getCurrentDimension() {
			if (mc.renderViewEntity == null) {
				return OVERWORLD;
			}
			
			if (mc.renderViewEntity.dimension == -1) {
				return NETHER;
			} else if (mc.renderViewEntity.dimension == 0) {
				return OVERWORLD;
			} else {
				//End dimension == 1
				return END;
			}
		}
		
		public static Dimension getDimensionFromId(int id) {
			for (Dimension dimension : Dimension.values()) {
				if (dimension.id == id) {
					return dimension;
				}
			}
			
			return OVERWORLD;
		}
	}
	
	public static class NamePos {
		public String name;
		public Vec3d pos;
		
		public NamePos(String name, Vec3d pos) {
			this.name = name;
			this.pos = pos;
		}
	}
}
