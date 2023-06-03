import java.util.TreeSet;

public class Test
{
    public static void main(String[] args)
    {
        System.out.println("Format: ");
        System.out.println("<number of consecutive ints>,<time in nanoseconds to add to my tree>,<\" \" to remove all from my tree>,<depth of my tree>,<time in nanoseconds to add to Java tree>,<\" \" to remove all from Java tree>");
        System.out.println();
        
        for (int base=0;base<28;base++)
        {
            AVLTree<Integer> tree = new AVLTree<Integer>();
            
            //Add all to my tree
            long start0= System.nanoTime();
            for (int i = 0; i < Math.pow(2, base); i++)
                tree.add(i);
            long end0 = System.nanoTime();
            
            int depth = tree.depth();
            
            //Remove all from my tree
            long start1= System.nanoTime();
            for (int i = 0; i < Math.pow(2, base); i++)
                tree.pollFirst();
            long end1 = System.nanoTime();
            
            
            TreeSet<Integer> compare = new TreeSet<Integer>();
            
            //All all to Java tree
            long start2 = System.nanoTime();
            for (int i = 0; i < Math.pow(2, base); i++)
                compare.add(i);
            long end2 = System.nanoTime();
            
            //Remove all from Java tree
            long start3 = System.nanoTime();
            for (int i = 0; i < Math.pow(2, base); i++)
                compare.pollFirst();
            long end3 = System.nanoTime();
            
            System.out.print((int)(Math.pow(2,base)) + ",");
            System.out.print((end0-start0) + "," + (end1-start1) + "," + depth + ",");
            System.out.print((end2-start2) + "," + (end3-start3));
            System.out.println("\n");
            
        }
    }
}
