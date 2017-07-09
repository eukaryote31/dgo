package dgo;

import java.util.Random;

import dgo.policy.PolicyFactory;
import dgo.policy.SimplePolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.rollout.SimpleRolloutFactory;
import dgo.tree.MCMemTree;
import dgo.tree.MCThreadedMemTree;
import dgo.tree.MCTree;
import dgo.util.FastRandom;
import dgo.value.SimpleExpValFactory;
import dgo.value.ValueFactory;

public class Main {

	public static void main(String[] args) {
		// for profiling purposes, mostly
		
		MCTree tree = new MCMemTree();
		Random rnd = new FastRandom();
		RolloutFactory rf = new SimpleRolloutFactory();
		PolicyFactory pf = new SimplePolicyFactory();
		ValueFactory vf = new SimpleExpValFactory();
		
		// simulate rounds
		for(int i = 0; i < 100; i++) {
			tree.simulate(rnd, rf, pf, vf);
		}
		
		System.out.println(tree.getBestMove(rf, pf));
	}

}
