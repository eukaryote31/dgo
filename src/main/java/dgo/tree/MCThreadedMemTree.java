package dgo.tree;

import java.util.Random;

import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.value.ValueFactory;

public class MCThreadedMemTree implements MCTree {
	MemTreeNode root = new MemTreeNode();
	MCMemTree[] trees = new MCMemTree[4];
	
	public MCThreadedMemTree() {
		for (int i = 0; i < 4; i++) {
			trees[i] = new MCMemTree(root);
		}
	}
	
	@Override
	public TreeNode getRoot() {
		return root;
	}

	@Override
	public void truncateTree(TreeNode newroot) {
		root = (MemTreeNode) newroot;
	}

	@Override
	public void simulate(Random rnd, RolloutFactory rf, PolicyFactory pf, ValueFactory vf) {
		Thread[] ths = new Thread[4];
		int i = 0;
		for(MCMemTree t : trees) {
			Thread th = new Thread() {
				@Override
				public void run() {
					t.simulate(rnd, rf, pf, vf);
				}
			};
			ths[i] = th;
			th.start();
			i++;
		}
		
		for(Thread th : ths)
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	@Override
	public TreeNode getBestMove(RolloutFactory rf, PolicyFactory pf) {
		return trees[0].getBestMove(rf, pf);
	}

}
