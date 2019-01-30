package game.model.objects;

public class GameObject{
	private float xPos;
	private float yPos;
	
	//tilemap
	private TileMap tileMap;
	private int tileSize;
	private int xmap; //map positions
	private int ymap;

	public int width, height;
	
	public GameObject(float x, float y, int width, int height, TileMap tm){
		setXPos(x);
		setYPos(y);
		setWidth(width);
		setHeight(height);
		tileMap = tm;
		tileSize = tileMap.getTileSize();
	} 
	
	public void setMapPosition(){
		xmap = (int)tileMap.getx();
		ymap = (int)tileMap.gety();
	}
	
	public float getXPos(){
		return xPos;
	}
	
	public float getYPos(){
		return yPos;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setXPos(float newXPos){
		xPos = newXPos;
	}
	
	public void setYPos(float newYPos){
		yPos = newYPos;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public void setHeight(int height){
		this.height = height;
	}
}