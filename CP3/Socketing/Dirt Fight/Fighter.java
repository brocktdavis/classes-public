import java.net.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class Fighter
{
    static int[] world;
    static int[] deltaY;
    static int player;
    static int enemy;
    static int turns;
    
    ///Change these for different servers
    public static final int PORT = 9090;
    public static String serverAddress = ""; 
    
    public static final int WORLD_LENGTH = 100;
    
    static BufferedReader in;
    static PrintWriter out;
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) throws IOException 
    {
        System.out.println("Client started. ");
        if (serverAddress.length() == 0)
        {
            System.out.print("Enter IP of server: ");
            serverAddress = sc.nextLine();
        }
        
        Socket s = new Socket(serverAddress, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        
        System.out.println("Socket connected. ");
        
        out.println("BrockSmashBot3000");
        
        boolean bombed = false;
        int rad = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        
        while (true)
        {
            String worldString = in.readLine();
            parseWorld(worldString);
            getDeltaY();
            
            //              far enough up               ahead of enemy     in right place
            if (!bombed && (player >= (3 + 2 * rad)) && (player < enemy) && (player % (2 + 2 * rad) == 1))
            {
                out.println("bomb " + (player - rad - 1) + " " + rad);
                bombed = true;
            }
            else
            {
                out.println("move right");
                bombed = false;
            }
            
            
        }
    }
    
    public static void parseWorld(String worldString)
    {
        world = new int[WORLD_LENGTH];
        String[] worldArray = worldString.split(";");
        turns = Integer.parseInt(worldArray[0]);
        
        
        for (int i = 1; i < worldArray.length ; i++)
        {
            String row = worldArray[i];
            
            for (int j = 0; j < row.length(); j++)
            {
                if (row.charAt(j) == '*') world[j] = world[j] > 0 ? world[j] : worldArray.length - i;
                else if (row.charAt(j) == 's') player = j;
                else if (row.charAt(j) == 'o') enemy = j;
            }
        }
        
        
    }
    
    public static void getDeltaY()
    {
        deltaY = new int[WORLD_LENGTH - 1];
        for (int i = 0; i < deltaY.length; i++)
        {
            deltaY[i] = Math.abs(world[i] - world[i + 1]);
        }
    }
    
    public static void printArray(int[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
    
    //positive if winning, negative if not
    public static int pathDiff(int worldLength)
    {
        int pCost = worldLength - player - 3 - turns;
        for (int i = player; i < worldLength - 3; i++)
        {
            pCost += deltaY[i];
        }
        
        int eCost = enemy - 2;
        for (int i = 2; i < enemy; i++)
        {
            eCost += deltaY[i];
        }
        return eCost - pCost;
    }
}
