package game.model.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Function;

public class AStar {
	private final PriorityQueue<Node> open;
	private final ArrayList<Node> closed;
	private final Node start;
	private final Function<Node, Boolean> checker;
	
	public AStar(Node start, Function<Node, Boolean> checker, AIPlayer.Coordinates c){
		this.checker = checker;
		this.open = new PriorityQueue<>(
			Comparator.comparingInt(
				a -> (int)(c.getDistanceTo(a.getState().getXPos(), a.getState().getYPos()))
			)
		);
		this.closed = new ArrayList<>();
		this.start = start;
	}

	public LinkedList<Node> search() {
		Node start_vertex = start;
		if (start_vertex == null) return null;

		open.add(start_vertex);
		Node current_vertex;

		LinkedList<Node> result = null;

		while (open.size() > 0) {
			current_vertex = open.poll();
			if (checker.apply(current_vertex)) {
				result = getPath(current_vertex);
				closed.add(current_vertex);
				break;
			}

			for(Node successor : current_vertex.getSuccessors()){
				if (!closed.contains(successor)){
                    open.add(successor);
				}
			}
			closed.add(current_vertex);
		}
		return result;
	}

	private LinkedList<Node> getPath(Node v) {
		LinkedList<Node> path = new LinkedList<>();

		for (; v != null; v = v.getParent())
            path.addFirst(v);

		final LinkedList<Node> ret = new LinkedList<>();

		for (v = path.pollFirst(); v != null; v = path.pollFirst())
			ret.add(v);

		return ret;
    }

	
}