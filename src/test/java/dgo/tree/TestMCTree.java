package dgo.tree;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import dgo.policy.PolicyFactory;
import dgo.policy.SimplePolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.rollout.SimpleRolloutFactory;
import dgo.util.FastRandom;
import dgo.value.SimpleExpValFactory;
import dgo.value.ValueFactory;

public class TestMCTree {
	@Test
	@Ignore
	public void testTree() {
		MCTree tree = new MCThreadedMemTree();
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
	
	@Test
	public void testBinarySearch() {
		double[] data = {0, 1, 2, 3, 4, 5, 6};
		double tofind = 3.5;
		
		int resultindex = MemTreeNode.greatestIndexNotExceeding(data, tofind);
		
		assertThat(resultindex, is(equalTo(3)));
	}
}
