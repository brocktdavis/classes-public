import java.net.*;
import java.util.*;
import java.io.*;

public class Client
{
    public static final int PORT = 9090;
    
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) throws IOException 
    {
        System.out.print("Enter IP of server: ");
        String serverAddress = sc.nextLine();
        Socket s = new Socket(serverAddress, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        
        String line = in.readLine();
        while (line.length() > 0)
        {
            if (line.indexOf("MSG ") == 0)
            {
                int numOfLines = Integer.parseInt(line.substring(4));
                for (int i = 0; i < numOfLines; i++)
                {
                    String toPrint = in.readLine();
                    if (toPrint.equals("EXIT"))
                    {
                        s.close();
                        return;
                    }
                    System.out.println(toPrint);
                }
            }
            System.out.print(">>> ");
            out.println(sc.nextLine());
            
            line = in.readLine();
        }
    }
}
