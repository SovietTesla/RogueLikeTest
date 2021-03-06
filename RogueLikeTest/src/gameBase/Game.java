package gameBase;

import java.awt.Color;
import java.util.ArrayList;

import creatures.*;
import asciiPanel.AsciiPanel;
import gameEntities.Door;
import gameEntities.Entity;
import gameEntities.Searcher;
import level.DungeonLevel;
import level.Tile;
import squidpony.squidgrid.Radius;
import statusEffects.*;
import squidpony.squidgrid.FOV;

public class Game {
	private ArrayList<DungeonLevel> levels;
	private AsciiPanel panel;
	private Player player;
	private int currentLevel;
	private ArrayList<String> helpItems;
	private ArrayList<ArrayList<Tile>> seenTiles;
	private Searcher search;
	private int rightSideMenuWidth;
	private Logger log;

	public static int NORTH = 0;
	public static int NORTH_EAST = 1;
	public static int EAST = 2;
	public static int SOUTH_EAST = 3;
	public static int SOUTH = 4;
	public static int SOUTH_WEST = 5;
	public static int WEST = 6;
	public static int NORTH_WEST = 7;

	private boolean searching;
	private boolean helpOpen;

	FOV fov;

	public Game() {

		levels = new ArrayList<DungeonLevel>();
		helpItems = new ArrayList<String>();
		
		helpItems.add("H - Help");
		helpItems.add("W - North");
		helpItems.add("A - West");
		helpItems.add("S - South");
		helpItems.add("D - East");
		helpItems.add("F - Inspect");
		helpItems.add("I - Interact");
		helpItems.add("O - Wait 1 turn");
		helpItems.add("P - Drink Potion");
		helpItems.add("L - Use Item");

		fov = new FOV(FOV.SHADOW);
		seenTiles = new ArrayList<ArrayList<Tile>>();
		searching = false;
		
		
		
		rightSideMenuWidth = 20;
	}

	public void start() {
		createPlayer();
		generateNextLevel();
		currentLevel = 0;
		createPlayer();
		insertEntity((Entity) player, levels.get(0).getEntrance());
		// player.addEffect(new PoisonStatusEffect(10));
		// addRegionToSeen(1,0);
		helpOpen = true;
		showHelp();
		log = new Logger(rightSideMenuWidth - 1, panel.getHeightInCharacters() - 6 - helpItems.size());
	}
	
	public int getCurrentLevel(){
		return currentLevel;
	}

	public void addPanel(AsciiPanel p) {
		panel = p;
	}

	public void generateNextLevel() {
		levels.add(new DungeonLevel(levels.size(), 51));
		levels.get(levels.size() - 1).generateLevel();
		seenTiles.add(new ArrayList<Tile>());
	}

	public void createPlayer() {
		player = new Player("Player", "", 20, 3, this);
	}

	public static Monster createLevel1Monster() {
		return new Monster("Wispy Spirit", "The weakest monster", 1, 1);
	}
	
	public static Monster createMonsterOfLevel(int level){
		if(level <= 1){
			return new Monster("Wispy Spirit", "The weakest monster", 1, 1);
		}
		if(level <= 2){
			return new Monster("Goblin", "Much stronger in numbers", 3, 4);
		}
		if(level <= 4){
			return new Monster("Goblin Soldier", "Tougher than the scouting goblins", 6, 8);
		}
		if(level <= 6){
			return new Monster("Shambling Brute", "The weakest undead", 10, 12);
		}
		else{
			return new Monster("Hellish Shambler", "Gets stronger the deeper they are located", level * 2, level * 2 + 4);
		}
	}

	public static Wizard createWizardOfLevel(int level)
	{
		if(level <= 1)
		{
			return new Wizard("Exiled Illusionist", "A former member of the fabled illusionists", 1, 1);
		}
		if(level <= 2)
		{
			return new Wizard("Maniacal Warlock", "Insane, Arcane, he'll wipe the floor with your membrane", new Color(255, 153, 51), 3, 2);
		}
		if(level <= 4)
		{
			return new Wizard("Honored Illusionist", "A well regarded professor of magic, I'm sure he'd love to chat over tea", new Color(0, 204, 0), 5, 4);
		}
		if(level <= 6)
		{
			return new Wizard("Prodigal Magician", "A gifted student of the arcane arts", new Color(204, 51, 0), 7, 5);
		}
		else
		{
			//purple for royalty
			return new Wizard("Grand Master Illustionist", "Gets stronger as your will to go deeper continues", new Color(153, 51, 153), level * 3, level * 2 - 3);
		}
	}
	
	public static void insertEntity(Entity e, Tile t) {
		t.addEntity(e);
	}

	public void displayMapAroundTile(Tile t, int level) {
		DungeonLevel d = getLevel(level);
		ArrayList<Tile> currentlySeenTiles = new ArrayList<Tile>();
		currentlySeenTiles = calcFOV(player, 14);

		for (Tile tile : currentlySeenTiles) {
			if (!seenTiles.get(level).contains(tile)) {
				seenTiles.get(level).add(tile);
			}
		}

		for (int i = 0; i < panel.getHeightInCharacters(); i++) {

			for (int j = 0; j < panel.getWidthInCharacters(); j++) {
				int posX = i + t.getX() - panel.getHeightInCharacters() / 2 - 3;
				int posY = j + t.getY() - panel.getWidthInCharacters() / 2 + 8;

				if (d.getTile(posX, posY) != null && seenTiles.get(level).contains(d.getTile(posX, posY))) {
					Color c;
					if (searching && search.getTile() == d.getTile(posX, posY))
						c = Color.YELLOW;
					else if (!currentlySeenTiles.contains(d.getTile(posX, posY))) {
						c = Color.DARK_GRAY;
					} else {
						c = AsciiPanel.black;
					}
					if (d.getTile(posX, posY).getIsRock()) {
						panel.setCursorPosition(j, i);
						panel.write('#', Color.WHITE, c);

					} else {
						panel.setCursorPosition(j, i);
						Entity e = d.getTile(posX, posY).getTopEntity();

						if (e == null)
							panel.write(' ', Color.WHITE, c);
						else if (!(e instanceof Creature))
							panel.write(e.getChar(), e.getColor(), c);
						else if (currentlySeenTiles.contains(e.getTile()))
							panel.write(e.getChar(), e.getColor(), c);
						else {
							panel.setCursorPosition(j, i);
							panel.write(' ', Color.WHITE, Color.DARK_GRAY);
						}

					}

				} else {
					panel.setCursorPosition(j, i);
					panel.write(' ', Color.WHITE, Color.GRAY);
				}
			}

		}
		createUpperBorder();
		createHelpMenu();
		displayLog();
		panel.updateUI();
	}

	public void createUpperBorder() {
		int borderHeight = 3;
		for (int i = 0; i < panel.getWidthInCharacters(); i++) {
			for (int j = 0; j < borderHeight; j++) {
				panel.setCursorPosition(i, j);
				panel.write(' ');
			}
			panel.setCursorPosition(i, borderHeight);
			panel.write('=');
		}

		panel.setCursorPosition(0, 1);
		panel.write("Health: ", Color.WHITE);
		if (player.getHealth() < player.getMaxHealth()) {
			panel.write(player.getHealth() + "/" + player.getMaxHealth(), Color.RED);
		} else {
			panel.write(player.getHealth() + "/" + player.getMaxHealth(), Color.GREEN);
		}
		panel.write(" Level: " + player.getLevel(), Color.BLUE);
		panel.write(" (" + player.getXp() + "/" +player.getNeededXp() + ")");
		panel.write(" Gold: " + player.getGold(), Color.WHITE);
		panel.write(" Keys: " + player.getKeys(), Color.WHITE);
		
		
		panel.setCursorPosition(0, 2);
		panel.write(player.items(), Color.WHITE);
	}

	public void createHelpMenu() {
		for (int i = 4; i < panel.getHeightInCharacters(); i++) {
			for (int j = panel.getWidthInCharacters() - rightSideMenuWidth; j < panel.getWidthInCharacters(); j++) {
				panel.setCursorPosition(j, i);
				panel.write(' ');
			}
			panel.setCursorPosition(panel.getWidthInCharacters() - rightSideMenuWidth - 1, i);
			panel.write('|');
		}
		for (int i = 4; i < panel.getHeightInCharacters() && i - 4 < helpItems.size(); i++) {
			panel.setCursorPosition(panel.getWidthInCharacters() - rightSideMenuWidth, i);
			panel.write(helpItems.get(i - 4));
		}

	}
	
	public void showHelp(){
		panel.clear();
		panel.setCursorPosition(0,1);
		panel.write("Welcome to the dungeon! Your main objective is to get as deep as possible.");
		panel.setCursorPosition(0,2);
		panel.write("You will need to know some things to progress. Here are some basic controls.");
		panel.setCursorPosition(0, 4);
		panel.write("W, A, S, and D ", Color.GREEN); panel.write("will move you around the dungeon.");
		panel.setCursorPosition(0, 5);
		panel.write("F ", Color.GREEN); panel.write("will allow you to inspect an tile. Move the yellow cursor and press F again to inspect.");
		panel.setCursorPosition(0,6);
		panel.write("I ", Color.GREEN); panel.write("has you interact with the tile you are standing on. Use this to pick up items and unlock chests.");
		panel.setCursorPosition(0,7);
		panel.write("O ", Color.GREEN); panel.write("will have you stand still for one turn. You can wait out negative effects like this.");
		panel.setCursorPosition(0,8);
		panel.write("P ", Color.GREEN); panel.write("will drink the current potion you are holding. Some potions have different actions.");
		panel.setCursorPosition(0, 9);
		panel.write("L ", Color.GREEN); panel.write("will use your Spec. Item. Some of these items cannot be used this way.");
		
		panel.setCursorPosition(0, 11);
		panel.write("Now you will learn about some basic things you will see in the dungeon.");
		panel.setCursorPosition(0, 13);
		panel.write("!", Color.RED); panel.write("   This is a monster. Walk directly towards them to attack.");
		panel.setCursorPosition(1, 14);
		panel.write("     Be careful; they will chase you and attack back.");
		panel.setCursorPosition(0, 15);
		panel.write(";", Color.red); panel.write("   This is a wizard. They will run away from you and shoot magic bolts at you.");
		panel.setCursorPosition(0, 16);
		panel.write("      Their magic hurts friend and foe");
		panel.setCursorPosition(0, 17);
		panel.write("+", Color.GRAY); panel.write("   This is a door. You can't see through it from far away.");
		panel.setCursorPosition(0, 18);
		panel.write("X", Color.RED); panel.write("   This is a trap! Disarm a trap by Interacting near it.");
		panel.setCursorPosition(0, 19);
		panel.write("K ", Color.ORANGE); panel.write("$",new Color(255,215,0)); panel.write(" Keys and chests. Pick up a key to unlock a chest.");
		panel.setCursorPosition(0, 20);
		panel.write("+ ", Color.GREEN); panel.write("  This is a potion. They come in different colors and do different things.");
		panel.setCursorPosition(0, 21);
		panel.write(". ", new Color(255,215,0)); panel.write("  This is a pile of gold. Collect 1000 to upgrade your weapons and armor!");
		panel.setCursorPosition(0, 22);
		panel.write("< >", Color.GRAY); panel.write(" These are stairs. Pointing left goes up, pointing right goes down. Go as deep as you can!");
		
		panel.setCursorPosition(0, 24);
		panel.write("You will encounter other things in the dungeon; some things aren't in here.");
		panel.setCursorPosition(0, 25);
		panel.write("Remember to "); panel.write("Inspect ", Color.YELLOW); panel.write("to learn about things you don't know.");
		panel.setCursorPosition(0,27);
		panel.write("Press H to exit this menu. Remember to get as far as you can and conquer the dungeon!");
		panel.setCursorPosition(0,  29);
		panel.write("GAME REQUIRES SCREEN RESOLUTION OF 1400x800 OR HIGHER TO DISPLAY PROPERLY");
		panel.setCursorPosition(0, 46);
		panel.write("If you read it this far, you may as well be our secret agent.");
		panel.setCursorPosition(0, 47);
		panel.write("Your mission, should you choose to accept:");
		panel.setCursorPosition(13, 49);
		panel.write("Find a bug that breaks the game");
		
		panel.updateUI();
		}

	public void displayLog() {
		int startY = 4 + helpItems.size();
		for (int i = panel.getWidthInCharacters() - rightSideMenuWidth; i < panel.getWidthInCharacters(); i++) {
			panel.setCursorPosition(i, startY);
			panel.write('=');
		}
		int i = startY + 1;
		if (log == null)
			return;
		for (Message msg : log.getMessages()) {
			panel.setCursorPosition(panel.getWidthInCharacters() - rightSideMenuWidth, i);
			panel.write(msg.toString(), msg.getColor());
			i++;
		}
	}

	public static ArrayList<Tile> calcFOV(Creature c, int diameter) {
		FOV fov = new FOV();
		Game g = new Game();
		ArrayList<Tile> seen = new ArrayList<Tile>();

		int startx = c.getTile().getX();
		int starty = c.getTile().getY();

		double[][] fovmap = fov.calculateFOV(g.generateResistances(c.getTile().getDungeon()), startx, starty, diameter,
				Radius.OCTAHEDRON);

		seen.clear();
		for (int i = 0; i < fovmap.length; i++) {
			for (int j = 0; j < fovmap[0].length; j++) {
				if (fovmap[i][j] > .5) {
					seen.add(c.getTile().getDungeon().getTile(i, j));
				}
			}
		}
		for (Tile t : c.getTile().getAdjacentTiles()) {
			if (t.getTopEntity() instanceof Door) {
				// player can see one tile beyond the door
				for (Tile j : t.getAdjacentTiles())
					for (int i = 0; i < 8; i += 2) {
						if (j.getTileInDirection(i) == t) {
							seen.add(j);
						}
					}
			}
		}
		return seen;
	}

	private double[][] generateResistances(DungeonLevel dun) {
		double[][] map = new double[dun.getMap().length][dun.getMap().length];
		for (int i = 0; i < dun.getMap().length; i++) {
			for (int j = 0; j < dun.getMap().length; j++) {
				if (dun.getTile(i, j).getIsRock()) {
					map[i][j] = 1;
				}
				if (dun.getTile(i, j).getTopEntity() instanceof Door) {
					map[i][j] = 1;
				}

			}
		}
		return map;
	}

	public DungeonLevel getLevel(int d) {
		if (d < levels.size() && d >= 0) {
			return levels.get(d);
		}
		return null;
	}

	public static boolean creatureCanMoveInDirection(Creature c, int direction) {
		if (c == null)
			return false;

		if ((c.getTile().getTileInDirection(direction) != null
				&& !c.getTile().getTileInDirection(direction).getIsRock()
				&& !(c.getTile().getTileInDirection(direction).getTopEntity() instanceof Creature))
				|| c.getTile().getTileInDirection(direction).getTopEntity() instanceof Projectile) {
			return true;
		}
		return false;
	}

	public void revealLevel(int level) {
		Tile[][] map = getLevel(level).getMap();
		int upperBound;
		Tile t;
		for (int i = 1; i < map.length - 1; i++) {
			upperBound = 8;
			for (int j = 1; j < map[i].length - 1; j++) {
				if (map[i][j].getRegion() != 0 && !map[i][j].getIsRock()) {
					map[i][j].revealTrap();
					seenTiles.get(level).add(map[i][j]);
					for (int k = 0; k < upperBound; k++) {
						t = map[i][j].getTileInDirection(k);
						if (t.getIsRock() || t.getRegion() == -1)
							seenTiles.get(level).add(t);
					}
					upperBound = 4;
				} else
					upperBound = 8;
			}
		}
	}

	public void addRegionToSeen(int region, int level) {
		Tile[][] map = getLevel(level).getMap();
		int upperBound;
		Tile t;
		for (int i = 1; i < map.length - 1; i++) {
			upperBound = 8;
			for (int j = 1; j < map[i].length - 1; j++) {
				if (map[i][j].getRegion() == region && !map[i][j].getIsRock()) {
					seenTiles.get(level).add(map[i][j]);
					for (int k = 0; k < upperBound; k++) {
						t = map[i][j].getTileInDirection(k);
						if (t.getIsRock() || t.getRegion() == -1)
							seenTiles.get(level).add(t);
					}
					upperBound = 4;
				} else
					upperBound = 8;
			}
		}
	}

	public void movePlayer(int direction) {
		if (player.isStunned()&&!searching){
			logMessage("You can't move!",Color.RED);
			endTurn();
		}
		else if (creatureCanMoveInDirection(player, direction)) {
			player.getTile().getTileInDirection(direction).addEntity(player);
			endTurn();
		} else if (player.getTile().getTileInDirection(direction).getTopEntity() != null
				&& player.getTile().getTileInDirection(direction).getTopEntity() instanceof Monster) {
			player.attack((Creature) player.getTile().getTileInDirection(direction).getTopEntity());
			endTurn();
		}

	}

	public void moveAction(int direction) {
		if (!searching) {
			movePlayer(direction);
		} else {
			if (search.getTile().getTileInDirection(direction) != null) {
				search.setTile(search.getTile().getTileInDirection(direction));
			}
			displayMapAroundTile(search.getTile(), currentLevel);
		}
	}

	public void endTurn() {
		player.tickAllEffects();
		for (Creature c : getLevel(currentLevel).getAllActors()) {
			c.act();
		}

		displayMapAroundTile(player.getTile(), currentLevel);

	}

	public void getKeyPress(String keyText) {

		if (keyText.length() == 1 && !helpOpen) {
			switch (keyText.charAt(0)) {
			case 'W':
				moveAction(NORTH);
				break;
			case 'A':
				moveAction(WEST);
				break;
			case 'S':
				moveAction(SOUTH);
				break;
			case 'D':
				moveAction(EAST);
				break;
			case 'F':
				if (!searching) {
					search = new Searcher(player.getTile());
					searching = true;
				} else {
					if (calcFOV(player, 14).contains(search.getTile())) {
						logMessage(search.getPropertiesOfTile());
					} else {
						logMessage("You can't see what is there");
					}
					searching = false;
				}
				displayMapAroundTile(player.getTile(), currentLevel);
				break;
			case 'I':
				if (!searching) {
					if (player.isStunned()){
						logMessage("You can't move!",Color.RED);
						displayMapAroundTile(player.getTile(),currentLevel);
						panel.updateUI();
					}
					else {
						player.interact();
					}
					endTurn();
				}
				break;
			case 'O':
				if (!searching) {
					endTurn();
				}
				break;
			case 'P':
				if (!searching) {
					if (player.isStunned()) {
						logMessage("You can't move!",Color.RED);
						displayMapAroundTile(player.getTile(),currentLevel);
						panel.updateUI();
					}
					else {
						player.usePotion();
					}
					endTurn();
				}
				break;
			case 'H':
				helpOpen = !helpOpen;
				showHelp();
				break;
			case 'L':
				if (!searching) {
					if (player.isStunned()) {
						logMessage("You can't move!",Color.RED);
						displayMapAroundTile(player.getTile(),currentLevel);
						panel.updateUI();
					}
					else {
						player.useSpecial();
					}
					endTurn();
				}
				break;
			}
			return;
		}
		else{
			if(helpOpen){
				if (keyText.length() == 1 && keyText.charAt(0) == 'H'){
					helpOpen = false;
					if(!searching){
						displayMapAroundTile(player.getTile(), currentLevel);
					}
					else{
						displayMapAroundTile(search.getTile(), currentLevel);
					}
				}
			}
		}

	}

	// after the player dies, he must go back to the beginning of the level
	// he keeps his level, but loses all items and gold in his inventory(handled
	// in Player's die method)
	// the player keeps his seenTiles
	public void revertToBeginning() {
		// removes any blockages of the entrance or the tiles around it
		// for(Tile t : levels.get(0).getEntrance().getAdjacentTiles()){
		// for(Tile o : t.getAdjacentTiles()){
		// o.removeEntity(o.getTopEntity());
		// }
		// }

		insertEntity((Entity) player, levels.get(0).getEntrance());
		currentLevel = 0;
		player.deleteAllEffects();
		player.heal(player.getMaxHealth());
		endTurn();
	}

	public void end() {
		// String
		// panel.setCursorPosition(panel.getWidthInCharacters()/2, y);
	}

	public void logMessage(String msg) {
		logMessage(msg, Color.WHITE);
	}

	public void logMessage(String msg, Color c) {
		log.logMessage(msg, c);
	}

	public void goDown() {
		if(levels.size() <= currentLevel + 1){
			generateNextLevel();
		}
		currentLevel++;
		levels.get(currentLevel).getEntrance().addEntity(player);
		logMessage("You are now at level " +(currentLevel+1)+ " of the dungeon.");
	}
	
	public void goUp(){
		if(currentLevel == 0){
			logMessage("You cannot leave the dungeon!");
		}
		else{
			currentLevel--;
			levels.get(currentLevel).getExit().addEntity(player);
			logMessage("You are now at level " +(currentLevel + 1)+ " of the dungeon.");
		}
	}
}
