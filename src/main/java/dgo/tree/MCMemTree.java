package dgo.tree;

import java.util.Random;

import dgo.model.Goban;
import dgo.model.GobanValuesD;
import dgo.model.GobanValuesI;
import dgo.policy.PolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.value.ValueFactory;

public class MCMemTree implements MCTree {
	final int maxdepth = 20;
	final int savedepth = 2;
	MemTreeNode root;

	public MCMemTree() {
		root = new MemTreeNode();
	}

	public MCMemTree(MemTreeNode root) {
		this.root = root;
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
		for (int i = 0; i < savedepth; i++) {
			TreeNode child = traversed.pickChild(rnd, rf, pf);
			if (child == null) {
				break;
			} else {
				traversed = child;
			}
		}

		Goban gb = traversed.getGoban();

		int nstate = -traversed.getTurnState();
		for (int i = 0; i < maxdepth; i++) {
			GobanValuesD policyvals = pf.build().evaluate(gb, nstate);

			double max = -1;
			int xbest = -1;
			int ybest = -1;

			for (int x = 0; x < Goban.WIDTH; x++) {
				for (int y = 0; y < Goban.HEIGHT; y++) {
					// skip
					if (gb.placeStone(x, y, nstate) == null)
						continue;
					
					double score = rf.build().score(traversed.getWins(), traversed.getSimulations(),
							traversed.isRoot() ? traversed.getSimulations() : traversed.getParent().getSimulations(),
							policyvals.getState(x, y));
					
					if (score > max) {
						max = score;
						xbest = x;
						ybest = y;
					}
				}
			}
			
			Goban board = gb.placeStone(xbest, ybest, nstate);
			
			if (board == null) {
				// if invalid, replay
				i--;
				continue;
			}
			
			gb = board;
			
			nstate *= -1;
		}

		int score = vf.build().score(gb);
		int winner = Integer.signum(score) * nstate * traversed.getTurnState();

		// if both signums are the same, then the signs become positive and
		// indicate a win for that player.
		boolean didwin = winner * traversed.getTurnState() > 0;
		traversed.propagateSimulation(didwin);
	}

	@Override
	public TreeNode getBestMove(RolloutFactory rf, PolicyFactory pf) {
		return root.getBestChild(rf, pf);
	}

}
