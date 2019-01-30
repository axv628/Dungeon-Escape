package game.model.objects;

import game.view.GamePanel;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the map for each level in the game. Each map is made up of square tiles and read in from a .txt and
 * for all objects in the game.
*/

public class TileMap{
	private int frameWidth;
	private int frameHeight;
	
	private ArrayList<String> spawnPositions;
	private int doorPosX;
	private int doorPosY;
	
	private int tileSize;
	private int[][] mapRead; //map.txt will be read into this from map file
	private int mapWidth;	//number of tiles x
	private int mapHeight;	//number of tiles y
	private BufferedImage tileset;
	private BufferedImage background;
	private Tile tile;
	private Tile[][] tiles;
	private int numTilesAcross;
	private int width;
	private int height;
	private String backgroundFile;
	//scrolling
	
	//position
	private double x;
	private double y;
	//bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;
	
	private int numRowsToDraw;
	private int numColsToDraw;
	private int rowOffset;
	private int colOffset;
	private ArrayList<Integer> enemySpawn;
	private ArrayList<Integer> coinList;

	/**
	 * Sets size of tiles and reads in the tiles that will make up the map to be used from a .txt file. Each tile has a unique key which
	 * specifies what it is which is then translated to an index of where the tile's image appears in the tileSet.png and put into it's relative position 
	 * an int[][](mapRead).
	 * Also sets the minimum and maximum bounds of the map to ensure scrolling view doesn't pass the edges of the map.
	 * @param mapFile String that specifies the location of the map .txt to be used
	 * @param tileSize size of tile in pixels
	*/

	public TileMap(String mapFile, int tileSize){
		
		spawnPositions = new ArrayList<String>();
		enemySpawn = new ArrayList<Integer>();
		coinList = new ArrayList<Integer>();
		
		this.frameWidth = GamePanel.width;
		this.frameHeight = GamePanel.height;
		this.tileSize = tileSize;
		
		numRowsToDraw = frameHeight/tileSize+2;
		numColsToDraw = frameWidth/tileSize+2;
		try{
			BufferedReader br = new BufferedReader(new FileReader(mapFile));
			
			mapWidth = Integer.parseInt(br.readLine());
			mapHeight = Integer.parseInt(br.readLine());
			backgroundFile = br.readLine();
			background = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/"+backgroundFile+".png"));
			mapRead = new int[mapHeight][mapWidth];
			width = mapWidth*tileSize;
			height = mapHeight*tileSize;
			
			xmin=frameWidth-width;
			xmax=0;
			ymin=frameHeight-height;
			ymax=0;
			
			for (int r = 0; r < mapHeight; r++){
				String line = br.readLine();
				
				for (int c=0; c<mapWidth; c++){
					char s = line.charAt(c);
					switch (s){
						case ' ' :
							break;
						case 'P' : 
							mapRead[r][c] = 9;
							break;
						case 'X' :
							mapRead[r][c] = 16;
						case 'R' :
							mapRead[r][c] = 10;
							break;
						case 'Q':
							mapRead[r][c] = 11;
							break;
						case 'p':
							mapRead[r][c] = 13;
							break;
						case 'r':
							mapRead[r][c] = 14;
							break;
						case 'q':
							mapRead[r][c] = 12;
							break;
						case 'B':
							mapRead[r][c] = 15;
							break;
						case 'D' : 
							mapRead[r][c] = 0;
							doorPosX = c;
							doorPosY = r;
							break;
						case 'S' :
							mapRead[r][c] = 0;
							spawnPositions.add((String.valueOf(r) + "," + String.valueOf(c)));
							break;
						case 'E' :
							mapRead[r][c] = 0;
							enemySpawn.add(c);
							enemySpawn.add(r);
							break;
						case 'L' :
							mapRead[r][c] = 18;
							break;
						case 'C' : 
							mapRead[r][c] = 27;
							coinList.add(c);
							coinList.add(r);
							break;
						case 'H' :
							mapRead[r][c] = 28;
							break;
						case 'A' :
							mapRead[r][c] = 29;
							break;
						case 'V' :
							mapRead[r][c] = 19;
							break;
						case 'W' :
							mapRead[r][c] = 1;
							break;
						case 'F' :
							mapRead[r][c] = 30;
							break;
						
					}
					
				}
			}
			br.close();
		}
		catch (FileNotFoundException e){
			
			System.out.println("mapread failed");
			System.out.println(mapFile);
			File f = new File("level1map.txt");
			System.out.println(f.getAbsolutePath());
			System.out.println(f.getPath());
		}
		catch (IOException a){
			System.out.println("ioex");
		}
	}
	

	/**
	 *@return List of spawn locations of enemies in the map
	*/
	public ArrayList<Integer> getEnemySpawn(){
		return enemySpawn;
	}
	
	/**
	 *@return List of coin locations for AI use
	*/
	public ArrayList<Integer> getCoinList(){
		return coinList;
	}
	

	/**
	 * Loads tile images from tileSet.png by retrieving subimages of size tileSize*tileSize and inserting them
	 * into a Tile[][] with each row in the array (and png) being for different types of tiles (NORMAL, SOLID, CLIMBABLE, COLLECTABLE OBJECTS)
	 * @param tilesetFile String of location of tileSet.png
	*/
	public void loadTiles(String tilesetFile){
		try{
			tileset = ImageIO.read(new File(tilesetFile));
			numTilesAcross = tileset.getWidth()/tileSize;
			tiles = new Tile[4][numTilesAcross];
			
			BufferedImage oneTile;
			for (int c=0; c<numTilesAcross; c++){
				oneTile = tileset.getSubimage(c*tileSize, 0, tileSize, tileSize);
				tiles[0][c] = new Tile(oneTile, 0); //each row in the tileset will be for different 'types' of tile
				
				oneTile = tileset.getSubimage(c*tileSize, tileSize, tileSize, tileSize);
				tiles[1][c] = new Tile(oneTile, 1);
				
				oneTile = tileset.getSubimage(c*tileSize, 2*tileSize, tileSize, tileSize);
				tiles[2][c] = new Tile(oneTile, 2);
				
				oneTile = tileset.getSubimage(c*tileSize, 3*tileSize, tileSize, tileSize);
				tiles[3][c] = new Tile(oneTile, 3);
			}
		}
		catch (Exception e){
			System.out.println("loadtiles failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * Draws only the section of map which is visible on the screen by selecting the relevant tiles in relation to the player's position
	 * in the map.
	 *@param g Graphics
	 *@param xp current position of player in map on x-axis
	 *@param yp current position of player in map on y-axis
	 *@param xv player's current velocity in the x-axis
	 *@param yv player's current velocity in the y-axis
	 *@param i interpolation
	*/
	public void draw(Graphics g, float xp, float yp, float xv, float yv, float i){//will be moved to renderer	
		x = frameWidth/2 - xp - xv*i; //changing coordinates to render coordinates which take interpolation into account
		this.x += (x - this.x) * 0.7;
		y = frameHeight/2 - yp - yv*i;
		this.y += ( y - this.y) * 0.7;

		checkBounds();
		
		colOffset = (int)-this.x/tileSize;
		rowOffset = (int)-this.y/tileSize;
		for (int r=rowOffset; r<rowOffset+numRowsToDraw; r++){
			if (r>=mapHeight) break;
			for (int c=colOffset; c<colOffset+numColsToDraw; c++){
				if (c>=mapWidth) break;
				int currentTile= mapRead[r][c];
				int row = currentTile/numTilesAcross;
				int column = currentTile%numTilesAcross;
				
				g.drawImage(tiles[row][column].getImage(), (int)(x+tileSize*c), (int)(y+tileSize*r), null);
			}
		}
	}

	/**
	 * @return int[][] of tiles read in from map file
	*/
	public int[][] getTiles(){
		return mapRead;
	}
	
	/**
	 * Checks the 'viewport' location doesn't exceed the edges of the map
	*/
	private void checkBounds(){
		if (x<xmin) { x=xmin;}
		if (y<ymin) { y=ymin;}
		if (x>xmax) { x=xmax;}
		if (y>ymax) { y=ymax;}
	}
	
	/**
	 *@return size of tiles used
	*/
	public int getTileSize(){
		return tileSize;
	}
	
	/**
	* @return spawn positions for player/s
	*/
	public ArrayList<String> getSpawnPositions(){
		return spawnPositions;
	}
	
	/**
	* @return position of view in map on x-axis
	*/
	public double getx(){
		return x;
	}
	
	/**
	*@return position of view in map on y-axis
	*/
	public double gety(){
		return y;
	}
	
	/**
	* Retrieves a random spawn position and deletes said position from ArrayList
	* @return spawn position in the form of a string containing the row and column of the location in the tile map
	*/
	public String getRanSpawnPos(){
		int size = spawnPositions.size();
		Random r = new Random();
		int rsp = r.nextInt(size);
		String sp = spawnPositions.get(rsp);
		spawnPositions.remove(rsp);
		return sp;
	}
	/**
	* @return position of door in x-axis
	*/
	public int getDoorPosX(){
		return doorPosX;
	}
	
	/**
	*@return position of door in y-axis
	*/
	public int getDoorPosY(){
		return doorPosY;
	}
	
	/**
	* @return BufferedImage of background
	*/
	public BufferedImage getBackground(){
		return background;
	}
}

