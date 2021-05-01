package me.bebeli555.cookieclient.mods.games;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.GuiScreenEvent;

public class Snake extends Mod {
	public static Snake instance;
	public static ArrayList<Integer> bodyX = new ArrayList<Integer>();
	public static ArrayList<Integer> bodyY = new ArrayList<Integer>();
	public static int snakeSize = 0;
	public static int snakeX, snakeY;
	public static int lastSnakeX, lastSnakeY;
	public static int lastBodyX, lastBodyY;
	public static boolean gameOver = true;
	public static int score = 0;
	public static int appleX, appleY;
	public static int delay = 0;
	public static long lastSec = 0;
	public static String direction;
	
	public static int x = mc.displayWidth / 4;
	public static int y = mc.displayHeight / 5;
	
	public Snake() {
		super(Group.GAMES, "Snake", "Snake game");
		instance = this;
	}
	
	@Override
	public void onGuiDrawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		GuiScreen.drawRect(x - 1, y - 1, x + 201, y + 201, 0xFF42f5bc);
		GuiScreen.drawRect(x, y, x + 200, y + 200, 0xFF000000);
		
		//Game over.
		if (snakeX < x || snakeX >= x + 200 || snakeY < y || snakeY >= y + 200) {
			gameOver();
		}
		
		if (!gameOver) {			
			//Die if head is colliding in body
			for (int i = 0; i < bodyX.size(); i++) {
				if (bodyX.get(i) == snakeX) {
					if (bodyY.get(i) == snakeY) {
						gameOver();
						return;
					}
				}
			}
			
			//Draw score
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED + "Score = " + ChatFormatting.GREEN + snakeSize, x + 3, y + 2, 0xffff);
			
			// Generate apple
			if (appleX == 0 || appleY == 0) {
				generateApple();
			}
			
			// Draw apple
			GuiScreen.drawRect(appleX, appleY, appleX + 20, appleY + 20, 0xFFff0000);

			long sec = System.currentTimeMillis() / 150;
			if (sec != lastSec) {	
				delay = 0;
				// Control snake movement
				if (direction.equals("Up")) {
					snakeY = snakeY - 20;
				} else if (direction.equals("Down")) {
					snakeY = snakeY + 20;
				} else if (direction.equals("Right")) {
					snakeX = snakeX + 20;
				} else if (direction.equals("Left")) {
					snakeX = snakeX - 20;
				}
				
				if (!bodyX.isEmpty()) {
					bodyX.remove(bodyX.get(0));
					bodyY.remove(bodyY.get(0));
					bodyX.add(lastSnakeX);
					bodyY.add(lastSnakeY);
				}
				lastSec = sec;
			}
			
			//Eat apple
			if (snakeX == appleX) {
				if (snakeY == appleY) {
					snakeSize++;
					appleX = 0;
					appleY = 0;
					
					//More body for snake bcs he fat and eating all those sugary apples
					if (bodyX.isEmpty()) {
						bodyX.add(lastSnakeX);
						bodyY.add(lastSnakeY);
					} else {
						bodyX.add(lastBodyX);
						bodyY.add(lastBodyY);
					}
					mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100.0f, 2.0F, true);
				}
			}
			
			lastSnakeX = snakeX;
			lastSnakeY = snakeY;
			if (!bodyX.isEmpty()) {
				lastBodyX = bodyX.get(bodyX.size() - 1);
				lastBodyY = bodyY.get(bodyY.size() - 1);
			}
			
			// Draw snake
			GuiScreen.drawRect(snakeX, snakeY, snakeX + 20, snakeY + 20, 0xFF55ff00);
			GuiScreen.drawRect(snakeX + 3, snakeY + 3, snakeX + 8, snakeY + 8, 0xFF000000);
			for (int i = 0; i < bodyX.size(); i++) {
				if (!bodyX.isEmpty()) {
					GuiScreen.drawRect(bodyX.get(i), bodyY.get(i), bodyX.get(i) + 20, bodyY.get(i) + 20, 0xFF55ff00);
				}
			}
		}
		
		//Game over screen
		if (gameOver == true) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(3.0F, 3.0F, 3.0F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED + "Game Over!", (x / 3) + 6, (y / 3) + 2, 0xffff);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.LIGHT_PURPLE + "Score = " + ChatFormatting.GREEN + snakeSize, (x / 3) + 9, (y / 3) + 12, 0xffff);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(2.0F, 2.0F, 2.0F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + "Click to Play!", (x / 2) + 18, (y / 2) + 85, 0xffff);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.5F, 1.5F, 1.5F);
			mc.fontRenderer.drawStringWithShadow(ChatFormatting.AQUA + "Use " + ChatFormatting.GREEN + "ARROW KEYS " + ChatFormatting.AQUA + "To play!", (int)(x / 1.5) + 5, (int)(y / 1.5) + 75, 0xffff);
			GlStateManager.popMatrix();
		}
		
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean onGuiClick(int x, int y, int button) {
		x /= 1.5;
		y /= 1.5;
		
		if (Snake.x < x && (Snake.x + 200) > x && Snake.y < y && (Snake.y + 200) > y) {
			if (gameOver == true) {
				startGame();
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onGuiKeyPress(GuiScreenEvent.KeyboardInputEvent.Post e) {
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			direction = "Down";
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			direction = "Up";
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			direction = "Right";
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			direction = "Left";
		}
	}
	
	public static void gameOver() {
		if (gameOver == false) {
			gameOver = true;
			bodyX.clear();
			bodyY.clear();
			lastSnakeX = 0;
			lastSnakeY = 0;
			appleX = 0;
			appleY = 0;
		}
	}
	
	public static void startGame() {
		gameOver = false;
		snakeX = x + 100;
		snakeY = y + 180;
		direction = "Up";
		snakeSize = 1;
	}
	
	public static void generateApple() {
		for (int i = 0; i < 100; i++) {
			Random rand = new Random();
			int random = rand.nextInt(10);
			int random2 = rand.nextInt(10);
			appleX = x + random * 20;
			appleY = y + random2 * 20;
			
			for (int i2 = 0; i2 < bodyX.size(); i2++) {
				if (!bodyX.isEmpty()) {
					if (bodyX.get(i2) == appleX) {
						if (bodyY.get(i2) == appleY) {
							appleX = 0;
							appleY = 0;
							break;
						}
					}
				}
			}
			
			if (snakeX == appleX) {
				if (snakeY == appleY) {
					appleX = 0;
					appleY = 0;
					continue;
				}
			}
			if (appleX < 140 || appleX > 340 || appleY < 140 || appleY > 340) {
				appleX = 0;
				appleY = 0;
				continue;
			}
			
			if (appleX != 0 && appleY != 0) { 
				break;
			}
		}
	}
}
