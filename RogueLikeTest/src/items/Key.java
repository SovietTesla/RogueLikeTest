package items;

import java.awt.Color;

import creatures.Creature;
import level.Tile;

public class Key extends Item{

	String name;
	String des;
	char myChar;
	Color myColor;
	
	public Key() {
		setStack(1);
		name = "Key";
		des = "A magic key that can open one chest to unlock the treasures within";	//is this too long? Technically no, but it will take up sever lines

		myChar = 'K';
		myColor = Color.ORANGE;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return des;
	}

	public char getChar() {
		return myChar;
	}

	public Color getColor() {
		return myColor;
	}

	public void use(Creature user) {
		//unlocks chests
		//one - time use
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
