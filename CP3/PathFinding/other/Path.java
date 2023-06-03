import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class Path
{
    public static final Color STARTC = new Color(0,255,0);
    public static final Color ENDC = new Color(255,0,0);
    public static final Color SLOWC = new Color(0,0,255);
    public static final Color WALLC = new Color(0,0,0);
    public static final Color OPENC = new Color(255,255,255);
    
    public static final byte START = -1;
    public static final byte END = 1;
    public static final byte SLOW = 2;
    public static final byte WALL = -2;
    public static final byte OPEN = 0;
    
    public static final byte RIGHT=10;
    public static final byte DOWN=11;
    public static final byte LEFT=12;
    public static final byte UP=13;
    
    //Cost to get to posistion, default is infinity for regular or zero for start
    private int[][] costMap;
    
    //Cost to move on terrain, byte value will be used by a function that returns actual value
    private byte[][] terrain;
    
    //Shows which Points have been evaluated already
    //Once a Point is evaluated, it does not need to be evaluated again
    private boolean[][] evaluated;
    
    //Set of points that are currently able to be evaluated
    ArrayList<Point> fringe=new ArrayList<Point>();
    
    //Set of points that need to be evaluated
    //Once all points have true from evaluated[p.y][p.x], path can be found
    ArrayList<Point> goal=new ArrayList<Point>();
    
    private int height;
    private int width;
    
    public Path(String filename)
    {
        init(filename);
        System.out.println("Done with init");
        
        
        int cost=0;
        
        while(!isSolved())
        {
            boolean inc=true;
            for (int i=0;i<fringe.size();i++)
            {
                Point p=fringe.get(i);
                
                long flag1=System.nanoTime();
                int h=heuristic(p);
                long flag2=System.nanoTime();
                System.out.println("Heuristic: "+(flag2-flag1));
                if (h==cost || h==0)
                {
                    
                    //If new points are found, don't increment
                    inc=false;
                    
                    flag1=System.nanoTime();
                    costMap[p.y][p.x]=getTileCost(p)+smallestNeighborVal(p);
                    flag2=System.nanoTime();
                    System.out.println("tilecost and neighborVal: "+(flag2-flag1));
                    
                    flag1=System.nanoTime();
                    evaluateAndAdd(p);
                    flag2=System.nanoTime();
                    System.out.println("evaluated and add: "+(flag2-flag1));
                    
                    flag1=System.nanoTime();
                    fringe.remove(p);
                    flag2=System.nanoTime();
                    System.out.println("remove: "+(flag2-flag1));
                    System.out.println();
                }
            }
            
            if (inc)
                cost++;
        }
        
    }
    
    private int numberEvaluated()
    {
        int count=0;
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
                if(evaluated[i][j])
                    count++;
        
        return count;
    }
    
    //Done
    private int getTileCost(Point p)
    {
        return getTileCost(p.x,p.y);
    }
    
    //Done
    private int getTileCost(int x, int y)
    {
        byte tile=terrain[y][x];
        if (tile==OPEN)
            return 1;
        else if (tile==SLOW)
            return 2;
        else if (tile==WALL)
            return Integer.MAX_VALUE;
        else if (tile==END)
            return 0;
        else
            System.out.println("Tried to get invalid tile or start tile's cost.\nThis shouldn't happen");
        
        return Integer.MIN_VALUE;
    }
    
    //Done
    private void evaluateAndAdd(Point p)
    {
        
        evaluateAndAdd(p.x,p.y);
    }
    
    //Done
    private void evaluateAndAdd(int x, int y)
    {
        evaluated[y][x]=true;
        
        //Try to add RIGHT
        try
        {
            if (!evaluated[y][x+1] && terrain[y][x+1]!=WALL)
                fringe.add(new Point(x+1,y));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add DOWN
        try
        {
            if (!evaluated[y+1][x] && terrain[y+1][x]!=WALL)
                fringe.add(new Point(x,y+1));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add LEFT
        try
        {
            if (!evaluated[y][x-1] && terrain[y][x-1]!=WALL)
                fringe.add(new Point(x-1,y));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add UP
        try
        {
            if (!evaluated[y-1][x] && terrain[y-1][x]!=WALL)
                fringe.add(new Point(x,y-1));
        }
        catch (IndexOutOfBoundsException e) { }
        
        
        
    }
    
    //Done
    private boolean isSolved()
    {
        
        for (int i=0;i<goal.size();i++)
        {
            Point p=goal.get(i);
            if (!evaluated[p.y][p.x])
                return false;
        }
        return true;
    }
    
    //Done
    private int heuristic(Point p)
    {
        int maxXDist=0;
        int maxYDist=0;
        
        for (int i=0;i<goal.size();i++)
        {
            Point compare=goal.get(i);
            
            //If the point is a goal point, return zero to evaluate next
            if (p.x == compare.x && p.y == compare.y)
                return 0;
            
            if (Math.abs(p.x-compare.x)>maxXDist)
                maxXDist=Math.abs(p.x-compare.x);
            if (Math.abs(p.y-compare.y)>maxYDist)
                maxYDist=Math.abs(p.y-compare.y);
        }
        
        //Manhattan distance
        int manDist=maxXDist+maxYDist;
        
        return manDist+smallestNeighborVal(p);
    }
    
    //Done
    private int smallestNeighborVal(Point p)
    {
        byte direction=-10;
        int minVal=Integer.MAX_VALUE;
        
        //Get Right Value
        try
        {
            if (evaluated[p.y][p.x+1] && costMap[p.y][p.x+1]<minVal)
            {
                minVal=costMap[p.y][p.x+1];
                direction=RIGHT;
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add DOWN
        try
        {
            if (evaluated[p.y+1][p.x] && costMap[p.y+1][p.x]<minVal)
            {
                minVal=costMap[p.y+1][p.x];
                direction=DOWN;
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add LEFT
        try
        {
            if (evaluated[p.y][p.x-1] && costMap[p.y][p.x-1]<minVal)
            {
                minVal=costMap[p.y][p.x-1];
                direction=LEFT;
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add UP
        try
        {
            if (evaluated[p.y-1][p.x] && costMap[p.y-1][p.x]<minVal)
            {
                minVal=costMap[p.y-1][p.x];
                direction=UP;
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        if (direction!=-10)
        {
            if (direction==RIGHT)
                return costMap[p.y][p.x+1];
            else if (direction==DOWN)
                return costMap[p.y+1][p.x];
            else if (direction==LEFT)
                return costMap[p.y][p.x-1];
            else if (direction==UP)
                return costMap[p.y-1][p.x];
        }
        else
            System.out.println("Couldn't find adjacent square for heuristic");
        
        return Integer.MAX_VALUE;
    }
    
    //Done
    private void init(String filename)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(filename));
            this.height=img.getHeight();
            this.width=img.getWidth();
        }
        catch (Exception e) { }
        
        this.terrain=new byte[height][width];
        this.costMap=new int[height][width];
        this.evaluated=new boolean[height][width];
        
        ArrayList<Point> tempEvaluated=new ArrayList<Point>();
        
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
            {
                costMap[i][j]=Integer.MAX_VALUE;
                
                Color val = new Color(img.getRGB(j,i));
                if (val.equals(STARTC))
                {
                    costMap[i][j]=0;
                    terrain[i][j]=START;
                    evaluated[i][j]=true;
                    tempEvaluated.add(new Point(j,i));
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
            
        for (int i=0;i<tempEvaluated.size();i++)
        {
            Point p=tempEvaluated.get(i);
            evaluateAndAdd(p);
        }
    }
    
    //Done
    private BufferedImage finalOutput()
    {
        BufferedImage img =new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
            {
                byte val = terrain[i][j];
                Color c=null;
                if (val == START)
                    c=STARTC;
                else if (val == END)
                    c=ENDC;
                else if (val == SLOW)
                    c=SLOWC;
                else if (val == WALL)
                    c=WALLC;
                else if (val == OPEN)
                    c=OPENC;
                img.setRGB(j,i,c.getRGB());
            }
        return img;
    }
    
    //Done
    private BufferedImage tempOutput()
    {
        BufferedImage img =new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
            {
                Color c=OPENC;
                
                if (evaluated[i][j])
                    c=new Color(128,128,128);
                
                byte val = terrain[i][j];
                if (val == START)
                    c=STARTC;
                else if (val == END && evaluated[i][j])
                    c=SLOWC;
                else if (val == END)
                    c=ENDC;
                else if (val == WALL)
                    c=WALLC;
                
                
                img.setRGB(j,i,c.getRGB());
            }
        int fringeColor=new Color(255,128,128).getRGB();
        for (int i=0;i<fringe.size();i++)
        {
            Point p=fringe.get(i);
            img.setRGB(p.x,p.y,fringeColor);
            
        }
        return img;
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
                
                /*
                if (stringSize-thisPoint.length()<0)
                {
                    System.out.println(i+" "+j);
                    //System.out.println(thisPoint);
                }
                */
                thisPoint+=new String(new char[stringSize-thisPoint.length()]).replace("\0", " ");
                out+=thisPoint;
            }
            if (i<height-1)
                out+="\n";
        }
        return out.trim();
    }
    
    //Done
    public String terrainToString()
    {
        String out="";
        for (int i=0;i<height;i++)
        {
            for (int j=0;j<width;j++)
            {
                if(terrain[i][j]==START)
                    out+="S";
                else if(terrain[i][j]==END)
                    out+="E";
                else if(terrain[i][j]==SLOW)
                    out+="~";
                else if(terrain[i][j]==WALL)
                    out+="X";
                else
                    out+=" ";
                
            }
            if (i<height-1)
                out+="\n";
        }
        return out;
        
        
        
    }
    
    //Done
    public int cost()
    {
        int out=Integer.MAX_VALUE;
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
                if (terrain[i][j]==END)
                    out=Math.min(out,costMap[i][j]);
        return out+1;
    }
    
    public static void main(String[] args)
    {
        
        Path p = new Path("input/dij.png");
        System.out.println(p.costMapToString());
    }
    
}
