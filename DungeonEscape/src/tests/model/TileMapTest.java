package tests.model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import game.model.objects.TileMap;

//Testing loading of maps; The expected output is size of the map 30x40

public class TileMapTest {
	private static TileMap tileMap;

	@BeforeClass
	public static void setUp(){
		tileMap = new TileMap("TheWildEscape/src/resources/L1.txt", 12);
		tileMap.loadTiles("TheWildEscape/src/resources/tileset2.png");
	}
	
	@Test
	public void MapSizeTest(){
		assertEquals(tileMap.getTiles().length, 30);
		assertEquals(tileMap.getTiles()[0].length, 40);
	}
	@Test
	public void EnemySpawnTest(){
		assertEquals(tileMap.getEnemySpawn().size(), 8);
	}
	@Test
	public void CoinListTest(){
		assertEquals(tileMap.getCoinList().size(), 68);
	}
	@Test
	public void SpawnTest(){
		assertEquals(tileMap.getSpawnPositions().size(), 1);
	}

}
