package game.model;

import java.util.ArrayList;
import java.awt.image.*;
import game.model.objects.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

public class World{
	private ConcurrentHashMap<String, Player> playerList;
	private ArrayList<AIPlayer> aiPlayerList;
	
	private ArrayList<Enemy> enemyList;
	
	private String thisClientID;

	private String time = "60";
	private int spawnX;
	private int spawnY;
	private int doorX;
	private int doorY;

	
	private Door door;
	private String mapFile;
	private TileMap tileMap;
	private BufferedImage image;
	private int[][] intMap;
	private int tileSize = 64;
	private boolean leaderBoard = false;

	private boolean single;
	
	/**
	* Constructor for World called for each client in multiplayer.
	* Initialises player, details of other players, etc. are then passed afterwards.
	*/
	
	public World(String mapFile, String thisClientID){
		this.thisClientID = thisClientID;
		this.mapFile = mapFile;
		initMap(mapFile);
		
		playerList = new ConcurrentHashMap<String, Player>();
		
		Player newPlayer = new Player(spawnX, spawnY, 45, 100, tileMap, thisClientID);
		newPlayer.setTiles(intMap);
		playerList.put(thisClientID, newPlayer);

		
	}
	
	/**
	* Constructor for World called in multiplayer.
	* Initialises players, ai players and enemies with spawn locations.
	* @param mapFile String of location of map .txt.
	* @param clientList List of player IDs.
	* @param aiList List if AI player IDs.
	*/
	
	public World(String mapFile, String[] clientList, String[] aiList){
		this.mapFile = mapFile;
		initMap(mapFile);
		
		playerList = new ConcurrentHashMap<String, Player>();

		ArrayList<Integer> coinList = tileMap.getCoinList();
		
		for (String clientID : clientList) {
			getSpawnPos();
			Player newPlayer = new Player(spawnX, spawnY, 45, 100, tileMap, true);
			newPlayer.setTiles(intMap);
			playerList.put(clientID, newPlayer);
			System.out.println("Adding " + clientID + " to server's world");
		}
		
		for (String aiID : aiList) {
			getSpawnPos();
			Player newAIPlayer = new AIPlayer(spawnX, spawnY, 45, 100, tileMap);
			newAIPlayer.setTiles(intMap);
			playerList.put(aiID, newAIPlayer);
			System.out.println("adding "+aiID+" to server world");
		}
		
	}
	
	/**
	* Constructor for World in singleplayer.
	* Sets world's player ID to 'SinglePlayer'.
	* Initialises player and enemies with spawn locations.
	* Initialises Door and door position.
	* @param mapFile String of location of map .txt.
	*/
	
	public World(String mapFile){
		
		this.mapFile = mapFile;
		initMap(mapFile);
		thisClientID = "SinglePlayer";
		single = true;
		
		playerList = new ConcurrentHashMap<String, Player>();
		

		getSpawnPos();
		Player newPlayer = new Player(spawnX, spawnY, 45, 100, tileMap, true);
		newPlayer.setTiles(intMap);
		playerList.put(thisClientID, newPlayer);
		
		ArrayList<Integer> coinList = tileMap.getCoinList();
		System.out.println(coinList.get(0) + coinList.get(1));
		
		enemyList = new ArrayList<Enemy>();
		ArrayList<Integer> enemySpawn = tileMap.getEnemySpawn();
		for (int i = 0; i < enemySpawn.size();i++){
			enemyList.add(new Enemy(enemySpawn.get(i)*64, enemySpawn.get(++i)*64 + 64 - 18, 70, 18, tileMap, newPlayer));
		}
		
		getDoorPos();
		door = new Door(doorX, doorY, 75, 110, tileMap);
	}

	/**
	* Initialises tile map.
	* @param map String of location of map file.
	*/
	
	private void initMap(String map){
		System.out.println("NEW WORLD CREATED" + thisClientID);
		tileMap = new TileMap(map, tileSize);
		tileMap.loadTiles("TheWildEscape/src/resources/tileset2.png");
		intMap = tileMap.getTiles();
	}
	
	/**
	* Damages closest player (by deducting a life) within a certain range when another player uses an attack advantage.
	* @param clientID ID of player using attack advantage
	*/	
	
	public void attack(String clientID){
		Player attacker = getPlayer(clientID);

		if (attacker.getHasAttack()){	
			String closestPlayer = "";
			int yCheck = 3*tileSize;
			int xCheck = 6*tileSize;
			for (String p : playerList.keySet()){
				if (!p.equals(clientID) && isPlayerAlive(p)){
					Player player = getPlayer(p);
					int newYCheck = (int)(player.getYPos()-attacker.getYPos());
					int newXCheck = (int)(player.getXPos()-attacker.getXPos());
					
					if (newYCheck <= yCheck || newYCheck <= (-1)*yCheck 
								|| newXCheck <= xCheck || newXCheck <= (-1)*xCheck){
						closestPlayer = p;
					}

				}
			}
			if (!closestPlayer.equals("")){
				attacker.setHasAttack(false);
				Player attacked = getPlayer(closestPlayer);
				attacked.deductLife();
			}
			
		}
	}
	
	/**
	* Freezes the players when a player uses a freeze advantage.
	* @param clientID ID of player using freeze advantage
	*/
	
	public void freeze(String clientID) {
		Player attacker = getPlayer(clientID);
		if(attacker.getHasFreeze()){
			for (String p : playerList.keySet()){
				if (!p.equals(clientID)){
					playerList.get(p).freeze();
				}			
			}
			attacker.setHasFreeze(false);
		}
	}
	
	/**
	* Sets whether there is a leader board.
	* @param leaderBoard1 boolean
	*/
	
	public void leaderBoard(boolean leaderBoard1){
		leaderBoard=leaderBoard1;
	}
	
	/**
	* @return if there is a leader board
	*/
	
	public boolean getLeaderBoard(){
		return leaderBoard;
	}
	
	/**
	* @return tileMap object for world
	*/
	
	public TileMap getTileMap(){
		return tileMap;
	}
	
	/**
	* @return world's player
	*/
	
	public Player getPlayer(){
		return (playerList.get(thisClientID));
	}

	/**
	* @param clientID ID of desired player
	* @return player with specified ID
	*/
	
	public Player getPlayer(String clientID){
		return playerList.get(clientID);
	}
	
	/**
	*@return door object
	*/

	public Door getDoor(){
		return door;
	}
	
	/**
	* Updates status of moving objects in the world.
	*/

	public void update(){
		for(String key: playerList.keySet()){
			Player p = playerList.get(key);
			if(p.getAI()){
				AIPlayer ap = (AIPlayer)p;
				ap.update();
				ap.setAnimations();
			}else{
				p.move();
				p.setAnimations();
			}
		}
				
		if (single){
			for (Enemy e : enemyList){
				e.move();
			}
		}
	}
	
	/**
	* Sends message to world's player to jump.
	*/
	
	public void playerJump(){
		(playerList.get(thisClientID)).jump();
	}
	
	/**
	* Sends message to player to jump.
	* @param clientID ID of relevant player.
	*/
	
	public void playerJump(String clientID){
		(playerList.get(clientID)).jump();
	}
	
	/**
	* Sends message to world's player to move left.
	*/
	
	public void playerLeft(){
		(playerList.get(thisClientID)).moveLeft();
	}
	
	/**
	* Sends message to player to move left.
	* @param clientID ID of relevant player.
	*/
	
	public void playerLeft(String clientID){
		(playerList.get(clientID)).moveLeft();
	}
	
	/**
	* Sends message to world's player to move right.
	*/
	
	public void playerRight(){
		(playerList.get(thisClientID)).moveRight();
	}
	
	/**
	* Sends message to player to move right.
	* @param clientID ID of relevant player.
	*/
	
	public void playerRight(String clientID){
		(playerList.get(clientID)).moveRight();
	}
	
	/**
	* Sends message to world's player to stop.
	*/
	
	public void playerStop(){
		(playerList.get(thisClientID)).stop();
	}
	
	/**
	* Sends message to player to stop.
	* @param clientID ID of relevant player.
	*/
	
	public void playerStop(String clientID){
		(playerList.get(clientID)).stop();
	}
	
	/**
	* Sends message to world's player to climb up.
	*/

	public void playerLadderUp() {
		(playerList.get(thisClientID)).ladderUp();
	}
	
	/**
	* Sends message to player to climb up.
	* @param clientID ID of relevant player.
	*/
	
	public void playerLadderUp(String clientID){
		(playerList.get(clientID)).ladderUp();
	}
	
	/**
	* Sends message to world's player to climb down.
	*/
	
	public void playerLadderDown() {
		(playerList.get(thisClientID)).ladderDown();
	}
	
	/**
	* Sends message to player to climb down.
	* @param clientID ID of relevant player.
	*/
	
	public void playerLadderDown(String clientID){
		(playerList.get(clientID)).ladderDown();
	}
	
	/**
	* @return Whether the world's player has reached the door.
	*/
	
	public boolean doorReached(){
		Player p = playerList.get(thisClientID);
		if (p.doorReached(door)){
			return true;
		}
		else return false;
	}
	
	/**
	* @param clientID ID of player.
	* @return Whether the player has reached the door and hence completed the level.
	*/
	
	public boolean doorReached(String clientID){
		Player p = playerList.get(clientID);
		if(p.doorReached(door)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	* Retrieves a random spawn position form the map and sets the 
	* correct positioning for the player.
	*/
	
	public void getSpawnPos(){
		String rsp = tileMap.getRanSpawnPos();
		String rsp1 = rsp.substring(0, rsp.indexOf(","));
		String rsp2 = rsp.substring(rsp.indexOf(",")+1);
		int r = Integer.parseInt(rsp1);
		int c = Integer.parseInt(rsp2);
		spawnY = (r*tileSize)+tileSize-Player.height;
		spawnX = (c*tileSize);
	}
	
	/**
	* Sets positions of door in x and y axis.
	*/
	
	public void getDoorPos(){
		doorX = tileMap.getDoorPosX()*tileSize;
		doorY = (tileMap.getDoorPosY()*tileSize)+tileSize-Door.height;
	}
	
	/**
	* @return Whether this world's player is alive.
	*/
	
	public boolean isPlayerAlive(){
		if(thisClientID != null){
			return (playerList.get(thisClientID)).getIsAlive();
		}else{
			return true;
		}
	}
	
	/**
	* @return the ID of the current highest scorer from the alive players in the world 
	*/
	
	public String getLeader(){
		int highScore = 0;
		String highScorer = "";
		for (String k : playerList.keySet()){
			if (playerList.get(k).getScore()>highScore && isPlayerAlive(k)){
				highScore = playerList.get(k).getScore();
				highScorer = k;
			}
		}
		return highScorer;
	}
	
	/**
	* Returns whether player is alive in world.
	* @param clientID ID of player in question
	* @return boolean of whether the player is alive
	*/
	
	public boolean isPlayerAlive(String clientID){
		return (playerList.get(clientID)).getIsAlive();
	}
	
	/**
	* @return List of players that are still alive in the world
	*/
	
	public ArrayList<String> getAlivePlayers(){
		ArrayList<String> alivePlayers = new ArrayList<String>();
		for (String k : playerList.keySet()){
			
			if (isPlayerAlive(k)){
				alivePlayers.add(k);
			}
		}
		return alivePlayers;
	}
	
	/**
	* Adds new player to list of players in the world
	* @param clientID ID of new player
	* @param x position of new player in x-axis
	* @param y position of new player in y-axis
	*/
	
	public void addNewPlayer(String clientID, float x, float y){
		Player newPlayer = new Player(x, y, 45, 100, tileMap, clientID);
		newPlayer.setTiles(intMap);
		playerList.put(clientID, newPlayer);
	}
	
	/**
	* Sets position of specified player
	* @param clientID ID of player
	* @param x new position of player in x-axis
	* @param y new position of player in y-axis
	*/ 
	
	public void dictatePlayerPosition(String clientID, float x, float y){
		Player p = playerList.get(clientID);
		p.setXPos(x);
		p.setYPos(y);
	}
	
	/**
	* @return List of players in world
	*/
	
	public ConcurrentHashMap<String, Player> getPlayerList() {
		return playerList;
	}
	
	/**
	* @return client/player ID for this world
	*/	

	public String getThisClientID(){
		return thisClientID;
	}
	
	/**
	* @return time left of gameplay
	*/
	
	public String getTime(){
		return time;
	}
	
	/**
	* Sets time
	* @param time time left for gameplay
	*/
	
	public void setTime(String time){
		this.time = time;
	}
	
	/**
	* @return list of enemy's in current world
	*/

	public ArrayList<Enemy> getEnemyList() {
		return enemyList;
	}
	
	/**
	* Sets player's isHaunting boolean
	*@param tf whether the player is haunting or not
	*/

	public void setIsPlayerHaunting(boolean tf){
		playerList.get(thisClientID).setIsHaunting(tf);
	}
	
	/**
	*@return boolean of whether player is in observation mode (haunting)
	*/
	
	public boolean getIsPlayerHaunting(){
		return playerList.get(thisClientID).getIsHaunting();
	}
}
