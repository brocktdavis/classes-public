import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.imageio.*;
import java.io.File;
import java.awt.Color;


public class Program
{
    public static byte[][] original;
    public static byte[][] terrain;
    public static byte[][] costMap;
    public static boolean[][] touched;
    
    public static int cityX;
    public static int cityY;
    public static byte cityLevel;
    
    public static final int imgSize=10012;
    
    //Puts city 500x500 in size on the map
    //level is between -128 and 127
    public static void putCity(int x, int y, byte level)
    {
	
		cityX=x;
		cityY=y;
		cityLevel=level;
		
		for (int i=0;i<500;i++)
			for (int j=0;j<500;j++)
				terrain[cityY+i][cityX+j]=level;
		
		//levelPerim(1);
	
    }

    public static void levelPerim(int distance)
    {
	
		int dist=distance-1;
	
		//Level top-edge
		for (int i=-dist;i<500;i++)
		{
			int level;
			int compare;
			if (i<0)
			{
				level = terrain[cityY-dist-i-1][cityX+i];
				compare = terrain[cityY-dist-i][cityX+i];
				
				if (level-compare>1)
				{
					terrain[cityY-dist-i-1][cityX+i]=(byte)(level-1);
				}
				else if (level-compare<-1)
					terrain[cityY-dist-i-1][cityX+i]=(byte)(level+1);
			}
			else
			{
				level = terrain[cityY-dist-1][cityX+i];
				compare = terrain[cityY-dist][cityX+i];
				if (level-compare>1)
					terrain[cityY-dist-1][cityX+i]=(byte)(level-1);
				else if (level-compare<-1)
					terrain[cityY-dist-1][cityX+i]=(byte)(level+1);
			}
		}
		
		//Level right-edge
		
    }
    
    public static void saveImage(String filename)
	{
		BufferedImage img =new BufferedImage(imgSize,imgSize,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<imgSize;i++)
			for(int j=0;j<imgSize;j++)
			{
				
				int val=terrain[i][j]+128;
				
				if (touched[i][j])
					img.setRGB(i,j,(new Color(0,0,val)).getRGB());
				else
					img.setRGB(i,j, (new Color(val,val,val)).getRGB());
			}
			
		for (int i=0;i<500;i++)
			for (int j=0;j<500;j++)
				img.setRGB(cityX+i,cityY+j, (new Color(0,cityLevel+128,0)).getRGB());
			
		try
		{
			ImageIO.write(img,"png",new File(filename));
		}
		catch(Exception e)
		{
		}
	}
       
    //Returns total cost
    public static int getDiff()
    {
	
		touched=new boolean[imgSize][imgSize];

		int cost=0;
		for (int i=0;i<imgSize;i++)
		{
			for (int j=0;j<imgSize;j++)
			{
				int diff=Math.abs(terrain[i][j]-original[i][j]);
				cost+=diff;//*costMap[i][j];
				
				if (diff>0)
					touched[i][j]=true;
			}
		}
		return cost;
    }
    
    public static void main(String[] args) throws Exception
    {
	
	//Init
	long flag0=System.nanoTime();
	
	//Input terrain
	terrain = new byte[imgSize][imgSize];
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File("terrain.png"));
		}
		catch (Exception e) {}
		for(int i=0;i<imgSize;i++)
		    for(int j=0;j<imgSize;j++)
		    {
			    terrain[j][i]=(byte)(img.getRGB(i,j)%256-128);
		    }
	}
	
	original= new byte[imgSize][imgSize];
	for(int i=0;i<imgSize;i++)
		for(int j=0;j<imgSize;j++)
			original[i][j]=(byte)(terrain[i][j]*1);
	
	
		
	long flag1=System.nanoTime();
	System.out.print("Time to input terrain: ");
	System.out.println((flag1-flag0)/Math.pow(10,9));
	
	//Put city
	putCity(200,100,(byte)10);
	
	long flag2=System.nanoTime();
	
	System.out.print("Time to put city : ");
	System.out.println((flag2-flag1)/Math.pow(10,9));
	
	//Get changed cost
	
	int cost=getDiff();
	
	long flag3=System.nanoTime();
	
	System.out.print("time to calc diff: ");
	System.out.println((flag3-flag2)/Math.pow(10,9));
	System.out.println("Cost: "+cost+ " acorns");
	
	//Save image
	saveImage("output.png");
	
	long flag4=System.nanoTime();
	
	System.out.print("time to save img: ");
	System.out.println((flag4-flag3)/Math.pow(10,9));
    }
}
