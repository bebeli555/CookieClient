package me.bebeli555.cookieclient.mods.games.tetris;

import java.util.ArrayList;

public class TetrisNode {
	public static ArrayList<TetrisNode> nodes = new ArrayList<TetrisNode>();
	private ArrayList<TetrisNode> familyNodes = new ArrayList<TetrisNode>();
	public static int multiplier = 10;
	private int x, y;
	private int color;
	private String shape;
	private int rotation;
	private int downpos;
	
	public TetrisNode(int x, int y) {
		this.x = x;
		this.y = y;
		this.rotation = 1;
		nodes.add(this);
	}
	
	public void removeFromList() {
		nodes.remove(this);
	}
	
	public void setColor(int color) {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			this.familyNodes.get(i).color = color;
		}
	}
	
	public int getColor() {
		return color;
	}
	
	public void setShape(String shape) {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			this.familyNodes.get(i).shape = shape;
		}
	}
	
	public String getShape() {
		return shape;
	}
	
	public void addToFamily(TetrisNode Node) {
		familyNodes.add(Node);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX () {
		return x;
	}
	
	public int getY () {
		return y;
	}
	
	public void moveDown() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = this.familyNodes.get(i);
			Node.setY(this.familyNodes.get(i).getY() + multiplier);
		}
	}
	
	public void moveRight() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = this.familyNodes.get(i);
			Node.setX(Node.getX() + multiplier);
		}
	}
	
	public void moveLeft() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = this.familyNodes.get(i);
			Node.setX(Node.getX() - multiplier);
		}
	}
	
	public boolean canMoveRight() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = getNode(this.familyNodes.get(i).getX() + 10, this.familyNodes.get(i).getY());
			if (Node != null && isInFamily(Node) == false) {
				return false;
			}
			if (this.familyNodes.get(i).getX() > Tetris.toX - 20) {
				return false;
			}	
		}
		return true;
	}
	
	public boolean canMoveLeft() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = getNode(this.familyNodes.get(i).getX() - 10, this.familyNodes.get(i).getY());
			if (Node != null && isInFamily(Node) == false) {
				return false;
			}
			if (this.familyNodes.get(i).getX() < Tetris.fromX + 10) {
				return false;
			}	
		}
		return true;
	}
	
	public ArrayList<TetrisNode> getFamily() {
		return familyNodes;
	}
	
	public void moveCompletelyDown() {
		for (int i = 0; i < 150; i++) {
			if (this.canGoDown()) {
				this.moveDown();
			}
		}
	}
	
	public boolean isInFamily(TetrisNode node) {
		if (familyNodes.contains(node)) {
			return true;
		}
		return false;
	}
	
	public boolean canGoDown() {		
		for (int i = 0; i < this.familyNodes.size(); i++) {
			TetrisNode Node = getNode(this.familyNodes.get(i).getX(), this.familyNodes.get(i).getY() + multiplier);		
			if (this.familyNodes.get(i).getY() > Tetris.toY - 10) {
				return false;
			}
			
			if (Node != null && isInFamily(Node) == false) {
				return false;
			}
		}
		return true;
	}

	public static TetrisNode getNode(int x, int y) {
		for (int i = 0; i < nodes.size(); i++) {
			TetrisNode Node = nodes.get(i);
			if (Node.getX() == x && Node.getY() == y) {
				return Node;
			}
		}
		return null;
	}
	
	public void clearFamily() {
		for (int i = 0; i < this.familyNodes.size(); i++) {
			if (!this.familyNodes.get(i).equals(this)) {
				nodes.remove(this.familyNodes.get(i));
			}
		}
		
		this.familyNodes.clear();
	}
	
	public void setDownPosition() {
		int OldY = this.familyNodes.get(0).getY();
		int OldY2 = this.familyNodes.get(1).getY();
		int OldY3 = this.familyNodes.get(2).getY();
		int OldY4 = this.familyNodes.get(3).getY();
		
		for (int i2 = 0; i2 < 100; i2++) {
			for (int i3 = 0; i3 < this.familyNodes.size(); i3++) {
				this.familyNodes.get(i3).setY(this.familyNodes.get(i3).getY() + 10);
			}
			int NodeY = this.y;
			
			if (!this.canGoDown()) {
				for (int i = 0; i < this.familyNodes.size(); i++) {
					this.familyNodes.get(i).downpos = (this.familyNodes.get(i).getY() - this.getY()) + NodeY;
				}
				this.familyNodes.get(0).setY(OldY);
				this.familyNodes.get(1).setY(OldY2);
				this.familyNodes.get(2).setY(OldY3);
				this.familyNodes.get(3).setY(OldY4);
				return;
			}
		}
	}
	
	public int getDownPosition() {
		return downpos;
	}
	
 	//Rotate tetris block
	public void rotate() {
		String rot = this.getShape();
		if (rot.equals("O")) {
			return;
		}
		this.rotation++;
		this.clearFamily();
		this.familyNodes.add(this);

		//I
		if (rot.equals("I")) {
			if (this.rotation > 2) {
				this.rotation = 1;
			}
			if(x+20 > Tetris.toX)x-=20;
			if(x-10 < Tetris.fromX)x+=10;
			if (rotation == 1) {
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y + 20));
				this.familyNodes.add(new TetrisNode(x, y + 30));
			} else {
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x + 20, y));
				this.familyNodes.add(new TetrisNode(x - 10, y));
			}
			this.setColor(0xFF36EAFF);
		}
		
		//S
		if (rot.equals("S")) {
			if (this.rotation > 2) {
				this.rotation = 1;
			}
			if(x+20 > Tetris.toX)x-=10;
			if (rotation == 1) {
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x - 10, y + 10));
			} else {
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x - 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y - 10));
			}
			this.setColor(0xFFFF0009);
		}
		
		//Z
		if (rot.equals("Z")) {
			if (this.rotation > 2) {
				this.rotation = 1;
			}
			if(x-10 < Tetris.fromX) x += 10;
			if (rotation == 1) {
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x + 10, y + 10));
				this.familyNodes.add(new TetrisNode(x - 10, y));
			} else {
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x + 10, y - 10));
				this.familyNodes.add(new TetrisNode(x, y + 10));
			}
			this.setColor(0xFF00FF2B);
		}
		
		//L
		if (rot.equals("L")) {
			if (this.rotation > 4) {
				this.rotation = 1;
			}
			
			if (rotation == 1) {
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y + 20));
				this.familyNodes.add(new TetrisNode(x + 10, y + 20));
			} else if (rotation == 2){
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y + 10));
			} else if (rotation == 3){
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x - 10, y));
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y + 20));
			} else{
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x + 10, y - 10));
				this.familyNodes.add(new TetrisNode(x - 10, y));
			}
			this.setColor(0xFFEC830C);
		}
		
		//J
		if (rot.equals("J")) {
			if (this.rotation > 4) {
				this.rotation = 1;
			}
			
			if (rotation == 1) {
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y + 20));
				this.familyNodes.add(new TetrisNode(x - 10, y + 20));
			} else if (rotation == 2){
				if(x+20 > Tetris.toX)x-=20;
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x + 10, y + 10));
				this.familyNodes.add(new TetrisNode(x + 20, y + 10));
			} else if (rotation == 3){
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y - 10));
				this.familyNodes.add(new TetrisNode(x + 10, y - 10));
			} else{
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y));
				this.familyNodes.add(new TetrisNode(x + 10, y + 10));
			}
			this.setColor(0xFFFF19EF);
		}
		
		//T
		if (rot.equals("T")) {
			if (this.rotation > 4) {
				this.rotation = 1;
			}
			
			if (rotation == 1) {
				if(x+10 > Tetris.toX)x-=10;
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y));
				this.familyNodes.add(new TetrisNode(x, y + 10));
			} else if (rotation == 2){
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y - 10));
				this.familyNodes.add(new TetrisNode(x - 10, y));
			} else if (rotation == 3){
				if(x+20 > Tetris.toX)x-=10;
				if(x-10 < Tetris.fromX)x+=10;
				this.familyNodes.add(new TetrisNode(x, y - 10));
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x - 10, y));
			} else{
				if(x+10 > Tetris.toX)x-=10;
				this.familyNodes.add(new TetrisNode(x + 10, y));
				this.familyNodes.add(new TetrisNode(x, y + 10));
				this.familyNodes.add(new TetrisNode(x, y - 10));
			}
			this.setColor(0xFF9100FF);
		}
	}
}
