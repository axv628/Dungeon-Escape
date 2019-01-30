package tests.model.objects;

import game.model.objects.GameObject;
import game.model.objects.TileMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameObjectTest {

    GameObject gameObject;

    @Before
    public void setUp() throws Exception {
        gameObject = new GameObject(50, 50, 100, 100, new TileMap("TheWildEscape/src/resources/L1.txt", 64));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getXPos() {
    	assertTrue(gameObject.getXPos() == 50);
    }

	@Test
    public void getYPos() {
		assertTrue(gameObject.getYPos() == 50);
    }

    @Test
    public void getWidth() {
    	assertTrue(gameObject.getWidth() == 100);
    }

    @Test
    public void getHeight() {
    	assertTrue(gameObject.getHeight() == 100);
    }

    @Test
    public void setXPos() {
        gameObject.setXPos(572);
        assertTrue(572 == gameObject.getXPos());
    }

    @Test
    public void setYPos() {
        gameObject.setYPos(123);
        assertTrue(123 == gameObject.getYPos());
    }

    @Test
    public void setWidth() {
        gameObject.setWidth(25);
        assertTrue(25 == gameObject.getWidth());
    }

    @Test
    public void setHeight() {
        gameObject.setHeight(3);
        assertTrue(3 == gameObject.getHeight());
    }

}