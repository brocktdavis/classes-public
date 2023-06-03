import java.util.*;
import java.math.BigInteger;

public class Factor
{
    public static LinkedList<BigInteger> primes = new LinkedList<BigInteger>();
    
    public static void genPrimes(BigInteger ceil)
    {
        
        BigInteger current = primes.peekLast().add(BigInteger.valueOf(2)); //  # starts with the first odd number above largest prime found
        BigInteger checkMax = bigIntSqrt(current);
        while (current.compareTo(ceil) == -1) //current is less than ceil
        {
            boolean is_prime = true;
            
            for (Iterator<BigInteger> iter = primes.iterator(); iter.hasNext(); )
            {  
                BigInteger prime = iter.next();
                if (prime.compareTo(checkMax) == 1) //prime greater than limit
                    break;
                if (current.mod(prime).equals(BigInteger.ZERO))
                {
                    is_prime = false;
                    break;
                }
                
            }
            
            if (is_prime)
            {
                primes.add(current);
            }
            
            current = current.add(BigInteger.valueOf(2));
            checkMax = bigIntSqrt(current);
        }
    }
    
    public static void printPrimes()
    {
        String out = "";
        for (Iterator<BigInteger> iter = primes.iterator(); iter.hasNext(); )
        {
            out += iter.next() + ", ";
        }
        out = "[" + out.substring(0, out.length() - 2) + "]";
        System.out.println(out);
    }
    
    //gives floor of squareroot
    public static BigInteger bigIntSqrt(BigInteger x)
    {
        
        if (x .equals(BigInteger.ZERO) || x.equals(BigInteger.ONE))
            return x;
        
        
        BigInteger two = BigInteger.valueOf(2L);
        BigInteger y;
        // starting with y = x / 2 avoids magnitude issues with x squared
        for (y = x.divide(two);
             y.compareTo(x.divide(y)) > 0;
             y = ((x.divide(y)).add(y)).divide(two));
             
        return y;
    }
    
    public static BigInteger factor(BigInteger N)
    {
        //if N is even, return 2
        if (N.and(BigInteger.ONE).equals(BigInteger.ZERO)) return BigInteger.valueOf(2);
        
        for (BigInteger n = bigIntSqrt(N).add(BigInteger.ONE); n.compareTo(N) == -1; n = n.add(BigInteger.ONE))
        {
            BigInteger check = n.multiply(n).subtract(N);
            //System.out.println(check);
            BigInteger checkRoot = bigIntSqrt(check);
            
            if (check.equals(checkRoot.multiply(checkRoot)))
            {
                return n.subtract(checkRoot);
            }
            
        }
        
        return BigInteger.ZERO;
    }
    
    /*
    public static void test()
    {
        //int count = 0;
        for (Iterator<Long> iter1 = primes.iterator(); iter1.hasNext(); )
        {
            long p = iter1.next();
            System.out.println(p);
            //count ++;
            for (Iterator<Long> iter2 = primes.iterator(); iter2.hasNext(); )
            {
                long q = iter2.next();
                long semiprime = p * q;
                
                long factor = factor(semiprime);
                if (factor == p || factor == q) continue;
                else
                {
                    System.out.println("Problem");
                    System.out.println(p + " * " + q + " = " + semiprime);
                    System.out.println("Result: " + semiprime);
                }
            }
        }
        System.out.println("test passed");
    }
    */
    
    public static void randTest(int trials)
    {
        int len = primes.size();
        for (int i = 0; i < trials; i++)
        {
            if (i % 100 == 0) System.out.println(i);
            
            
            int pIndex = (int)(Math.random() * (len + 1));
            int qIndex = (pIndex + 500) % len;
            
            BigInteger p = primes.get(pIndex);
            BigInteger q = primes.get(qIndex);
            
            BigInteger semiprime = p.multiply(q);
            
            BigInteger factor = factor(semiprime);
            
            if (!factor.equals(p) && !factor.equals(q))
            {
                System.out.println("!!!!OH NO!!!");
                System.out.println(p + " * " + q + " = " + semiprime);
                System.out.println(factor);
            }
            
        }
        
        
    }
    
    public static void main(String[] args)
    {
        
        //System.out.println(factor(new BigInteger("4294967297")));
        //System.out.println(factor(new BigInteger("100160063")));
        System.out.println(factor(new BigInteger("3289192017")));
        //System.out.println(factor(new BigInteger("455839")));
        //System.out.println(factor(new BigInteger("1127451830576035879")));
        
        primes.add(BigInteger.valueOf(2));
        primes.add(BigInteger.valueOf(3));
        
        genPrimes(BigInteger.valueOf(10));
        System.out.println("10^1 done");
        genPrimes(BigInteger.valueOf(100));
        System.out.println("10^2 done");
        genPrimes(BigInteger.valueOf(1000));
        System.out.println("10^3 done");
        genPrimes(BigInteger.valueOf(10000));
        System.out.println("10^4 done");
        genPrimes(BigInteger.valueOf(100000));
        System.out.println("10^5 done");
        genPrimes(BigInteger.valueOf(1000000));
        System.out.println("10^6 done");
        genPrimes(BigInteger.valueOf(10000000));
        System.out.println("10^7 done");
        genPrimes(BigInteger.valueOf(100000000));
        System.out.println("10^8 done");
        
        randTest(100000);
    }
    
}
