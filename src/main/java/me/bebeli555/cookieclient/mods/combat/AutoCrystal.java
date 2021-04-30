package me.bebeli555.cookieclient.mods.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.EntityRemovedEvent;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.CrystalUtil;
import me.bebeli555.cookieclient.utils.EntityUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import me.bebeli555.cookieclient.utils.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

//The base for this AutoCrystal is from salhack or spidermod.
//Just improved a bit
public class AutoCrystal extends Mod {
    public static Timer removeVisualTimer = new Timer();
    public static List<BlockPos> placedCrystals = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<EntityEnderCrystal, Integer> attackedCrystals = new ConcurrentHashMap<>();
    private List<BlockPos> placeLocations = new CopyOnWriteArrayList<>();
    private int remainingTicks;
	private BlockPos placePos, breakPos;
    private Timer lastPlaceOrBreak = new Timer();
	private boolean rotating;
    
	public static Setting players = new Setting(Mode.BOOLEAN, "Players", true, "Attacks players but not friends");
	public static Setting monsters = new Setting(Mode.BOOLEAN, "Monsters", false, "Attacks monsters");
	public static Setting neutrals = new Setting(Mode.BOOLEAN, "Neutrals", false, "Attacks neutral entities like enderman");
	public static Setting passive = new Setting(Mode.BOOLEAN, "Passive", false, "Attacks passive entities like animals");
    public static Setting breakMode = new Setting(null, "BreakMode", "Always", new String[]{"Allways"}, new String[]{"Smart"}, new String[]{"OnlyOwn"});
    public static Setting placeRadius = new Setting(Mode.DOUBLE, "PlaceRadius", 5.5, "Radius for placing");
    public static Setting breakRadius = new Setting(Mode.DOUBLE, "BreakRadius", 5.5, "Radius for breaking");
    public static Setting wallsRange = new Setting(Mode.DOUBLE, "WallsRange", 3.5, "Max distance through walls");
    public static Setting multiPlace = new Setting(Mode.BOOLEAN, "MultiPlace", false, "Tries to multiplace");
    public static Setting ticks = new Setting(Mode.INTEGER, "Ticks", 2, "The number of ticks to ignore on client update");
    public static Setting minDmg = new Setting(Mode.DOUBLE, "MinDMG", 5, "Minimum damage to do to your opponent");
    public static Setting maxSelfDmg = new Setting(Mode.DOUBLE, "MaxSelfDMG", 10, "Max self damage allowed");
    public static Setting facePlace = new Setting(Mode.DOUBLE, "FacePlace", 8, "Required target health for faceplacing");
    public static Setting autoSwitch = new Setting(Mode.BOOLEAN, "AutoSwitch", true, "Automatically switches to crystals in your hotbar");
    public static Setting pauseIfHittingBlock = new Setting(Mode.BOOLEAN, "PauseIfHittingBlock", false, "Pauses when your hitting a block with a pickaxe");
    public static Setting pauseWhileEating = new Setting(Mode.BOOLEAN, "PauseWhileEating", false, "Pauses while eating");
    public static Setting noSuicide = new Setting(Mode.BOOLEAN, "NoSuicide", true, "Doesn't commit suicide/pop if you are going to take fatal damage from self placed crystal");
    public static Setting antiWeakness = new Setting(Mode.BOOLEAN, "AntiWeakness", true, "Switches to a sword to try and break crystals");
    public static Setting extraRotatePackets = new Setting(Mode.BOOLEAN, "ExtraRotatePackets", false, "Sends a rotation packet every rotation", "Having this on will not work on most servers");
    public static Setting renderFillBox = new Setting(Mode.BOOLEAN, "RenderFillBox", true, "Renders filled box around place / break spot");
    	public static Setting renderFillBoxColor = new Setting(renderFillBox, Mode.INTEGER, "Color", 0x3600ffbf, "Color for the box in hex with alpha");
    public static Setting boundingBox = new Setting(Mode.BOOLEAN, "BoundingBox", true, "Renders bounding box around the place / break spot", "Like the corners", "If its breaking then its red and placing is green");
    	
    public AutoCrystal() {
        super(Group.COMBAT, "AutoCrystal", "Places and destroyes crystals", "In attempt to kill target");
    }
    
    @Override
    public void onEnabled() {
        placedCrystals.clear();
        remainingTicks = 0;
        lastPlaceOrBreak.reset();
        rotating = false;
    }

    @Override
    public void onDisabled() {
    	placePos = null;
    	breakPos = null;
    	RotationUtil.stopRotating();
    }
    
    @EventHandler
    private Listener<EntityRemovedEvent> onEntityRemove = new Listener<>(event -> {
        if (event.entity instanceof EntityEnderCrystal) {
        	attackedCrystals.remove((EntityEnderCrystal)event.entity);
        }
    });

    private boolean validateCrystal(EntityEnderCrystal e) {
        if (e == null || e.isDead) {
            return false;
        }
        
        if (attackedCrystals.containsKey(e) && attackedCrystals.get(e) > 5) {
            return false;
        }
        
        if (e.getDistance(mc.player) > (!mc.player.canEntityBeSeen(e) ? wallsRange.doubleValue() : breakRadius.doubleValue())) {
            return false;
        }
        
        if (breakMode.stringValue().equals("OnlyOwn")) {
        	return e.getDistance(e.posX, e.posY, e.posZ) <= 3;
        } else if (breakMode.stringValue().equals("Smart")) {
            float selfDamage = CrystalUtil.calculateDamage(new Vec3d(e.posX, e.posY, e.posZ), mc.player);
            if (selfDamage > maxSelfDmg.doubleValue() || noSuicide.booleanValue() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                return false;
            }

            //Finds the best position for most damage
            for (EntityPlayer player : mc.world.playerEntities) {
                //Ignore if the player is us, a friend, dead, or has no health (the dead variable is sometimes delayed)
                if (player == mc.player || Friends.isFriend(player) || mc.player.isDead || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }
                
                //Store this as a variable for faceplace per player
                double minDamage = minDmg.doubleValue();
                
                //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                if (player.getHealth() + player.getAbsorptionAmount() <= facePlace.doubleValue()) {
                    minDamage = 1f;
                }
                
                float calculatedDamage = CrystalUtil.calculateDamage(new Vec3d(e.posX, e.posY, e.posZ), player);
                if (calculatedDamage > minDamage) {
                    return true;
                }
            }
        }
        
        return true;
    }
    
    /*
     * Returns nearest crystal to an entity, if the crystal is not null or dead
     * @entity - entity to get smallest distance from
     */
    public EntityEnderCrystal GetNearestCrystalTo(Entity entity) {
        return mc.world.getLoadedEntityList().stream().filter(e -> e instanceof EntityEnderCrystal && validateCrystal((EntityEnderCrystal)e)).map(e -> (EntityEnderCrystal)e).min(Comparator.comparing(e -> entity.getDistance(e))).orElse(null);
    }
    
    public void addAttackedCrystal(EntityEnderCrystal crystal) {
        if (attackedCrystals.containsKey(crystal)) {
            int value = attackedCrystals.get(crystal);
            attackedCrystals.put(crystal, value + 1);
        } else {
        	attackedCrystals.put(crystal, 1);
        }
    }
    
    private boolean VerifyCrystalBlocks(BlockPos pos) {
    	//Check distance
        if (mc.player.getDistanceSq(pos) > placeRadius.doubleValue() * placeRadius.doubleValue()) {
            return false;
        }
        
        //Check walls range
        if (wallsRange.doubleValue() > 0) {
            if (!BlockUtil.canSeePos(pos) && pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ) > wallsRange.doubleValue()) {
                return false;
            }
        }
        
        //Check self damage
        float selfDamage = CrystalUtil.calculateDamage(pos, mc.player);
        
        //Make sure self damage is not greater than maxselfdamage
        if (selfDamage > maxSelfDmg.doubleValue()) {
            return false;
        }

        //No suicide, verify self damage won't kill us
        if (noSuicide.booleanValue() && selfDamage >= mc.player.getHealth()+mc.player.getAbsorptionAmount()) {
            return false;
        }
        
        //Its an ok position.
        return true;
    }
    
    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
    	if (mc.player == null) {
    		return;
    	}
    	
    	//Stop rotating if we havent destroyed or placed any crystals recently
    	if (rotating && lastPlaceOrBreak.hasPassed(500)) {
    		rotating = false;
    		placePos = null;
    		breakPos = null;
    		RotationUtil.stopRotating();
    	}
    	
        //This is our 1 second timer to remove our attackedEnderCrystals list, and remove the first placedCrystal for the visualizer.
        if (removeVisualTimer.hasPassed(1000)) {
            removeVisualTimer.reset();
            if (!placedCrystals.isEmpty()) {
                placedCrystals.remove(0);
            }
            
            attackedCrystals.clear();
        }
        
        if (remainingTicks > 0) {
            remainingTicks--;
            return;
        }
        
        if (needPause()) {
            remainingTicks = 0;
            return;
        }
        
        remainingTicks = ticks.intValue();
        
        //This is the most expensive code, we need to get valid crystal blocks. -> todo verify stream to see if it's slower than normal looping.
        final List<BlockPos> cachedCrystalBlocks = CrystalUtil.findCrystalBlocks(mc.player, (float)placeRadius.doubleValue()).stream().filter(pos -> VerifyCrystalBlocks(pos)).collect(Collectors.toList());
        
        //This is where we will iterate through all players (for most damage) and cachedCrystalBlocks
        if (!cachedCrystalBlocks.isEmpty()) {
            float damage = 0f;
            EntityLivingBase target = null;
            
            //Iterate through all entities, and crystal positions to find the best position for most damage
            for (Entity entity2 : mc.world.loadedEntityList) {
            	EntityLivingBase entity = null;
            	try {
            		entity = (EntityLivingBase)entity2;
            	} catch (ClassCastException ex) {
            		continue;
            	}
            	
                //Ignore if the player is us, dead, or has no health (the dead variable is sometimes delayed)
                if (entity == mc.player || mc.player.isDead || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }
                
                //continue if the entity isnt accepted type
                if (entity instanceof EntityPlayer && !players.booleanValue() || entity instanceof EntityPlayer && Friends.isFriend(entity) || EntityUtil.isHostileMob(entity) && !monsters.booleanValue()
                		|| EntityUtil.isNeutralMob(entity) && !neutrals.booleanValue() || EntityUtil.isPassive(entity) && !passive.booleanValue()) {
                	continue;
                }
                
                //Store this as a variable for faceplace per player
                double minDamage = minDmg.doubleValue();
                
                //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                if (entity.getHealth() + entity.getAbsorptionAmount() <= facePlace.doubleValue()) {
                    minDamage = 1f;
                }
                
                //Iterate through all valid crystal blocks for this player, and calculate the damages.
                for (BlockPos pos : cachedCrystalBlocks) {
                    float calculatedDamage = CrystalUtil.calculateDamage(pos, entity);
                    
                    if (calculatedDamage >= minDamage && calculatedDamage > damage) {
                        damage = calculatedDamage;
                        if (!placeLocations.contains(pos)) {
                            placeLocations.add(pos);
                        }
                        
                        target = entity;
                    }
                }
            }
            
            if (target != null) {
                //The player could have died during this code run, wait till next tick for doing more calculations.
                if (target.isDead || target.getHealth() <= 0.0f) {
                    return;
                }
                
                //Ensure we have place locations
                if (!placeLocations.isEmpty()) {
                    //Store this as a variable for faceplace per player
                    double minDamage = minDmg.doubleValue();
                    
                    //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                    if (target.getHealth() + target.getAbsorptionAmount() <= facePlace.doubleValue()) {
                        minDamage = 1f;
                    }
                    
                    //Iterate this again, we need to remove some values that are useless, since we iterated all players
                    for (BlockPos pos : placeLocations) {
                        //Make sure the position will still deal enough damage to the player
                        float calculatedDamage = CrystalUtil.calculateDamage(pos, target);
                     
                        //Remove if this doesnt
                        if (calculatedDamage < minDamage) {
                            placeLocations.remove(pos);
                        }
                    }
                    
                    //At this point, the place locations list is in asc order, we need to reverse it to get to desc
                    Collections.reverse(placeLocations);
                }
            }
        }
        
        //At this point, we are going to destroy/place crystals.
        //Get nearest crystal to the player, we will need to null check this on the timer.
        EntityEnderCrystal crystal = GetNearestCrystalTo(mc.player);
        
        //Get a valid crystal in range, and check if it's in break radius
        boolean isValidCrystal = crystal != null ? mc.player.getDistance(crystal) < breakRadius.doubleValue() : false;
        if (!isValidCrystal && placeLocations.isEmpty()) {
            remainingTicks = 0;
            return;
        }
        
        //We are checking null here because we don't want to waste time not destroying crystals right away
        if (isValidCrystal) {
            if (antiWeakness.booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
    			//If player has strenght 2 then dont do it because strenght 2 allows u to break it with ur fist
    			boolean hasStrenght2 = false;
    			for (PotionEffect effect : mc.player.getActivePotionEffects()) {
    				if (effect.getEffectName().contains("damageBoost") && effect.getAmplifier() == 1) {
    					hasStrenght2 = true;
    					break;
    				}
    			}
            	
                if (!hasStrenght2 && mc.player.getHeldItemMainhand() == ItemStack.EMPTY || !hasStrenght2 && (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemTool))) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        
                        if (stack.isEmpty()) {
                            continue;
                        }
                        
                        if (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword) {
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }
            }
            
            //Rotate to crystal
            rotate(new Vec3d(crystal.posX + 0.5, crystal.posY + 0.5, crystal.posZ + 0.5));
            rotating = true;
            
            //Swing arm and attack the entity
            breakPos = crystal.getPosition().add(0, -1, 0);
            placePos = null;
            lastPlaceOrBreak.reset();
            mc.playerController.attackEntity(mc.player, crystal);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            addAttackedCrystal(crystal);
            
            //If we are not multiplacing return here, we have something to do for this tick.
            if (!multiPlace.booleanValue()) {
                return;
            }
        }
        
        //Verify the placeTimer is ready, selectedPosition is not 0,0,0 and the event isn't already cancelled
        if (!placeLocations.isEmpty()) {
            //If player is not holding crystals switch to them or return if autoswitch is off
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                if (autoSwitch.booleanValue() && InventoryUtil.hasItem(Items.END_CRYSTAL)) {
            		InventoryUtil.switchItem(InventoryUtil.getSlot(Items.END_CRYSTAL), false);
                    mc.playerController.updateController();
                } else {
                	return;
                }
            }
            
            //Iterate through available place locations
            BlockPos selectedPos = null;
            for (BlockPos pos : placeLocations) {
                // verify we can still place crystals at this location, if we can't we try next location
                if (CrystalUtil.canPlaceCrystal(pos)) {
                    selectedPos = pos;
                    break;
                }
            }
            
            //Nothing found... this is bad, wait for next tick to correct it
            if (selectedPos == null) {
                remainingTicks = 0;
                return;
            }
            
            //Rotate to block where its gonna place it
            rotate(new Vec3d(selectedPos.getX() + 0.5, selectedPos.getY() + 0.5, selectedPos.getZ() + 0.5));
            rotating = true;
            
            //Create a raytrace between player's position and the selected block position
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(selectedPos.getX() + 0.5, selectedPos.getY() - 0.5, selectedPos.getZ() + 0.5));
    
            //This will allow for bypassing placing through walls afaik
            EnumFacing facing;
            if (result == null || result.sideHit == null) {
                facing = EnumFacing.UP;
            } else {
                facing = result.sideHit;
            }
            
            placePos = selectedPos;
            breakPos = null;
            lastPlaceOrBreak.reset();
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(selectedPos, facing, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            
            //If placedcrystals already contains this position, remove it because we need to have it at the back of the list
            if (placedCrystals.contains(selectedPos)) {
                placedCrystals.remove(selectedPos);
            }
            
            //Adds the selectedPos to the back of the placed crystals list
            placedCrystals.add(selectedPos);
            
            //Reset the placed location, we just placed there
            placeLocations.clear();
        }
    }
    
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.packet;

            if (mc.world == null) {
                return;
            }
            
            //We need to remove crystals on this packet, because the server sends packets too slow to remove them
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                //LoadedEntityList is not thread safe, create a copy and iterate it
                new ArrayList<Entity>(mc.world.loadedEntityList).forEach(e ->  {
                    //If it's an endercrystal, within 6 distance, set it to be dead
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
                            e.setDead();
                        }
                    }
                    
                    //Remove all crystals within 6 blocks from the placed crystals list
                    placedCrystals.removeIf(p_Pos -> p_Pos.getDistance((int)packet.getX(), (int)packet.getY(), (int)packet.getZ()) <= 6.0);
                });
            }
        }
    });
    
    @Override
    public void onRenderWorld(float partialTicks) {
    	BlockPos pos = placePos;
    	Color color = Color.GREEN;
    	if (pos == null) {
    		pos = breakPos;
    		color = Color.RED;
    	}
    	
    	if (pos != null) {
    		if (boundingBox.booleanValue()) {
    			RenderUtil.drawBoundingBox(RenderUtil.getBB(pos, 1), 1, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    		}
			
    		if (renderFillBox.booleanValue()) {
    			RenderUtil.drawFilledBox(RenderUtil.getBB(pos, 1), renderFillBoxColor.intValue());
    		}
    	}
    }
    
    public void rotate(Vec3d vec) {
    	if (extraRotatePackets.booleanValue()) {
    		RotationUtil.rotateSpoof(vec);
    	} else {
    		RotationUtil.rotateSpoofNoPacket(vec);
    	}
    }
    
    public boolean needPause() {
    	return false;
    }
}