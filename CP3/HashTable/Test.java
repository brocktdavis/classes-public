import java.util.*;
import java.io.*;

public class Test
{
    public static void printList(ArrayList<String> words)
    {
        for (int i = 0; i < words.size(); i++)
            System.out.println(words.get(i));
    }
    
    public static HashMap<String, Integer> readTextJava(File f, int maxWords)
    {
        java.util.HashMap<String, Integer> map = new java.util.HashMap<String, Integer>();
        Scanner scan = null;
        try
        {
            scan = new Scanner(f);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("no file");
            System.exit(0);
        }
        
        int i = 0;
        while (scan.hasNext())
        {
            String word = scan.next().replaceAll("[^A-Za-z]", "").toLowerCase();
            
            if (map.containsKey(word))
            {
                int occurences = map.remove(word);
                map.put(word, occurences+1);
            }
            else
                map.put(word, 1);
            
            i++;
            if (i == maxWords)
                return map;
        }
        return map;
    }
    
    public static HashTable<String, Integer> readText(File f, int maxWords)
    {
        HashTable<String, Integer> map = new HashTable<String, Integer>();
        Scanner scan = null;
        try
        {
            scan = new Scanner(f);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("no file");
            System.exit(0);
        }
        
        int totalCollisions=0;
        
        int i = 0;
        while (scan.hasNext())
        {
            String word = scan.next().replaceAll("[^A-Za-z]", "").toLowerCase();
            
            if (map.containsKey(word))
            {
                int occurences = map.remove(word);
                map.put(word, occurences + 1);
            }
            else
            {
                totalCollisions += map.put(word, 1);
            }
            
            i++;
            if (i == maxWords)
            {
                System.out.print(" " + totalCollisions*1.0/map.size());
                return map;
            }
        }
        return map;
    }
    
    public static int getMostOccurencesJava(HashMap<String, Integer> map)
    {
        int mostOccurences = 0;
        
        Iterator<String> words = map.keySet().iterator();
        while (words.hasNext())
        {
            String word = words.next();
            int occurences = map.get(word);
            if (occurences > mostOccurences)
                mostOccurences = occurences;
        }
        
        return mostOccurences;
    }
    
    public static int getMostOccurences(HashTable<String, Integer> map)
    {
        int mostOccurences = 0;
        
        Iterator<String> words = map.keySet().iterator();
        while (words.hasNext())
        {
            String word = words.next();
            int occurences = map.get(word);
            if (occurences > mostOccurences)
                mostOccurences = occurences;
        }
        
        return mostOccurences;
    }
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("Java");
    
        
        //for (int n = 10000; n < 340000; n += 10000)
        {
            //System.out.print(n + " ");
            //Read in all words
            //long f1 = System.nanoTime();
            HashMap<String, Integer> test = readTextJava(new File("text.txt"), Integer.MAX_VALUE);
            //long f2 = System.nanoTime();
            
            //System.out.print(" " + ((f2-f1)/1000000000.0));
            //System.out.println(" " + test.size());
            
            int occ = getMostOccurencesJava(test);
            
            ArrayList<String> commonWords = new ArrayList<String>();
            while (commonWords.size() < 100)
            {
                Iterator<String> words = test.keySet().iterator();
                while (words.hasNext())
                {
                    String word = words.next();
                    if (test.get(word) == occ) commonWords.add(word);
                }
                occ--;
            }
            
            printList(commonWords);
        }
        System.out.println("\nMy Implementation");
        
        
        //for (int n = 10000; n < 340000; n += 10000)
        {
            //System.out.print(n + " ");
            //Read in all words
            //long f1 = System.nanoTime();
            HashTable<String, Integer> test = readText(new File("text.txt"), Integer.MAX_VALUE);
            //long f2 = System.nanoTime();
            
            //System.out.print(" " + ((f2-f1)/1000000000.0));
            //System.out.print(" " + test.size());
            //System.out.println(" " + test.loadFactor());
            
            int occ = getMostOccurences(test);
            
            ArrayList<String> commonWords = new ArrayList<String>();
            while (commonWords.size() < 100)
            {
                commonWords.addAll(test.getKeys(occ));
                occ--;
            }
            
            printList(commonWords);
        }
        
    }
}
