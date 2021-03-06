package items;

import java.awt.Color;

import creatures.Creature;
import creatures.Player;
import statusEffects.InvulnStatusEffect;
import statusEffects.LevitationStatusEffect;

public class RevivalCharm extends Item implements RevivalItem {
	
	public RevivalCharm(){
	}
	
	@Override
	public Color getColor() {
		return new Color(255,180,180);
	}
	
	@Override
	public char getChar() {
		return 'O';
	}
	public String getName(){
		return "Revival Charm";
	}

	@Override
	public String getDescription() {
		return "A strong revival item";
	}
	
	@Override
	public void revive(Creature user){
		user.setHealth(user.getMaxHealth());
		user.addEffect(new InvulnStatusEffect(5));
		user.addEffect(new LevitationStatusEffect(5));
	}

	@Override
	public void use(Creature user){
		if (user instanceof Player){
			((Player)user).getGame().logMessage("You cannot use this item.",Color.RED);
		}
	}
}
