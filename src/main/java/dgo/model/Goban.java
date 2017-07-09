package dgo.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import dgo.util.ZobristHash;

/**
 * Goban is just any (immutable) board position. Uses a Zobrist hash for
 * efficiency.
 * 
 * @author eukaryote
 *
 */
public class Goban implements Serializable {
	private static final long serialVersionUID = -6415776252103592499L;

	public static final int HEIGHT = 19;
	public static final int WIDTH = 19;

	private static final Goban EMPTY = new Goban(new int[WIDTH * HEIGHT], 0);

	private final int[] state;
	private final int zobristhash;

	public Goban(int[] state) {
		// input sanitation
		for (int i = 0; i < state.length; i++) {
			validateState(state[i]);
		}

		this.state = state;

		// compute zobrist hash
		zobristhash = ZobristHash.getZobristHash(this);
	}

	private Goban(int[] state, int zobrist) {
		this.state = state;
		zobristhash = zobrist;
	}

	public static Goban emptyGoban() {
		return EMPTY;
	}

	@Override
	public boolean equals(Object another) {
		// use zobrist hash because comparing two integers is a lot faster than
		// comparing two arrays [citation needed]
		if (another == null)
			return false;
		if (this == another)
			return true;
		if (another instanceof Goban) {
			if (another.hashCode() == this.hashCode())
				return true;
		}

		return false;
	}

	public int[] getState() {
		return state;
	}

	public int getState(int x, int y) {
		validateXY(x, y);
		return state[x + y * WIDTH];
	}

	@Override
	public int hashCode() {
		return zobristhash;
	}

	public Goban setState(int x, int y, int nstate) {
		// sanitize your inputs!"); DROP TABLE users; --
		validateState(nstate);

		if (!validateXY(x, y))
			throw new IllegalArgumentException("Position (" + x + ", " + y + ") out of bounds!");

		// if nothing changes just return this
		if (nstate == this.getState(x, y))
			return this;

		int newhash;
		if (nstate == 0) {
			// undo state at (x, y) by xoring it by itself
			newhash = zobristhash ^ ZobristHash.getIndice(x, y, this.getState(x, y));
		} else {
			newhash = zobristhash ^ ZobristHash.getIndice(x, y, nstate);
		}

		// make copy of states array with new state set
		int[] newstate = Arrays.copyOf(getState(), WIDTH * HEIGHT);
		newstate[x + y * WIDTH] = nstate;

		return new Goban(newstate, newhash);
	}

	public Goban placeStone(int x, int y, int nstate) {
		// placing stones on stones is illegal
		if (this.getState(x, y) != 0)
			return null;

		if (nstate == 0)
			throw new IllegalArgumentException();

		List<Point> neighbors = getNeighbors(x, y);

		// check if placed stone has liberties itself
		boolean hasLiberties = false;
		for (Point p : neighbors) {
			if (getState(p.x, p.y) == 0) {
				hasLiberties = true;
				break;
			}
		}

		List<Point> toremove = new LinkedList<>();

		int numoppositeadj = 0;
		for (Point p : neighbors) {
			if (getState(p.x, p.y) == 0)
				continue;

			if (getState(p.x, p.y) == nstate) {
				// neighbor same state as placed stone

				if (hasLiberties) {
					// has liberties and is connected, therefore alive
					continue;
				} else {
					Set<Point> s = checkGroup(p, null);

					toremove.addAll(s);
				}
			} else {
				// neighbor different state as placed stone

				numoppositeadj++;

				Set<Point> s = checkGroup(p, new Point(x, y));

				toremove.addAll(s);
			}
		}

		// its surrounded on all sides by opposite color
		if (numoppositeadj == neighbors.size() && toremove.isEmpty())
			return null;

		if (toremove.isEmpty())
			return this.setState(x, y, nstate);

		int[] retstate = Arrays.copyOf(this.getState(), WIDTH * HEIGHT);
		for (Point p : toremove) {
			// clear dead stones from board
			retstate[p.x + WIDTH * p.y] = 0;
		}

		retstate[x + WIDTH * y] = nstate;

		return new Goban(retstate);
	}

	/**
	 * Internal method for checking if a group would be alive given a placed
	 * stone.
	 */
	private Set<Point> checkGroup(Point pt, Point newpt) {
		Set<Point> connected = new HashSet<>();
		Queue<Point> queue = new LinkedList<>();
		queue.add(pt);
		connected.add(pt);

		int nstate = getState(pt.x, pt.y);

		// simple, naïve floodfill

		while (!queue.isEmpty()) {
			Point p = queue.poll();

			for (Point conn : getNeighbors(p.x, p.y)) {
				int state = conn.equals(newpt) ? -nstate : getState(conn.x, conn.y);

				if (state == 0) {
					// if theres a liberty, the entire group is alive
					return Collections.emptySet();
				} else if (state != nstate) {
					// ignore if its opposite color
					continue;
				} else {
					if (connected.contains(conn)) {
						// ignore if we've already visited it
						continue;
					} else {
						// new one we havent visited yet
						queue.add(conn);
						connected.add(conn);
					}
				}
			}
		}

		return connected;
	}

	@Deprecated
	public Goban removeDeadStones(int px, int py) {
		int[] state = getState();
		Set<Point> alive = new HashSet<>(361);

		if (px != -1 && py != -1)
			alive.add(new Point(px, py));

		// quick and dirty algorithm
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {

				if (state[x + y * WIDTH] == 0) {
					Queue<Point> q = new LinkedList<>();
					q.add(new Point(x, y));

					while (!q.isEmpty()) {
						Point p = q.poll();

						if (p.x == px && p.y == py)
							continue;

						List<Point> neighbors = getNeighbors(x, y);
						for (Point n : neighbors) {
							// boring cases
							if (alive.contains(n))
								continue;
							if (state[p.x + p.y * WIDTH] == 0 || state[n.x + n.y * WIDTH] == state[p.x + p.y * WIDTH]) {
								q.add(n);
								alive.add(n);
							}
						}
					}

				}
			}
		}

		int[] newstate = new int[WIDTH * HEIGHT];
		for (Point p : alive) {
			newstate[p.x + p.y * HEIGHT] = state[p.x + p.y * HEIGHT];
		}

		return new Goban(newstate);
	}

	public List<Point> getNeighbors(int x, int y) {
		List<Point> ret = new ArrayList<>(4);

		if (validateXY(x - 1, y))
			ret.add(new Point(x - 1, y));
		if (validateXY(x, y - 1))
			ret.add(new Point(x, y - 1));
		if (validateXY(x + 1, y))
			ret.add(new Point(x + 1, y));
		if (validateXY(x, y + 1))
			ret.add(new Point(x, y + 1));

		return ret;
	}

	private static void validateState(int state) {
		if (state != 1 && state != 0 && state != -1)
			throw new IllegalArgumentException("State can only be -1, 0, or 1, but got " + state);
	}

	public static boolean validateXY(int x, int y) {
		return !(x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT);
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int y = 0; y < Goban.HEIGHT; y++) {
			for (int x = 0; x < Goban.WIDTH; x++) {
				if (getState(x, y) == 1)
					ret.append('#');
				else if (getState(x, y) == -1)
					ret.append('O');
				else
					ret.append(' ');
			}

			ret.append('\n');
		}

		return ret.toString();
	}
}
