import java.util.*;
import java.io.*;
import java.awt.Color;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class SolveSmall
{
    public static Color OPENC=new Color(255,255,255);
    public static Color STARTC=new Color(0,255,0);
    public static Color ENDC=new Color(255,0,0);
    public static Color WALLC=new Color(0,0,0);
    public static Color SLOWC=new Color(0,0,255);
    
    public static int PATH_COLOR=new Color(255,127,0).getRGB();
    
    public static byte OPEN=0;
    public static byte START=1;
    public static byte END=2;
    public static byte WALL=3;
    public static byte SLOW=4;
    
    ArrayList<Point> fringe = new ArrayList<Point>();
    ArrayList<Point> goal = new ArrayList<Point>();
    
    HashSet<Point> toAdd;
    
    byte[][] terrain;
    int[][] costMap;
    
    private int height;
    private int width;
    
    public SolveSmall(String file)
    {
        init(file);
        
        fillCostMap();
        
        Point end=getClosestPoint();
        traceAndOutput(file, end);
        
    }

    private void traceAndOutput(String file, Point end)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(file));
            this.height=img.getHeight();
            this.width=img.getWidth();
        }
        catch (IOException e) { }
        
        Point current=end;
        while (costMap[current.y][current.x]!=0)
        {
            
            int x=current.x;
            int y=current.y;
            int cost=costMap[y][x];
            
            img.setRGB(x,y,PATH_COLOR);
            
            Point next=null;
            
            //RIGHT
            try
            {
                int newCost=costMap[y][x+1];
                if (newCost<cost)
                {
                    cost=newCost;
                    next=new Point(x+1,y);
                }
            }
            catch (IndexOutOfBoundsException e) { }
            
            //DOWN
            try
            {
                int newCost=costMap[y+1][x];
                if (newCost<cost)
                {
                    cost=newCost;
                    next=new Point(x,y+1);
                }
            }
            catch (IndexOutOfBoundsException e) { }
            
            
            //LEFT
            try
            {
                int newCost=costMap[y][x-1];
                if (newCost<cost)
                {
                    cost=newCost;
                    next=new Point(x-1,y);
                }
            }
            catch (IndexOutOfBoundsException e) { }
            
            
            //UP
            try
            {
                int newCost=costMap[y-1][x];
                if (newCost<cost)
                {
                    cost=newCost;
                    next=new Point(x,y-1);
                }
            }
            catch (IndexOutOfBoundsException e) { }
            
            current=next;
            
        }
        
        try	{ ImageIO.write(img,"png",new File("warmup_paths.png"));	}
		catch(Exception e)	{ }
        
    }

    //Requires filled costMap
    private Point getClosestPoint()
    {
        int minCost=Integer.MAX_VALUE;
        Point closest=null;
        for (int i=0;i<goal.size();i++)
        {
            Point p=goal.get(i);
            
            if (costMap[p.y][p.x]<minCost)
            {
                minCost=costMap[p.y][p.x];
                closest=p;
            }
        }
        return closest;
        
    }

    //Done
    private void fillCostMap()
    {
        //Once fringe is empty, whole map has been solved
        int cost=0;
        while(!fringe.isEmpty())
        {
            
            this.toAdd = new HashSet<Point>();
            HashSet<Point> toRemove = new HashSet<Point>();
            for (int i=0;i<fringe.size();i++)
            {
                Point p=fringe.get(i);
                if (costMap[p.y][p.x]==cost)
                {
                    update(p);
                    toRemove.add(p);
                }
            }
            
            fringe.addAll(toAdd);
            fringe.removeAll(toRemove);
            
            cost++;
        }
    }
    
    //Done
    private int getCost(int thisCost, Point adj)
    {
        byte val=terrain[adj.y][adj.x];
        if (val==OPEN) return thisCost+1;
        else if (val==START) return thisCost+1;
        else if (val==END) return thisCost+1001;
        else if (val==SLOW) return thisCost+2;
        else return Integer.MAX_VALUE;
        
    }
    
    //Done
    private void update(Point p)
    {
        int x=p.x;
        int y=p.y;
        int thisCost=costMap[y][x];
        byte thisHeight=terrain[y][x];
        
        //RIGHT (y,x+1)
        try
        {
            Point right=new Point(x+1,y);
            int newCost=getCost(thisCost, right);
            if (newCost<costMap[y][x+1])
            {
                costMap[y][x+1]=newCost;
                if (terrain[y][x+1]!=WALL)
                    toAdd.add(right);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //DOWN (y+1,x)
        try
        {
            Point down=new Point(x,y+1);
            int newCost=getCost(thisCost, down);
            if (newCost<costMap[y+1][x])
            {
                costMap[y+1][x]=newCost;
                if (terrain[y+1][x]!=WALL)
                    toAdd.add(down);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //LEFT (y,x-1)
        try
        {
            Point left=new Point(x-1,y);
            int newCost=getCost(thisCost, left);
            if (newCost<costMap[y][x-1])
            {
                costMap[y][x-1]=newCost;
                if (terrain[y][x-1]!=WALL)
                    toAdd.add(left);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //UP (y-1,x)
        try
        {
            Point up=new Point(x,y-1);
            int newCost=getCost(thisCost, up);
            if (newCost<costMap[y-1][x])
            {
                costMap[y-1][x]=newCost;
                if (terrain[y-1][x]!=WALL)
                    toAdd.add(up);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
    }
    
    //Done
    private void init(String file)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(file));
            this.height=img.getHeight();
            this.width=img.getWidth();
        }
        catch (IOException e) { }
        
        this.terrain=new byte[height][width];
        this.costMap=new int[height][width];
        
        //Input terrain and costMap
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
            {
                costMap[i][j]=Integer.MAX_VALUE;
                
                Color val = new Color(img.getRGB(j,i));
                if (val.equals(STARTC))
                {
                    costMap[i][j]=0;
                    terrain[i][j]=START;
                    fringe.add(new Point(j,i));
                }
                    
                else if (val.equals(ENDC))
                {
                    goal.add(new Point(j,i));
                    terrain[i][j]=END;
                }
                
                else if (val.equals(SLOWC))
                    terrain[i][j]=SLOW;
                
                else if (val.equals(WALLC))
                    terrain[i][j]=WALL;
                    
                else if (val.equals(OPENC))
                    terrain[i][j]=OPEN;
                
            }
             
    }
    
    /*
    //Done
    public String terrainToString()
    {
        String out="";
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                if (terrain[i][j]==OPEN)
                    out+=" ";
                else if (terrain[i][j]==START)
                    out+="S";
                else if (terrain[i][j]==END)
                    out+="E";
                else if (terrain[i][j]==WALL)
                    out+="X";
                else if (terrain[i][j]==SLOW)
                    out+="~";
            }
            if (i < height-1)
                out+="\n";
        }
        return out;
    }
    
    //Like the toString but prints out continually
    public void printCostMap()
    {
		
        int maxVal=0;
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
                if (costMap[i][j]!=Integer.MAX_VALUE && costMap[i][j]>maxVal)
                    maxVal=costMap[i][j];
        
        int stringSize=(int)Math.ceil(Math.log10(maxVal))+1;
        
        if (maxVal==0) stringSize=2;
        
        for (int i=0;i<height;i++)
        {
            for (int j=0;j<width;j++)
            {
                String thisPoint;
                if(costMap[i][j]==Integer.MAX_VALUE)
                    thisPoint="I";
                else if (costMap[i][j]<0)
                    thisPoint="~";
                else
                    thisPoint=costMap[i][j]+"";
                
                thisPoint+=new String(new char[stringSize-thisPoint.length()]).replace("\0", " ");
                System.out.print(thisPoint);
            }
            System.out.println();
        }
	}
    
    //Done
    public String costMapToString()
    {
        String out="";
        
        int maxVal=0;
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
                if (costMap[i][j]!=Integer.MAX_VALUE && costMap[i][j]>maxVal)
                    maxVal=costMap[i][j];
        
        int stringSize=(int)Math.ceil(Math.log10(maxVal))+1;
        
        if (maxVal==0) stringSize=2;
        
        for (int i=0;i<height;i++)
        {
            for (int j=0;j<width;j++)
            {
                String thisPoint;
                if(costMap[i][j]==Integer.MAX_VALUE)
                    thisPoint="I";
                else if (costMap[i][j]<0)
                    thisPoint="~";
                else
                    thisPoint=costMap[i][j]+"";
                
                thisPoint+=new String(new char[stringSize-thisPoint.length()]).replace("\0", " ");
                out+=thisPoint;
            }
            if (i<height-1)
                out+="\n";
        }
        return out;
    }
    
    //Done
    public String toString()
    {
        return costMapToString();
    }
    */
    
    public static void main(String[] args)
    {
        
        SolveSmall s = new SolveSmall("input/dij.png");
    }
}
