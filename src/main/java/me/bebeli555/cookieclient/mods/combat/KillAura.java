package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.hud.components.LagNotifierComponent;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.utils.EntityUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class KillAura extends Mod {
	private static boolean isRotating;
	
	public static Setting range = new Setting(Mode.DOUBLE, "Range", 4.5, "How far the target can be");
	public static Setting players = new Setting(Mode.BOOLEAN, "Players", true, "Attacks players but not friends");
	public static Setting monsters = new Setting(Mode.BOOLEAN, "Monsters", false, "Attacks monsters");
	public static Setting neutrals = new Setting(Mode.BOOLEAN, "Neutrals", false, "Attacks neutral entities like enderman");
	public static Setting passive = new Setting(Mode.BOOLEAN, "Passive", false, "Attacks passive entities like animals");
	public static Setting pauseIfCrystal = new Setting(Mode.BOOLEAN, "PauseIfCrystal", false, "Pauses killaura if ur holding an end crystal");
	public static Setting pauseIfGap = new Setting(Mode.BOOLEAN, "PauseIfGap", true, "Pauses killaura if ur holding a gapple");
	public static Setting tpsSync = new Setting(Mode.BOOLEAN, "TPSSync", true, "Syncs attack delay with tps");
	
	public KillAura() {
		super(Group.COMBAT, "KillAura", "Attacks nearby targets automatically");
	}

	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null) {
			return;
		}
		
		//Pause if holding crystals or gaps and the settings are on
		if (pauseIfCrystal.booleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || pauseIfGap.booleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE) {
			if (isRotating) RotationUtil.stopRotating();
			isRotating = false;
			return;
		}
		
		//Search entity to attack
		Entity best = null;
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity.getDistance(mc.player) <= range.doubleValue() && isValid(entity)) {
				if (best == null || entity.getDistance(mc.player) < best.getDistance(mc.player)) {
					best = entity;
				}
			}
		}
		
		//Return and stop rotating if no entity found
		if (best == null) {
			if (isRotating) RotationUtil.stopRotating();
			isRotating = false;
			return;
		}
		
		//Switch to weapon in hotbar
		int slot = getWeaponSlot();
		
		if (slot != -1 && slot != mc.player.inventory.currentItem) {
			mc.player.inventory.currentItem = slot;
			mc.playerController.updateController();
		}
		
		//Rotate to entity
		isRotating = true;
		RotationUtil.rotateSpoofNoPacket(new Vec3d(best.posX, best.posY + 1, best.posZ));
		
		//Attack entity
        float ticks = 20.0f - (float)LagNotifierComponent.getTps();
        boolean isReady = mc.player.getCooledAttackStrength(tpsSync.booleanValue() ? -ticks : 0.0f) >= 1;
        
        if (isReady) {
            mc.player.connection.sendPacket(new CPacketUseEntity(best));
            mc.player.swingArm(EnumHand.MAIN_HAND);
            
            if (mc.playerController.currentGameType != GameType.SPECTATOR) {
            	attackTargetEntityWithCurrentItem(best);
                mc.player.resetCooldown();
            }
        }
	}
	
	public static boolean isValid(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.renderViewEntity) || !entity.isEntityAlive()) {
			return false;
		}
		
		if (entity instanceof EntityPlayer && players.booleanValue() && !Friends.isFriend(entity)) {
			return true;
		}
		
		if (EntityUtil.isHostileMob(entity) && monsters.booleanValue()) {
			return true;
		}
		
		if (EntityUtil.isNeutralMob(entity) && neutrals.booleanValue()) {
			return true;
		}
		
		if (EntityUtil.isPassive(entity) && passive.booleanValue()) {
			return true;
		}
		
		return false;
	}
	
	public static int getWeaponSlot() {
		Item[] items = {Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.IRON_SWORD, Items.IRON_AXE, Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.STONE_SWORD,
		Items.STONE_AXE, Items.WOODEN_SWORD, Items.WOODEN_AXE};
		
		for (Item item : items) {
			for (int i = 0; i < 9; i++) {
				ItemStack itemStack = InventoryUtil.getItemStack(i);
				 
				if (itemStack.getItem() == item) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public static boolean isWeapon(Item item) {
		Item[] items = {Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.IRON_SWORD, Items.IRON_AXE, Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.STONE_SWORD,
		Items.STONE_AXE, Items.WOODEN_SWORD, Items.WOODEN_AXE};
		
		for (Item check : items) {
			if (check == item) {
				return true;
			}
		}
		
		return false;
	}
	
	//This is from minecrafts source code but just removed the thing where it stops sprinting after a hit
    public static void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(mc.player)) {
                float f = (float)mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                float f1;

                if (targetEntity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
                } else {
                    f1 = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
                }

                float f2 = mc.player.getCooledAttackStrength(0.5F);
                f = f * (0.2F + f2 * f2 * 0.8F);
                f1 = f1 * f2;
                mc.player.resetCooldown();

                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockbackModifier(mc.player);

                    if (mc.player.isSprinting() && flag)  {
                    	mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, mc.player.getSoundCategory(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && mc.player.fallDistance > 0.0F && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(MobEffects.BLINDNESS) && !mc.player.isRiding() && targetEntity instanceof EntityLivingBase;
                    flag2 = flag2 && !mc.player.isSprinting();

                    if (flag2) {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean flag3 = false;
                    double d0 = (double)(mc.player.distanceWalkedModified - mc.player.prevDistanceWalkedModified);

                    if (flag && !flag2 && !flag1 && mc.player.onGround && d0 < (double)mc.player.getAIMoveSpeed()) {
                        ItemStack itemstack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f4 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(mc.player);

                    if (targetEntity instanceof EntityLivingBase) {
                        f4 = ((EntityLivingBase)targetEntity).getHealth();

                        if (j > 0 && !targetEntity.isBurning()) {
                            flag4 = true;
                            targetEntity.setFire(1);
                        }
                    }

                    double d1 = targetEntity.motionX;
                    double d2 = targetEntity.motionY;
                    double d3 = targetEntity.motionZ;
                    boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(mc.player), f);

                    if (flag5) {
                        if (i > 0) {
                            if (targetEntity instanceof EntityLivingBase) {
                                ((EntityLivingBase)targetEntity).knockBack(mc.player, (float)i * 0.5F, (double)MathHelper.sin(mc.player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(mc.player.rotationYaw * 0.017453292F)));
                            } else  {
                                targetEntity.addVelocity((double)(-MathHelper.sin(mc.player.rotationYaw * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(mc.player.rotationYaw * 0.017453292F) * (float)i * 0.5F));
                            }

                            //mc.player.motionX *= 0.6D;
                            //mc.player.motionZ *= 0.6D;
                            //mc.player.setSprinting(false);
                        }

                        if (flag3) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(mc.player) * f;

                            for (EntityLivingBase entitylivingbase : mc.player.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D))) {
                                if (entitylivingbase != mc.player && entitylivingbase != targetEntity && !mc.player.isOnSameTeam(entitylivingbase) && mc.player.getDistanceSq(entitylivingbase) < 9.0D) {
                                    entitylivingbase.knockBack(mc.player, 0.4F, (double)MathHelper.sin(mc.player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(mc.player.rotationYaw * 0.017453292F)));
                                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(mc.player), f3);
                                }
                            }

                            mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, mc.player.getSoundCategory(), 1.0F, 1.0F);
                            mc.player.spawnSweepParticles();
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                            ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = d1;
                            targetEntity.motionY = d2;
                            targetEntity.motionZ = d3;
                        }

                        if (flag2) {
                        	mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, mc.player.getSoundCategory(), 1.0F, 1.0F);
                        	mc.player.onCriticalHit(targetEntity);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                            	mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, mc.player.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                            	mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, mc.player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                        	mc.player.onEnchantmentCritical(targetEntity);
                        }

                        mc.player.setLastAttackedEntity(targetEntity);

                        if (targetEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, mc.player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(mc.player, targetEntity);
                        ItemStack itemstack1 = mc.player.getHeldItemMainhand();
                        Entity entity = targetEntity;

                        if (targetEntity instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;

                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase)  {
                            itemstack1.hitEntity((EntityLivingBase)entity, mc.player);

                            if (itemstack1.isEmpty()) {
                            	mc.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                            float f5 = f4 - ((EntityLivingBase)targetEntity).getHealth();
                            mc.player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

                            if (j > 0) {
                                targetEntity.setFire(j * 4);
                            }

                            if (mc.player.world instanceof WorldServer && f5 > 2.0F) {
                                int k = (int)((double)f5 * 0.5D);
                                ((WorldServer)mc.player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        mc.player.addExhaustion(0.1F);
                    } else {
                    	mc.player.world.playSound((EntityPlayer)null, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, mc.player.getSoundCategory(), 1.0F, 1.0F);

                        if (flag4) {
                            targetEntity.extinguish();
                        }
                    }
                }
            }
        }
    }
 }
