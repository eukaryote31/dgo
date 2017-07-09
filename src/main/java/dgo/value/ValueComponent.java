package dgo.value;

import java.util.Arrays;

import dgo.model.Goban;
import dgo.model.GobanValuesI;

public interface ValueComponent {
	public GobanValuesI evaluate(Goban gb);

	default public int score(Goban gb) {
		// evaluate goban
		GobanValuesI gv = evaluate(gb);

		// sum up scores
		int total = 0;

		for (int i : gv.getState())
			total += Math.signum(i);

		return total;
	}
}
