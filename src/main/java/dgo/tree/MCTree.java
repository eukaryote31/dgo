package dgo.tree;

import java.util.Random;

import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.value.ValueFactory;

public interface MCTree {
	public TreeNode getRoot();
	
	public void truncateTree(TreeNode newroot);
	
	public void simulate(Random rnd, RolloutFactory rf, PolicyFactory pf, ValueFactory vf);
}
