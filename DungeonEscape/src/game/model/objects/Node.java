package game.model.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Function;

public class Node{
	private final Player state;
	private final Node parent;
	private final Action step;
	private final int score;
	
	public enum Action{
	UP, DOWN, LEFT, RIGHT, JUMP;
		static Action[] actions = {UP, DOWN, LEFT, RIGHT, JUMP};
	}
	
	public Node(Player state, Node parent, Action step, int score) {
		this.state = state;
		this.parent = parent;
		this.step = step;
		this.score = score;
	}
	
	public Player getState(){
		return state;
	}

	public Action getAction(){
		return step;
	}
	
	public int getScore(){
		return score;
	}
	
	public Node getParent(){
		return parent;
	}
	
	Node[] getSuccessors(){
		if (state.isOnLadder()){
			Node[] successors = new Node[4];
			for (int i = 0; i < successors.length; i++) {
				Player newState = state.clone();
				switch (Action.actions[i]) {
					case UP:
						newState.ladderUp();
						break;
					case DOWN:
						newState.ladderDown();
						break;
					case LEFT:
						newState.moveLeft();
						break;
					case RIGHT:
						newState.moveRight();
						break;
				}
				newState.move();
				successors[i] = new Node(newState, this, Action.actions[i], newState.getScore());
			}
			return successors;
		} else {
			Node[] successors = new Node[3];
			for (int i = 0; i < successors.length; i++) {
				Player newState = state.clone();
				switch (Action.actions[i+2]) {
					case LEFT:
						newState.moveLeft();
						break;
					case RIGHT:
						newState.moveRight();
						break;
					case JUMP:
						newState.jump();
						break;
				}
				newState.move();
				successors[i] = new Node(newState, this, Action.actions[i+2], newState.getScore());
			}
			return successors;
		}
	}
}