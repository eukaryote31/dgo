package dgo.tree;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import dgo.model.Goban;
import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;

public interface TreeNode {
	public int getX();

	public int getY();

	public int getTurnState();

	public TreeNode getParent();

	public List<TreeNode> getChildren();

	public Goban getGoban();

	public TreeNode pickChild(Random rnd, RolloutFactory rf, PolicyFactory pf);

	public int getWins();

	public int getSimulations();

	public void propagateSimulation(boolean won);

	public double getScore(RolloutFactory rf, PolicyFactory pf);

	public TreeNode getBestChild(RolloutFactory rf, PolicyFactory pf);

	public default boolean isRoot() {
		return Objects.isNull(getParent());
	}
}
