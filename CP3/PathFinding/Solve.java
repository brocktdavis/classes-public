import java.util.*;
import java.io.*;
import java.awt.Color;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Solve
{
    public static final int CITYSIZE=500;
    
    ArrayList<String> cityNames = new ArrayList<String>();
    
    ArrayList<Point> cities = new ArrayList<Point>();
    int startCity=0;
    
    ArrayList<Point> fringe = new ArrayList<Point>();
    
    HashSet<Point> toAdd;
    
    byte[][] terrain;
    int[][] costMap;
    
    private int height;
    private int width;
    
    public Solve(String terrainFile, String townFile)
    {
        populateCities(townFile);
        BufferedImage img=init(terrainFile);
        
        while (startCity<cities.size()-1)
        {
			
            initCostMap();
            fillCostMap();
            
            ArrayList<Point> starts=printAndSaveCosts();
            tracePaths(img, starts);
            
            startCity++;
        }
        save(img);
    }
    
    private void save(BufferedImage img)
    {
		try	{ ImageIO.write(img,"png",new File("mars_paths.png"));	}
		catch(Exception e)	{ }
	}
    
    //Alters the BufferedImage and draws on it directly
    private void tracePaths(BufferedImage img, ArrayList<Point> starts)
    {
        for (int i=0;i<starts.size();i++)
        {
            Point current=starts.get(i);
            while (costMap[current.y][current.x]!=0)
            {
                
                int x=current.x;
                int y=current.y;
                int cost=costMap[y][x];
                byte height=terrain[y][x];
                
                img.setRGB(x,y,new Color(height+128,0,0).getRGB());
                
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
            
        }
    }
    
    //Requires filled costMap
    private ArrayList<Point> printAndSaveCosts()
    {
        //For printing lowest cost of city
        String city1Name=cityNames.get(startCity);
        
        //For retracing through map
        ArrayList<Point> out=new ArrayList<Point>();
        
        for (int i=startCity+1;i<cities.size();i++)
        {
            //Starting loc of city
            Point city=cities.get(i);
            
            //Point of city that has lowest cost
            Point lowestCostPoint=null;
            
            int minCost=Integer.MAX_VALUE;
            
            for (int y=city.y;y<city.y+CITYSIZE;y++)
                for (int x=city.x;x<city.x+CITYSIZE;x++)
                    if (costMap[y][x]<minCost)
                    {
                        //Update cost and point
                        minCost=costMap[y][x];
                        lowestCostPoint=new Point(x,y);
                    }
            
            minCost-=1000;
            String city2Name=cityNames.get(i);
            
            //Output cost
            System.out.println(city1Name+" "+city2Name+" "+minCost);
            //Add point to output
            out.add(lowestCostPoint);
        }
        return out;
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
    private void initCostMap()
    {
        
        //Reset costMap beforehand
        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
                costMap[i][j]=Integer.MAX_VALUE;
                
        
        int startX=cities.get(startCity).x;
        int startY=cities.get(startCity).y;
        
        for (int i=startY;i<startY+CITYSIZE;i++)
            for (int j=startX;j<startX+CITYSIZE;j++)
            {
                costMap[i][j]=0;
                fringe.add(new Point(j,i));
            }
            
        
    }
    
    //Done
    private int getCost(int thisCost, byte thisHeight, Point adj)
    {
        int cost=thisCost+1;
        cost+=(thisHeight-terrain[adj.y][adj.x])*(thisHeight-terrain[adj.y][adj.x]);
        for (int i=0;i<cities.size();i++)
        {
            Point city=cities.get(i);
            if (adj.x>=city.x && adj.x<city.x+CITYSIZE && adj.y>=city.y && adj.y<city.y+CITYSIZE)
                cost+=1000;
            
        }
        
        return cost;
        
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
            int newCost=getCost(thisCost, thisHeight, right);
            if (newCost<costMap[y][x+1])
            {
                costMap[y][x+1]=newCost;
                toAdd.add(right);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //DOWN (y+1,x)
        try
        {
            Point down=new Point(x,y+1);
            int newCost=getCost(thisCost, thisHeight, down);
            if (newCost<costMap[y+1][x])
            {
                costMap[y+1][x]=newCost;
                toAdd.add(down);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //LEFT (y,x-1)
        try
        {
            Point left=new Point(x-1,y);
            int newCost=getCost(thisCost, thisHeight, left);
            if (newCost<costMap[y][x-1])
            {
                costMap[y][x-1]=newCost;
                toAdd.add(left);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
        //UP (y-1,x)
        try
        {
            Point up=new Point(x,y-1);
            int newCost=getCost(thisCost, thisHeight, up);
            if (newCost<costMap[y-1][x])
            {
                costMap[y-1][x]=newCost;
                toAdd.add(up);
            }
        }
        catch (IndexOutOfBoundsException e) { }
        
    }
    
    //Done
    private BufferedImage init(String terrainFile)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(terrainFile));
            this.height=img.getHeight();
            this.width=img.getWidth();
        }
        catch (IOException e) { }
        
        this.terrain=new byte[height][width];
        this.costMap=new int[height][width];
        
        //Input terrain
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
                terrain[i][j]=(byte)((img.getRGB(j,i)%256)+128);
             
        return img;
    }
    
    //Like the toString but prints out continuously instead of saving a String
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
    
    //Done
    public void populateCities(String townFile)
    {
        Scanner sc=null;
        try
        {
            sc=new Scanner(new File(townFile));            
        }
        catch (FileNotFoundException e) { }
        
        sc.nextLine();
        while (sc.hasNextLine())
        {
            String[] line=sc.nextLine().split(",");
            int x = Integer.parseInt(line[3]);
            int y = Integer.parseInt(line[4]);
            cities.add(new Point(x,y));
            cityNames.add(line[1]);
            
        }
        
    }
    
    public static void main(String[] args)
    {
        
        Solve s = new Solve("input/input.png", "input/towns.txt");
    }
}
