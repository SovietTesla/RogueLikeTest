import java.awt.Color;

/* Weapon is an Item with a value that adds to a Creature's damage
 * Weapon must have a positive value 
 */
public class Weapon implements Item{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tile getTile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTile(Tile t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public char getChar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStack() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStack(int stack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void use(Creature user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canRevive() {
		// TODO Auto-generated method stub
		return false;
	}

}