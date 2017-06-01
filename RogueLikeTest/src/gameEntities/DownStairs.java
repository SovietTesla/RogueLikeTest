package gameEntities;
import java.awt.Color;

import level.Tile;

public class DownStairs extends Entity {
	private Tile myTile;
	
	public DownStairs(){
		
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return "Stairs down";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Leads to the level below";
	}

	@Override
	public Tile getTile() {
		// TODO Auto-generated method stub
		return myTile;
	}

	@Override
	public void setTile(Tile t) {
		myTile = t;

	}

	@Override
	public char getChar() {
		// TODO Auto-generated method stub
		return '>';
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.GRAY;
	}

}
