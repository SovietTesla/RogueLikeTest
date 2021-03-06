package creatures;
import java.awt.Color;
import java.util.ArrayList;

import gameBase.Game;
import gameEntities.Entity;
import items.Item;
import level.Tile;
import statusEffects.StatusEffect;
/**
 * Represents a creature in the game.
 * @author Nikolai Trintchouk, Ben Burdette
 *
 */
public class Creature extends Entity{
	
	Game game;
	private ArrayList<Item> items = new ArrayList<Item>();
	protected ArrayList<StatusEffect> status = new ArrayList<StatusEffect>();
	private String name;
	private String des;
	private int currentHealth;
	private int maxHealth;
	protected int dmg;
	protected Tile myT;
	private Color myColor;
	private char myChar;
	private int stuns;
	
	/**
	 * Creates a creature with a name, description, health and damage.
	 * @param aName The name of the creature
	 * @param description The description of the creature
	 * @param health The health of the creature
	 * @param damage The attack power of the creature
	 */
	public Creature(String aName, String description, int health, int damage)
	{
		name = aName;
		des = description;
		currentHealth = health;	//creature always starts with max health
		maxHealth = health;
		dmg = damage;
		myColor = Color.BLACK;
		myChar = '@';
		stuns = 0;
	}
	
	/**
	 * Creates a creature with a name, description, health, damage, color and display character
	 * @param aName The name of the creature
	 * @param description The description of the creature
	 * @param health The health of the creature
	 * @param damage The attack power of the creature
	 * @param c The creature's color
	 * @param letter The creature's display character
	 */
	public Creature(String aName, String description, int health, int damage, Color c, char letter)
	{
		name = aName;
		des = description;
		currentHealth = health;	//creature always starts with max health
		maxHealth = health;
		dmg = damage;
		myColor = c;
		myChar = letter;
		stuns = 0;
	}
	
	public void addItem(Item i){
		items.add(i);
	}
	
	public void deleteItem(Item i){
		items.remove(i);
	}
	
	public ArrayList<Item> getItems(){
		return items;
	}
	
	public void addEffect(StatusEffect e){
		if (getIndexOfEffect(e.getId())==-1){
			if (e.start(this))
				status.add(e);
		}
		else
			setDuration(e.getId(),e.getDuration());
	}
	
	public void deleteEffect(int id){
		while (getIndexOfEffect(id)!=-1){
			status.get(getIndexOfEffect(id)).end(this);
			status.remove(getIndexOfEffect(id));
		}
	}
	
	public void deleteAllEffects(){
		for (StatusEffect e:status){
			e.end(this);
		}
		status = new ArrayList<StatusEffect>();
	}
	
	public void deleteAllNegativeEffects(){
		for (StatusEffect e:status){
			if (e.isHazardous()){
				e.end(this);
				status.remove(e);
			}
		}
	}
	
	public boolean hasEffect(int id){
		return getIndexOfEffect(id)!=-1;
	}
	
	public void tickAllEffects(){
		for (int i=0;i<status.size();i++){
			if (status.get(i).tick(this)){
				if (status.size()==0) return;
				deleteEffect(status.get(i).getId());
				i--;
			}
			if (currentHealth<=0) return;
		}
	}
	
	/**
	 * Gets the index of the given effect
	 * @param id the ID of the effect
	 * @return the index of the effect
	 */
	protected int getIndexOfEffect(int id){
		for (int i=0;i<status.size();i++){
			if (status.get(i).getId()==id){
				return i;
			}
		}
		return -1;
	}
	
	public void setDuration(int id, int dur) {
		status.get(getIndexOfEffect(id)).setDuration(dur);
	}
	
	/**
	 * returns name of creature
	 */
	public String getName()
	{
		return name;
	}
    
	/**
	 * returns description of creature
	 */
    public String getDescription()
    {
    	return des;
    }
	
    /**
     * Stuns the creature
     */
	public void addStun() {
		stuns++;
	}
	
    /**
     * Removes the stun
     */
	public void removeStun() {
		stuns--;
	}
	
    /**
     * Removes the stun
     */
	public boolean isStunned() {
		return stuns>0;
	}
    
    /**
     * returns Health of creature
     * @return the health of the creature
     */
    public int getHealth()
    {
    	return currentHealth;
    }
    
    /**
     * returns maximum health of creature
     * @return maximum health of creature
     */
    public int getMaxHealth()
    {
    	return maxHealth;
    }
    
    public void setMaxHealth(int h){
    	maxHealth = h;
    }
    
    /**
     * Has creature take an amount of damage. If h is less than or equal to zero the value will default to 1, with a chance of missing
     * @param h the amount of damage to be taken by the creature.
     */
    public void takeDamage(int h)
    {
    	if (hasEffect(4)||hasEffect(3)) return;
    	if(h <= 0){
    		//attacks that would do <1 damage have a chance of missing
    		if (-1*(h-2)*Math.random()<1)
    			h = 1;
    		else
    			return;
    	}
    	
    	currentHealth -= h;
    	if(currentHealth <= 0)
    		die();
    }
     
    /**
     * Sets the target's health
     * @param h the target's new health value
     */ 
    public void setHealth(int h){
    	if (hasEffect(3)&&h<currentHealth) return;
    	if (h<0)
    		h = 0;
    	currentHealth = h;
    	if (currentHealth<=0)
    		die();
    	else if(currentHealth > maxHealth)
    		currentHealth = maxHealth;
    }
    
    /**
     * Creature gains amount of health
     * @param h amount of health gained by the creature. Precondition: h >= 0
     */
    public void heal(int h)
    {
    	currentHealth += h;
    	if(currentHealth > maxHealth)
    		currentHealth = maxHealth;
    }
    
    /**
     * returns attack power of creature
     * @return attack power of creature
     */
    public int getDamage()
    {
    	return dmg;
    }
    
    /**
     * returns the Tile in which the creature exists. Null if not in a tile.
     */
    public Tile getTile()
    {
    	return myT;
    }
    
    /**
     * Removes the creature from the game.
     */
    public void die()
    {
    	if (hasEffect(3)){
    		if (currentHealth<=0)
    			currentHealth = 1;
    		return;
    	}
    	deleteAllEffects();
    	if(myT != null){
    		myT.removeEntity(this);
    	}
    }
    
    
    /**
     * Sets tile of creature. Should only be called from gameBase.Tile
     */
    public void setTile(Tile t)
    {
    	myT = t;
    }

	/**
	 * returns the displayed character
	 */
	public char getChar() {
		return myChar;
	}

	/**
	 * returns the color of the creature
	 */
	public Color getColor() {
		if (status.size()==0)
			return myColor;
		return status.get(0).getColor();
	}
	
	/**
	 * Sets the color of the creature.
	 * @param c Color to be set
	 */
	protected void setColor(Color c){
		myColor = c;
	}
    
	/**
	 * sets the display character of the creature
	 * @param c Character to be set
	 */
    protected void setChar(char c) {
    	myChar = c;
    }

	public void act() {
		// TODO Auto-generated method stub
		
	}
}
