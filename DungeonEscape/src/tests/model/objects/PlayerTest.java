package tests.model.objects;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.model.World;
import game.model.objects.GameObject;
import game.model.objects.Player;
import game.model.objects.TileMap;

public class PlayerTest {

	private Player player;
	private World world;

	@Before
    public void setUp() throws Exception {
		world = new World("TheWildEscape/src/resources/L1.txt","");
		world.addNewPlayer("", 50, 50);
		player = world.getPlayer();
    }
	
	@Test
	public void JumpTest(){
		player.jump();
		assertTrue(player.getYVel()<0);
	}
	@Test
	public void LeftTest(){
		player.moveLeft();
		assertTrue(player.getXVel()<0);
	}
	@Test
	public void RightTest(){
		player.moveRight();
		assertTrue(player.getXVel()>0);
	}
	@Test
	public void StopTest(){
		player.stop();
		assertTrue(player.getXVel()<=0);
	}
	@Test
	public void LadderUpTest(){
		player.setOnLadder(true);
		player.ladderUp();
		assertTrue(player.getYVel()<0);
	}
	@Test
	public void LadderDownTest(){
		player.setOnLadder(true);
		player.ladderDown();
		assertTrue(player.getYVel()>0);
	}
	
	@Test
	public void DeductLife(){
		int lives =player.getLives();
		player.deductLife();
		assertTrue(Math.max(lives-1, 0) ==player.getLives());
	}
	
	@Test
	public void AddLifeTest(){
		int lives =player.getLives();
		player.addLife();
		assertTrue(Math.min(lives+1, 3) ==player.getLives());
	}
	
	@Test 
	public void RefreshLives(){
		player.deductLife();
		player.deductLife();
		player.addLife();
		player.deductLife();
		player.refreshLives();
		assertEquals(player.getLives(), 3);
	}
	
	@Test
	public void AddPoints(){
		int points =player.getScore();
		player.addPoints(5);
		assertTrue(points+5== player.getScore());	
	}
	@Test
	public void ClientIdTest(){
		player.setClientID("Jolly");
		assertEquals("Jolly", player.getClientID());
	}
	@Test 
	public void KillPlayerTest(){
		player.deductLife();
		player.deductLife();
		player.deductLife();
		assertTrue(!player.getIsAlive());
	}
	
}
