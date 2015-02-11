
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

public class Solver {
	private final MinPQ pq = new MinPQ();
	/*
	 * public Solver(Board initial) // find a solution to the initial board
	 * (using the A* algorithm) +public boolean isSolvable() // is the initial
	 * board solvable? +public int moves() // min number of moves to solve
	 * initial board; -1 if no solution +public Iterable<Board> solution() //
	 * sequence of boards in a shortest solution; null if no solution +public
	 * static void main(String[] args) // solve a slider puzzle (given below)
	 */

	private searchNode solutionG = null;
	private boolean solvableG = false;
	private int movesG = -1;

	private class searchNode implements Comparable<searchNode> {
		private Board board;
		private searchNode previous;
		private int nOfMoves;
		private boolean twin=false;

		public int compareTo(searchNode that) {
			int thisWeight = this.board.manhattan() + this.nOfMoves;
			int thatWeight = that.board.manhattan() + that.nOfMoves;
			if (thisWeight > thatWeight)
				return 1;
			if (thisWeight < thatWeight)
				return -1;
			if(this.board.manhattan()>that.board.manhattan()) return 1;
			if(this.board.manhattan()<that.board.manhattan()) return -1;
			return 0;
		}

	}

	public Solver(Board initial) {
		searchNode s = new searchNode();
		s.board = initial;
		s.previous = null;
		s.nOfMoves = 0;

		searchNode stwin = new searchNode();
		stwin.board = initial.twin();
		stwin.previous = null;
		stwin.nOfMoves = 0;
		stwin.twin=true;  

		pq.insert(s);
		pq.insert(stwin);
		while (!solve()) {
		}
		if (this.isSolvable())
			solutionG = (searchNode) pq.delMin();
		else
			solutionG = null;
	}

	public Iterable<Board> solution() {

		if (solutionG == null) {
			return null;
		}

		Stack<Board> stack = new Stack<Board>();
		searchNode node = solutionG;
		stack.push(node.board);
		while (node.previous != null) {
			stack.push(node.previous.board);
			node = node.previous;
		}

		return stack;
	}

	public int moves() {
		return this.movesG;
	}

	public boolean isSolvable() {
		return this.solvableG;
	}

	// *********************************PRIVATE**********

	private boolean solve() {
		searchNode minNode = (searchNode) pq.delMin();
		
		
		if (minNode.board.isGoal()) {
			pq.insert(minNode);
			solvableG = true;
			movesG = minNode.nOfMoves;
			if(minNode.twin){
				solvableG = false;
				movesG = -1;
			}
			return true;
		}
		Iterable<Board> neighbors = minNode.board.neighbors();

		for (Board next : neighbors) {
			boolean doEnq=true;
			if(minNode.previous==null){ 
				doEnq=true;
				}
			else{
				if(minNode.previous.board.equals(next)) doEnq=false;
			    }
			if(doEnq){
				searchNode sn = new searchNode();
				sn.previous = minNode;
				sn.board = next;
				sn.nOfMoves = minNode.nOfMoves + 1;
				sn.twin = minNode.twin;
				pq.insert(sn);
			}

		}
		return false;

	}

	public static void main(String[] args) {
		// create initial board from file

		//In in = new In("d:\\4\\8puzzle\\puzzle48.txt"); //args[0]);
		In in = new In(args[0]);
		int N = in.readInt();

		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);
		
		// solve the puzzle
		Solver solver = new Solver(initial);
		// print solution to standard output

		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}

	}

}
