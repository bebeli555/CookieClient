package me.bebeli555.cookieclient.mods.bots.elytrabot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.combat.Surround;
import me.bebeli555.cookieclient.mods.misc.AutoMend;
import me.bebeli555.cookieclient.rendering.RenderPath;
import me.bebeli555.cookieclient.utils.BaritoneUtil;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.EatingUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import me.bebeli555.cookieclient.utils.MiningUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ElytraBot extends Mod {
	private static Thread thread;
	private static ArrayList<BlockPos> path;
	private static BlockPos goal, previous, lastSecondPos;
	private static Direction direction;
	private static int x, z;
	private static double jumpY = -1;
	private static int packetsSent, lagbackCounter, useBaritoneCounter;
	private static boolean lagback;
	private static double blocksPerSecond;
	private static int blocksPerSecondCounter;
	private static Timer blocksPerSecondTimer = new Timer();
	private static Timer packetTimer = new Timer();
	private static Timer fireworkTimer = new Timer();
	private static Timer takeoffTimer = new Timer();
	
	public static Setting mode = new Setting(null, "Mode", "Highway", new String[]{"Overworld", "Pathfinding designed for overworld"}, new String[]{"Highway", "Pathfinding designed for the highways."}, new String[]{"Tunnel", "For 2x1 tunnels", "Also mines above block if its preventing takeoff"});
		public static Setting useBaritone = new Setting(mode, "Highway", Mode.BOOLEAN, "UseBaritone", true, "Uses baritone to walk a bit if stuck or cant find a path", "Then tries to takeoff again");
			public static Setting useBaritoneBlocks = new Setting(useBaritone, Mode.INTEGER, "Blocks", 20, "Amount of blocks to walk from current position");
	public static Setting takeoffMode = new Setting(null, "Takeoff", "SlowGlide", new String[]{"PacketFly", "Uses packetfly for takeoff"}, new String[]{"Jump", "Just jumps and tries to open the elytra", "Only works if the server has really good tps"}, new String[]{"SlowGlide", "Glides slowly down giving more time to open elytra"});
	public static Setting flyMode = new Setting(null, "FlyMode", "ElytraFly", new String[]{"Firework", "Uses fireworks and rotates head to traverse the path"}, new String[]{"ElytraFly", "Uses elytrafly to traverse the path"});
		public static Setting elytraFlySpeed = new Setting(flyMode, "ElytraFly", Mode.DOUBLE, "Speed", 1.81, "Speed for elytrafly");
		public static Setting elytraFlyManuverSpeed = new Setting(flyMode, "ElytraFly", Mode.DOUBLE, "ManuverSpeed", 1.0, "Speed used for manuvering", "It has to be lower because it would", "Go off the target pos with too high speed");
		public static Setting fireworkDelay = new Setting(flyMode, "Firework", Mode.DOUBLE, "FireworkDelay", 2.8, "Delay between the clicks on the fireworks", "In seconds");
	public static Setting pathfinding = new Setting(Mode.LABEL, "Pathfinding", true, "Settings about the pathfinding");
		public static Setting avoidLava = new Setting(pathfinding, Mode.BOOLEAN, "AvoidLava", false, "Avoids lava");
		public static Setting maxY = new Setting(pathfinding, Mode.INTEGER, "MaxY", "", "Max Y-Coordinate the pathfinding can go to");
	public static Setting coordinates = new Setting(Mode.BOOLEAN, "Coordinates", false, "If false then it will just start going straight", "To the direction you are looking at", "If true then it will go to the given coordinates below");
		public static Setting gotoX = new Setting(coordinates, Mode.INTEGER, "X", "", "X-Coordinate where the bot will try to go");
		public static Setting gotoZ = new Setting(coordinates, Mode.INTEGER, "Z", "", "Z-Coordinate where the bot will try to go");
	public static Setting autoSwitch = new Setting(Mode.BOOLEAN, "AutoSwitch", true, "Switches equipped low durability elytra with a new one");
	public static Setting autoEat = new Setting(Mode.BOOLEAN, "AutoEat", true, "Eats gaps or other food when hunger is low or health is low");
		public static Setting minHealth = new Setting(autoEat, Mode.INTEGER, "Health", 10, "When health goes below or equal to the given amount it will eat food");
		public static Setting minHunger = new Setting(autoEat, Mode.INTEGER, "Hunger", 10, "When hunger goes below or equal to the given amount it will eat food");
		public static Setting gaps = new Setting(autoEat, Mode.BOOLEAN, "Gaps", true, "Allows the bot to eat gapples");
	public static Setting toggleOnPop = new Setting(Mode.BOOLEAN, "ToggleOnPop", false, "Toggles the module off", "When you pop a totem");
	
	public ElytraBot() {
		super(Group.BOTS, "ElytraBot", "Pathfinding bot for elytras");
	}
	
	@Override
	public void onEnabled() {
		int up = 1;
		
		if (!coordinates.booleanValue()) {
			//Calculate the direction so it will put it to diagonal if the player is on diagonal highway.
			if (Math.abs(Math.abs(mc.player.posX) - Math.abs(mc.player.posZ)) <= 5 && Math.abs(mc.player.posX) > 10 && Math.abs(mc.player.posZ) > 10 && mode.stringValue().equals("Highway")) {
				direction = Direction.getDiagonalDirection();
			} else {
				direction = Direction.getDirection();
			}
			
			goal = generateGoalFromDirection(direction, up);
		} else {
			x = gotoX.intValue();
			z = gotoZ.intValue();
			goal = new BlockPos(gotoX.intValue(), mc.player.posY + up, gotoZ.intValue());
		}
		
		thread = new Thread() {
			public void run() {
				while (thread != null && thread.equals(this)) {
					try {
						loop();
					} catch (NullPointerException e) {

					}
					
					Mod.sleep(50);
				}
			}
		};
		
		blocksPerSecondTimer.reset();
		Mod.EVENT_BUS.subscribe(this);
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		Mod.EVENT_BUS.unsubscribe(this);
		direction = null;
		path = null;
		useBaritoneCounter = 0;
		lagback = false;
		lagbackCounter = 0;
		blocksPerSecond = 0;
		blocksPerSecondCounter = 0;
		lastSecondPos = null;
		jumpY = -1;
		RenderPath.clearPath();
		PacketFly.toggle(false);
		ElytraFly.toggle(false);
		clearStatus();
		BaritoneUtil.forceCancel();
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		//Check if the goal is reached and then stop
		if (BlockUtil.distance(getPlayerPos(), goal) < 15) {
			mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100.0f, 18.0F, true);
			sendMessage("Goal reached!", false);
			disable();
			return;
		}
		
		//Check if there is an elytra equipped if not then equip it or toggle off if no elytra in inventory
		if (InventoryUtil.getItemStack(38).getItem() != Items.ELYTRA) {
			if (InventoryUtil.hasItem(Items.ELYTRA)) {
				int elytraSlot = InventoryUtil.getSlot(Items.ELYTRA);
				InventoryUtil.clickSlot(elytraSlot);
				InventoryUtil.clickSlot(38);
				InventoryUtil.clickSlot(elytraSlot);
			} else {
				sendMessage("You need an elytra", true);
				disable();
				return;
			}
		}
		
		//Toggle off if no fireworks while using firework mode
		if (flyMode.stringValue().equals("Firework") && !InventoryUtil.hasItem(Items.FIREWORKS)) {
			sendMessage("You need fireworks as your using firework mode", true);
			disable();
			return;
		}
		
		//Wait still if in unloaded chunk
		if (!BlockUtil.isInRenderDistance(getPlayerPos())) {
			setStatus("We are in unloaded chunk. Waiting");
			mc.player.setVelocity(0, 0, 0);
			return;
		}
		
		//Switch low durability elytra with one one if setting is on
		if (autoSwitch.booleanValue()) {
			ItemStack elytra = InventoryUtil.getItemStack(38);
			
			if (AutoMend.getDurability(elytra) <= 15) {
				for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
					if (itemStack.itemStack.getItem() == Items.ELYTRA && AutoMend.getDurability(itemStack.itemStack) >= 100) {
						InventoryUtil.clickSlot(itemStack.slotId);
						InventoryUtil.clickSlot(38);
						InventoryUtil.clickSlot(itemStack.slotId);
						break;
					}
				}
			}
		}
		
		//Takeoff
		double preventPhase = (jumpY + 0.6);
		if (mc.player.isElytraFlying() || mc.player.posY < preventPhase || mc.player.onGround) {
			if (PacketFly.toggled) {
				sleep(1500);
				
				if (mc.player.isElytraFlying() || mc.player.posY < preventPhase || mc.player.onGround) {
					PacketFly.toggle(false);
					sleep(100);
				}
			}
		}
		
		if (!mc.player.isElytraFlying()) {
			ElytraFly.toggle(false);
			
			//If there is a block above then usebaritone
			if (mc.player.onGround && isSolid(getPlayerPos().add(0, 2, 0)) && useBaritone.booleanValue() && mode.stringValue().equals("Highway")) {
				setStatus("Using baritone because a block above is preventing takeoff");
				useBaritone();
			}
			
			//Mine above block in tunnel mode
			if (isSolid(getPlayerPos().add(0, 2, 0)) && mode.stringValue().equals("Tunnel")) {
				if (getBlock(getPlayerPos().add(0, 2, 0)) != Blocks.BEDROCK) {
					setStatus("Mining above block so we can takeoff");
					Surround.centerMotionFull();
					MiningUtil.mineAnyway(getPlayerPos().add(0, 2, 0), false);
				} else {
					if (useBaritone.booleanValue()) {
						setStatus("Using baritone to walk because above block is bedrock");
						useBaritone();
					} else {
						sendMessage("Above block is bedrock and usebaritone is false", true);
						disable();
						return;
					}
				}
			}
			
			//Use baritone to get back into path if we have fallen off the highway or something
			if (jumpY != -1 && Math.abs(mc.player.posY - jumpY) >= 2) {
				if (useBaritone.booleanValue() && direction != null && mode.stringValue().equals("Highway")) {
					setStatus("Using baritone to get back to the highway");
					useBaritone();
				}
			}
			
			if (packetsSent < 20) setStatus("Trying to takeoff");
			
			fireworkTimer.ms = 0;
			
			//Jump if on ground
			if (mc.player.onGround) {
				jumpY = mc.player.posY;
				generatePath();
				mc.player.jump();
			} 
			
			//Check if were falling. If so then try to open elytra
			else if (mc.player.posY < mc.player.lastTickPosY) {
				if (takeoffMode.stringValue().equals("PacketFly")) {
					if (mc.player.posY > preventPhase && !PacketFly.toggled) {
						PacketFly.toggle(true);	
					}
				} else if (takeoffMode.stringValue().equals("SlowGlide")) {
					mc.player.setVelocity(0, -0.04, 0);
				}
				
				//Dont send anymore packets for about 15 seconds if the takeoff isnt successfull.
				//Bcs 2b2t has this annoying thing where it will not let u open elytra if u dont stop sending the packets for a while
				if (packetsSent <= 15) {
					if (takeoffTimer.hasPassed(650)) {
						mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
						
						takeoffTimer.reset();
						packetTimer.reset();
						packetsSent++;
					}
				} else if (packetTimer.hasPassed(15000)) {
					packetsSent = 0;
				} else {
					setStatus("Waiting for 15s before sending elytra open packets again");
				}
			}
			
			return;
		} 
		
		else if (!PacketFly.toggled) {
			packetsSent = 0;
			
			//If we arent moving anywhere then activate usebaritone
			double speed = PlayerUtil.getSpeed(mc.player);
			if (speed < 0.1) {
				useBaritoneCounter++;
				
				if (useBaritoneCounter >= 15) {
					useBaritoneCounter = 0;
					
					if (useBaritone.booleanValue()) {
						setStatus("Using baritone to walk a bit because we are stuck");
						useBaritone();
					} else {
						sendMessage("We are stuck. UseBaritone setting would help", true);
						disable();
						return;
					}
				}
			} else {
				useBaritoneCounter = 0;
			}
			
			if (flyMode.stringValue().equals("Firework")) {
				//Prevent lagback on 2b2t by not clicking on fireworks. I hope hause would fix hes plugins tho
				if (speed > 3) {
					lagback = true;
				}
				
				//Remove lagback thing after it stops and click on fireworks again.
				if (lagback) {
					if (speed < 1) {
						lagbackCounter++;
						if (lagbackCounter > 3) {
							lagback = false;
							lagbackCounter = 0;
						}
					} else {
						lagbackCounter = 0;
					}
				}
				
				//Click on fireworks
				if (fireworkTimer.hasPassed((int)(fireworkDelay.doubleValue() * 1000)) && !lagback) {
					clickOnFirework();
				}	
			}
		}
		
		//Eat food if using autoeat
		if (autoEat.booleanValue() && !EatingUtil.isEating()) {
			if (!flyMode.stringValue().equals("Firework") || flyMode.stringValue().equals("Firework") && !fireworkTimer.hasPassed(100)) {
				float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
				float hunger = mc.player.getFoodStats().getFoodLevel();
				
				if (health <= minHealth.intValue() || hunger <= minHunger.intValue()) {
					Item eat = null;
					
					for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
						Item item = itemStack.itemStack.getItem();
						
						if (item instanceof ItemFood) {
							if (item == Items.GOLDEN_APPLE && gaps.booleanValue()) {
								eat = item;
								break;
							} else if (item != Items.CHORUS_FRUIT && item != Items.SPIDER_EYE) {
								eat = item;
							}
						}
					}
					
					EatingUtil.eatItem(eat, false);
				}
			}
		}
		
		//Generate more path
		if (path == null || path.size() <= 20 || isNextPathTooFar()) {
			generatePath();
		}
		
		//Distance how far to remove the upcoming path.
		//The higher it is the smoother the movement will be but it will need more space.
		int distance = 12;
		if (mode.stringValue().equals("Highway") || flyMode.stringValue().equals("ElytraFly")) {
			distance = 2;
		}
		
		//Remove passed positions from path
		boolean remove = false;
		ArrayList<BlockPos> removePositions = new ArrayList<BlockPos>();
		
		for (BlockPos pos : path) {
			if (!remove && BlockUtil.distance(pos, getPlayerPos()) <= distance) {
				remove = true;
			}
			
			if (remove) {
				removePositions.add(pos);
			}
		}
		
		for (BlockPos pos : removePositions) {
			path.remove(pos);
			previous = pos;
		}
		
		if (path.size() > 0) {
			if (direction != null) {
				setStatus("Going to " + direction.name);
			} else {
				setStatus("Going to X: " + x + " Z: " + z);
				
				if (blocksPerSecondTimer.hasPassed(1000)) {
					blocksPerSecondTimer.reset();
					if (lastSecondPos != null) {
						blocksPerSecondCounter++;
						blocksPerSecond += BlockUtil.distance(getPlayerPos(), lastSecondPos);
					}
					
					lastSecondPos = getPlayerPos();
				}
				
				int seconds = (int)(BlockUtil.distance(getPlayerPos(), goal) / (blocksPerSecond / blocksPerSecondCounter));
				int h = seconds / 3600;
				int m = (seconds % 3600) / 60;
				int s = seconds % 60;

				addToStatus("Estimated arrival in " + ChatFormatting.GOLD + h + "h " + m + "m " + s + "s", 1);
				
				if (flyMode.stringValue().equals("Firework") ) {
					addToStatus("Estimated fireworks needed: " + ChatFormatting.GOLD + (int)(seconds / fireworkDelay.doubleValue()), 2);
				}
			}

			if (flyMode.stringValue().equals("Firework")) {
				//Rotate head to next position
				RotationUtil.rotate(new Vec3d(path.get(path.size() - 1)).add(0.5, 0.5, 0.5), false);
			} else if (flyMode.stringValue().equals("ElytraFly")) {
				ElytraFly.toggle(true);
				
				BlockPos next = null;
				if (path.size() > 1) {
					next = path.get(path.size() - 2);
				}
				ElytraFly.setMotion(path.get(path.size() - 1), next, previous);
			}
		}
	}
	
	//Generate path
	public void generatePath() {
		//The positions the AStar algorithm is allowed to move from current.
		BlockPos[] positions = {new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
								new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1),
								new BlockPos(0, -1, 0), new BlockPos(0, 1, 0)};
		
		ArrayList<BlockPos> checkPositions = new ArrayList<BlockPos>();
		if (mode.stringValue().equals("Highway")) {
			BlockPos[] list = {new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
							   new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1)};
			checkPositions = new ArrayList<BlockPos>(Arrays.asList(list));
		} else if (mode.stringValue().equals("Overworld")) {
			int radius = 3;
	        for (int x = (int) (-radius); x < radius; x++) {
	            for (int z = (int) (-radius); z < radius; z++) {
	                for (int y = (int) (radius); y > -radius; y--) {
	                	checkPositions.add(new BlockPos(x, y, z));
	                }
	            }
	        }
		} else if (mode.stringValue().equals("Tunnel")) {
			positions = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
			checkPositions = new ArrayList<BlockPos>(Arrays.asList(new BlockPos[]{new BlockPos(0, -1, 0)}));
		}
		
		if (path == null || path.size() == 0 || isNextPathTooFar() || mc.player.onGround) {
			BlockPos start;
			if (mode.stringValue().equals("Overworld")) {
				start = getPlayerPos().add(0, 4, 0);
			} else if (Math.abs(jumpY - mc.player.posY) <= 2) {
				start = new BlockPos(mc.player.posX, jumpY + 1, mc.player.posZ);
			} else {
				start = getPlayerPos().add(0, 1, 0);
			}
			
			if (isNextPathTooFar()) {
				start = getPlayerPos();
			}
			
			path = AStar.generatePath(start, goal, positions, checkPositions, 500);
		} else {
			ArrayList<BlockPos> temp = AStar.generatePath(path.get(0), goal, positions, checkPositions, 500);
			try {
				temp.addAll(path);
			} catch (NullPointerException ignored) {
				
			}
			
			path = temp;
		}
		
		RenderPath.setPath(path, new Color(255, 0, 0, 150));
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
		if (event.packet instanceof SPacketEntityStatus) {
			SPacketEntityStatus packet = (SPacketEntityStatus)event.packet;
			
			if (packet.getOpCode() == 35 && packet.getEntity(mc.world) == mc.player && toggleOnPop.booleanValue()) {
				sendMessage("You popped a totem.", false);
				disable();
			}
		}
	});
	
	public static void useBaritone() {
		ElytraFly.toggle(false);
		int y = (int)(jumpY - mc.player.posY);
		int x = 0;
		int z = 0;
		
		int blocks = useBaritoneBlocks.intValue();
		if (direction == Direction.ZM) {
			z = -blocks;
		} else if (direction == Direction.XM) {
			x = -blocks;
		} else if (direction == Direction.XP) {
			x = blocks;
		} else if (direction == Direction.ZP) {
			z = blocks;
		} else if (direction == Direction.XP_ZP) {
			x = blocks;
			z = blocks;
		} else if (direction == Direction.XM_ZM) {
			x = -blocks;
			z = -blocks;
		} else if (direction == Direction.XP_ZM) {
			x = blocks;
			z = -blocks;
		} else if (direction == Direction.XM_ZP) {
			x = -blocks;
			z = blocks;
		}
		
		BaritoneUtil.walkTo(getPlayerPos().add(x, y, z), true);
		sleep(5000);
		sleepUntil(() -> !BaritoneUtil.isPathing(), 120000);
		BaritoneUtil.forceCancel();
	}
	
	public static void clickOnFirework() {
		if (mc.player.getHeldItemMainhand().getItem() != Items.FIREWORKS) {
			InventoryUtil.switchItem(InventoryUtil.getSlot(Items.FIREWORKS), false);
		}
		
		//Click
		mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
		fireworkTimer.reset();
	}
	
	public static BlockPos generateGoalFromDirection(Direction direction, int up) {
		if (direction == Direction.ZM) {
			return new BlockPos(0, mc.player.posY + up, mc.player.posZ - 42042069);
		} else if (direction == Direction.ZP) {
			return new BlockPos(0, mc.player.posY + up, mc.player.posZ + 42042069);
		} else if (direction == Direction.XM) {
			return new BlockPos(mc.player.posX - 42042069, mc.player.posY + up, 0);
		} else if (direction == Direction.XP) {
			return new BlockPos(mc.player.posX + 42042069, mc.player.posY + up, 0);
		} else if (direction == Direction.XP_ZP) {
			return new BlockPos(mc.player.posX + 42042069, mc.player.posY + up, mc.player.posZ + 42042069);
		} else if (direction == Direction.XM_ZM) {
			return new BlockPos(mc.player.posX - 42042069, mc.player.posY + up, mc.player.posZ - 42042069);
		} else if (direction == Direction.XP_ZM) {
			return new BlockPos(mc.player.posX + 42042069, mc.player.posY + up, mc.player.posZ - 42042069);
		} else {
			return new BlockPos(mc.player.posX - 42042069, mc.player.posY + up, mc.player.posZ + 42042069);
		}
	}
	
	public static boolean isNextPathTooFar() {
		try {
			return BlockUtil.distance(getPlayerPos(), path.get(path.size() - 1)) > 15;
		} catch (Exception e) {
			return false;
		}
	}
}
