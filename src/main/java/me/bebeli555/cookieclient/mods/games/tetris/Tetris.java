package me.bebeli555.cookieclient.mods.games.tetris;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiScreenEvent;

public class Tetris extends Mod {
	public static Tetris instance;
	public static int fromX, toX;
	public static int fromY, toY;
	public static TetrisNode currentNode;
	public static int beenDown = 0;
	public static boolean gameOver = true;
	public static int score = 0;
	public static long lastSec = 0;
	public static long lastSecMove = 0;
	
	public Tetris() {
		super(Group.GAMES, "Tetris", "Tetris game");
		instance = this;
	}
	
	@Override
	public void onGuiDrawScreen(int mouseX, int mouseY, float partialTicks) {
		//Set values
		fromX = 400;
		toX = fromX + 150;
		fromY = 150;
		toY = fromY + 250;

		GuiScreen.drawRect(fromX, fromY, toX, toY, 0xFF000000);
		
		int Divided = 150 - (score / 10);
		if (Divided < 10) {
			Divided = 10;
		}
		long sec = System.currentTimeMillis() / Divided;
		if (sec != lastSec && gameOver == false) {
			//Create new node from the top.
			if (TetrisNode.nodes.isEmpty() || currentNode.canGoDown() == false) {	
				score++;
				BlockPos Player = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
				mc.world.playSound(Player, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.AMBIENT, 10222.5f, 1.5f, true);
				removeLayer();
				setShapes();
				
				if (!currentNode.canGoDown()) {
					GameOver();
				}
			}
			
			if (currentNode.canGoDown()) {
				currentNode.moveDown();
			}
			lastSec = sec;
		}
		
		long sec2 = System.currentTimeMillis() / 40;
		if (sec2 != lastSecMove) {
			// Move automatically if holding key
			if (gameOver == false && currentNode != null) {
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					beenDown++;
					if (beenDown > 3) {
						if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
							if (currentNode.canMoveRight()) {
								currentNode.moveRight();
							}
						} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
							if (currentNode.canMoveLeft()) {
								currentNode.moveLeft();
								;
							}
						} else {
							if (currentNode.canGoDown()) {
								currentNode.moveDown();
							}
						}
					}
				} else {
					beenDown = 0;
				}
			}
			lastSecMove = sec2;
		}
		
		//Draw the tetris blocks.
		for (int i = 0; i < TetrisNode.nodes.size(); i++) {
			TetrisNode node = TetrisNode.nodes.get(i);
			GuiScreen.drawRect(node.getX(), node.getY(), node.getX() + TetrisNode.multiplier, node.getY() - TetrisNode.multiplier, node.getColor());
		}
		
		//Draw white lines.
		drawWhiteLine(fromX, toX, fromY, toY);
		
		//Draw score
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.25F, 1.25F, 1.25F);
		mc.fontRenderer.drawStringWithShadow(ChatFormatting.DARK_AQUA + "Score: " + ChatFormatting.GREEN + score, 325, 125, 0xffff);
		GlStateManager.popMatrix();
		
		if (gameOver == true) {
			// GameOver Screen
			GlStateManager.pushMatrix();
			GlStateManager.scale(2.5F, 2.5F, 2.5F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED + "Game Over!", 163, 70, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED + "Score = " + ChatFormatting.GREEN + score, 165, 80, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.AQUA + "Controls:", 167, 95, 0xffff);
			GuiScreen.drawRect(165, 140, 215, 155, 0xFFFFFFFF);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			GlStateManager.scale(1.25F, 1.25F, 1.25F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Arrow UP: " + ChatFormatting.DARK_AQUA + "Rotate", 335, 215, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Arrow Right: " + ChatFormatting.DARK_AQUA + "Move Right", 322, 225, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Arrow Left: " + ChatFormatting.DARK_AQUA + "Move Left", 325, 235, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Arrow Down: " + ChatFormatting.DARK_AQUA + "Drop Soft", 325, 245, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Space: " + ChatFormatting.DARK_AQUA + "Drop Hard", 335, 255, 0xffff);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			GlStateManager.scale(1.8F, 1.8F, 1.8F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Click to play", 233, 200, 0xffff);
			GlStateManager.popMatrix();
			return;
		}
		
		//Draw white marks at bottom.
		for (int i = 0; i < currentNode.getFamily().size(); i++) {
			if (currentNode.canGoDown()) {
				currentNode.setDownPosition();
				TetrisNode Node = currentNode.getFamily().get(i);
				int x = Node.getX();
				int y = Node.getDownPosition();
				GuiScreen.drawRect(x, y, x + 1, y - TetrisNode.multiplier, 0xFFFFFFFF);
				GuiScreen.drawRect(x, y, x + TetrisNode.multiplier, y + 1, 0xFFFFFFFF);
				GuiScreen.drawRect(x + TetrisNode.multiplier, y, x + TetrisNode.multiplier + 1, y - TetrisNode.multiplier, 0xFFFFFFFF);
				GuiScreen.drawRect(x, y - TetrisNode.multiplier, x + TetrisNode.multiplier, y - TetrisNode.multiplier + 1, 0xFFFFFFFF);
			}
		}
	}
	
	@Override
	public boolean onGuiClick(int x, int y, int button) {
		if (fromX < x && toX > x && fromY < y && toY > y) {
			if (gameOver == true) {
				startGame();
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onGuiKeyPress(GuiScreenEvent.KeyboardInputEvent.Post e) {
		//Control tetris block movement
		if (currentNode == null || gameOver == true) {
			return;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			if (currentNode.canMoveRight()) {
				currentNode.moveRight();
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			if (currentNode.canMoveLeft()) {
				currentNode.moveLeft();
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			currentNode.moveCompletelyDown();
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			currentNode.rotate();
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (currentNode.canGoDown()) {
				currentNode.moveDown();
			}
		}
	}
	
	//Set a random shape.
	public static void setShapes() {
		TetrisNode n = new TetrisNode(fromX + 50, fromY);
		n.addToFamily(n);
		int x = n.getX();
		int y = n.getY();
		
		Random rand = new Random();
		int random = rand.nextInt(7);
		if (random == 0) {
			n.addToFamily(new TetrisNode(x + 10, y));
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x + 10, y + 10));
			n.setShape("O");
			n.setColor(0xFFFFFF00);
		} else if (random == 1) {
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x, y + 20));
			n.addToFamily(new TetrisNode(x, y + 30));
			n.setShape("I");
			n.setColor(0xFF36EAFF);
		} else if (random == 2) {
			n.addToFamily(new TetrisNode(x + 10, y));
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x - 10, y + 10));
			n.setShape("S");
			n.setColor(0xFFFF0009);
		} else if (random == 3) {
			n.addToFamily(new TetrisNode(x - 10, y));
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x + 10, y + 10));
			n.setShape("Z");
			n.setColor(0xFF00FF2B);
		} else if (random == 4) {
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x, y + 20));
			n.addToFamily(new TetrisNode(x + 10, y + 20));
			n.setShape("L");
			n.setColor(0xFFEC830C);
		} else if (random == 5) {
			n.addToFamily(new TetrisNode(x, y + 10));
			n.addToFamily(new TetrisNode(x, y + 20));
			n.addToFamily(new TetrisNode(x - 10, y + 20));
			n.setShape("J");
			n.setColor(0xFFFF19EF);
		} else {
			n.addToFamily(new TetrisNode(x + 10, y));
			n.addToFamily(new TetrisNode(x - 10, y));
			n.addToFamily(new TetrisNode(x, y + 10));
			n.setShape("T");
			n.setColor(0xFF9100FF);
		}
		
		currentNode = n;
	}
	
	public static void removeLayer() {
		if (currentNode == null) {
			return;
		}
		
		for (int i = 0; i < currentNode.getFamily().size(); i++) {
			int y = currentNode.getFamily().get(i).getY();
			int x = fromX - 10;
			for (int i2 = 0; i2 < 100; i2++) {
				x = x + 10;
				if (x > toX - 10) {
					x = fromX - 10;
					for (int i3 = 0; i3 < 100; i3++) {
						x = x + 10;
						TetrisNode.nodes.remove(TetrisNode.getNode(x, y));
					}
					for (int i4 = 0; i4 < TetrisNode.nodes.size(); i4++) {
						if (TetrisNode.nodes.get(i4).getY() <= y) {
							TetrisNode.nodes.get(i4).setY(TetrisNode.nodes.get(i4).getY() + 10);
						}
					}
					BlockPos Player = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
					mc.world.playSound(Player, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 10222.5f, 1.5f, true);
					score += 5;
					break;
				}
				
				if (TetrisNode.getNode(x, y) == null) {
					break;
				}
			}
		}
	}
	
	public static void GameOver() {
		gameOver = true;
		TetrisNode.nodes.clear();
	}
	
	public static void startGame() {
		gameOver = false;
		score = 0;
	}
	
	public static void drawWhiteLine(int x, int x2, int y, int y2) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.25f, 0.25f, 0.25f);
		int thisx = x * 4; int thisy = y * 4; int thisx2 = x2 * 4; int thisy2 = y2 * 4;
		GuiScreen.drawRect(thisx, thisy, thisx + 2, thisy2, 0xFFFFFFFF);
		GuiScreen.drawRect(thisx2, thisy, thisx2 - 2, thisy2, 0xFFFFFFFF);
		GuiScreen.drawRect(thisx, thisy, thisx2, thisy + 2, 0xFFFFFFFF);
		GuiScreen.drawRect(thisx, thisy2, thisx2, thisy2 + 2, 0xFFFFFFFF);
		GlStateManager.popMatrix();
	}
}
