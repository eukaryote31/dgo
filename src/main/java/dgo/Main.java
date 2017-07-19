package dgo;

import java.util.Random;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dgo.model.Goban;
import dgo.policy.PolicyFactory;
import dgo.policy.SimplePolicyFactory;
import dgo.rollout.RolloutFactory;
import dgo.rollout.SimpleRolloutFactory;
import dgo.tree.MCMemTree;
import dgo.tree.MCThreadedMemTree;
import dgo.tree.MCTree;
import dgo.tree.TreeNode;
import dgo.util.FastRandom;
import dgo.value.SimpleExpValFactory;
import dgo.value.ValueFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) {
		// for profiling purposes, mostly
		

		// reduce debug spam
		Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		// change this if you want user to go first!
		// note: experimental
		final boolean userfirst = false;
		
		final boolean selfplay = true;
		
		if (selfplay) {
			selfplay();
			return;
		}
		
		MCTree tree = new MCMemTree();
		Random rnd = new FastRandom();
		RolloutFactory rf = new SimpleRolloutFactory();
		PolicyFactory pf = new SimplePolicyFactory();
		ValueFactory vf = new SimpleExpValFactory();
		
		Goban game = Goban.emptyGoban();
		
		while (true) {
			if (!userfirst || userfirst && !game.equals(Goban.emptyGoban())) {
				// simulate rounds
				for(int i = 0; i < 36000; i++) {
					if (i % 1000 == 0) {
						System.out.println(i);
					}
					tree.simulate(rnd, rf, pf, vf);
				}
				
				int sum = 0;
				for (TreeNode tn : tree.getRoot().getChildren()) {
					System.out.println(tn.getX() + ", " + tn.getY() + ": " + tn.getWins() + "/" + tn.getSimulations());
					sum += tn.getSimulations();
				}
				
				TreeNode bestMove = tree.getBestMove(rf, pf);
				
				System.out.println("ai: " + bestMove);
				
				log.info("sum {}", sum);
				log.info("rootsim {}", tree.getRoot().getSimulations());
				game = game.placeStone(bestMove.getX(), bestMove.getY(), userfirst ? -1 : 1);
				tree.truncateTree(bestMove);
			}
			
			log.info("\nGame: \n{}", game);
			boolean accepted = false;
			int ux = -1;
			int uy = -1;
			do {
				try {
					String s = System.console().readLine();
					String[] parts = s.split(" ");
					ux = Integer.parseInt(parts[0]);
					uy = Integer.parseInt(parts[1]);
					
					if (game.placeStone(ux, uy, userfirst ? 1 : -1) == null) {
						log.error("IVD LOC");
						continue;
					}
				} catch(NumberFormatException e) {
					log.error("IVD FORMAT");
					continue;
				}
				accepted = true;
			} while(!accepted);

			game = game.placeStone(ux, uy, userfirst ? 1 : -1);
			for (TreeNode stn : tree.getRoot().getChildren()) {
				if (stn.getX() == ux && stn.getY() == uy) {
					tree.truncateTree(stn);
					break;
				}
			}
			
		}
	}
	
	public static void selfplay() {
		final int sims = 36000;

		MCTree tree = new MCMemTree();
		
		Random rnd = new FastRandom();
		RolloutFactory rf = new SimpleRolloutFactory();
		PolicyFactory pf = new SimplePolicyFactory();
		ValueFactory vf = new SimpleExpValFactory();
		
		Goban game = Goban.emptyGoban();
		
		while (true) {
			// simulate rounds
			for(int i = 0; i < sims; i++) {
				if (i % 1000 == 0) {
					System.out.println(i);
				}
				tree.simulate(rnd, rf, pf, vf);
			}
			
			int sum = 0;
			for (TreeNode tn : tree.getRoot().getChildren()) {
				System.out.println(tn.getX() + ", " + tn.getY() + ": " + tn.getWins() + "/" + tn.getSimulations());
				sum += tn.getSimulations();
			}
			
			TreeNode bestMove = tree.getBestMove(rf, pf);
			
			System.out.println("ai: " + bestMove + ", pl " + bestMove.getTurnState());
			
			log.info("sum {}", sum);
			log.info("rootsim {}", tree.getRoot().getSimulations());
			game = game.placeStone(bestMove.getX(), bestMove.getY(), bestMove.getTurnState());
			tree.truncateTree(bestMove);
			log.info("\nGame: \n{}", game);
		}
	}

}
