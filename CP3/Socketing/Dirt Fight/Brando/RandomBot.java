import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RandomBot {
	private static final int PORT = 1024;
	private static BufferedWriter out;

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the server's IP");
		String ip = sc.nextLine();
		Socket sock = new Socket(ip, PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		in.readLine();
		send("RandomBot");
		while (true) {
			try {
				for (int i = 0; i < 31; i++) {
					in.readLine();
				}
				double r = Math.random();
				if (r < 0.8)
					send("move right");
				else if (r < 0.85)
					send("move left");
				else if (r < 0.95) {
					int radius = (int) (Math.random() * 3 + 1);
					int col = (int) (Math.random() * 30);
					send("bomb " + col + " " + radius);
				} else {
					int self = (int) (Math.random() * 2);
					int dir = (int) (Math.random() * 2);
					String out = "dirt ";
					if (self == 0) {
						out += "self ";
						if (dir == 0)
							out += "left";
						else out += "right";
					} else {
						if (dir == 0)
							out += "left";
						else out += "right";
						out += " self";
					}
					send(out);
				}

			} catch (IOException e) {
				System.out.println("Connection dropped");
				break;
			}
		}
		in.close();
		out.close();
		sock.close();
		sc.close();
	}

	private static void send(String s) {
		try {
			out.write(s + "\n");
			out.flush();
			System.out.println("Sent: " + s);
		} catch (IOException e) {
			System.out.println("Connection dropped");
			System.exit(1);
		}
	}
}
