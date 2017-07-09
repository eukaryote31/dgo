package dgo.tree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import dgo.exception.InvalidMoveException;
import dgo.model.Goban;
import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MemTreeNode implements TreeNode {
	int x;
	int y;
	int state;
	AtomicInteger wins;
	AtomicInteger sims;
	List<TreeNode> children;
	Goban goban;
	MemTreeNode parent;

	public MemTreeNode(int x, int y, int state, MemTreeNode parent, Goban goban) {
		this(x, y, state, new AtomicInteger(0), new AtomicInteger(0), new LinkedList<>(), goban, parent);
	}

	public MemTreeNode(int x, int y, MemTreeNode parent) throws InvalidMoveException {
		this(x, y, -parent.getTurnState(), parent, parent.getGoban().placeStone(x, y, -parent.getTurnState()));
		if (goban == null)
			throw new InvalidMoveException("Given move commits suicide");
	}
	
	public MemTreeNode() {
		// make root node
		this(-1, -1, 0, null, null);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public int getTurnState() {
		return state;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public List<TreeNode> getChildren() {
		return children;
	}

	@Override
	public Goban getGoban() {
		return goban;
	}

	@Override
	public TreeNode pickChild(Random rnd, RolloutFactory rf, PolicyFactory pf) {
		double sum = 0;
		double[] cumsum = new double[children.size()];

		if (children.size() == 0) {
			for (int i = 0; i < Goban.WIDTH * Goban.HEIGHT; i++) {
				try {
					children.add(new MemTreeNode(i / Goban.WIDTH, i % Goban.WIDTH, this));
				} catch (InvalidMoveException e) {}	 // swallow exception
			}
		}
		
		if (children.size() == 0)
			return null;
		
		for (int i = 0; i < children.size(); i++) {
			TreeNode n = children.get(i);

			cumsum[i] = sum;
			sum += n.getScore(rf, pf);
		}

		return children.get(greatestIndexNotExceeding(cumsum, rnd.nextDouble() * sum));
	}

	public static int greatestIndexNotExceeding(double[] data, double limit) {
		if (data.length < 1) {
			return -1;
		}
		return greatestIndexNotExceeding(data, limit, 0, data.length - 1);
	}

	private static int greatestIndexNotExceeding(double[] data, double limit, int lb, int ub) {
		final int mid = (lb + ub) / 2;

		// Need to go lower but can't
		if (mid == lb && data[mid] > limit) {
			return -1;
		}

		// Found a candidate, and can't go higher
		if (data[mid] <= limit && (mid == ub || data[mid + 1] > limit)) {
			return mid;
		}

		if (data[mid] <= limit) {
			// Consider upper half
			return greatestIndexNotExceeding(data, limit, mid + 1, ub);
		} else {
			// Consider lower half
			return greatestIndexNotExceeding(data, limit, lb, mid);
		}
	}

	@Override
	public int getWins() {
		return wins.get();
	}

	@Override
	public int getSimulations() {
		return sims.get();
	}

	@Override
	public void propagateSimulation(boolean won) {
		sims.incrementAndGet();
		// flip outcome, because parent node is move by opposite player
		parent.propagateSimulation(!won);

		if (won)
			wins.incrementAndGet();
	}

	@Override
	public double getScore(RolloutFactory rf, PolicyFactory pf) {
		return rf.build().score(this.getWins(), this.getSimulations(), this.getParent().getSimulations(),
				pf.build().evaluate(this.getGoban()).getState(this.getX(), this.getY()));
	}

}
