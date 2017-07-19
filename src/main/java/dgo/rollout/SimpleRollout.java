package dgo.rollout;

import dgo.model.Goban;
import dgo.model.GobanValuesD;

public class SimpleRollout implements RolloutPolicy {

	@Override
	public double score(int wins, int simulations, int parentsimulations, double policyweight) {
		// Using (modified) UCT formula, and policy weight as exploration parameter
		double exploitation = (simulations != 0) ? (Math.pow(wins, 1.5) / simulations) : 0;

		double exploration = (policyweight + 1) / (simulations + 1);
//		if (!Double.isFinite((policyweight) * (exploration + exploitation + 1))) throw new RuntimeException(wins + "/" + simulations + ":" + parentsimulations + "::" + policyweight);
		return Math.sqrt(policyweight) * (exploration + exploitation);
	}

}
