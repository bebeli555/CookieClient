package me.bebeli555.cookieclient;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.gui.Gui;
import me.bebeli555.cookieclient.gui.GuiNode;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.mods.render.Search;
import me.bebeli555.cookieclient.mods.render.Waypoints;
import me.bebeli555.cookieclient.mods.render.Waypoints.Dimension;
import me.bebeli555.cookieclient.mods.render.Waypoints.Waypoint;
import me.bebeli555.cookieclient.mods.render.XRay;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//Im going to make this have components so its easier to add commands like the hud works. Sometime but too lazy rn
public class Commands extends Mod {
	public static boolean openGui;
    
	@SubscribeEvent
	public void onChat(ClientChatEvent e) {
		String messageReal = e.getMessage();
		String message = messageReal.toLowerCase();
		String prefix = GuiSettings.prefix.stringValue();
		
		if (message.startsWith(prefix)) {
			e.setCanceled(true);
			mc.ingameGUI.getChatGUI().addToSentMessages(messageReal);
			message = message.substring(prefix.length());

			//Open gui command
			if (message.equals("gui")) {
				openGui = true;
				MinecraftForge.EVENT_BUS.register(Gui.gui);
			}
			
			//Set settings
			else if (message.startsWith("set")) {
				String id = "";
				String value = "";
				
				try {
					String split[] = messageReal.split(" ");
					id = split[1].replace("_", " ");
					value = split[2];
				} catch (Exception e2) {
					sendMessage("Invalid arguments. Working example: ++set Tetris false", true);
					return;
				}
				
				GuiNode guiNode = Settings.getGuiNodeFromId(id);
				if (guiNode == null) {
					sendMessage("Cant find setting with id: " + id, true);
				} else {
					if (guiNode.isTypeable != Settings.isBoolean(value)) {
						if (!guiNode.isTypeable) {
							guiNode.toggled = Boolean.parseBoolean(value);
							guiNode.setSetting();
						} else {
							try {								
								guiNode.stringValue = value;
								guiNode.setSetting();
							} catch (Exception ex) {
								sendMessage("Wrong input. This might be caused if u input a string value and the setting only accepts integer or double", true);
								return;
							}
						}
						
						sendMessage("Set " + id + " to " + value, false);
						
						if (Settings.isBoolean(value)) {
							try {
								Mod.toggleMod(id, Boolean.parseBoolean(value));
							} catch (Exception ignored) {

							}
						}
					} else {
						if (guiNode.isTypeable) {
							sendMessage("This setting requires a boolean value", true);
						} else {
							sendMessage("This setting requires a string or integer value", true);
						}
					}
				}
			}
			
			//List of settings
			else if (message.equals("list")) {
				String list = "";
				for (GuiNode node : GuiNode.all) {
					list += node.id.replace(" ", "_") + ", ";
				}
				
				sendMessage(list, false);
			}
			
			//Add friend
			else if (message.startsWith("friend add")) {
				try {
					String name = message.split(" ")[2];
					if (!Friends.friends.contains(name)) {
						Friends.addFriend(name);
						sendMessage("Added " + name + " to friends", false);
					} else {
						sendMessage(name + " is already a friend", true);
					}
				} catch (Exception e2) {
					sendMessage("Invalid arguments", true);
				}
			}
			
			//Remove friend
			else if (message.startsWith("friend del")) {
				try {
					String name = message.split(" ")[2];
					
					if (Friends.friends.contains(name)) {
						Friends.removeFriend(name);
						sendMessage("Removed " + name + " from friends", false);
					} else {
						sendMessage(name + " is not a friend", true);
					}
				} catch (Exception e2) {
					sendMessage("Invalid arguments", true);
				}
			}
			
			//Add or remove xray blocks
			else if (message.startsWith("xray")) {
				try {
					String id = message.split(" ")[2];
					
					if (message.split(" ")[1].equals("add")) {
						XRay.addBlock(Integer.parseInt(id));
						sendMessage("Added block to XRay", false);
					} else if (message.split(" ")[1].equals("remove")) {
						XRay.removeBlock(Integer.parseInt(id));
						sendMessage("Removed block from XRay", false);
					} else {
						sendMessage("Wrong arguments. Valid = add, remove", true);
					}
				} catch (Exception e2) {
					sendMessage("Invalid arguments. Working example: xray add 69", true);
				}
			}
			
			//Add or remove search blocks
			else if (message.startsWith("search")) {
				try {
					String id = message.split(" ")[2];
					
					if (message.split(" ")[1].equals("add")) {
						Search.addBlock(Integer.parseInt(id));
						sendMessage("Added block to Search", false);
					} else if (message.split(" ")[1].equals("remove")) {
						Search.removeBlock(Integer.parseInt(id));
						sendMessage("Removed block from Search", false);
					} else {
						sendMessage("Wrong arguments. Valid = add, remove", true);
					}
				} catch (Exception e2) {
					sendMessage("Invalid arguments. Working example: search add 69", true);
				}
			}
			
			//Add waypoint
			else if (message.startsWith("waypoint add")) {
				try {
					message = message.toLowerCase();
					String[] split = message.split(" ");
					String dimension = "overworld";
					if (mc.player.dimension == -1) {
						dimension = "nether";
					}
					
					Vec3d pos = null;
					if (split.length > 3) {
						//Custom position
						try {
							dimension = split[6];
						} catch (Exception e2) {
							
						}

						pos = new Vec3d(Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]));
					} else {
						//Players position
						try {
							dimension = split[3];
						} catch (Exception e2) {
							
						}
						
						pos = new Vec3d(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
					}
					
					//Delete existing waypoint with same name
					Waypoint existing = Waypoint.getWaypointFromName(split[2]);
					if (existing != null) {
						Waypoints.removeWaypoint(existing);
					}
					
					Waypoint waypoint = new Waypoint(split[2], pos);
					if (dimension.startsWith("o")) {
						waypoint.originalDimension = Dimension.OVERWORLD;
					} else {
						waypoint.originalDimension = Dimension.NETHER;
					}
					waypoint.server = PlayerUtil.getServerIp();
					
					Waypoints.addWaypoint(waypoint);
					sendMessage("Waypoint added with name: " + split[2], false);
				} catch (Exception e2) {
					sendMessage("Invalid arguments. Working example: waypoint add name 0 0 0 Overworld", true);
				}
			}
			
			//Remove waypoint
			else if (message.startsWith("waypoint remove")) {
				try {
					String name = message.split(" ")[2];
					Waypoint waypoint = Waypoint.getWaypointFromName(name);
					
					if (waypoint != null && waypoint.server.equals(PlayerUtil.getServerIp())) {
						Waypoints.removeWaypoint(waypoint);
						sendMessage("Removed waypoint named: " + name, false);
					} else {
						sendMessage("No waypoint with name: " + name, true);
					}
				} catch (Exception e2) {
					sendMessage("Invalid arguments. Working example: waypoint remove name", true);
				}
			}
			
			//Waypoint list
			else if (message.equals("waypoint list")) {
				for (Waypoint waypoint : Waypoints.waypoints) {
					if (waypoint.server.equals(PlayerUtil.getServerIp())) {
						sendMessage(waypoint.name + " X: " + (int)waypoint.getOriginalPos().x + " Y: " + (int)waypoint.getOriginalPos().y + " Z: " + (int)waypoint.getOriginalPos().z + " Dimension: " + waypoint.originalDimension.name, false);
					}
				}
			}
			
			//Help
			else if (message.equals("help")) {
				sendMessage(prefix + "gui - Opens the GUI", false);
				sendMessage(prefix + "set settingId value - sets setting with given id to given value", false);
				sendMessage(prefix + "list - Gives a list of all the settingIds", false);
				sendMessage(prefix + "friend add name - Add friend", false);
				sendMessage(prefix + "friend del name - Remove friend", false);
				sendMessage("Also theres more commands that are explained in the modules description if it has a command", false);
			}
			
			//Set custom render distance
			else if (message.startsWith("renderdistance")) {
				int value = Integer.parseInt(message.split(" ")[1]);
				mc.gameSettings.renderDistanceChunks = value;
				sendMessage("Set render distance to " + value, false);
			}
			
			//Disables the tutorial step thing sometimes its really annoying and u have to restart ur game to get rid of it so this will remove it
			else if (message.equals("disable tutorial")) {
				mc.gameSettings.tutorialStep = TutorialSteps.NONE;
			}
			
			//toggles pause if lost focus setting
			else if (message.equals("nopause")) {
				mc.gameSettings.pauseOnLostFocus = !mc.gameSettings.pauseOnLostFocus;
			}
			
			//Unknown command
			else {
				sendMessage("Unknown command. Type " + ChatFormatting.GREEN + prefix + "help" + ChatFormatting.RED + " for help", true);
			}
		}
	}
}
