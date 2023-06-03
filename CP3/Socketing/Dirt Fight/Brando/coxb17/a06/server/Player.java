package coxb17.a06.server;

import java.io.*;
import java.net.Socket;

public class Player {

	private int wait;
	private int id;
	private String name;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	public Player(Socket sock, int id) throws IOException {
		this.sock = sock;
		this.id = id;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		wait = 0;
		System.out.println(id + " CON:\tConnected to client at " + sock.getInetAddress() + ". Mapped to ID " + id + ".");
	}

	boolean doTurn(String boardString, char[][] world, int otherWait) {
		if (wait > 0) {
			wait--;
			System.out.println(id + " SVR:\tWaited 1 turn. Waiting " + wait + " more turns.");
			return true;
		} else {
			send(otherWait + "\n" + boardString);
			String act = read();
			int[] pos = findChar(Game.LEFT_CHAR, world);
			assert pos != null;
			int x = pos[1];
			int y = pos[0];
			String[] data = act.split(" ");
			if (data[0].equals("move") && data.length == 2) {
				if (data[1].equals("left") && x > 0) {
					world[y][x] = Game.AIR;
					world[0][x - 1] = Game.LEFT_CHAR;
					wait = calculateDelta(world, x, x - 1);
					if (wait != 0)
						System.out.println(id + " SVR:\tMoved left with a delta of " + calculateDelta(world, x, x - 1) + ". Waiting for " + wait + " turns.");
					return true;
				} else if (data[1].equals("right") && x < 99) {
					world[y][x] = Game.AIR;
					world[0][x + 1] = Game.LEFT_CHAR;
					wait = calculateDelta(world, x, x + 1);
					if (wait != 0)
						System.out.println(id + " SVR:\tMoved right with a delta of " + calculateDelta(world, x, x + 1) + ". Waiting for " + wait + " turns.");
					return true;
				}
			} else if (data[0].equals("bomb") && data.length == 3) {
				try {
					int col = Integer.parseInt(data[1]);
					int r = Integer.parseInt(data[2]);
					if (r < 1)
						return false;
					if (col < 0 || col > 99)
						return false;
					int dist = Math.abs(col - x);
					wait += (int) (dist / 10.0 + Math.pow(r, 1.5)) - 1;
					System.out.println(id + " SVR:\tBombed " + dist + " cells away with a radius of " + r + ". Waiting " + wait + " turns.");
					bomb(col, r, world);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else if (data[0].equals("dirt") && data.length == 3) {
				if (!data[1].equals("self") && !data[2].equals("self"))
					return false;
				if (data[1].equals(data[2]))
					return false;
				int from;
				int to;
				switch (data[1]) {
					case "self":
						from = x;
						break;
					case "left":
						from = x - 1;
						break;
					case "right":
						from = x + 1;
						break;
					default:
						return false;
				}
				switch (data[2]) {
					case "self":
						to = x;
						break;
					case "left":
						to = x - 1;
						break;
					case "right":
						to = x + 1;
						break;
					default:
						return false;
				}
				world[y][x] = Game.AIR;
				world[0][x] = Game.LEFT_CHAR;
				return placeDirt(from, to, world);
			}
			return false;
		}
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	private boolean placeDirt(int from, int to, char[][] world) {
		int fromTop = -1;
		for (int i = 0; i < 30; i++) {
			if (fromTop == -1 && world[i][from] == Game.DIRT) {
				fromTop = i;
			}
		}
		if (fromTop == -1 || world[1][to] != Game.AIR)
			return false;
		world[fromTop][from] = Game.AIR;
		world[1][to] = Game.DIRT;
		return true;
	}

	private void bomb(int col, int r, char[][] world) {
		//really dumb, but this way was easy to write
		int y = 29;
		for (int i = 0; i < 30; i++) {
			if (world[i][col] == Game.DIRT) {
				y = i;
				break;
			}
		}
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 100; j++) {
				int dist = Math.abs(i - y) + Math.abs(j - col);
				if (dist <= r && world[i][j] == Game.DIRT)
					world[i][j] = Game.AIR;
			}
		}
	}

	private int calculateDelta(char[][] world, int a, int b) {
		int aStart = 30;
		int bStart = 30;
		for (int i = 0; i < world.length; i++) {
			if (aStart == 30 && world[i][a] == Game.DIRT) {
				aStart = i;
			}
			if (bStart == 30 && world[i][b] == Game.DIRT) {
				bStart = i;
			}
		}
		return Math.abs(aStart - bStart);
	}

	boolean send(String s) {
		try {
			if (!s.contains("\n")) {
				out.write(s + "\n");
				System.out.println(id + " SND:\t" + s);
			} else {
				String[] arr = s.split("\n");
				for (String anArr : arr) {
					if (!anArr.isEmpty()) {
						out.write(anArr + "\n");
						out.flush();
					}
				}
			}
			out.flush();

			return true;
		} catch (IOException e) {
			System.out.println(id + " ERR:\tFailed to write message: " + s);
			System.out.println("0 SVR:\tEnding game.");
			return false;
		}
	}

	String read() {
		try {
			String re = in.readLine();
			System.out.println(id + " RCV:\t" + re);
			return re;
		} catch (IOException e) {
			System.out.println(id + " ERR:\tFailed to read line");
			System.out.println("0 SVR:\tEnding game.");
			return null;
		}
	}

	public void kill() throws IOException {
		in.close();
		out.close();
		sock.close();
	}

	int getWait() {
		return wait;
	}

	private static int[] findChar(char c, char[][] world) {
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
}
