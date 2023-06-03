public class Point implements Comparable
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
    
    public int compareTo(Object obj)
    {
	if (this == obj)
	    return 0;
	if (obj == null)
	    return 0;
	if (getClass() != obj.getClass())
	    return 0;
	
	Point p = (Point) obj;
	
	if (this.y<p.y)
	    return 1;
	else if (this.y==p.y)
	{
	    if (this.x<p.x)
		return 1;
	    else if (this.x==p.y)
		return 0;
	    else return -1;
	}
	else return -1;
    }
    
    @Override
    public int hashCode() {
	return 50000*x+y;
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

    public String toString()
    {
	return "x: "+x+"\ty: "+y;
    }
}
