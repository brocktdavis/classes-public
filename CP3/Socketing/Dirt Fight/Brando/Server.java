import coxb17.a06.server.Game;
import coxb17.a06.server.Player;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;

public class Server {

	private static final int PORT = 1024;
	private static final boolean SHOW_GRAPHIC_DISPLAY = false;

	public static void main(String[] args) throws IOException {
		while (true) {
			try (ServerSocket server = new ServerSocket(PORT)) {
				System.out.println("0 SVR:\tWaiting for new clients...");
				System.out.println("0 SVR:\tMy IP is " + Inet4Address.getLocalHost());
				System.out.println("0 SVR:\tConnect on port " + PORT + ".");
				Player p1 = new Player(server.accept(), 1);
				Player p2 = new Player(server.accept(), 2);
				Game game = new Game(p1, p2);
				game.play(SHOW_GRAPHIC_DISPLAY);
				p1.kill();
				p2.kill();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
