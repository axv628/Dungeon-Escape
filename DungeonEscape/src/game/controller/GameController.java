package game.controller;

import game.model.*;
import game.model.objects.MovableObject;
import game.model.objects.Player;

/**
*Controls the game state
*/
public class GameController {

	private World world;
	private GameEngine gameEng;
	private boolean single = false;

	/**
	*Creates a controller with the specified world to control, and the game engine.
	*@param world The world to be controlled
	*@param gameEng The gameEng that 
	*/
	public GameController(World world, GameEngine gameEng) {
		this.world = world;
		this.gameEng = gameEng;
	}

	public void update(){
		world.update();
		if (!world.isPlayerAlive() && !world.getIsPlayerHaunting()){
			gameOver("DIED");
			world.setIsPlayerHaunting(true);
		}
	}
	
	public void timeUpdate(String time){
		world.setTime(time);
	}
	
	public void doPlayerDamage(String id){
		System.out.println(id);
		Player p = world.getPlayer(id);
		p.deductLife();
	}

	public void jump() {
		world.playerJump();
	}

	public void jump(String clientID) {
		world.playerJump(clientID);
	}

	public void left() {
		world.playerLeft();
	}

	public void left(String clientID) {
		world.playerLeft(clientID);
	}

	public void right() {
		world.playerRight();
	}

	public void right(String clientID) {
		world.playerRight(clientID);
	}

	public void stop() {
		world.playerStop();
	}

	public void stop(String clientID) {
		world.playerStop(clientID);
	}

	public void upClimb() {
		world.playerLadderUp();
		if (world.doorReached()) {
			gameEng.levelComplete();
		}
	}

	public void upClimb(String clientID) {
		world.playerLadderUp(clientID);
		if (world.doorReached(clientID)) {
			gameEng.levelComplete();
		}
	}

	public void downLadder() {
		world.playerLadderDown();
	}

	public void downLadder(String clientID) {
		world.playerLadderDown(clientID);
	}
	
	public void leaderBoard(boolean leaderBoard){
		world.leaderBoard(leaderBoard);
	}
	
	public void attack(String clientID){
		world.attack(clientID);
	}
	
	public void freeze(String clientID){
		world.freeze(clientID);
	}
	
	public void addNewPlayer(String clientID, float x, float y){
		System.out.println("Controller adding new player: " + clientID);
		world.addNewPlayer(clientID, x, y);
	}
	
	public void dictatePlayerPosition(String clientID, float x, float y){
		world.dictatePlayerPosition(clientID, x, y);
	}
	
	public void gameOver(String winnerID){
		gameEng.gameOver(winnerID);
	}
}