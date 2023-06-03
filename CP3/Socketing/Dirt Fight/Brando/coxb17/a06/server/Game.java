package coxb17.a06.server;

import java.awt.event.WindowEvent;

public class Game {

	static final char DIRT = '*';
	static final char LEFT_FLAG = 'S';
	static final char RIGHT_FLAG = 'O';
	static final char LEFT_CHAR = 's';
	static final char RIGHT_CHAR = 'o';
	static final char AIR = ' ';

	private Player p1;
	private Player p2;

	private char[][] world;

	public Game(Player p1, Player p2) {
		this.p1 = p1;
		this.p2 = p2;
		world = new char[30][100];
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (i >= 15)
					world[i][j] = DIRT;
				else
					world[i][j] = AIR;
			}
		}
		world[14][0] = LEFT_FLAG;
		world[14][1] = LEFT_CHAR;
		world[14][99] = RIGHT_FLAG;
		world[14][98] = RIGHT_CHAR;
	}

	public void play(boolean graphic) {
		p1.send("world 30,100");
		p1.setName(p1.read());
		p2.send("world 30,100");
		p2.setName(p2.read());
		Player turn = p1;
		Player notTurn = p2;
		Display disp = null;
		if (graphic)
			disp = new Display(world);
		int left = 1;
		while (true) {
			boolean valid = turn.doTurn(getWorldString(), world, p2.getWait());
			gravity();
			if (!valid) {
				System.out.println(left + " ERR:\t" + turn.getName() + " has submitted an invalid move");
				System.out.println("0 SVR:\t" + notTurn.getName() + " has won!");
				break;
			}
			if (isWinner()) {
				System.out.println("0 SVR:\t" + turn.getName() + " has won!");
				break;
			}
			if (left != 1 && graphic) {
				flipWorld();
				disp.update(world);
				flipWorld();
			} else if(graphic) {
				disp.update(world);
			}
			flipWorld();
			left = 3 - left;
			if (turn == p1) {
				turn = p2;
				notTurn = p1;
			} else {
				turn = p1;
				notTurn = p2;
			}
			if (graphic) {
				while (!disp.isReady())
					try {
						Thread.sleep(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
		if (graphic)
			disp.dispatchEvent(new WindowEvent(disp, WindowEvent.WINDOW_CLOSING));
	}

	private void gravity() {
		for (int x = 0; x < 100; x++) {
			for (int y = 28; y > 0; y--) {
				while (y != 29 && world[y][x] == AIR && world[y - 1][x] != AIR) {
					world[y][x] = world[y - 1][x];
					world[y - 1][x] = AIR;
					y++;
				}
			}
		}
	}

	private boolean isWinner() {
		int[] flag = findChar(RIGHT_FLAG);
		int[] c = findChar(LEFT_CHAR);
		assert flag != null;
		assert c != null;
		int dist = Math.abs(c[1] - flag[1]);
		return dist <= 1;
	}

	private void flipWorld() {
		char[][] oldWorld = world;
		world = new char[30][100];
		for (int i = 0; i < oldWorld.length; i++) {
			for (int j = 0; j < oldWorld[i].length; j++) {
				if (oldWorld[i][j] == LEFT_CHAR)
					world[i][99 - j] = RIGHT_CHAR;
				else if (oldWorld[i][j] == RIGHT_CHAR)
					world[i][99 - j] = LEFT_CHAR;
				else if (oldWorld[i][j] == LEFT_FLAG)
					world[i][99 - j] = RIGHT_FLAG;
				else if (oldWorld[i][j] == RIGHT_FLAG)
					world[i][99 - j] = LEFT_FLAG;
				else
					world[i][99 - j] = oldWorld[i][j];
			}
		}
	}

	private int[] findChar(char c) {
		int[] re = new int[2];
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[i][j] == c) {
					re[0] = i;
					re[1] = j;
					return re;
				}
			}
		}
		return null;
	}

	private String getWorldString() {
		String s = "";
		for (char[] aWorld : world) {
			for (char anAWorld : aWorld) {
				s += anAWorld;
			}
			s += "\n";
		}
		return s;
	}
}
