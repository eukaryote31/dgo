package dgo.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import dgo.exception.InvalidMoveException;
import dgo.model.Goban;
import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.ToString;

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
		this(x, y, state, new AtomicInteger(0), new AtomicInteger(0), Collections.synchronizedList(new LinkedList<>()),
				goban, parent);
	}

	public MemTreeNode(int x, int y, MemTreeNode parent) throws InvalidMoveException {
		this(x, y, -parent.getTurnState(), parent, parent.getGoban().placeStone(x, y, -parent.getTurnState()));
		if (goban == null)
			throw new InvalidMoveException("Given move commits suicide");
	}

	public MemTreeNode() {
		// make root node
		this(-1, -1, -1, null, Goban.emptyGoban());
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
	@Synchronized
	public TreeNode pickChild(Random rnd, RolloutFactory rf, PolicyFactory pf) {

		// make child nodes
		if (children.size() == 0) {
			for (int i = 0; i < Goban.WIDTH * Goban.HEIGHT; i++) {
				try {
					children.add(new MemTreeNode(i / Goban.WIDTH, i % Goban.WIDTH, this));
				} catch (InvalidMoveException e) {
				} // swallow exception
			}
		}

		double sum = 0;
		double[] cumsum = new double[children.size()];

		if (children.size() == 0)
			return null;

		for (int i = 0; i < children.size(); i++) {
			TreeNode n = children.get(i);

			cumsum[i] = sum;
			sum += n.getScore(rf, pf);
		}
		return children.get(greatestIndexNotExceeding(cumsum, rnd.nextDouble() * sum));
	}

	public static int greatestIndexNotExceeding(double[] data, double tofind) {
		int lb = 0;
		int ub = data.length - 1;
		while (true) {
			int spread = ub - lb;

			if (spread == 1)
				return ub <= tofind ? ub : lb;

			// take middle
			int mid = (lb + ub) / 2;

			if (data[mid] > tofind) {
				ub = mid;
				continue;
			} else if (data[mid] < tofind) {
				lb = mid;
				continue;
			} else {
				return mid;
			}
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

		if (won)
			wins.incrementAndGet();

		if (this.isRoot()) {

			return;
		}


		// flip outcome, because parent node is move by opposite player
		parent.propagateSimulation(!won);
	}

	@Override
	public double getScore(RolloutFactory rf, PolicyFactory pf) {
		// TODO: compute once and store in parent
		return rf.build().score(this.getWins(), this.getSimulations(), this.getParent().getSimulations(),
				pf.build().evaluate(this.getGoban(), 0).getState(this.getX(), this.getY()));
	}

	@Override
	@Synchronized
	public TreeNode getBestChild(RolloutFactory rf, PolicyFactory pf) {
		TreeNode best = null;
		double bestscore = -1;

		for (TreeNode tn : getChildren()) {
			double score = tn.getScore(rf, pf);

			System.out.println(score);

			if (score > bestscore) {
				bestscore = score;
				best = tn;
			}
		}

		return best;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d) player %d, w/s: %d/%d", x, y, state, wins.get(), sims.get());
	}

}
