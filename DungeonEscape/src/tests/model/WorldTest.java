package tests.model;

import static org.junit.Assert.*;

import org.junit.Before;

import game.model.World;
import game.model.objects.Door;
import game.model.objects.GameObject;
import game.model.objects.Player;
import game.model.objects.TileMap;

public class WorldTest {
	public World world;
	@Before
    public void setUp() throws Exception {
        world = new World("TheWildEscape/src/resources/L1.txt");
    }

    @org.junit.Test
    public void getTileMap() {
    	int width = world.getTileMap().getTiles().length;
    	assertTrue(width == 30);
    }

    @org.junit.Test
    public void getPlayer() {
    	Player player = world.getPlayer();
    	assertTrue(player.getHeight() == 100);
    }

    @org.junit.Test
    public void getDoor() {
    	Door door = world.getDoor();
    	assertTrue(door.getHeight() == 110);
    }

    @org.junit.Test
    public void playerJump() {
    	Player player = world.getPlayer();
    	world.playerJump();
    	assertTrue(player.getYVel()<0);
    }

    @org.junit.Test
    public void playerStop() {
    	Player player = world.getPlayer();
    	world.playerJump();
    	assertTrue(player.getXVel() == 0);
    }

    @org.junit.Test
    public void playerLadderUp() {
    	Player player = world.getPlayer();
    	world.playerJump();
    	assertTrue(player.getYVel() < 0);
    }

    @org.junit.Test
    public void isPlayerAlive() {
    	assertTrue(world.isPlayerAlive());
    }
    
    @org.junit.Test
    public void isPlayerDead() {
    	world.getPlayer().deductLife();
    	world.getPlayer().deductLife();
    	world.getPlayer().deductLife();
    	assertTrue(!world.isPlayerAlive());
    }

    @org.junit.Test
    public void getTime() {
    	world.setTime("100");
    	assertEquals(world.getTime(), "100");
    }

    @org.junit.Test
    public void getEnemyList() {
    	assertTrue(world.getEnemyList().size() == 4);
    }
}