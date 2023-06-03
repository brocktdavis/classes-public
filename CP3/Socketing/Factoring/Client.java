import java.net.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class Client
{
    public static final int PORT = 9090;
    
    static BufferedReader in;
    static PrintWriter out;
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) throws IOException 
    {
        System.out.println("Client started. ");
        String serverAddress = ""; //put address here if already know
        if (serverAddress.length() == 0)
        {
            System.out.print("Enter IP of server: ");
            serverAddress = sc.nextLine();
        }
        
        Socket s = new Socket(serverAddress, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        
        System.out.println("Socket connected. ");
        
        String line = in.readLine();
        while (line != null)
        {
            //Get task specification
            if (line.indexOf("SOLVE") == 0)
            {
                String[] entries = line.split(" ");
                BigInteger toSolve = new BigInteger(entries[1]);
                BigInteger startN = new BigInteger(entries[2]);
                BigInteger stopN = new BigInteger(entries[3]);
                
                BigInteger f1 = factor(toSolve, startN, stopN);
                
                if (!f1.equals(BigInteger.ONE) && !f1.equals(BigInteger.ZERO))
                {
                    BigInteger f2 = toSolve.divide(f1);
                    out.println("CRACKED " + f1 + " " + f2);
                }
                else
                {
                    out.println("FAILED");
                }
            }
            
            else if (line.indexOf("DISCONNECT") == 0)
            {
                s.close();
                return;
            }
            
            else
            {
                System.out.println("Unrecognized protocol");
                System.out.println("----- Message -----");
                System.out.println(line);
                System.out.println("-------------------");
            }
            
            line = in.readLine();
        }
    }
    
    public static BigInteger bigIntSqrt(BigInteger x)
    {
        
        if (x .equals(BigInteger.ZERO) || x.equals(BigInteger.ONE))
            return x;
        
        
        BigInteger two = BigInteger.valueOf(2L);
        BigInteger y;
        // starting with y = x / 2 avoids magnitude issues with x squared
        for (y = x.divide(two);
             y.compareTo(x.divide(y)) > 0;
             y = ((x.divide(y)).add(y)).divide(two));
             
        return y;
    }
    
    public static BigInteger factor(BigInteger semiprime, BigInteger startN, BigInteger stopN)
    {
        
        //if N is even, return 2
        if (semiprime.and(BigInteger.ONE).equals(BigInteger.ZERO)) return BigInteger.valueOf(2);
        
        for (BigInteger n = startN; n.compareTo(stopN) == -1; n = n.add(BigInteger.ONE))
        {
            
            BigInteger check = n.multiply(n).subtract(semiprime);
            if (check.compareTo(BigInteger.ZERO) == -1) continue;
            BigInteger checkRoot = bigIntSqrt(check);
            
            if (check.equals(checkRoot.multiply(checkRoot)))
            {
                return n.subtract(checkRoot);
            }
            
        }
        
        return BigInteger.ZERO;
    }
    
}
