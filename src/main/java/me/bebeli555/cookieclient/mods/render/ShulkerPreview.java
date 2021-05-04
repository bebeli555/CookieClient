
package me.bebeli555.cookieclient.mods.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.render.RenderTooltipEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShulkerPreview extends Mod {
	public static Setting enderChest = new Setting(Mode.BOOLEAN, "EnderChest", true, "Allows you to see ur enderchest content", "(You need to open it once before)");

    private ArrayList<ItemStack> enderChestItems = new ArrayList<ItemStack>();
    private HashMap<String, List<ItemStack>> savedShulkerItems = new HashMap<String, List<ItemStack>>();
    private int enderChestWindowId = -1;
    private int shulkerWindowId = -1;
    private Timer timer = new Timer();
    private String lastWindowTitle = "";

    public ShulkerPreview() {
    	super(Group.RENDER, "ShulkerPreview", "See shulker and enderchest content", "By hovering ur mouse over them");
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    	if (event.getEntity() == null || !(event.getEntity() instanceof EntityItem)) {
    		return;
    	}
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EntityItem item = (EntityItem)event.getEntity();
                
                if (!(item.getItem().getItem() instanceof ItemShulkerBox)) {
                    return;
                }
                
                ItemStack shulker = item.getItem();
                NBTTagCompound shulkerNBT = getShulkerNBT(shulker);
                
                if (shulkerNBT != null) {
                    TileEntityShulkerBox fakeShulker = new TileEntityShulkerBox();
                    fakeShulker.loadFromNbt(shulkerNBT);
                    String customName = shulker.getDisplayName();

                    ArrayList<ItemStack> items = new ArrayList<ItemStack>();                    
                    
                    for (int i = 0; i < 27; i++) {
                    	items.add(fakeShulker.getStackInSlot(i));
                    }
                    
                    if (savedShulkerItems.containsKey(customName)) {
                        savedShulkerItems.remove(customName);
                    }
                    
                    savedShulkerItems.put(customName, items);
                }
            }
        }, 5000);
    }

    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketWindowItems) {
            final SPacketWindowItems packet = (SPacketWindowItems)event.packet;

            if (packet.getWindowId() == enderChestWindowId) {
                enderChestItems.clear();

                for (int i = 0; i < packet.getItemStacks().size(); ++i) {
                    ItemStack itemStack = packet.getItemStacks().get(i);
                    if (itemStack == null) {
                        continue;
                    }
                        
                    if (i > 26) {
                        break;
                    }

                    enderChestItems.add(itemStack);
                }
            } else if (packet.getWindowId() == shulkerWindowId) {
                if (savedShulkerItems.containsKey(lastWindowTitle)) {
                    savedShulkerItems.remove(lastWindowTitle);
                }
                
                ArrayList<ItemStack> list = new ArrayList<ItemStack>();

                for (int i = 0; i < packet.getItemStacks().size(); ++i) {
                    ItemStack itemStack = packet.getItemStacks().get(i);
                    if (itemStack == null) {
                        continue;
                    }
                       
                    if (i > 26) {
                        break;
                    }

                    list.add(itemStack);
                }
                
                savedShulkerItems.put(lastWindowTitle, list);
            }
        } else if (event.packet instanceof SPacketOpenWindow) {
            final SPacketOpenWindow packet = (SPacketOpenWindow) event.packet;

            if (packet.getWindowTitle().getFormattedText().startsWith("Ender")) {
                enderChestWindowId = packet.getWindowId();
            } else {
                shulkerWindowId = packet.getWindowId();
                lastWindowTitle = packet.getWindowTitle().getUnformattedText();
            }
        }
    });

    @EventHandler
    private Listener<RenderTooltipEvent> renderTooltip = new Listener<>(event -> {
        if (event.itemStack == null) {
            return;
        }

        //Render enderchest
        if (enderChest.booleanValue() && Item.getIdFromItem(event.itemStack.getItem()) == 130) {
            int x = event.x;
            int y = event.y;

            GlStateManager.translate(x + 10, y - 5, 0);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            
            GuiScreen.drawRect(-3, -mc.fontRenderer.FONT_HEIGHT - 4, 9 * 16 + 3, 3 * 16 + 3, 0x99101010);
            GuiScreen.drawRect(-2, -mc.fontRenderer.FONT_HEIGHT - 3, 9 * 16 + 2, 3 * 16 + 2, 0xFF202020);
            GuiScreen.drawRect(0, 0, 9 * 16, 3 * 16, 0xFF101010);

            mc.fontRenderer.drawStringWithShadow("EnderChest Preview", 0, -mc.fontRenderer.FONT_HEIGHT - 1, 0xFFFFFFFF);
            
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 150.0F;
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < enderChestItems.size(); i++) {
                ItemStack itemStack = enderChestItems.get(i);
                if (itemStack == null) {
                    continue;
                }

                int offsetX = (i % 9) * 16;
                int offsetY = (i / 9) * 16;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
            }

            event.cancel();

            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.translate(-(x + 10), -(y - 5), 0);
        }
        
        //Render shulkerbox
        else if (event.itemStack.getItem() instanceof ItemShulkerBox) {
            ItemStack shulker = event.itemStack;
            NBTTagCompound tagCompound = shulker.getTagCompound();
            
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                
                if (blockEntityTag.hasKey("Items", 9)) {
                    event.cancel();

                    NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);

                    int x = event.x;
                    int y = event.y;

                    GlStateManager.translate(x + 10, y - 5, 0);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();

                    GuiScreen.drawRect(-3, -mc.fontRenderer.FONT_HEIGHT - 4, 9 * 16 + 3, 3 * 16 + 3, 0x99101010);
                    GuiScreen.drawRect(-2, -mc.fontRenderer.FONT_HEIGHT - 3, 9 * 16 + 2, 3 * 16 + 2, 0xFF202020);
                    GuiScreen.drawRect(0, 0, 9 * 16, 3 * 16, 0xFF101010);

                    mc.fontRenderer.drawStringWithShadow(shulker.getDisplayName(), 0, -mc.fontRenderer.FONT_HEIGHT - 1, 0xFFFFFFFF);

                    GlStateManager.enableDepth();
                    mc.getRenderItem().zLevel = 150.0F;
                    RenderHelper.enableGUIStandardItemLighting();

                    for (int i = 0; i < nonnulllist.size(); i++) {
                        ItemStack itemStack = nonnulllist.get(i);
                        int offsetX = (i % 9) * 16;
                        int offsetY = (i / 9) * 16;
                        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
                    }

                    RenderHelper.disableStandardItemLighting();
                    mc.getRenderItem().zLevel = 0.0F;
                    GlStateManager.enableLighting();
                    GlStateManager.translate(-(x + 10), -(y - 5), 0);
                }
            }     
        }
    });
    
    //Get NBT data for the shulkerbox itemStack
    public NBTTagCompound getShulkerNBT(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound tags = compound.getCompoundTag("BlockEntityTag");
            if (tags.hasKey("Items", 9)) {
                return tags;
            }
        }

        return null;
    }
}
