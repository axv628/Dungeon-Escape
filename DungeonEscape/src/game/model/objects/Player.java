package game.model.objects;

import java.io.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;


import game.view.SoundManager;
import game.view.Animation;

public class Player extends MovableObject{
	private boolean ai = false;
	
	private BufferedImage sprite;
	private BufferedImage gSprite;
	private BufferedImage life1;
	private BufferedImage life2;
	private BufferedImage life3;
	private BufferedImage gem;
	
	//animations
	private Animation animation;
	private int currentAction;
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {1, 1, 1, 6, 6, 2, 1, 6};
	
	//actions
	private static final int L_STILL = 0;
	private static final int R_STILL = 1;
	private static final int L_MOVING = 3;
	private static final int R_MOVING = 4;
	private static final int JUMPING = 2;
	private static final int CLIMBING = 5;
	private static final int FROZEN = 6;
	private static final int DAMAGE = 7;
	
	private String clientID;
	
	private float jumpVel;	//the standard value for jumping
	private float walkVel;	//the standard value for walking 
	
	private Door door;
	private int lives;
	private int playerOption;  //used when we have multiple characters to choose from
	private int score;
	private boolean hasAttack;
	
	private int[][] intMap;
	private int mapHeight;
	private int mapWidth;
	private TileMap tileMap;   
	private int tileSize;

	private boolean doorReached = false;
	private boolean isAlive = true; //set to false if player dies/gets knocked out of multiplayer game, maybe replace with playerstates
	
	private boolean onLadder = false;
	private boolean isLeftHeld;
	private boolean isRightHeld;
	private boolean hasFreeze;
	private boolean isFrozen;
	private boolean isHaunting;
	private boolean isDamaged;
	
	
	private static final float GRAVITY = 0.6f;
	public static final int width = 45; 
	public static final int height = 100;

	private static final float TERMINAL_FALL = 30.0f;
	private static final int FREEZE_TIME = 5;
	
	private SoundManager soundManager;
	
	public Player(int lives, int score, int width, int height, TileMap tileMap, int[][] intMap, float xPos, float yPos, float xVel, float yVel, float xAcc, float yAcc) {
        this(xPos, yPos, width, height, tileMap, true);
        this.lives = lives;
        this.score = score;
        this.intMap = intMap;
        setXVel(xVel);
        setYVel(yVel);
        setXAcc(xAcc);
        setYAcc(yAcc);
        soundManager = new SoundManager();
    }


    @Override
    public Player clone() {
        return new Player(lives, score, width, height, tileMap, intMap,  getXPos(), getYPos(), getXVel(), getYVel(), getXAcc(), getYAcc());
    }
	
    /**
	 *one of the players in a multiplayer game
	 */
	public Player(float x, float y, int width, int height, TileMap tm, String clientID){
		super(x, y, width, height, tm);
		this.clientID = clientID;
		animation = new Animation();
		tileSize = tm.getTileSize();
		jumpVel = -10.0f;
		walkVel = 10f;
		
		refreshLives();

		loadSprites("TheWildEscape/src/resources/playerset.png");
		loadLifeSprites("TheWildEscape/src/resources/");
		loadGem("TheWildEscape/src/resources/attack.png");
		score = 0;
		hasAttack = false;
		animation.setFrames(sprites.get(L_STILL));
		soundManager = new SoundManager();
	}
	
	/**
	 *The player in singleplayer mode
	 */
	
	public Player(float x, float y, int width, int height, TileMap tm, boolean loadSprites){
		super(x, y, width, height, tm);
		animation = new Animation();
		tileSize=tm.getTileSize();
		jumpVel = -10.0f;// maybe these need to be FINAL?
		walkVel = 10f;
		refreshLives();
		if(loadSprites) {
			loadSprites("TheWildEscape/src/resources/playerset.png");
			loadLifeSprites("TheWildEscape/src/resources/");
			loadGem("TheWildEscape/src/resources/attack.png");
			animation.setFrames(sprites.get(JUMPING));
		}
		score = 0;
		soundManager = new SoundManager();
	}
	
	/**
	* Loads player's spritesheet.
	*/
	
	private void loadSprites(String spriteFile){
		try {
			BufferedImage spriteSheet = ImageIO.read(new File(spriteFile)); 
			sprites = new ArrayList<BufferedImage[]>();
			
			for (int i = 0; i<8; i++){//number of actions
				BufferedImage[] image = new BufferedImage[numFrames[i]];
				for (int j=0; j<numFrames[i]; j++){
					image[j] = spriteSheet.getSubimage(j*width, i*height, width, height);
				}
				sprites.add(i, image);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* Loads ghost version of the player's sprite sheet.
	*/
	
	private void loadGhostSprites(){
		loadSprites("TheWildEscape/src/resources/playersetGhost.png");
	}
	
	/**
	* Loads life sprites.
	*/
	
	private void loadLifeSprites(String path){
		try{
			life1= ImageIO.read(new File(path+"lives1.png"));
			life2= ImageIO.read(new File(path+"lives2.png"));
			life3= ImageIO.read(new File(path+"lives3.png"));
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	* Loads attack gem sprite for feedback.
	* @param path String of location of  .png
	*/
	
	private void loadGem(String path){
		try{
			gem = ImageIO.read(new File(path));
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	
	/**
	 *Called in World
	 *sends the tileMap in the form of an int array to Player
	 */
	public void setTiles(int[][] intMap){
		this.intMap = intMap;
		mapHeight = intMap.length;
		mapWidth = intMap[0].length;
	}
	/**
	 *part of the game loop
	 *updates the playes position every tick according to velocities
	 */
	public void move(){
		setXPos(getXPos() + getXVel());
		setYPos(getYPos() + getYVel());
		checkCollisions();
	}
	
	/**
	 *check for platform collisions
	 *check for item collisions
	 *check whether the player is on a ladder
	 */
	
	private void checkCollisions(){
		checkLadder();
		checkObjects();
	
		if (checkDown(getYPos(),getXPos(),getYVel())){
			if (getYVel()>TERMINAL_FALL){
				deductLife();
			}
			setYPos((int)getYPos()/tileSize*tileSize+27.5f);
			setYVel(0);
		} else if (checkUp(getYPos(), getXPos(),getYVel())){
			setYPos((int)getYPos()+5);
			setYVel(0);
		}
		
		if (checkRight(getYPos(),getXPos(),getXVel())){
			setXVel(0);
			setXPos((int)getXPos()/tileSize*tileSize+tileSize-width);
			isRightHeld = true;
		} else isRightHeld = false;
		
		if (checkLeft(getYPos(),getXPos(),getXVel())){
			setXVel(0);
			setXPos(((int)getXPos())/tileSize*tileSize+tileSize-1);
			isLeftHeld = true;
		} else isLeftHeld = false;
		
		if (getYPos() >= -12 && getYPos() < (mapHeight -2)* tileSize && !onLadder && getYVel()<TERMINAL_FALL){
			setYVel(getYVel() + GRAVITY);
		} 
		if (getYPos() < 0){ //upward map collision
			setYPos(0);
		}
	}

	/**
	* Controls player movement to jump.
	*/
	
	public void jump(){
		if (!isFrozen && (getYPos()==(mapHeight-2) * tileSize || checkDown(getYPos()+2, getXPos(), getYVel()))){ //this should check whether the object is on a collidable surface
			setYVel(jumpVel); //sets y velocity to the standard velocity for that object
		}
	}
	
	/**
	* Controls player movement to move left.
	*/
	
	public void moveLeft(){
		if (!isLeftHeld && !isFrozen){
			setXVel(-walkVel - getXAcc());
			if (getXAcc() < 3.0f){
				setXAcc(getXAcc() * 1.2f);
			}
		}
	}
	
	/**
	* Controls player movement to move right.
	*/
	
	public void moveRight(){
		if (!isRightHeld && !isFrozen){	
			setXVel(walkVel + getXAcc());
			if (getXAcc() < 3.0f){
				setXAcc(getXAcc() * 1.2f);
			}
		}
	}
	
	/**
	* Controls player movement to stop.
	*/
	
	public void stop(){
		setXVel(0);
		setXAcc(0.2f);
		animation.setDelay(-1);
	}
	
	/**
	* Controls player movement to climb upwards.
	*/
	
	public void ladderUp() {
		if (onLadder && !isFrozen){
			setYVel(-walkVel - getXAcc());
			
			if (getXAcc() < 3.0f){
				setXAcc(getXAcc() * 1.2f);
				setClimbing();
			}
		}
	}
	
	/**
	* Controls player movement to climb downwards.
	*/

	public void ladderDown() {
		if (onLadder && !isFrozen){
			setYVel(walkVel + getXAcc());
			
			if (getXAcc() < 3.0f){
				setXAcc(getXAcc() * 1.2f);
				setClimbing();
			}
		}		
	}
	
	/**
	* @return Whether there is a climable object.
	*/
	
	public void checkLadder(){
		if (getType(intMap[((int)getYPos()+height/2)/tileSize ][((int)getXPos() + width/2)/tileSize]) == 2 ){
			onLadder  = true;
			setYVel(0);
		}else onLadder = false;
	}
	
	public void setOnLadder(boolean onLadder){
		this.onLadder=onLadder;
	}
	
	/**
	 * Checks whether a player can pick up an item from its position and updates relevant fields.
	 */
	
	public void checkObjects(){
		if (isAlive){
			int checkY = ((int)getYPos()+height*2/3)/tileSize;
			int checkY2 = ((int)getYPos()+tileSize/2)/tileSize;
			int checkX = ((int)getXPos() + width/2)/tileSize;
			
			if (getType(intMap[checkY][checkX]) == 27 ){
				addPoints(1);
				soundManager.playSoundEffect();
				intMap[checkY][checkX] = 0;
				Coin coin = new Coin(checkY, checkX, intMap);
				coin.respawn(); //coin respawns after 30 seconds
			} //checks for a coin in the bottom half of the player
			else if (getType(intMap[checkY2][checkX]) == 27){
				addPoints(1);	
				soundManager.playSoundEffect();
				intMap[checkY2][checkX] = 0;
				Coin coin = new Coin(checkY2, checkX, intMap);
				coin.respawn(); //coin respawns after 30 seconds
			}//checks for a coin in the top half
			else if (lives < 3 && getType(intMap[checkY2][checkX]) == 28){
				addLife();	
				soundManager.playSoundEffect();
				intMap[checkY2][checkX] = 0;
				LifeBox lifeBox = new LifeBox(checkY2, checkX, intMap);
				lifeBox.respawn();
			}
			else if (lives < 3 && getType(intMap[checkY][checkX]) == 28){
				addLife();	
				soundManager.playSoundEffect();
				intMap[checkY][checkX] = 0;
				LifeBox lifeBox = new LifeBox (checkY, checkX, intMap);
				lifeBox.respawn();
			}
			else if (!hasAttack && getType(intMap[checkY2][checkX]) == 29){
				hasAttack = true;
				soundManager.playSoundEffect();
				System.out.println("Collected attack");		
				intMap[checkY2][checkX] = 0;
				AttackBox attackBox = new AttackBox(checkY2, checkX, intMap);
				attackBox.respawn();
			}
			else if (!hasAttack && getType(intMap[checkY][checkX]) == 29){
				hasAttack = true;
				soundManager.playSoundEffect();
				System.out.println("Collected attack");				
				intMap[checkY][checkX] = 0;
				AttackBox attackBox = new AttackBox(checkY2, checkX, intMap);
				attackBox.respawn();
			}
			else if (!hasFreeze && getType(intMap[checkY][checkX]) == 30){
				hasFreeze = true;
				soundManager.playSoundEffect();
				System.out.println("Collected Freeze");				
				intMap[checkY][checkX] = 0;
				FreezeBox freezeBox = new FreezeBox (checkY, checkX, intMap);
				freezeBox.respawn();
			}
		}
	}
	
	/**
	* Checks collisions above the player.
	* @return Whether a solid object lies in the way of the player.
	*/
	
	public boolean checkUp(float y, float x, float yVel){	
		if (yVel<0 && getType(intMap [(int)y/tileSize][((int)x+13)/tileSize]) == 1  
	          	   || getType(intMap [(int)y/tileSize][((int)x+width-13)/tileSize])==1){
			return true;
		}
		return false;
	}
	
	/**
	* Checks collisions below the player.
	* @return Whether a solid object lies in the way of the player.
	*/
	
	public boolean checkDown(float y, float x, float yVel){
		if (yVel>=0 && (getType(intMap [((int)y+height)/tileSize][((int)x+13)/tileSize])==1 
				|| getType(intMap [((int)y+height)/tileSize][((int)x+width-13)/tileSize])==1)){
			return true;
		}return false;
	}
	
	/**
	* Checks collisions to the right of the player.
	* @return Whether a solid object lies in the way of the player.
	*/
	
	private boolean checkRight(float y, float x, float xVel) {
		if (xVel>=0 && getType(intMap [((int)y+5)/tileSize][((int)x+width)/tileSize]) == 1
				|| getType(intMap[((int)y+height)/tileSize][((int)x+width)/tileSize]) == 1
				|| getType(intMap[((int)y+height/2)/tileSize][((int)x+width)/tileSize]) == 1){
			return true;
		} else return false;
	}
	
	/**
	* Checks collisions to the left of the player.
	* @return Whether a solid object lies in the way of the player.
	*/

	private boolean checkLeft(float y, float x, float xVel) {
		if (xVel<=0 && getType(intMap[((int)y+5)/tileSize][((int)x)/tileSize]) == 1  
				 || getType(intMap[((int)y+height)/tileSize][((int)x)/tileSize]) == 1
				 || getType(intMap[((int)y+height/2)/tileSize][((int)x)/tileSize]) ==1 ){
			return true;
		} else return false;
	}
	
	/**
	* @return Whether player has reached the door.
	*/

	public boolean doorReached(Door door){
		if(door != null){
			this.door = door;
			int doorx = (int)door.getXPos();
			int doory = (int)door.getYPos();
		
			if ((((int)getXPos()+width)>doorx)&&((int)getXPos()<(doorx+door.getWidth()))){	//if any part of the player is in contact with the door horizontally
				if (((int)getYPos()+height>doory)&&((int)getYPos()<=(doory+door.getHeight()))){	//same case as above but vertically
					doorReached = true;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	* Deducts a life from the player and notifies if the player has run out of lives. 
	* If this happens, it also loads the ghost sprites for haunting mode.
	*/
	
	public void deductLife(){	
		lives--;
		isDamaged = true;
		if (lives==0){
			setIsAlive(false);	//no lives left, player has lost<<<<<<<<<<
			loadGhostSprites();
		}
	}
	
	/**
	* Adds a life if the player has less than the maximum of three.
	*/
	
	public void addLife(){
		if (lives < 3){
			lives++;
		}
	}
	
	/**
	* Reloads player's lives.
	*/
	
	public void refreshLives(){
		lives = 3;
	}
	
	/**
	* @return Player's lives.
	*/
	
	public int getLives(){
		return lives;
	}
	
	/**
	* @return Player's score.
	*/
	
	public int getScore(){
		return score;
	}
	
	/**
	* Adds to player's score.
	* @param Points to be added.
	*/
	
	public void addPoints(int pointsAdded){
		score=score+pointsAdded;
	}
	
	/**
	* @return Sprite for player feedback of lives.
	* @param n Number of lives.
	*/
	
	public BufferedImage getLifeSprite(int n){
		if (n==1){ return life1;}
		if (n==2){ return life2;}
		else return life3;
	}
	
	/**
	* @return Type of tile for player collisions.
	*/
	
	public int getType(int tile){
		if (tile<9){
			return 0;
		}
		if (tile<18){
			return 1;
		}
		if (tile<27){
			return 2;
		}
		else return tile;
	}
	
	/**
	* Sets player ID.
	* @param clientID Player ID
	*/
	
	public void setClientID(String clientID){
		this.clientID = clientID;
	}
	
	/**
	* @return ID of player.
	*/
	
	public String getClientID(){
		return clientID;
	}
	
	/**
	* @return Height of player in pixels.
	*/
	
	public int getHeight(){
		return height;
	}
	
	/**
	* @return Width of player in pixels.
	*/
	
	public int getWidth(){
		return width;
	}
	
	/**
	* @return Whether player is alive.
	*/
	
	public boolean getIsAlive(){
		return isAlive;
	}
	
	/**
	* @return Whether player has attack advantage.
	*/
	
	public boolean getHasAttack(){
		return hasAttack;
	}
	
	/**
	* Sets whether player has attack advantage.
	* @param tf boolean
	*/
	
	public void setHasAttack(boolean tf){
		hasAttack = tf;
	}
	
	/**
	* Sets whether player is alive.
	* @param tf boolean
	*/
	
	public void setIsAlive(boolean tf){
		isAlive=tf;
	}
	
	/**
	* @return Attack gem sprite.
	*/
	
	public BufferedImage getAttackImage(){
		return gem;
	}
	
	/**
	* @return Whether on ladder.
	*/
	
	public boolean isOnLadder(){
		return onLadder;
	}
	
	/**
	* Sets appropriate animation for player depending on velocity and location and damage/frozen status.
	*/
	
	public void setAnimations(){
		if (isFrozen){
			if (currentAction!=FROZEN){
				currentAction = FROZEN;
				animation.setFrames(sprites.get(FROZEN));
				animation.setDelay(-1);
			}
		}
		else if (isDamaged){
			if (currentAction!=DAMAGE){
				currentAction = DAMAGE;
				animation.setFrames(sprites.get(DAMAGE));
				animation.setDelay(100);
				animateDamage();
			}
		}
		else if (getYVel() < 0){
			if (currentAction!=JUMPING){
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
			}
		}
		else if (isOnLadder()){
			if (currentAction!=CLIMBING){
				currentAction = CLIMBING;
				animation.setFrames(sprites.get(CLIMBING));
				animation.setDelay(-1);
			}
		}
		else if (getXVel()==0){
			if (currentAction!=L_STILL || currentAction!=R_STILL){
				if (currentAction == L_MOVING){
					currentAction = L_STILL;
				}
				if (currentAction == R_MOVING){
					currentAction = R_STILL;
				}
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(-1);
			}
		}
		else if (getXVel() < 0){
			if (currentAction!=L_MOVING){
				currentAction = L_MOVING;
				animation.setFrames(sprites.get(L_MOVING));
				animation.setDelay(60);
			}
		}
		else if (getXVel() > 0){
			if (currentAction != R_MOVING){
				currentAction = R_MOVING;
				animation.setFrames(sprites.get(R_MOVING));
				animation.setDelay(60);
			}
		}
		
		else if (getYVel() > 0){
			if (currentAction!=JUMPING){
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
			}
		}
		
		animation.update();
	}
	
	public void setClimbing(){
		if (currentAction == CLIMBING){
			animation.setDelay(100);
		}
	}
	
	public BufferedImage getImage(){
		return animation.getImage();
	}

	
	/**
	 * Handles the effect of a freeze box being used on the player, frozen for 5 seconds
	 */
	 
	public void freeze() {
		System.out.println("player " + getClientID() + "is frozen");
		soundManager.playSoundEffect();
		isFrozen = true;
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			@Override
			  public void run() {
				  isFrozen = false;
			  }
			}, FREEZE_TIME *1000);
	}
	
	/**
	* @return whether player has freeze advantage.
	*/
	
	public boolean getHasFreeze(){
		return hasFreeze;
	}
	
	/**
	* Sets whether player has a freeze advantage.
	* @param tf boolean
	*/
	
	public void setHasFreeze(boolean tf){
		hasFreeze = tf;
	}
	
	/**
	* Sets whether player is AI
	*/
	
	public void setAI(boolean isAI){
		ai = isAI;
	}
	
	/**
	* @return Whether player is an AI player.
	*/
	
	public boolean getAI(){
		return ai;
	}
	
	/**
	* Sets whether player is haunting.
	* @param tf boolean
	*/
	
	public void setIsHaunting(boolean tf){
		isHaunting = tf;
	}
	
	/**
	* @return Whether player is haunting.
	*/
	
	public boolean getIsHaunting(){
		return isHaunting;
	}
	
	/**
	* Sets timer for damage animation.
	*/
	
	public void animateDamage(){
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			@Override
			  public void run() {
				  isDamaged = false;
			  }
			}, 500);
	}
}
