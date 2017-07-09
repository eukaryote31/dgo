package dgo.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

public class TestGoban {

	@Test
	public void testDeadStoneRemoval() {
		Goban gb = Goban.emptyGoban();
		gb = gb.placeStone(1, 0, 1);
		gb = gb.placeStone(0, 1, 1);
		gb = gb.placeStone(1, 1, -1);
		gb = gb.placeStone(0, 2, -1);
		
		gb = gb.placeStone(0, 0, -1);
		
		assertThat(gb.hashCode(), is(equalTo(-166314238)));
		assertThat(gb.placeStone(0, 1, 1).hashCode(), is(equalTo(818313186)));
	}

	@Test
	public void testIllegalStoneRemoval() {
		Goban gb = Goban.emptyGoban();
		gb = gb.placeStone(1, 0, 1);
		gb = gb.placeStone(0, 1, 1);
		gb = gb.placeStone(0, 0, -1);
		
		assertThat(gb, is(equalTo(null)));
	}

}
