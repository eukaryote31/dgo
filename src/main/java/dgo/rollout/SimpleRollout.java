package dgo.rollout;

import dgo.model.Goban;
import dgo.model.GobanValuesD;

public class SimpleRollout implements RolloutPolicy {

	@Override
	public double score(int wins, int simulations, int parentsimulations, double policyweight) {
		// Using (modified) UCT formula, and policy weight as exploration parameter
		double exploitation = (simulations != 0) ? (wins / simulations) : 0;

		double exploration = policyweight
				* Math.sqrt(1 + Math.log(parentsimulations) / (simulations == 0 ? 0.1 : simulations));

		return exploration + exploitation;
	}

}
