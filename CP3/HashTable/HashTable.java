import java.util.*;
import java.security.MessageDigest;

public class HashTable<K, V>
{
    
    //table that stoes values
    private K[] keys;
    private V[] values;
    
    //Size of table
    private int capacity = 100;
    
    //Number of elements in table
    private int size = 0;
    
    private float loadFactor = 0.75f;
    
    public HashTable()
    {
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
    }
    
    public HashTable(int initialCapacity)
    {
        this.capacity = initialCapacity;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
    }
    
    public HashTable(int initialCapacity, float loadFactor)
    {
        this.capacity=initialCapacity;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.loadFactor=loadFactor;
    }
    
    //Done
    public void clear()
    {
        this.capacity = 10;
        this.size = 0;
        
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
    }
    
    //Done
    public HashTable<K, V> clone()
    {
        HashTable<K, V> clone = new HashTable<K, V>(10, loadFactor);
        
        for (int i = 0; i < keys.length; i++)
        {
            if (keys[i] == null) continue;
            else
                clone.put(keys[i], values[i]);
        }
        return clone;
    }
    
    //Done
    public boolean containsKey(K key)
    {
        int i = hash(key);
        int coll = 0;
        while (keys[(i+coll)%capacity] != null)
        {
            if (keys[(i+coll)%capacity].equals(key))
                return true;
                
            coll++;
        }
        
        return false;
    }
    
    //Done
    public boolean containsValue(V val)
    {
        for (int i = 0; i < values.length; i++)
            if (values[i].equals(val)) return true;
            
        return false;
        
    }
    
    //Done
    public V get(K key)
    {
        //try speed test while saving currentKey
        int i = hash(key);
        int coll = 0;
        while (keys[(i+coll)%capacity] != null)
        {
            if (keys[(i+coll)%capacity].equals(key))
                return values[(i+coll)%capacity];
                
            coll++;
        }
        
        return null;
    }
    
    public Set<K> getKeys(V val)
    {
        HashSet<K> out = new HashSet<K>();
        
        for (int i = 0; i < capacity; i++)
        {
            if (values[i] != null && values[i].equals(val)) out.add(keys[i]);
        }
        return (Set<K>)out;
    }
    
    //Done
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    //Done
    public Set<K> keySet()
    {
        HashSet<K> out = new HashSet<K>();
        
        for (int i = 0; i < keys.length; i++)
        {
            if (keys[i] == null) continue;
            
            out.add(keys[i]);
        }
        return (Set<K>) out;
    }
    
    //Returns number of collisions
    public int put(K key, V value)
    {
        int i = hash(key);
        int coll = 0;
        while (keys[(i+coll)%capacity] != null)
        {
            if (keys[(i+coll)%capacity].equals(key))
                return -1;
            coll++;
        }
        
        keys[(i+coll)%capacity] = key;
        values[(i+coll)%capacity] = value;
        size ++;
        updateTable();
        return coll;
        
    }
    
    //Done
    public V remove(K k)
    {
        V val = null;
        int i = hash(k)%capacity;
        ArrayList<K> toAddK = new ArrayList<K>();
        ArrayList<V> toAddV = new ArrayList<V>();
        
        while (keys[i % capacity] != null)
        {
            if (keys[i % capacity].equals(k))
            {
                size --;
                val = values[i % capacity];
                keys[i % capacity] = null;
                values[i % capacity] = null;
                K next = keys[(i + 1) % capacity];
                while (next != null)
                {
                    if (hash(next) % (i + 1) == 0)
                    {
                        toAddK.add(next);
                        toAddV.add(values[(i + 1) % capacity]);
                        keys[(i + 1) % capacity] = null;
                        values[(i + 1) % capacity] = null;
                        size--;
                        break;
                    }
                    i++;
                    next = keys[(i + 1) % capacity];
                }
                break;
            }
            i++;
        }
        
        for (int ind = 0; ind < toAddK.size(); ind++)
            this.put(toAddK.get(ind), toAddV.get(ind));
        
        return val;
    }
    
    //Done
    public int size()
    {
        return size;
    }
    
    //Done
    public int capacity()
    {
        return capacity;
    }
    
    //Done
    public float loadFactor()
    {
        return (float) size / capacity;
    }
    
    //Done
    public String toString()
    {
        String out = "";
        
        int keyStringSize = 0;
        for (int i = 0; i < capacity; i ++)
        {
            K key = keys[i];
            if (key==null) continue;
            else if (key.toString().length() > keyStringSize)
                keyStringSize = key.toString().length();
        }
        keyStringSize ++;
        
        for (int i=0;i<capacity;i++)
        {
            K key = keys[i];
            String keyString = (key == null) ? "" : key.toString();
            keyString += new String(new char[keyStringSize - keyString.length()]).replace("\0", " ");
            out += keyString;
            
            out += "|";
            out += (values[i] == null) ? "" : values[i].toString();
            out += "\n";
        }
        return out;
    }
    
    //Done
    public int hashCode()
    {
        int out = 0;
        for (int i = 0; i < capacity; i++)
        {
            if (keys[i] == null) continue;
            out += keys[i].hashCode();
            out += values[i].hashCode();
        }
        
        return out;
    }
    
    //Done
    private int hash(K k) {
        int hashCode = k.hashCode();
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        return Math.abs(hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4));
    }
    
    //Done
    private void updateTable()
    {
        //if loadFactor is exceeded, expand table
        if (loadFactor() > loadFactor)
        {
            K[] oldKeys = keys.clone();
            V[] oldValues = values.clone();
            capacity *= 2;
            size = 0;
            keys = (K[]) new Object[capacity];
            values = (V[]) new Object[capacity];
            
            for (int i = 0; i < oldKeys.length; i++)
            {
                if (oldKeys[i] != null)
                {
                    this.put(oldKeys[i], oldValues[i]);
                }
                
            }
        }
    }
    
}
