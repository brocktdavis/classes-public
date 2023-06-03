import java.util.Stack;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;

public class MazeMaker
{
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private boolean[][] maze;
    private Stack<Point> points= new Stack<Point>();
    private Point current;
    private boolean debug;
    
    public MazeMaker(int h, int w, boolean debug)
    {
	this.maze = new boolean[2*w+1][2*h+1];
	this.current = new Point(1+(h-h%2),1+(w-w%2));
	this.debug=debug;
	this.drawMaze();
    }
    
    private int[] outArray(boolean[] input, int count)
    {
	
	int[] out=new int[count];
	
	int index=0;
	
	if (input[UP]==true)
	{
	    out[index]=UP;
	    index++;
	}
	if (input[RIGHT]==true)
	{
	    out[index]=RIGHT;
	    index++;
	}
	if (input[DOWN]==true)
	{
	    out[index]=DOWN;
	    index++;
	}
	if (input[LEFT]==true)
	{
	    out[index]=LEFT;
	}
	return out;
   }
   
    private int[] getPossibles()
    {
	boolean[] out=new boolean[4];
	int count=0;
	
	//UP
	try
	{
	    if  (maze[current.y-2][current.x] == false)
	    {
		out[UP]=true;
		count++;
	    }
	}
	catch (Exception e)	{ }
	
	//RIGHT
	try
	{
	    if  (maze[current.y][current.x+2] == false)
	    {
		out[RIGHT]=true;
		count++;
	    }
	}
	catch (Exception e)	{ }
	
	//DOWN
	try
	{
	    if  (maze[current.y+2][current.x] == false)
	    {
		out[DOWN]=true;
		count++;
	    }
	}
	catch (Exception e)	{ }
	
	//LEFT
	try
	{
	    if  (maze[current.y][current.x-2] == false)
	    {
		out[LEFT]=true;
		count++;
	    }
	}
	catch (Exception e)	{ }
	
	return outArray(out, count);
   }
   
    private void makeMove(int choice)
    {
	if (choice == UP)
	{
	    current=new Point(current.x,current.y-2);
	    maze[current.y][current.x] = true;
	    maze[current.y+1][current.x] = true;
	}
	else if (choice == RIGHT)
	{
	    current=new Point(current.x+2,current.y);
	    maze[current.y][current.x] = true;
	    maze[current.y][current.x-1] = true;
	}
	else if (choice==DOWN)
	{
	    current=new Point(current.x,current.y+2);
	    maze[current.y][current.x] = true;
	    maze[current.y-1][current.x] = true;
	}
	else if (choice==LEFT)
	{
	    current=new Point(current.x-2,current.y);
	    maze[current.y][current.x] = true;
	    maze[current.y][current.x+1] = true;
	}
	
    }
   
    private void drawMaze()
    {
	int i=0;
	maze[current.y][current.x] = true;
	points.push(current.copy());
	
	while (!points.empty())
	{
	    int[] possibles = getPossibles();
	    if (possibles.length > 0)
	    {
		int choice = possibles[(int) (Math.random() * possibles.length)];
		
		//Makes new current Point
		makeMove(choice);
		
		points.push(current.copy());
	    }
	    else
	        current=points.pop();
		
	    if (debug)
	    {
		String filename=String.format("create_anim\\%1$04d.png", i);
		saveProg(filename);
		i++;
	    }
	}
	maze[0][1] = true;
	maze[(maze.length)-1][(maze[0].length)-2] = true;
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
	return out;
    }
    
    private void saveProg(String filename)
    {
	BufferedImage img =new BufferedImage(maze.length,maze[0].length,BufferedImage.TYPE_INT_RGB);
	for(int i=0;i<maze.length;i++)
	    for(int j=0;j<maze[0].length;j++)
	    {
		int val = (maze[i][j]) ? 255 : 0;
		img.setRGB(i,j,(new Color(val,val,val)).getRGB());
	    }
	
	try
	{
	    Point last=points.elementAt(0);
	    for (int i = 0; i < points.size(); i++)
	    {
		Point temp=points.elementAt(i);
		img.setRGB(temp.y,temp.x,255);
		//img.setRGB((temp.y+last.y)/2,(temp.x+last.x)/2,255);
		last=temp;
	    }
	}
	catch (Exception e)	{ }
	
	try	{ ImageIO.write(img,"png",new File(filename));	}
	catch(Exception e)	{ }
	
    }
    
    public void saveMaze(String filename)
    {
	BufferedImage img =new BufferedImage(maze.length,maze[0].length,BufferedImage.TYPE_BYTE_BINARY);
	for(int i=0;i<maze.length;i++)
	    for(int j=0;j<maze[0].length;j++)
	    {
		img.setRGB(i,j,(maze[i][j] ? 0xFFFFFF : 0));
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
    
    public static void main(String[] args)
    {
	//MazeMaker debugMaze = new MazeMaker(34,55,true);
	//debugMaze.saveMaze("create_anim/final.png");
	
	
	long last=System.nanoTime();
	for (int i = 0; i < 21; i++)
	{
	    int w = getFib(i+1);
	    int h = getFib(i);
	    MazeMaker maze = new MazeMaker(h,w,false);
	    String filename = "mazes/maze" + i + ".png";
	    maze.saveMaze(filename);
	    long now=System.nanoTime();
	    System.out.println(i + ", " + w + ", " + h + ", " + (now-last)/Math.pow(10,9));
	    last=now;
	}
	
    }
}
