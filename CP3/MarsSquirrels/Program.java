import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.imageio.*;
import java.io.File;
import java.awt.Color;

public class Program
{
	
	public static short[][] original;
    public static short[][] terrain;
    public static short[][] costMap;
    public static boolean[][] touched;
    
    public static int cityX;
    public static int cityY;
    public static short cityLevel;
    
    public static final int imgSize=10012;
    
    public static long putBestCity(int seconds)
    {
		int count=0;
		long start=System.nanoTime();
		int bestX=1;
		int bestY=1;
		long bestCost=100000000;
		while(((System.nanoTime()-start)/Math.pow(10,9))<seconds)
		{
			int x=400 + (int)(Math.random() * ((imgSize-1200)+1));
			int y=400 + (int)(Math.random() * ((imgSize-1200)+1));
			
			try
			{	
				putCity(x,y);
			}
			catch (Exception e)
			{
				
			}
			
			long cost=getDiff();
			
			if (cost<bestCost && cityErosionSafe())
			{
				bestX=x;
				bestY=y;
				bestCost=cost;
			}
			resetArrays();
			count++;
		}
		putCity(bestX,bestY);
		return bestCost;
	}
    
    public static boolean cityErosionSafe()
    {
		for (int i=0;i<imgSize;i++)
			for (int j=0;j<imgSize;j++)
				if (touched[i][j] && !erosionSafe(i,j))
					return false;
		
		return true;
		
	}
    
    public static boolean erosionSafe(int x, int y)
    {
		int[] dX={1,0,-1,0};
		int[] dY={0,1,0,-1};
		
		short level=terrain[x][y];
		
		for (int i=0;i<4;i++)
		{
			try	{ if (Math.abs(terrain[x+dX[i]][y+dY[i]]-level)>1) return false;	}
			catch (Exception e)	{ }
		}
		return true;
	}
    
    public static void putCity(int x, int y)
    {
	
		cityX=x;
		cityY=y;
		
		double total=0;
		for (int i=0;i<500;i++)
			for (int j=0;j<500;j++)
				total+=terrain[x+i][y+j];
				
		cityLevel=(short)(Math.round(total/(500*500)));
		
		for (int i=0;i<500;i++)
			for (int j=0;j<500;j++)
			{
				terrain[cityX+i][cityY+j]=cityLevel;
				touched[cityX+i][cityY+j]=true;
			}
		
		
		for (int i=0;i<255;i++)
			levelPerim(i);
    }
    
    public static void levelPerim(int dist)
    {
	
		//Level top-edge
		for (int i=-dist;i<500;i++)
		{
			int level;
			int compare;
			if (i<0 && touched[cityX+i][cityY-dist-i])
			{
				level = terrain[cityX+i][cityY-dist-i-1];
				compare = terrain[cityX+i][cityY-dist-i];
				
				if (level-compare>1)
				{
					terrain[cityX+i][cityY-dist-i-1]=(short)(compare+1);
					touched[cityX+i][cityY-dist-i-1]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX+i][cityY-dist-i-1]=(short)(compare-1);
					touched[cityX+i][cityY-dist-i-1]=true;
				}
			}
			else if (touched[cityX+i][cityY-dist])
			{
				level = terrain[cityX+i][cityY-dist-1];
				compare = terrain[cityX+i][cityY-dist];
				if (level-compare>1)
				{
					terrain[cityX+i][cityY-dist-1]=(short)(compare+1);
					touched[cityX+i][cityY-dist-1]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX+i][cityY-dist-1]=(short)(compare-1);
					touched[cityX+i][cityY-dist-1]=true;
				}
			}
		}

		//Right-edge
		for (int i=-dist;i<500;i++)
		{
			int level;
			int compare;
			if (i<0 && touched[cityX+499+dist+i][cityY+i])
			{
				level = terrain[cityX+500+dist+i][cityY+i];
				compare = terrain[cityX+499+dist+i][cityY+i];
				
				if (level-compare>1)
				{
					terrain[cityX+500+dist+i][cityY+i]=(short)(compare+1);
					touched[cityX+500+dist+i][cityY+i]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX+500+dist+i][cityY+i]=(short)(compare-1);
					touched[cityX+500+dist+i][cityY+i]=true;
				}
			}
			else if (touched[cityX+499+dist][cityY+i])
			{
				level = terrain[cityX+500+dist][cityY+i];
				compare = terrain[cityX+499+dist][cityY+i];
				if (level-compare>1)
				{
					terrain[cityX+500+dist][cityY+i]=(short)(compare+1);
					touched[cityX+500+dist][cityY+i]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX+500+dist][cityY+i]=(short)(compare-1);
					touched[cityX+500+dist][cityY+i]=true;
				}
			}
			
		}
	
		//Bottom-edge
		for (int i=-dist;i<500;i++)
		{
			int level;
			int compare;
			if (i<0 && touched[cityX+499-i][cityY+499+dist+i])
			{
				level = terrain[cityX+499-i][cityY+500+dist+i];
				compare = terrain[cityX+499-i][cityY+499+dist+i];
				
				if (level-compare>1)
				{
					terrain[cityX+499-i][cityY+500+dist+i]=(short)(compare+1);
					touched[cityX+499-i][cityY+500+dist+i]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX+499-i][cityY+500+dist+i]=(short)(compare-1);
					touched[cityX+499-i][cityY+500+dist+i]=true;
				}
			}
			else if (touched[cityX+499-i][cityY+499+dist])
			{
				level = terrain[cityX+499-i][cityY+500+dist];
				compare = terrain[cityX+499-i][cityY+499+dist];
				
				if (level-compare>1)
				{
					terrain[cityX+499-i][cityY+500+dist]=(short)(compare+1);
					touched[cityX+499-i][cityY+500+dist]=true;
					
				}
				else if (level-compare<-1)
				{
					terrain[cityX+499-i][cityY+500+dist]=(short)(compare-1);
					touched[cityX+499-i][cityY+500+dist]=true;
				}
			}
			
		}
	
		//Left-edge
		for (int i=-dist;i<500;i++)
		{
			int level;
			int compare;
			if (i<0 && touched[cityX-dist-i][cityY+499-i])
			{
				level = terrain[cityX-dist-i-1][cityY+499-i];
				compare = terrain[cityX-dist-i][cityY+499-i];
				
				if (level-compare>1)
				{
					terrain[cityX-dist-i-1][cityY+499-i]=(short)(compare+1);
					touched[cityX-dist-i-1][cityY+499-i]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX-dist-i-1][cityY+499-i]=(short)(compare-1);
					touched[cityX-dist-i-1][cityY+499-i]=true;
				}
			}
			else if (touched[cityX-dist][cityY+499-i])
			{
				level = terrain[cityX-dist-1][cityY+499-i];
				compare = terrain[cityX-dist][cityY+499-i];
				if (level-compare>1)
				{
					terrain[cityX-dist-1][cityY+499-i]=(short)(compare+1);
					touched[cityX-dist-1][cityY+499-i]=true;
				}
				else if (level-compare<-1)
				{
					terrain[cityX-dist-1][cityY+499-i]=(short)(compare-1);
					touched[cityX-dist-1][cityY+499-i]=true;
					
				}
			}
			
		}
	}
		
	public static void resetArrays()
	{
		terrain=new short[imgSize][imgSize];
		for (int i=0;i<imgSize;i++)
			for (int j=0;j<imgSize;j++)
				terrain[i][j]=original[i][j];
				
		touched=new boolean[imgSize][imgSize];
	}
    
    //Saves output.png
    public static void output()
	{
		BufferedImage img =new BufferedImage(imgSize,imgSize,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<imgSize;i++)
			for(int j=0;j<imgSize;j++)
			{
				int val=terrain[i][j];
				
				if (touched[i][j])
					img.setRGB(i,j,(new Color(0,0,val)).getRGB());
				else
					img.setRGB(i,j, (new Color(val,val,val)).getRGB());
					
			}
			
		for (int i=0;i<500;i++)
			for (int j=0;j<500;j++)
				img.setRGB(cityX+i,cityY+j, (new Color(0,cityLevel,0)).getRGB());
			
		try	{ ImageIO.write(img,"png",new File("output.png"));	}
		catch(Exception e)	{ }
	}
    
    //Returns total cost and diff boolean array
    public static long getDiff()
    {
	
		touched = new boolean[imgSize][imgSize];
		long cost=0;
		for (int i=0;i<imgSize;i++)
		{
			for (int j=0;j<imgSize;j++)
			{
				int diff=Math.abs(terrain[i][j]-original[i][j]);
				cost+=diff*costMap[i][j];
				
				if (diff>0)
					touched[i][j]=true;
			}
		}
		return cost;
    }
    
    //Reads in terrain.png and costMap.png and saves to terrain, costMap, and original arrays
    public static void init()
    {	
		//Input terrain
		original=new short[imgSize][imgSize];
		try
		{
			BufferedImage origImg= ImageIO.read(new File("terrain.png"));
			BufferedImage img= new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_3BYTE_BGR);
			img.getGraphics().drawImage(origImg, 0, 0, null);
			
			for(int i=0;i<imgSize;i++)
				for(int j=0;j<imgSize;j++)
					original[i][j]=(short)(img.getRGB(i,j)&0xFF);
		}
		catch(Exception e)	{ e.printStackTrace();	}
		
		//Input costmap
		costMap=new short[imgSize][imgSize];
		try
		{
			BufferedImage original= ImageIO.read(new File("costMap.png"));
			BufferedImage img= new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_3BYTE_BGR);
			img.getGraphics().drawImage(original, 0, 0, null);
			
			for(int i=0;i<imgSize;i++)
				for(int j=0;j<imgSize;j++)
					costMap[i][j]=(short)(img.getRGB(i,j)&0xFF);
		}
		catch(Exception e)	{ e.printStackTrace();	}
	
		resetArrays();
	}
    
    public static void main(String[] args)
    {
		init();
		
		putBestCity(45);
		
		output();
	}
}
