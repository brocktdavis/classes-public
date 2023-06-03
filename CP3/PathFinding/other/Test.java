public class Test
{
	public static void main(String[] args)
	{
		int[][] array=new int[10000][10000];
		for (int i=0;i<array.length;i++)
			for (int j=0;j<array[0].length;j++)
			{
				for (int k=0;k<4;k++)
				{
					try
					{
						array[i+j][i-j]=Integer.MAX_VALUE;
					}
					catch (Exception e)
					{
						
					}
					array[i][j]=i+j;
				}
			}
	}
	
}
