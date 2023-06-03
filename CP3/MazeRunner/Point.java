//import java.lang.Comparable;

public class Point
{
    public int x,y;
    
    public Point(int x, int y)
    {
	this.x=x;
	this.y=y;
    }
    
    public Point copy()
    {
	return new Point(this.x*1,this.y*1);
    }
    
    @Override
    public int hashCode() {
		return 100000*x+y;
    }
    
    @Override
    public boolean equals(Object obj) {
	
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	    
	Point p = (Point) obj;
	return (this.x == p.x && this.y == p.y);
    } 
}
