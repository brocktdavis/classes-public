import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MoveRightBot {

	private static final int PORT = 1024;
	private static BufferedWriter out;

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		//System.out.println("Enter the server's IP");
		String ip = "localhost";//sc.nextLine();
		Socket sock = new Socket(ip, PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		in.readLine();
		send("MoveRightBot");
		while (true) {
			try {
				for (int i = 0; i < 31; i++) {
					in.readLine();
				}
				out.write("move right\n");
				out.flush();
				System.out.println("Sent");
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
		} catch (IOException e) {
			System.out.println("Connection dropped");
			System.exit(1);
		}
	}
}
