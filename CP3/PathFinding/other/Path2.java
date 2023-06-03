import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Path2
{
    
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
    
    private int town1X;
    private int town1Y;
    private int town2X;
    private int town2Y;
    
    public Path2(String filename, int town1X, int town1Y, int town2X, int town2Y)
    {
        this.town1X=town1X;
        this.town1Y=town1Y;
        this.town2X=town2X;
        this.town2Y=town2Y;
        
        
        init(filename,town1X,town1Y,town2X,town2Y);
        System.out.println("Done with init");
        
        
        int cost=0;
        
        while(!isSolved())
        {
            
            boolean inc=true;
            for (int i=0;i<fringe.size();i++)
            {
                Point p=fringe.get(i);
                
                int h=heuristic(p);
                if (h==cost || h==0)
                {
                    
                    //If new points are found, don't increment
                    inc=false;
                    
                    costMap[p.y][p.x]=getCost(p);
                    evaluateAndAdd(p);
                    fringe.remove(p);
                }
            }
            if (inc)
                cost++;
            
            //System.out.println(fringe.size());
            
            BufferedImage img = getWindow();
            ImageViewer iv=new ImageViewer(img, true);
            
        }
    }
    
    //Used
    private int getCost(Point p)
    {
        int x=p.x;
        int y=p.y;
        
        int leastVal=Integer.MAX_VALUE;
        
        //RIGHT Value
        try
        {
            int heightDiff=terrain[y][x]-terrain[y][x+1];
            if (evaluated[y][x+1])
                leastVal=Math.min(leastVal,1+(heightDiff*heightDiff));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //DOWN Value
        try
        {
            int heightDiff=terrain[y][x]-terrain[y+1][x];
            if (evaluated[y+1][x])
                leastVal=Math.min(leastVal,1+(heightDiff*heightDiff));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //LEFT Value
        try
        {
            int heightDiff=terrain[y][x]-terrain[y][x-1];
            if (evaluated[y][x-1])
                leastVal=Math.min(leastVal,1+(heightDiff*heightDiff));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //UP Value
        try
        {
            int heightDiff=terrain[y][x]-terrain[y-1][x];
            if (evaluated[y-1][x])
                leastVal=Math.min(leastVal,1+(heightDiff*heightDiff));
        }
        catch (IndexOutOfBoundsException e) { }
        
        return leastVal;
        
    }
    
    //Used
    private void evaluateAndAdd(Point p)
    {
        
        evaluateAndAdd(p.x,p.y);
    }
    
    //Used
    private void evaluateAndAdd(int x, int y)
    {
        evaluated[y][x]=true;
        
        //Try to add RIGHT
        try
        {
            if (!evaluated[y][x+1])
                fringe.add(new Point(x+1,y));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add DOWN
        try
        {
            if (!evaluated[y+1][x])
                fringe.add(new Point(x,y+1));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add LEFT
        try
        {
            if (!evaluated[y][x-1])
                fringe.add(new Point(x-1,y));
        }
        catch (IndexOutOfBoundsException e) { }
        
        //Try to add UP
        try
        {
            if (!evaluated[y-1][x])
                fringe.add(new Point(x,y-1));
        }
        catch (IndexOutOfBoundsException e) { }
        
        
        
    }
    
    //Used
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
    
    //Used
    private int heuristic(Point p)
    {
        int maxXDist=0;
        int maxYDist=0;
        int maxZDist=0;
        
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
            if (Math.abs(terrain[p.y][p.x]-terrain[compare.y][compare.x])>maxZDist)
                maxZDist=Math.abs(terrain[p.y][p.x]-terrain[compare.y][compare.x]);
        }
        
        return maxXDist+maxYDist+maxZDist;
    }
    
    //Used
    private void init(String filename, int startX, int startY, int endX, int endY)
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
        
        //Input terrain
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
            {
                costMap[i][j]=Integer.MAX_VALUE;
                byte h=(byte)((img.getRGB(j,i)%256)-128);
                terrain[i][j]=h;
            }
             
        //Add starting points
        for (int i=startY;i<startY+500;i++)
            for (int j=startX;j<startX+500;j++)
            {
                costMap[i][j]=0;
                evaluated[i][j]=true;
                tempEvaluated.add(new Point(j,i));
            }
        
        //Add goal points
        for (int i=endY;i<endY+500;i++)
            for (int j=endX;j<endX+500;j++)
                goal.add(new Point(j,i));
        
       for (int i=0;i<tempEvaluated.size();i++)
       {
            Point p=tempEvaluated.get(i);
            evaluateAndAdd(p);
        }
    }
    
    //Done
    private BufferedImage getWindow()
    {
        
        BufferedImage img =new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
            {
                Color c=null;
                int val=terrain[i][j]+128;
                
                //If point is within town 1
                if (town1Y<=i && i<town1Y+500 && town1X<=j && j<town1X+500)
                    c=new Color(0,val,0);
                    
                //If point is within town 2
                else if (town2Y<=i && i<town2Y+500 && town2X<=j && j<town2X+500)
                    c=new Color(val,0,0);
                
                else
                    c=new Color(val,val,val);
                
                img.setRGB(j,i,c.getRGB());
            }
        
        for (int i=0;i<fringe.size();i++)
        {
            Point p=fringe.get(i);
            img.setRGB(p.x,p.y,new Color(0,0,255).getRGB());
            
        }
        
        return img;
    }
    
    //Used
    public int pathCost(int endX,int endY)
    {
        int out=Integer.MAX_VALUE;
        for (int i=endY;i<endY+500;i++)
            for (int j=endX;j<endX+500;j++)
                out=Math.min(out,costMap[i][j]);
        return out+1;
    }
    
    public static void main(String[] args)
    {
        int y1=73;
        int x1=250;
        int y2=883;
        int x2=130;
        Path2 p = new Path2("input/small_in.png",x1,y1,x2,y2);
        BufferedImage im=p.getWindow();
        ImageViewer iv=new ImageViewer(im,true);
    }
    
}
