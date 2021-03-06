package statusEffects;

import java.awt.Color;

import creatures.Creature;

/**
 * Basically PoisonStatusEffect, but does extra damage and is affected by resistances
 * @author 1839208
 *
 */
public class BurnStatusEffect extends StatusEffect {
	
	int damage;
	
	public BurnStatusEffect() {
		super();
		damage = 2;
	}
	
	public BurnStatusEffect(int dur) {
		super(dur);
		damage = 2;
	}
	
	public BurnStatusEffect(int dur, int dam) {
		super(dur);
		damage = dam;
	}
	
	public int getId() {
		return 2;
	}
	
	@Override
	public boolean start(Creature target) {
		if (target.hasEffect(4))
			return false;
		else
			target.deleteEffect(5);
		return true;
	}
	
	@Override
	protected void act(Creature target) {
		target.takeDamage(damage);
	}
	
	public Color getColor(){
		return new Color(255,150,0);
	}
	
	public String getName() {
		return "burn";
	}
	
	public String getAdjective() {
		return "Fire";
	}
	
	public String getTriggerMessage(){
		return "You burst into flame!";
	}
	
	public boolean isHazardous(){
		return true;
	}
}
