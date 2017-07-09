package dgo.tree;

import java.util.Random;

import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.value.ValueFactory;

public class MCMemTree implements MCTree {
	final int depth = 30;
	MemTreeNode root;
	
	public MCMemTree() {
		root = new MemTreeNode();
	}
	
	@Override
	public TreeNode getRoot() {
		return root;
	}

	@Override
	public void truncateTree(TreeNode newroot) {
		// trigger garbage cleanup
		root.children = null;
		root.parent = null;

		// now replace with new root. new root still references old root as
		// parent, to get values from it, but children of parent are not used
		root = (MemTreeNode) newroot;
	}

	@Override
	public void simulate(Random rnd, RolloutFactory rf, PolicyFactory pf, ValueFactory vf) {
		TreeNode traversed = root;
		for (int i = 0; i < depth; i++) {
			TreeNode child = root.pickChild(rnd, rf, pf);
			if (child == null)
				break;
			else
				traversed = child;
		}
		
		System.out.println(traversed);
		
		int score = vf.build().score(traversed.getGoban());
		int winner = Integer.signum(score);
		
		// if both signums are the same, then the signs become positive and indicate a win for that player.
		boolean didwin = winner * traversed.getTurnState() > 0;
		traversed.propagateSimulation(didwin);
	}

}
