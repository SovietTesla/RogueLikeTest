package items;


import creatures.Player;

public class Inventory {

	private Weapon myWeapon;
	private Armor myArmor;
	private Potion myPotion;
	private Item mySpecial;
	
	public Inventory(){
	}
	
	public Item pickUpItem(Item i){
		if(i instanceof Weapon)
			return setMyWeapon((Weapon)i);
		if(i instanceof Armor)
			return setMyArmor((Armor)i);
		if(i instanceof Potion)
			return setMyPotion((Potion)i);
		if(i != null)
			return setMySpecial(i);
		return null;
	}
	public Weapon getMyWeapon() {
		return myWeapon;
	}

	public Weapon setMyWeapon(Weapon myWeapon) {
		Weapon w = this.myWeapon;
		this.myWeapon = myWeapon;
		return w;
	}

	public Armor getMyArmor() {
		return myArmor;
	}

	public Armor setMyArmor(Armor myArmor) {
		Armor a = this.myArmor;
		this.myArmor = myArmor;
		return a;
	}

	public Potion getMyPotion() {
		return myPotion;
	}

	public Potion setMyPotion(Potion myPotion) {
		Potion p = this.myPotion;
		this.myPotion = myPotion;
		return p;
	}

	public Item getMySpecial() {
		return mySpecial;
	}

	public Item setMySpecial(Item mySpecial) {
		Item i = this.mySpecial;
		this.mySpecial = mySpecial;
		return i;
	}
	
}