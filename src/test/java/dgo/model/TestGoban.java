package dgo.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestGoban {

	@Test
	public void testDeadStoneRemoval() {
		Goban gb = Goban.emptyGoban();
		gb = gb.placeStone(0, 0, 1);
		gb = gb.placeStone(1, 1, 1);
		gb = gb.placeStone(2, 1, -1);
		gb = gb.placeStone(3, 0, -1);
		gb = gb.placeStone(2, 0, 1);
		gb = gb.placeStone(1, 0, -1);
		gb = gb.placeStone(3, 0, -1);
		gb = gb.placeStone(1, 0, -1);
		
		assertThat(gb.hashCode(), is(equalTo(-1954486995)));
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
