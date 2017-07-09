package dgo.rollout;

import dgo.model.Goban;
import dgo.model.GobanValuesD;

public interface RolloutPolicy {
	public double score(int wins, int simulations, int parentsimulations, double policyweight);
}
