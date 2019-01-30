package game.model.objects;

public class MovableObject extends GameObject{
	
	//Velocity
	private float xVel, yVel;
	//Acceleration
	private float xAcc, yAcc;
	
	public MovableObject(float x, float y, int width, int height, TileMap tm){
		super(x, y, width, height, tm);
	}
	
	public float getXVel(){
		return xVel;
	}
	
	public float getYVel(){
		return yVel;
	}
	
	public void setXVel(float newXVelocity){
		xVel = newXVelocity;
	}
	
	public void setYVel(float newYVelocity){
		yVel = newYVelocity;
	}
	
	public void setXAcc(float newXAcceleration){
		xAcc = newXAcceleration;
	}
	
	public void setYAcc(float newYAcceleration){
		yAcc = newYAcceleration;
	}
	
	public float getXAcc(){
		return xAcc;
	}
	
	public float getYAcc(){
		return yAcc;
	}
}
