package me.bebeli555.cookieclient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.events.bus.EventBus;
import me.bebeli555.cookieclient.events.bus.EventManager;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.GuiNode;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.gui.Keybind;
import me.bebeli555.cookieclient.gui.SetGuiNodes;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.hud.HudEditor;
import me.bebeli555.cookieclient.hud.HudSettings;
import me.bebeli555.cookieclient.hud.components.ArrayListComponent;
import me.bebeli555.cookieclient.mods.bots.ObbyBuilderBot;
import me.bebeli555.cookieclient.mods.bots.elytrabot.ElytraBot;
import me.bebeli555.cookieclient.mods.combat.AutoArmor;
import me.bebeli555.cookieclient.mods.combat.AutoCrystal;
import me.bebeli555.cookieclient.mods.combat.AutoLog;
import me.bebeli555.cookieclient.mods.combat.AutoTotem;
import me.bebeli555.cookieclient.mods.combat.AutoTrap;
import me.bebeli555.cookieclient.mods.combat.Criticals;
import me.bebeli555.cookieclient.mods.combat.HoleFiller;
import me.bebeli555.cookieclient.mods.combat.KillAura;
import me.bebeli555.cookieclient.mods.combat.NoKnockback;
import me.bebeli555.cookieclient.mods.combat.Offhand;
import me.bebeli555.cookieclient.mods.combat.PistonAura;
import me.bebeli555.cookieclient.mods.combat.SelfWeb;
import me.bebeli555.cookieclient.mods.combat.Surround;
import me.bebeli555.cookieclient.mods.exploits.Burrow;
import me.bebeli555.cookieclient.mods.exploits.LiquidInteract;
import me.bebeli555.cookieclient.mods.exploits.MiningSpoof;
import me.bebeli555.cookieclient.mods.exploits.NewChunks;
import me.bebeli555.cookieclient.mods.exploits.PacketFly;
import me.bebeli555.cookieclient.mods.exploits.PortalGodMode;
import me.bebeli555.cookieclient.mods.exploits.Reach;
import me.bebeli555.cookieclient.mods.games.Snake;
import me.bebeli555.cookieclient.mods.games.tetris.Tetris;
import me.bebeli555.cookieclient.mods.misc.AntiAFK;
import me.bebeli555.cookieclient.mods.misc.AutoEat;
import me.bebeli555.cookieclient.mods.misc.AutoFirework;
import me.bebeli555.cookieclient.mods.misc.AutoHotbar;
import me.bebeli555.cookieclient.mods.misc.AutoInventoryManager;
import me.bebeli555.cookieclient.mods.misc.AutoMend;
import me.bebeli555.cookieclient.mods.misc.AutoMessager;
import me.bebeli555.cookieclient.mods.misc.AutoReconnect;
import me.bebeli555.cookieclient.mods.misc.ChestSwap;
import me.bebeli555.cookieclient.mods.misc.DiscordRPC;
import me.bebeli555.cookieclient.mods.misc.FakePlayer;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.mods.misc.MiddleClickFriends;
import me.bebeli555.cookieclient.mods.misc.NoSound;
import me.bebeli555.cookieclient.mods.misc.PacketCanceller;
import me.bebeli555.cookieclient.mods.misc.UpdateChecker;
import me.bebeli555.cookieclient.mods.misc.VisualRange;
import me.bebeli555.cookieclient.mods.misc.XCarry;
import me.bebeli555.cookieclient.mods.movement.AntiHunger;
import me.bebeli555.cookieclient.mods.movement.AntiLevitation;
import me.bebeli555.cookieclient.mods.movement.AutoSprint;
import me.bebeli555.cookieclient.mods.movement.AutoWalk;
import me.bebeli555.cookieclient.mods.movement.Blink;
import me.bebeli555.cookieclient.mods.movement.ElytraFly;
import me.bebeli555.cookieclient.mods.movement.EntityControl;
import me.bebeli555.cookieclient.mods.movement.EntitySpeed;
import me.bebeli555.cookieclient.mods.movement.Flight;
import me.bebeli555.cookieclient.mods.movement.HighJump;
import me.bebeli555.cookieclient.mods.movement.IceSpeed;
import me.bebeli555.cookieclient.mods.movement.InventoryMove;
import me.bebeli555.cookieclient.mods.movement.Jesus;
import me.bebeli555.cookieclient.mods.movement.LiquidSpeed;
import me.bebeli555.cookieclient.mods.movement.NoFall;
import me.bebeli555.cookieclient.mods.movement.NoRotate;
import me.bebeli555.cookieclient.mods.movement.NoSlowDown;
import me.bebeli555.cookieclient.mods.movement.SafeWalk;
import me.bebeli555.cookieclient.mods.movement.Speed;
import me.bebeli555.cookieclient.mods.movement.Step;
import me.bebeli555.cookieclient.mods.movement.Strafe;
import me.bebeli555.cookieclient.mods.render.AutoTrapIndicator;
import me.bebeli555.cookieclient.mods.render.BlockVision;
import me.bebeli555.cookieclient.mods.render.EntityESP;
import me.bebeli555.cookieclient.mods.render.Freecam;
import me.bebeli555.cookieclient.mods.render.FullBright;
import me.bebeli555.cookieclient.mods.render.HoleESP;
import me.bebeli555.cookieclient.mods.render.LiquidVision;
import me.bebeli555.cookieclient.mods.render.NameTags;
import me.bebeli555.cookieclient.mods.render.NoRender;
import me.bebeli555.cookieclient.mods.render.Search;
import me.bebeli555.cookieclient.mods.render.ShulkerPreview;
import me.bebeli555.cookieclient.mods.render.Tracers;
import me.bebeli555.cookieclient.mods.render.Trajectories;
import me.bebeli555.cookieclient.mods.render.VoidESP;
import me.bebeli555.cookieclient.mods.render.Waypoints;
import me.bebeli555.cookieclient.mods.render.XRay;
import me.bebeli555.cookieclient.mods.render.Zoom;
import me.bebeli555.cookieclient.mods.world.AutoBuilder;
import me.bebeli555.cookieclient.mods.world.AutoEnderChestMiner;
import me.bebeli555.cookieclient.mods.world.AutoEnderpearl;
import me.bebeli555.cookieclient.mods.world.AutoFish;
import me.bebeli555.cookieclient.mods.world.AutoRespawn;
import me.bebeli555.cookieclient.mods.world.AutoTool;
import me.bebeli555.cookieclient.mods.world.CrystalBlock;
import me.bebeli555.cookieclient.mods.world.FastUse;
import me.bebeli555.cookieclient.mods.world.NoEntityTrace;
import me.bebeli555.cookieclient.mods.world.NoGlitchBlocks;
import me.bebeli555.cookieclient.mods.world.PacketMine;
import me.bebeli555.cookieclient.mods.world.Scaffold;
import me.bebeli555.cookieclient.mods.world.SpeedMine;
import me.bebeli555.cookieclient.mods.world.StashLogger;
import me.bebeli555.cookieclient.mods.world.Timer;
import me.bebeli555.cookieclient.rendering.Renderer;
import me.bebeli555.cookieclient.utils.EatingUtil;
import me.bebeli555.cookieclient.utils.InformationUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@net.minecraftforge.fml.common.Mod(modid = Mod.MODID, name = Mod.NAME, version = Mod.VERSION)
public class Mod {
    public static final String MODID = "cookieclient";
    public static final String NAME = "CookieClient";
    public static final String VERSION = "1.01";
    public static final String DISCORD = "discord.gg/xSukBcyd8m";
    
    public static Minecraft mc = Minecraft.getMinecraft();
    public static final EventBus EVENT_BUS = new EventManager();

    public String name = "";
    public String[] description;
    public Group group;
    private boolean toggled, hiddenOn, lastHiddenOn;
    public boolean defaultOn, defaultHidden;
    public boolean autoSubscribe = true;
    private GuiNode guiNode, hiddenNode;
    private int renderNumber = -1;
    public static ArrayList<Mod> modules = new ArrayList<Mod>();
    
    public Mod(Group group, String name, String... description) {
    	this.group = group;
    	this.name = name;
    	this.description = description;
    	modules.add(this);
    }
    
    public Mod(Group group) {
    	this.group = group;
    	modules.add(this);
    }
    
    public Mod() {
    	
    }
    
    @EventHandler
	public void init(FMLInitializationEvent event) {
		long ms = System.currentTimeMillis();
		
		//Register classes to event busses
    	MinecraftForge.EVENT_BUS.register(new HelpMessage());
    	MinecraftForge.EVENT_BUS.register(new Commands());
    	MinecraftForge.EVENT_BUS.register(new Keybind());
    	MinecraftForge.EVENT_BUS.register(new EatingUtil());
    	MinecraftForge.EVENT_BUS.register(new Renderer());
    	Mod.EVENT_BUS.subscribe(new Renderer());
    	InformationUtil informationUtil = new InformationUtil();
    	MinecraftForge.EVENT_BUS.register(informationUtil);
    	Mod.EVENT_BUS.subscribe(informationUtil);
    	
    	//Init mods
    	initMods();
		
    	//Initialize stuff
		new File(Settings.path).mkdir();
		Friends.loadFriends();
    	SetGuiNodes.setGuiNodes();
    	SetGuiNodes.setDefaults();
		Settings.loadSettings();
		Keybind.setKeybinds();
		HudSettings.loadSettings();
		
		for (Mod module : modules) {
			module.onPostInit();
		}
		
		System.out.println("CookieClient - Initialization took " + Math.abs(System.currentTimeMillis() - ms) + "ms");
    }
    
    public void initMods() {
    	//Combat
    	new AutoArmor();
    	new AutoCrystal();
    	new AutoLog();
    	new AutoTotem();
    	new AutoTrap();
    	new Criticals();
    	new HoleFiller();
    	new KillAura();
    	new NoKnockback();
    	new SelfWeb();
    	new Surround();
    	new Offhand();
    	new PistonAura();
    	
    	//Exploits
    	new Burrow();
    	new MiningSpoof();
    	new NewChunks();
    	new PacketFly();
    	new Reach();
    	new PortalGodMode();
    	new LiquidInteract();
		
    	//Misc
    	new AntiAFK();
    	new AutoEat();
    	new AutoFirework();
    	new AutoInventoryManager();
    	new AutoMend();
    	new AutoMessager();
    	new AutoReconnect();
    	new ChestSwap();
    	try {new DiscordRPC();} catch(UnsatisfiedLinkError e) {}
    	new FakePlayer();
    	new Friends();
    	new MiddleClickFriends();
    	new PacketCanceller();
    	new UpdateChecker();
    	new VisualRange();
    	new XCarry();
    	new NoSound();
    	new AutoHotbar();
		
    	//Movement
    	new AntiHunger();
    	new AntiLevitation();
    	new AutoSprint();
    	new AutoWalk();
    	new Blink();
    	new ElytraFly();
    	new EntityControl();
    	new EntitySpeed();
    	new Flight();
    	new HighJump();
    	new IceSpeed();
    	new InventoryMove();
    	new Jesus();
    	new NoFall();
    	new NoRotate();
    	new NoSlowDown();
    	new SafeWalk();
    	new Speed();
    	new Step();
    	new Strafe();
    	new LiquidSpeed();
		
    	//Render
    	new AutoTrapIndicator();
    	new BlockVision();
    	new EntityESP();
    	new Freecam();
    	new FullBright();
    	new HoleESP();
    	new LiquidVision();
    	new NameTags();
    	new NoRender();
    	new Search();
    	new ShulkerPreview();
    	new Tracers();
    	new Trajectories();
    	new VoidESP();
    	new Waypoints();
    	new XRay();
    	new Zoom();
		
    	//World
    	new AutoBuilder();
    	new AutoEnderChestMiner();
    	new AutoFish();
    	new CrystalBlock();
    	new FastUse();
    	new NoEntityTrace();
    	new NoGlitchBlocks();
    	new PacketMine();
    	new Scaffold();
    	new SpeedMine();
    	new Timer();
    	new AutoTool();
    	new AutoRespawn();
    	new StashLogger();
    	new AutoEnderpearl();
		
    	//Games
		new Snake();
		new Tetris();
		
    	//Bots
    	new ElytraBot();
    	new ObbyBuilderBot();
		
		//Sort the modules list from A to Z
		List<String> names = new ArrayList<String>();
		for (Mod module : modules) {
			names.add(module.name);
		}
		
		String[] sortedNames = new String[names.size()];
		sortedNames = names.toArray(sortedNames);
		Arrays.sort(sortedNames);
		
		ArrayList<Mod> temp = new ArrayList<Mod>();
		for (String name : sortedNames) {
			for (Mod module : modules) {
				if (module.name.equals(name)) {
					temp.add(module);
					break;
				}
			}
		}
		
		modules = temp;
		
    	//Gui
    	new HudEditor();
		new GuiSettings();
    }
    
    public void onEnabled(){}
    public void onDisabled(){}
    public void onPostInit(){}
    public void onGuiDrawScreen(int mouseX, int mouseY, float partialTicks){}
    public boolean onGuiClick(int x, int y, int button){return false;}
    public void onGuiKeyPress(GuiScreenEvent.KeyboardInputEvent.Post e){}
    public void onRenderWorld(float partialTicks) {}
    
    /**
     * Sends a clientSided message
     * @param red if true then message will be red if false then it will be some other color
     * @param name of the module it will add in the message
     * @param remove removes all the past messages made by the mod if true
     */
    public void sendMessage(String text, boolean red) {
    	if (mc.player == null) {
    		return;
    	}

    	//Send message
    	String module = "";
    	ChatFormatting color = ChatFormatting.WHITE;
    	if (red) {
    		color = ChatFormatting.RED;
    	}
    	if (!name.isEmpty()) {
    		module = "-" + name;
    	}
    	
    	mc.player.sendMessage(new TextComponentString(ChatFormatting.GREEN + "[" + ChatFormatting.LIGHT_PURPLE + NAME + module + ChatFormatting.GREEN + "] " + color + text));
    }
    
    public int getRenderNumber() {
    	return this.renderNumber;
    }
    
    public void setRenderNumber(int number) {
    	if (this.renderNumber == -1) {
    		ArrayListComponent.lastArraylistSize = -1;
    	}
    	
    	this.renderNumber = number;
    }
    
    public void enable() {
    	if (autoSubscribe) {
    		MinecraftForge.EVENT_BUS.register(this);
    		Mod.EVENT_BUS.subscribe(this);
    	}
    	getGuiNode().toggled = true;
    	ArrayListComponent.arraylist.add(this);
		this.toggled = true;
		this.onEnabled();
    }
    
    public void disable() {
    	if (autoSubscribe) {
    		MinecraftForge.EVENT_BUS.unregister(this);
    		Mod.EVENT_BUS.unsubscribe(this);
    	}
    	getGuiNode().toggled = false;
		ArrayListComponent.arraylist.remove(this);
		this.toggled = false;
		this.onDisabled();
    }
    
    public void toggle() {
    	if (toggled) {
    		disable();
    	} else {
    		enable();
    	}
    }
    
    /**
     * Sets the module on but doesnt show it in gui or arraylist or anything
     * This can be used by other modules to turn this module on
     */
    public void setHiddenOn(boolean value) {
    	hiddenOn = value;
    	
    	if (hiddenOn != lastHiddenOn) {	
    		if (hiddenOn) {
            	if (autoSubscribe) {
            		MinecraftForge.EVENT_BUS.register(this);
            		Mod.EVENT_BUS.subscribe(this);
            	}	
        		onEnabled();
    		} else {
            	if (autoSubscribe) {
            		MinecraftForge.EVENT_BUS.unregister(this);
            		Mod.EVENT_BUS.unsubscribe(this);
            	}
        		onDisabled();
    		}
    	}
    	
    	lastHiddenOn = hiddenOn;
    }
    
    public GuiNode getGuiNode() {
    	if (guiNode == null) {
    		guiNode = Settings.getGuiNodeFromId(name);
    		return guiNode;
    	} else {
    		return guiNode;
    	}
    }
    
    public boolean isHidden() {
    	if (hiddenOn) {
    		return true;
    	}
    	
    	if (hiddenNode == null) {
        	hiddenNode = Settings.getGuiNodeFromId(name + "Hidden");
    	}
    	
    	return hiddenNode.toggled;
    }
    
    public boolean isOn() {
    	return toggled;
    }
    
    public static void toggleMod(String name, boolean on) {
    	GuiNode node = Settings.getGuiNodeFromId(name);
    	
    	node.toggled = on;
    	node.setSetting();
    	
    	for (Mod module : modules) {
    		if (module.name.equals(name)) {
    			if (on) {
    				module.enable();
    			} else {
    				module.disable();
    			}
    			
    			module.toggled = on;
    			break;
    		}
    	}
    }
    
    /**
     * 1 = 50% change and so on
     */
    public static boolean random(int i) {
    	return new Random().nextInt(i + 1) == 0;
    }
    
    /**
     * Generates random number between min and max
     */
    public static int random(int min, int max) {
    	return new Random().nextInt(min + max) - min;
    }
    
	public void setStatus(String status) {
		setStatus(status, name);
	}
	
	public static void setStatus(String status, String module) {
    	if (!module.isEmpty()) {
    		module = "-" + module;
    	}
    	
    	Renderer.status = new String[10];
    	Renderer.status[0] = ChatFormatting.GREEN + "[" + ChatFormatting.LIGHT_PURPLE + NAME + module + ChatFormatting.GREEN + "] " + ChatFormatting.WHITE + status;
	}
	
	public void addToStatus(String status, int index) {
		addToStatus(status, index, name);
	}
	
	public static void addToStatus(String status, int index, String module) {
    	if (!module.isEmpty()) {
    		module = "-" + module;
    	}
    	
    	Renderer.status[index] = ChatFormatting.WHITE + status;
	}
	
	public static void clearStatus() {
		Renderer.status = null;
	}
	
	@SuppressWarnings("deprecation")
	public static void suspend(Thread thread) {
		if (thread != null) thread.suspend();
	}
	
	public static Block getBlock(BlockPos pos) {
		try {
			return mc.world.getBlockState(pos).getBlock();
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public static boolean isSolid(BlockPos pos) {
		try {
			return mc.world.getBlockState(pos).getMaterial().isSolid();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static BlockPos getPlayerPos() {
		return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
	}
	
	/**
	 * Checks if the player has the given potion effect like "regeneration"
	 */
	public static boolean isPotionActive(String name, EntityPlayer player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getEffectName().contains(name.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String[] addToArray(String[] myArray, String newItem) {
		int currentSize = myArray.length;
		int newSize = currentSize + 1;
		String[] tempArray = new String[ newSize ];
		for (int i = 0; i < currentSize; i++) {
		    tempArray[i] = myArray [i];
		}
		tempArray[newSize- 1] = newItem;
		
		return tempArray;
	}
	
    public static void sleep(int ms) {
    	try {
    		Thread.sleep(ms);
    	} catch (Exception ignored) {
    		
    	}
    }
    
	public static void sleepUntil(BooleanSupplier condition, int timeout) {
		long startTime = System.currentTimeMillis();
		while(true) {
			if (condition.getAsBoolean()) {
				break;
			} else if (timeout != -1 && System.currentTimeMillis() - startTime >= timeout) {
				break;
			}
			
			sleep(10);
		}
	}
	
	public static void sleepUntil(BooleanSupplier condition, int timeout, int amountToSleep) {
		long startTime = System.currentTimeMillis();
		while(true) {
			if (condition.getAsBoolean()) {
				break;
			} else if (timeout != -1 && System.currentTimeMillis() - startTime >= timeout) {
				break;
			}
			
			sleep(amountToSleep);
		}
	}
	
	//Send a help message telling the prefix and stuff if the settings file doesnt exist which would mean the person is using the mod for the first time
	public static class HelpMessage {
		boolean check = false;
		
		@SubscribeEvent
		public void onTick(ClientTickEvent e) {
	    	if (!check && mc.player != null) {
	    		if (!Settings.settings.exists()) {
	    			new Mod().sendMessage("Welcome to " + ChatFormatting.GREEN + NAME + ChatFormatting.WHITE + " version " + ChatFormatting.GREEN + VERSION, false);
	    			new Mod().sendMessage("You can open the GUI by typing " + ChatFormatting.GREEN + GuiSettings.prefix.stringValue() + "gui" + ChatFormatting.WHITE + " on chat", false);
	    			Settings.saveSettings();
	    		}
	    		
	    		check = true;
	    		MinecraftForge.EVENT_BUS.unregister(this);
	    	}
		}
	}
}
