package game.model.objects;

import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Thread.sleep;

public class AIPlayer extends Player{

    private LinkedList<Node> actions;
    private Coordinates[] coins;

    public AIPlayer(float x, float y, int width, int height, TileMap tm) {
        super(x + 50, y, width, height, tm, true);
		this.setAI(true);
		
		ArrayList<Integer> coinList = tm.getCoinList();
        coins = new Coordinates[coinList.size() / 2];
        for (int i = 0; i < coins.length; i++){
			coins[i] = new Coordinates(coinList.get(i * 2) * 64, coinList.get(i * 2 + 1) * 64);
		}
    }

    public void update() {
        if (actions == null || actions.size() < 1) {
            Coordinates closestCoin = getClosestCoin();
            final int score = this.getScore();
            AStar aStar = new AStar(new Node(this, null, null, 0), (Node n) -> n.getScore() > score, closestCoin);
            actions = null;
            final Thread thread = new Thread(() -> actions = aStar.search());
            thread.start();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (actions == null)
                thread.interrupt();
        }

        if (actions != null) {
            final Node node = actions.pollFirst();
            if (node.getAction() != null)
                switch (node.getAction()) {
                    case UP:
                        ladderUp();
                        break;
                    case DOWN:
                        ladderDown();
                        break;
                    case LEFT:
                        moveLeft();
                        break;
                    case RIGHT:
                        moveRight();
                        break;
                    case JUMP:
                        jump();
                        break;
                }
        } else {
            moveLeft();
            if(isOnLadder()){
                ladderUp();
            }
            jump();
            
            // implement follow player?

        }
        move();
    }

    private Coordinates getClosestCoin() {
        int best = 0;
        final Coordinates currPos = new Coordinates(getXPos(), getYPos());
        float bestDistance = currPos.getDistanceTo(coins[0]);
        for (int i = 0; i < coins.length; i++) {
            float dist = currPos.getDistanceTo(coins[i]);
            if (dist < bestDistance) {
                bestDistance = dist;
                best = i;
            }
        }
        return coins[best];
    }

    public class Coordinates {
        final float x, y;

        Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float getDistanceTo(Coordinates c) {
            return (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y);
        }

        float getDistanceTo(float xPos, float yPos) {
            return (xPos - x) * (xPos - x) + (yPos - y) * (yPos - y);
        }
    }
}