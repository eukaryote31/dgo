package dgo.value;

import java.util.Arrays;

import dgo.model.Goban;
import dgo.model.GobanValuesI;

public class SimpleExpValue implements ValueComponent {

	public GobanValuesI evaluateCycle(GobanValuesI gb) {
		// make deep copy
		GobanValuesI ret = new GobanValuesI(Arrays.copyOf(gb.getState(), Goban.WIDTH * Goban.HEIGHT));

		for (int x = 0; x < Goban.WIDTH; x++) {
			for (int y = 0; y < Goban.HEIGHT; y++) {
				int up = gb.validateXY(x, y + 1) ? gb.getState(x, y + 1) : 0;
				int down = gb.validateXY(x, y - 1) ? gb.getState(x, y - 1) : 0;
				int left = gb.validateXY(x - 1, y) ? gb.getState(x - 1, y) : 0;
				int right = gb.validateXY(x + 1, y) ? gb.getState(x + 1, y) : 0;

				int sum = up + down + left + right;

				// the more of the same type of stone bordering influence, the
				// better.
				// since negative numbers represent white, we want a -n sum to
				// have a -n^2 influence
				int sq = Integer.signum(sum) * Math.min(50, sum * sum);
				
				ret.setState(x, y, sq);
			}
		}
		
		return ret;
	}

	@Override
	public GobanValuesI evaluate(Goban gb) {
		final int cycles = 2;
		
		GobanValuesI gvi = new GobanValuesI(gb.getState());
		for (int i = 0; i < cycles; i++) {
			gvi = evaluateCycle(gvi);
		}

		return gvi;
	}

}
