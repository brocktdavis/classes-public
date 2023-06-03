import java.util.*;
import java.io.*;
import java.net.*;


public class Server
{
    public static final int PORT = 9090;
    
    public static PrintWriter log;
    
    public static void log(String message)
    {
        System.out.println(message);
        log.println(message);
        log.flush();
    }
    
    public static void main(String[] args) throws IOException {
        
        log = new PrintWriter(new File("server.log"));
        log("server started");
        ServerSocket listener = new ServerSocket(PORT);
        try
        {
            while (true)
            {
                Socket socket = listener.accept();
                User user = new User(socket);
                log("user connected");
                user.start();
            }
        }
        finally
        {
            listener.close();
        }
    }
    
    static class User extends Thread
    {
        
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        
        public User(Socket socket)
        {
            this.socket = socket;
        }
        
        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                //variables: magicNum, userNum, maxNum, wins, again, diff
                //all vars but userNum and diff only change between games
                int userNum = 0;
                int wins = 0;
                int maxNum = 100;
                char again = 'y';
                
                //inital magic num
                int magicNum = (int) (Math.random() * (maxNum) + 1);
                log("Magic num: " + magicNum);
                
                out.println("MSG 3");
                out.println("Welcome to a game of Hot 'n Cold!!!");
                out.println("Enter natural numbers to try and guess the magic one!");
                
                while (again == 'y')
                {
                    
                    out.println("Enter a number bewtween 1 and " + maxNum);
                    
                    userNum = Integer.parseInt(in.readLine());
                                        
                    double diff = magicNum - userNum;


                    if (diff == 0)//equal to
                    {
                        out.println("MSG 10");
                        out.println("You got it right! Congrats!\n");
                        out.println("Y   Y  OO  U  U   W       W IIIII NN    N !!");
                        out.println(" Y Y  O  O U  U   W       W   I   N N   N !!");
                        out.println("  Y   O  O U  U    W  W  W    I   N  N  N !!");
                        out.println("  Y   O  O U  U    W W W W    I   N   N N");
                        out.println("  Y    OO   UUU     W   W   IIIII N    NN !!\n");
                        
                        wins ++;
                        maxNum = (int) Math.pow(10,wins + 2);
                        magicNum = (int) (Math.random() * (maxNum) + 1);
                        log("Magic num: " + magicNum);


                        out.println("You have " + wins + " win(s).");
                        out.println("Play again (y or n)? ");
                        again = in.readLine().toLowerCase().charAt(0);
                        out.println("MSG 1");
                        continue;
                    }
                    
                    out.println("MSG 2");
                    
                    if (diff > 0 && (diff / maxNum) >= .25)//Very cold and less
                    {
                        out.println("You are very cold. Try going up.");
                    }
                    else if (diff > 0 && (diff / maxNum) >= .1)//Cold and less
                    {
                        out.println("You are cold. Try going up.");
                    }
                    else if (diff > 0 && (diff / maxNum) >= .05)//Warm and less
                    {
                        out.println("You are warm. Try going up.");
                    }
                    else if (diff > 0 && (diff / maxNum) >= .01)//Hot and Less
                    {
                        out.println("You are hot! Try going up.");
                    }
                    else if (diff > 0 && (diff / maxNum) > 0)//Scalding Hot and Less
                    {
                        out.println("You are scalding hot!! Try going up.");
                    }
                    else if (diff < 0 && (diff / maxNum) <= -.25)//Very Cold and More
                    {
                        out.println("You are very cold. Try going down.");
                    }
                    else if (diff < 0 && (diff / maxNum) <= -.1)//Cold and More
                    {
                        out.println("You are cold. Try going down.");
                    }
                    else if (diff < 0 && (diff / maxNum) <= -.05)//Warm and More
                    {
                        out.println("You are warm. Try going down.");
                    }
                    else if (diff < 0 && (diff / maxNum) <= -.01)//Hot and more
                    {
                        out.println("You are hot! Try going down.");
                    }
                    else if (diff < 0 && (diff / maxNum) < 0)
                    {
                        out.println("You are scalding hot!! Try going down.");
                    }
                    else//error
                    {
                        out.println("Something bad happened. Sorry! :(");
                    }
                }   
                
                out.println("EXIT");
            }
            catch (IOException e)
            {
                log(e.getMessage());
            }
            finally
            {
                try
                {
                    socket.close();
                    log("user disconnected");
                }
                catch (IOException e) { }
            }
            
        }
    }
}
