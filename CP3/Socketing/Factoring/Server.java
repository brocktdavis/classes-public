import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

public class Server
{
    public static final int PORT = 9090;
    public static final long START_TIME = System.nanoTime();
    
    public static int currentID = 100;
    public static Task[] nums = new Task[100];
    
    public static PrintWriter log;
    public static PrintWriter solved;
    
    public static void main(String[] args) throws IOException
    {
        
        log = new PrintWriter(new File("server.log"));
        solved = new PrintWriter(new File("times.csv"));
        readNumbers(new File("brock.txt"));
        
        ServerSocket listener = new ServerSocket(PORT);
        log("Server started");
        
        try
        {
            //Keep connecting and adding sockets
            while (true)
            {
                Socket socket = listener.accept();
                User user = new User(socket, currentID);
                currentID ++;
                user.start();
            }
        }
        finally
        {
            listener.close();
            log.close();
            solved.close();
        }
    }
    
    static class User extends Thread
    {
        
        private Socket socket;
        private String id;
        
        private BufferedReader in;
        private PrintWriter out;
        
        int numIndex;
        BigInteger crackNum;
        BigInteger startN;
        BigInteger stopN;
        
        public User(Socket socket, int id)
        {
            this.socket = socket;
            this.id = "User" + id;
        }
        
        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                log("Connection to " + id + " established.");
                
                while (true)
                {
                    
                    //Choose task to assign
                    for (int i = 0; i < nums.length; i++)
                    {
                        if (nums[i].solved) continue;
                        
                        Task toCrack = nums[i];
                        if (toCrack.checkedTo.compareTo(toCrack.num) == 1) continue; //if checkedTo exceeds number, keep going
                        
                        numIndex = i;
                        crackNum = toCrack.num;
                        startN = toCrack.checkedTo;
                        stopN = crackNum.min(startN.add(BigInteger.valueOf(10000000)));
                        toCrack.checkedTo = stopN;
                        break;
                        
                    }
                    
                    out.println("SOLVE " + crackNum + " " + startN + " " + stopN);
                    
                    if (numIndex > 9)
                        log("Gave " + id + " semiprime#" + numIndex + " to work on.");
                    else
                        log("Gave " + id + " semiprime#" + numIndex + " to solve.");
                    
                    String line = in.readLine();
                    if (line.indexOf("CRACKED") == 0)
                    {
                        nums[numIndex].solved = true;
                        logSolve(numIndex);
                        
                        String[] entries = line.split(" ");
                        BigInteger f1 = new BigInteger(entries[1]);
                        BigInteger f2 = new BigInteger(entries[2]);
                        log("Semiprime #" + numIndex + " cracked by " + id + ".\r\n\tFactors: " + f1 + " & " + f2 + ".");
                    }
                    
                    else if (line.indexOf("FAILED") == 0)
                    {
                        log("Progress was made on semiprime#" + numIndex + " by " + id + ".");
                    }
                    
                    else
                    {
                        log("Unrecognized protocol.\n" +
                        "-----Message-----\n" + 
                        line + 
                        "\n-----------------");
                    }
                    
                }
            }
            catch (IOException e) { }
            finally
            {
                try
                {
                    nums[numIndex].checkedTo = startN;
                    socket.close();
                    log(id + " disconnected.");
                }
                catch (IOException e) { System.out.println("Bad error"); }
            }
            
        }
    }
    
    //class to store all info about number needing to be cracked
    static class Task
    {
        
        public BigInteger num;
        public BigInteger sqrt;
        public boolean solved = false;
        
        public BigInteger checkedTo;
        
        public Task(String numText)
        {
            this.num = new BigInteger(numText);
            this.sqrt = bigIntSqrt(num);
            this.checkedTo = sqrt;
        }
        
    }
    
    public static void readNumbers(File input)
    {
        Scanner sc = null;
        try
        {
            sc = new Scanner(input);
        }
        catch (FileNotFoundException e) { }
        
        int i = 0;
        while (sc.hasNextLine())
        {
            String numText = sc.nextLine().split(" ")[1];
            nums[i] = new Task(numText);
            
            i++;
        }
    }
    
    //hr:min:sec
    public static String timestamp(boolean log)
    {
        if (log)
        {
            long now = System.nanoTime();
            long milli = (now - START_TIME) / 1000000;
            long sec = milli / 1000;
            long min = sec / 60;
            long hrs = min / 60;
            
            milli %= 1000;
            sec %= 60;
            min %= 60;
            
            return hrs + ":" + min + ":" + sec + "." + milli;
        }
        
        else
        {
            long now = System.nanoTime();
            double hrs = (now - START_TIME) / 1000000000.0 / 60 / 60;
            return String.format("%.4f", hrs);
            
        }
        
    }
    
    public static void log(String message)
    {
        String finalMessage = timestamp(true) + " " + message.trim();
        System.out.println(finalMessage);
        log.println(finalMessage);
        log.flush();
    }
    
    public static void logSolve(int index)
    {
        solved.println(index + "," + timestamp(false));
        solved.flush();
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
    
}
