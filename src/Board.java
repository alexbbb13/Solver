import java.util.Iterator;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.introcs.StdOut;

public class Board {
	/*
	 * public Board(int[][] blocks) // construct a board from an N-by-N array of
	 * blocks // (where blocks[i][j] = block in row i, column j) +public int
	 * dimension() // board dimension N public int hamming() // number of blocks
	 * out of place +public int manhattan() // sum of Manhattan distances
	 * between blocks and goal +public boolean isGoal() // is this board the
	 * goal board? +public Board twin() // a board obtained by exchanging two
	 * adjacent blocks in the same row +public boolean equals(Object y) // does
	 * this board equal y? +public Iterable<Board> neighbors() // all
	 * neighboring boards +public String toString() // string representation of
	 * the board (in the output format specified below)
	 */

	private char [] boardTable;
	private int dimensionVal = 0;
	private int manhattanVal = 999999;
	private int hashVal = 0;
	private int zeroI = 0;
	private int zeroJ = 0;
	private int zeroIPrevious = 0;
	private int zeroJPrevious = 0;
	
	//private static Board goalBoard=null;
	
	
	public Board(int[][] blocks) {
		int N = blocks[0].length;
		if (blocks[1].length != N)
			StdOut.println("Array is not square");
		dimensionVal = N;
		boardTable = new char[N * N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (blocks[i][j] == 0) {
					zeroI = i;
					zeroJ = j;
					zeroIPrevious = i;
					zeroJPrevious = j;
				}
				set(i, j, blocks[i][j]);
			}
		}
		//goalBoard=getGoal();
		manhattanVal = manhattan(); // initialize
		hashVal = hash(); // init hash
	}

	// ************************DEBUG*****************************
	
	private Board(int size) {
		boardTable = new char[size * size];
		dimensionVal = size;
		//goalBoard=getGoal();
	}

	// ************************DEBUG*****************************

	public int dimension() {
		return dimensionVal;
	}

	public int manhattan() {
		if (manhattanVal != 999999)
			return manhattanVal; // cached value
		manhattanVal = 0;
		for (int i = 0; i < dimension(); i++)
			for (int j = 0; j < dimension(); j++) {
				int val = get(i, j);
				if (val != 0) { // we do not count zero
					manhattanVal += manhattanGet(i, j, val);
				}
			}
		return manhattanVal;
	}

	public int hamming() {
		int hamming = 0;
		for (int i = 0; i < (dimensionVal * dimensionVal) - 1; i++) {
			if (this.boardTable[i] != (char) (i + 1))
				hamming++;
		}
		return hamming;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(dimensionVal + "\n");
		for (int i = 0; i < dimensionVal; i++) {
			for (int j = 0; j < dimensionVal; j++) {

				s.append(String.format("%2d ", this.get(i, j)));
			}
			s.append("\n");
		}
		return s.toString();
	}

	public boolean equals(Object y) {
		if (y==null)  return false;
		if (this == y)  return true;
		if (y.getClass() != this.getClass()) return false;
		
		Board that = (Board)y;
		if (that.manhattan() != this.manhattan())
			return false;
		if (that.zeroI != this.zeroI)
			return false;
		if (that.zeroJ != this.zeroJ)
			return false;
		if (that.hash() != this.hash())
			return false;
		for (int i = 0; i < dimensionVal * dimensionVal; i++) {
			if (this.boardTable[i] != that.boardTable[i])
				return false;
		}
		return true;
	}

	public boolean isGoal() {
		
		if (this.manhattan()!=0)
			return false;
		if (this.zeroI!=(this.dimension()-1))
			return false;
		if (this.zeroJ!=(this.dimension()-1))
			return false;
		
		for (int i = 0; i < dimensionVal * dimensionVal-1; i++) {
			if (this.boardTable[i] != (char)(i+1))
				return false;
		}
	return true;
	}

	public Board twin() {
		Board twin = new Board(dimensionVal);
		for (int i = 0; i < dimensionVal; i++) {
			for (int j = 0; j < dimensionVal; j++) {
				twin.set(i, j, this.get(i, j));
			}
		}
		twin.dimensionVal = this.dimensionVal;
		twin.hashVal = this.hashVal;
		twin.manhattanVal = this.manhattanVal;

		for (int i = 0; i < dimensionVal; i++) {
			for (int j = 0; j < dimensionVal - 1; j++) {
				if (twin.get(i, j) != 0 && twin.get(i, j + 1) != 0) {
					int temp = twin.get(i, j);
					twin.set(i, j, twin.get(i, j + 1));
					twin.set(i, j + 1, temp);
					twin.reset();
					return twin;
				}
			}
		}
		return twin;

	}

	public Iterable<Board> neighbors(){
		Queue<Board> neighbours = new Queue <Board>();
		boolean up = true;
		boolean down = true;
		boolean left = true;
		boolean right = true;
		Board b = Board.this;
		if (b.zeroI == 0)
			left = false;
		if (b.zeroI == b.dimensionVal - 1)
			right = false;
		if (b.zeroJ == 0)
			up = false;
		if (b.zeroJ == b.dimensionVal - 1)
			down = false;
		//if (right
		//		&& !((zeroI + 1) == zeroIPrevious && zeroJ == zeroJPrevious)) {
		if (right){
			Board copy = new Board(dimension());
			copy = b.getCopy();
			copy.moveZeroFromTo(copy.zeroI, copy.zeroJ, copy.zeroI + 1,
					copy.zeroJ);
			copy.hashUpdate();
			copy.zeroI = b.zeroI + 1;
			copy.zeroJ = b.zeroJ;
			copy.zeroIPrevious = b.zeroI;
			copy.zeroJPrevious = b.zeroJ;
			neighbours.enqueue(copy);
		}
		
		if (down){
		// if (down
		//		&& !((zeroI) == zeroIPrevious && (zeroJ + 1) == zeroJPrevious)) {
			Board copy = new Board(dimension());
			copy = b.getCopy();
			copy.moveZeroFromTo(copy.zeroI, copy.zeroJ, copy.zeroI,
					copy.zeroJ + 1);
			copy.hashUpdate();
			copy.zeroI = b.zeroI;
			copy.zeroJ = b.zeroJ + 1;
			copy.zeroIPrevious = b.zeroI;
			copy.zeroJPrevious = b.zeroJ;
			neighbours.enqueue(copy);
		}

		if (left){
		//if (left
		//		&& !((zeroI - 1) == zeroIPrevious && zeroJ == zeroJPrevious)) {
			Board copy = new Board(dimension());
			copy = b.getCopy();
			copy.moveZeroFromTo(copy.zeroI, copy.zeroJ, copy.zeroI - 1,
					copy.zeroJ);
			copy.hashUpdate();
			copy.zeroI = b.zeroI - 1;
			copy.zeroJ = b.zeroJ;
			copy.zeroIPrevious = b.zeroI;
			copy.zeroJPrevious = b.zeroJ;
			neighbours.enqueue(copy);
		}

		if (up){
		//if (up
		//		&& !((zeroI) == zeroIPrevious && (zeroJ - 1) == zeroJPrevious)) {
			Board copy = new Board(dimension());
			copy = b.getCopy();
			copy.moveZeroFromTo(copy.zeroI, copy.zeroJ, copy.zeroI,
					copy.zeroJ - 1);
			copy.hashUpdate();
			copy.zeroI = b.zeroI;
			copy.zeroJ = b.zeroJ - 1;
			copy.zeroIPrevious = b.zeroI;
			copy.zeroJPrevious = b.zeroJ;
			neighbours.enqueue(copy);
		}

			return neighbours;

	
	}
	
	private Board getCopy() {
		Board copy = new Board(dimensionVal);
		for (int i = 0; i < dimensionVal * dimensionVal; i++) {
			copy.boardTable[i] = this.boardTable[i];
		}
		copy.hashVal = this.hashVal;
		copy.manhattanVal = this.manhattanVal;
		copy.zeroI = this.zeroI;
		copy.zeroJ = this.zeroJ;
		copy.zeroIPrevious = this.zeroIPrevious;
		copy.zeroJPrevious = this.zeroJPrevious;
	//	copy.goalBoard = this.goalBoard;
		return copy;

	}

	private void reset() {
		this.hashVal = 0;
		this.manhattanVal = 999999; //impossible in all practical cases
		manhattan();
		hash();

	}

	private int manhattanGet(int i, int j, int value) {
		int idist = i - rowGoal(value);
		if (idist < 0)
			idist = -idist;
		int jdist = j - colGoal(value);
		if (jdist < 0)
			jdist = -jdist;
		return (idist + jdist);
	}

	private int rowGoal(int value) {
		if (value == 0)
			return dimensionVal - 1;
		return (value - 1) / dimensionVal;
	}

	private int colGoal(int value) {
		if (value == 0)
			return dimensionVal - 1;
		return (value - 1) % dimensionVal;
	}

	private void set(int i, int j, int value) {
		boardTable[j + (i * dimensionVal)] = (char) value;
	}

	private int get(int i, int j) {
		return (int) boardTable[j + (i * dimensionVal)];
	}

	private int hash() {
		if (this.hashVal != 0)
			return hashVal;
		int hash = 0;
		for (int i = 0; i < dimensionVal * dimensionVal; i++) {
			hash += boardTable[i];
			hash = hash << 1;
		}
		return hash;
	}

	private void hashUpdate() {
		int hash = 0;
		for (int i = 0; i < dimensionVal * dimensionVal; i++) {
			hash += boardTable[i];
			hash = hash << 1;
		}
		this.hashVal = hash;
	}

	private void moveZeroFromTo(int i, int j, int newi, int newj) {
		int val = get(newi, newj);
		set(newi, newj, 0);
		set(i, j, val);
		int oldmanhattan = manhattanGet(newi, newj, val);
		int newmanhattan = manhattanGet(i, j, val);
		manhattanVal = manhattanVal - oldmanhattan + newmanhattan;

	}
	

	
}
