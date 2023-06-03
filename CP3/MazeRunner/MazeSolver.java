import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.io.IOException;
import java.util.Scanner;

public class MazeSolver
{
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private boolean[][] maze;
    private int direction = RIGHT;
    private Point current;
    private Point finalPoint;
    private Stack<Point> traveled = new Stack<Point>();
    private boolean debug;
    private int count=0;
    
    public MazeSolver(String in_filename, String out_filename, boolean debug)
    {
	
	try
	{
	    BufferedImage origImg= ImageIO.read(new File(in_filename));
	    int w = origImg.getWidth();
	    int h = origImg.getHeight();
	    BufferedImage img= new BufferedImage(w, h , BufferedImage.TYPE_3BYTE_BGR);
	    img.getGraphics().drawImage(origImg, 0, 0, null);
	    
	    this.maze=new boolean[h][w];
	    
	    for(int i=0;i<h;i++)
		for(int j=0;j<w;j++)
		    maze[i][j] = (img.getRGB(j,i)&0xFF)>0 ? true : false;
	}
	catch(IOException e)	{ }
	
	this.current=new Point(1,1);
	this.finalPoint = new Point(maze[0].length-2,maze.length-2);
	
	traveled.add(current.copy());
	
	this.debug=debug;
	
	while (!current.equals(finalPoint))
	{
	    solveMaze();
	}
	
	if (debug)
	    this.saveImg(out_filename);
    }
    
    private void makeMove(int direction)
    {
	if (direction == UP)
	    current.y-=2;
	    
	else if (direction == RIGHT)
	    current.x+=2;
	    
	else if (direction == DOWN)
	    current.y+=2;
	    
	else if (direction == LEFT)
	    current.x-=2;
	
	int size=traveled.size();
	if (size > 1 && traveled.get(size-2).equals(current))
	    traveled.remove(size-1);
	else
	    traveled.push(current.copy());
	
    }
    
    private boolean[] getPossibles()
    {
	boolean[] out=new boolean[4];
	//UP
	try
	{
	    if (maze[current.y-1][current.x] == true)
		out[UP]=true;
	}
	catch (Exception e)	{ }
	
	//RIGHT
	try
	{
	    if (maze[current.y][current.x+1] == true)
		out[RIGHT]=true;
	}
	catch (Exception e)	{ }
	
	//DOWN
	try
	{
	    if (maze[current.y+1][current.x] == true)
		out[DOWN]=true;
	}
	catch (Exception e)	{ }
	
	//LEFT
	try
	{
	    if (maze[current.y][current.x-1] == true)
		out[LEFT]=true;
	}
	catch (Exception e)	{ }
	
	return out;
   }
    
    public void solveMaze()
    {
	/**
	if (this.debug)
	{
	    String filename=String.format("solve_anim\\%1$04d.png",count);
	    this.saveImg(filename);
	    count++;
	}*/
	
	boolean[] possibles = getPossibles();
	
	//Checks direction 90deg clockwise
	if (possibles[(direction+1)%4])
	    direction = (direction+1)%4;
	    
	//Checks direction straight ahead
	else if (possibles[direction]);
	
	//Checks direction 90deg counter clockwise
	else if (possibles[(direction+3)%4])
	    direction = (direction+3)%4;
	    
	//Turns around if no other options (dead end)
	else if (possibles[(direction+2)%4])
	    direction = (direction+2)%4;
	    
	makeMove(direction);
	
    }
    
    public String toString()
    {
	String out = "";
	for (int i = 0; i < maze.length; i++)
	{
	    for (int j = 0; j < maze[0].length; j++)
	    {
		if (maze[i][j] == true)
		    out+=" ";
		else
		    out+="X";
	    }
	    out+="\n";
	}
	
	return out.trim();
    }
    
    private void saveImg(String filename)
    {
	BufferedImage img =new BufferedImage(maze[0].length,maze.length,BufferedImage.TYPE_INT_RGB);
	for(int i=0;i<maze.length;i++)
	    for(int j=0;j<maze[0].length;j++)
	    {
		int val= (maze[i][j]) ? 255 : 0;
		img.setRGB(j,i,new Color(val,val,val).getRGB());
	    }
	    
	
	for (int i = 0; i < traveled.size(); i++)
	{
	    int tempX=traveled.elementAt(i).x;
	    int tempY=traveled.elementAt(i).y;
	    img.setRGB(tempX,tempY,255);
	}
	try	{ ImageIO.write(img,"png",new File(filename));	}
	catch(Exception e)	{ }
    
    }
    
    public static int getFib(int n)
    {
	if (n == 0)
	    return 1;
	if (n == 1)
	    return 1;
	
	return getFib(n-1)+getFib(n-2);
    }
    
    public int getStackLength()
    {
	return traveled.size();
    }
    
    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner sc = new Scanner(new File("files.txt"));
        
        while(sc.hasNextLine())
        {
            String file_in=sc.nextLine();
            long start=System.nanoTime();
            MazeSolver m = new MazeSolver(file_in,"this_shouldnt_exist.png",false);
            long end=System.nanoTime();
            System.out.println(file_in.substring(6,10)+" "+(end-start)+" "+m.getStackLength());
        }
            
    }
}
