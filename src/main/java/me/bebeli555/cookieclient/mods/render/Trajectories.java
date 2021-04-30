package me.bebeli555.cookieclient.mods.render;

import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLineWidth;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Trajectories extends Mod {
	private Queue<Vec3d> flightPoint = new ConcurrentLinkedQueue<>();
	
	public static Setting width = new Setting(Mode.DOUBLE, "Width", 1, "How thicc the lines are");
	public static Setting red = new Setting(Mode.INTEGER, "Red", 66, "Rbg color");
	public static Setting green = new Setting(Mode.INTEGER, "Green", 245, "Rbg color");
	public static Setting blue = new Setting(Mode.INTEGER, "Blue", 218, "Rbg color");
	public static Setting alpha = new Setting(Mode.INTEGER, "Alpha", 255, "Rbg color");
	
	public Trajectories() {
		super(Group.RENDER, "Trajectories", "Renders where projectiles will land when shot", "Like enderpearls, arrows etc");
	}
	
	@Override
	public void onRenderWorld(float partialTicks) {
        ThrowableType throwingType = getTypeFromCurrentItem(mc.player);
        if (throwingType == ThrowableType.NONE) {
            return;
        }

        FlightPath flightPath = new FlightPath(mc.player, throwingType);
        while (!flightPath.isCollided()) {
            flightPath.onUpdate();

            flightPoint.offer(new Vec3d(flightPath.position.x - mc.getRenderManager().viewerPosX, flightPath.position.y - mc.getRenderManager().viewerPosY,
            flightPath.position.z - mc.getRenderManager().viewerPosZ));
        }

        boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);
        glLineWidth((float)width.doubleValue());
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableDepth();
        glEnable(GL32.GL_DEPTH_CLAMP);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        while (!flightPoint.isEmpty()) {
            bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            Vec3d head = flightPoint.poll();
            bufferbuilder.pos(head.x, head.y, head.z).color(red.intValue() / 255.0f, green.intValue()  / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f).endVertex();

            if (flightPoint.peek() != null) {
                Vec3d point = flightPoint.peek();
                bufferbuilder.pos(point.x, point.y, point.z).color(red.intValue() / 255.0f, green.intValue() / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f).endVertex();
            }

            tessellator.draw();
        }

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

        if (flightPath.collided) {
            RayTraceResult hit = flightPath.target;
            AxisAlignedBB bb = null;
            
            if (hit == null) {
                return;
            }

            if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = hit.getBlockPos();
                IBlockState iblockstate = mc.world.getBlockState(blockpos);

                if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                    Vec3d interp = Tracers.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    bb = iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z);
                }
            } else if (hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit != null) {
                AxisAlignedBB entityBB = hit.entityHit.getEntityBoundingBox();
                if (entityBB != null) {
                    bb = new AxisAlignedBB(entityBB.minX - mc.getRenderManager().viewerPosX, entityBB.minY - mc.getRenderManager().viewerPosY, entityBB.minZ - mc.getRenderManager().viewerPosZ,
                    entityBB.maxX - mc.getRenderManager().viewerPosX, entityBB.maxY - mc.getRenderManager().viewerPosY, entityBB.maxZ - mc.getRenderManager().viewerPosZ);
                }
            }

            if (bb != null) {
                RenderUtil.drawBoundingBox(bb, (float)width.doubleValue(), red.intValue() / 255.0f, green.intValue() / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f);
            }
        }
	}
	
    public static ThrowableType getTypeFromCurrentItem(EntityPlayerSP player) {
        if (player.getHeldItemMainhand() == null) {
            return ThrowableType.NONE;
        }

        ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
        switch (Item.getIdFromItem(itemStack.getItem())) {
            case 261: // ItemBow
                if (player.isHandActive())
                    return ThrowableType.ARROW;
                break;
            case 346: // ItemFishingRod
                return ThrowableType.FISHING_ROD;
            case 438: // splash potion
            case 441: // splash potion linger
                return ThrowableType.POTION;
            case 384: // ItemExpBottle
                return ThrowableType.EXPERIENCE;
            case 332: // ItemSnowball
            case 344: // ItemEgg
            case 368: // ItemEnderPearl
                return ThrowableType.NORMAL;
            default:
                break;
        }

        return ThrowableType.NONE;
    }
    
    public static class FlightPath {
        private EntityPlayerSP shooter;
        private Vec3d position;
        private Vec3d motion;
        private float yaw;
        private float pitch;
        private AxisAlignedBB boundingBox;
        private boolean collided;
        private RayTraceResult target;
        private ThrowableType throwableType;

        FlightPath(EntityPlayerSP player, ThrowableType throwableType) {
            this.shooter = player;
            this.throwableType = throwableType;
            this.setLocationAndAngles(this.shooter.posX, this.shooter.posY + this.shooter.getEyeHeight(), this.shooter.posZ, this.shooter.rotationYaw, this.shooter.rotationPitch);
            Vec3d startingOffset = new Vec3d(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F, 0.1d, MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
            this.position = this.position.subtract(startingOffset);
            this.setPosition(this.position);
            this.motion = new Vec3d(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI),
            -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI), MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));
            this.setThrowableHeading(this.motion, this.getInitialVelocity());
        }

        public void onUpdate() {
            Vec3d prediction = this.position.add(this.motion);
            RayTraceResult blockCollision = this.shooter.getEntityWorld().rayTraceBlocks(this.position, prediction, this.throwableType == ThrowableType.FISHING_ROD, !this.collidesWithNoBoundingBox(), false);

            if (blockCollision != null) {
                prediction = blockCollision.hitVec;
            }

            this.onCollideWithEntity(prediction, blockCollision);

            if (this.target != null) {
                this.collided = true;
                this.setPosition(this.target.hitVec);
                return;
            }

            if (this.position.y <= 0.0d) {
                this.collided = true;
                return;
            }

            this.position = this.position.add(this.motion);
            float motionModifier = 0.99F;
            if (this.shooter.getEntityWorld().isMaterialInBB(this.boundingBox, Material.WATER)) {
                motionModifier = this.throwableType == ThrowableType.ARROW ? 0.6F : 0.8F;
            }

            if (this.throwableType == ThrowableType.FISHING_ROD) {
                motionModifier = 0.92f;
            }

            this.motion = new Vec3d(motion.x * motionModifier, motion.y * motionModifier, motion.z * motionModifier);
            this.motion = this.motion.subtract(0.0d, this.getGravityVelocity(), 0.0d);
            this.setPosition(this.position);
        }

        private boolean collidesWithNoBoundingBox() {
            switch (this.throwableType) {
                case FISHING_ROD:
                case NORMAL:
                    return true;
                default:
                    return false;
            }
        }

        private void onCollideWithEntity(Vec3d prediction, RayTraceResult blockCollision) {
            Entity collidingEntity = null;
            RayTraceResult collidingPosition = null;

            double currentDistance = 0.0d;
            List<Entity> collisionEntities = Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(this.shooter,
            this.boundingBox.expand(this.motion.x, this.motion.y, this.motion.z).grow(1.0D, 1.0D, 1.0D));

            for (Entity entity : collisionEntities) {
                if (!entity.canBeCollidedWith()) {
                    continue;
                }

                float collisionSize = entity.getCollisionBorderSize();
                AxisAlignedBB expandedBox = entity.getEntityBoundingBox().expand(collisionSize, collisionSize, collisionSize);
                RayTraceResult objectPosition = expandedBox.calculateIntercept(this.position, prediction);

                if (objectPosition != null) {
                    double distanceTo = this.position.distanceTo(objectPosition.hitVec);

                    if (distanceTo < currentDistance || currentDistance == 0.0D) {
                        collidingEntity = entity;
                        collidingPosition = objectPosition;
                        currentDistance = distanceTo;
                    }
                }
            }

            if (collidingEntity != null) {
                this.target = new RayTraceResult(collidingEntity, collidingPosition.hitVec);
            } else {
                this.target = blockCollision;
            }
        }

        private float getInitialVelocity() {
            switch (this.throwableType) {
                case ARROW:
                    int useDuration = this.shooter.getHeldItem(EnumHand.MAIN_HAND).getItem().getMaxItemUseDuration(this.shooter.getHeldItem(EnumHand.MAIN_HAND)) - this.shooter.getItemInUseCount();
                    float velocity = (float) useDuration / 20.0F;
                    velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
                    if (velocity > 1.0F) {
                        velocity = 1.0F;
                    }

                    return (velocity * 2.0f) * throwableType.getVelocity();
                default:
                    return throwableType.getVelocity();
            }
        }

        private float getGravityVelocity() {
            return throwableType.getGravity();
        }

        private void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
            this.position = new Vec3d(x, y, z);
            this.yaw = yaw;
            this.pitch = pitch;
        }

        private void setPosition(Vec3d position) {
            this.position = new Vec3d(position.x, position.y, position.z);
            double entitySize = (this.throwableType == ThrowableType.ARROW ? 0.5d : 0.25d) / 2.0d;
            this.boundingBox = new AxisAlignedBB(position.x - entitySize, position.y - entitySize, position.z - entitySize, position.x + entitySize, position.y + entitySize, position.z + entitySize);
        }

        private void setThrowableHeading(Vec3d motion, float velocity) {
            this.motion = new Vec3d(motion.x / (float)motion.length(), motion.y / (float)motion.length(), motion.z / (float)motion.length());
            this.motion = new Vec3d(motion.x * velocity, motion.y * velocity, motion.z * velocity);
        }

        public boolean isCollided() {
            return collided;
        }

        public RayTraceResult getCollidingTarget() {
            return target;
        }
    }
    
    public static enum ThrowableType {
        NONE(0.0f, 0.0f),
        ARROW(1.5f, 0.05f),
        POTION(0.5f, 0.05f),
        EXPERIENCE(0.7F, 0.07f),
        FISHING_ROD(1.5f, 0.04f),
        NORMAL(1.5f, 0.03f);

        private float velocity;
        private float gravity;
        
        ThrowableType(float velocity, float gravity) {
            this.velocity = velocity;
            this.gravity = gravity;
        }

        public float getVelocity() {
            return velocity;
        }

        public float getGravity() {
            return gravity;
        }
    }
}
