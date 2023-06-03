/*
 * An AVL binary tree. Made by Brock Davis March 2017
 * Unlike the Java TreeSet, this implementation allows duplicates
 * Values equal to a node will move to the right, and removing a value will remove the rightmost node
 * 
 * @param <E> the type of elements maintained by this set
 */
public class AVLTree<E>
{
    
    private Node<E> root = null;
    private int size=0;
    
    /**
     * Constructs a new, empty tree, sorted according to the natural ordering of its elements.
     */
    public AVLTree() { }
    
    /**
     * Adds the specified element to the tree, rebalancing if needed
     * 
     * @param e the element to be added
     */
    public void add(E e)
    {
        size++;
    
        if (root == null)
            root = new Node<E>(e);
        
        else
            root = root.add(new Node<E>(e));
        
    }
    
    /**
     * Clears the tree so there are no elements
     */
    public void clear()
    {
        root=null;
        size=0;
    }
    
    /**
     * returns a shallow copy of the tree
     * (the elements themselves are not cloned)
     * 
     * @return a shallow copy of the tree
     */
    @Override
    public AVLTree<E> clone()
    {
        if (root == null) return null;
        AVLTree<E> out = new AVLTree<E>();
        out.root = root.clone();
        return out;
    }
    
    /**
     * Searches for the specified element in the tree
     * 
     * @param e the element to be searched for
     * @return true if the tree contains the element, false otherwise
     */
    public boolean contains(E e)
    {
        if (root == null) return false;
        return root.contains(e);
    }
    
    /**
     * Outputs how "tall" the tree is
     * More specifically, it is the length of the longest path to any node
     * 
     * @return the depth of the tree, and -1 if the tree is empty
     */
    public int depth()
    {
        if (root == null) return -1;
        return root.depth - 1;
    }
    
    /**
     * Returns the first element of the tree i.e. the far left of the tree
     * 
     * @return the first element of the tree, and null if the tree is empty
     */
    public E first()
    {
        if (root == null) return null;
        return root.first().value;
    }
    
    /**
     * Returns the last element of the tree i.e. the far right of the tree
     * 
     * @return the last element of the tree, and null if the tree is empty
     */
    public E last()
    {
        if (root == null) return null;
        return root.last().value;
    }
    
    /**
     * Returns and removes the first element of the tree i.e. the far left of the tree
     * 
     * @return the first element of the tree, and null if the tree is empty
     */
    public E pollFirst()
    {
        if (root == null) return null;
        Node<E> first = root.first();
        E returnVal = first.value;
        remove(returnVal);
        return returnVal;
    }
    
    /**
     * Returns and removes the last element of the tree i.e. the far right of the tree
     * 
     * @return the last element of the tree, and null if the tree is empty
     */
    public E pollLast()
    {
        if (root == null) return null;
        Node<E> last = root.last();
        E returnVal = last.value;
        remove(returnVal);
        return returnVal;
    }
    
    /**
     * Removes the element from the tree and rebalances the tree, if needed
     * 
     * @param e the element of the tree to remove
     * @return true if the element was in the tree and successfully removed, false otherwise
     */
    public boolean remove(E e)
    {
        if (root == null) return false;
        size--;
        try
        {
            return root.remove(new Node<E>(e));
        }
        catch (NullPointerException except)
        {
            root = null;
            return true;
        }
        
    
    }
    
    /**
     * Returns how many elements are in the tree
     * 
     * @return how many elements are in the tree
     */
    public int size()
    {
        return size;
    }
    
    /**
     * Returns an in-order array of all the elements
     * 
     * @return an in-order array of all the elements
     */
    public Object[] toArray()
    {
        if (root == null) return null;
        Object[] out = new Object[size];
        AVLTree<E> toUse = this.clone();
        System.out.println("Array size: "+out.length);
        System.out.println("Clone size: "+toUse.size());
        for (int i=0;i<size;i++)
        {
            out[i]=toUse.pollFirst();
        }
        return out;
    }
    
    /**
     * Returns the String representation of the string
     * The form is (leftTree) root [rightTree]
     * This form is recursively called for each subtree
     * An example of this would be ((1) 4 [(7) 10]) 15 [(18) 20]
     * 
     * @return a String representation of the tree, or an empty String for an empty tree
     */
    @Override
    public String toString()
    {
        if (root == null)
            return "";
        else
            return root.toString();
    }
    
    private class Node<E>
    {
        E value;
        
        Node<E> parent;
        Node<E> left;
        Node<E> right;
        
        int depth=1;
        
        Node(E value)
        {
            this.value=value;
        }
        
        public Node<E> clone()
        {
            Node<E> newRoot = new Node<E>(value);
            newRoot.left = (left == null) ? null : left.clone();
            newRoot.right = (right == null) ? null : right.clone();
            
            return newRoot;
        }
        
        Node<E> add(Node<E> node)
        {
            
            //Cast values to comparables
            Comparable<E> thisVal = (Comparable<E>)value;
            
            //If new node is less than or equal this, to the left
            if (thisVal.compareTo(node.value) > 0)
            {
                if (this.left==null)
                {
                    this.left=node;
                    node.parent=this;
                    //depth is always 1 if one side was previously null
                    this.depth=2;
                    //never need to rebalance
                    return this;
                }
                else
                {
                    left=left.add(node);
                }
            }
            
            //To the right
            else
            {
                if (this.right==null)
                {
                    this.right=node;
                    node.parent=this;
                    //depth is always 1 if one side was previously null
                    this.depth=2;
                    //never need to rebalance
                    return this;
                }
                else
                {
                    right=right.add(node);
                }
            }
            updateDepth();
            
            return rebalance();
            
        }
        
        void removeLeaf(Node<E> node)
        {
            if (this.left != null && this.left.equals(node))
                this.left=null;
            
            else if (this.right != null && this.right.equals(node))
                this.right=null;
            
            updateDepth();
        }
        
        void balanceSubNode(Node<E> node)
        {
            if (left != null && left.equals(node))
                left=left.rebalance();
                
            else if (right != null && right.equals(node))
                right=right.rebalance();
            
            updateDepth();
        }
        
        boolean remove(Node<E> node)
        {
            boolean out = true;
            
            //Cast value to comparables
            Comparable<E> thisVal = (Comparable<E>)value;
            
            //Remove this node
            if (thisVal.compareTo(node.value) == 0)
            {
                
                boolean leftNull = left == null;
                boolean rightNull = right == null;
                
                if (leftNull && rightNull)
                    parent.removeLeaf(this);
                    
                else if (!rightNull)
                {
                    Node<E> rightMin = right.first();
                    this.value = rightMin.value;
                    right.remove(this);
                }
                else if (!leftNull)
                {
                    Node<E> leftMax = left.last();
                    this.value = leftMax.value;
                    left.remove(this);
                }
            }
            
            //Check to the left
            else if (thisVal.compareTo(node.value) > 0)
            {
                if (this.left==null)
                    return false;
                    
                else
                    out = left.remove(node);
            }
            
            //Check to the right
            else
            {
                if (this.right==null)
                    return false;
                    
                else
                    out = right.remove(node);
            }
            
            updateDepth();
            if (parent == null)
            {
                parent = new Node<E>(null);
                parent.balanceSubNode(this);
                parent=null;
            }
            else
                parent.balanceSubNode(this);
                
            
            return out;
        }
        
        boolean equals(Node<E> node)
        {
            return (this.value.equals(node.value));
        }
        
        Node<E> rebalance()
        {
            
            //Check for rebalancing
            int leftDepth = (left == null) ? 0 : left.depth;
            int rightDepth = (right == null) ? 0 : right.depth;
            
            if (leftDepth - rightDepth > 1)
            {
                int leftRightDepth = (left.right == null) ? 0 : left.right.depth;
                int leftLeftDepth = (left.left == null) ? 0 : left.left.depth;
                
                //Check for double rotate
                if (leftRightDepth > leftLeftDepth)
                    left=left.rotateLeft();
                
                return rotateRight();
            }
            
            else if (leftDepth - rightDepth < -1)
            {
                int rightRightDepth = (right.right == null) ? 0 : right.right.depth;
                int rightLeftDepth = (right.left == null) ? 0: right.left.depth;
                
                //Check for double rotate
                if (rightLeftDepth > rightRightDepth)
                    right=right.rotateRight();
                
                return rotateLeft();
            }
            
            //no rebalancing happened
            return this;
        }
        
        int size()
        {
            int leftSize = (left==null) ? 0 : left.size();
            int rightSize = (right ==null) ? 0 : right.size();
            return leftSize + 1 + rightSize;
        }
        
        void updateDepth()
        {
            int leftDepth = (left == null) ? 0 : left.depth;
            int rightDepth = (right ==  null) ? 0 : right.depth;
            
            this.depth = Math.max(leftDepth, rightDepth) + 1;
        }
        
        Node<E> first()
        {
            if (left == null) return this;
            Node<E> leftMost=left;
            while (leftMost.left != null)
                leftMost=leftMost.left;
            
            return leftMost;
        }
        
        Node<E> last()
        {
            if (right == null) return this;
            Node<E> rightMost=right;
            while (rightMost.right != null)
                rightMost=rightMost.right;
                
            return rightMost;
        }
        
        //Done
        boolean contains(E e)
        {
            Comparable<E> thisVal = (Comparable<E>)value;
            
            if (thisVal.compareTo(e) == 0)
                return true;
            else 
            {
                if (left != null && thisVal.compareTo(e) > 0)
                {
                    return left.contains(e);
                }
                else if (right != null)
                    return right.contains(e);
            }
            return false;
        }
        
        //returns new root node
        Node<E> rotateRight()
        {
            Node<E> head = this;
            Node<E> leftTree = left;
            Node<E> leftRightTree = leftTree.right;
            
            //Complex logic, look at a diagram or something
            leftTree.parent=head.parent;
            head.left=leftRightTree;
            head.parent=leftTree;
            leftTree.right=head;
            if (leftRightTree != null) leftRightTree.parent=head;
            
            head.updateDepth();
            leftTree.updateDepth();
            
            return leftTree;
        }
        
        //returns new root node
        Node<E> rotateLeft()
        {
            
            Node<E> head = this;
            Node<E> rightTree = right;
            Node<E> rightLeftTree = rightTree.left;
            
            //Also complex logic, but reversed of rotate right
            rightTree.parent=head.parent;
            head.right=rightLeftTree;
            head.parent=rightTree;
            rightTree.left=head;
            if (rightLeftTree != null) rightLeftTree.parent=head; 
            
            head.updateDepth();
            rightTree.updateDepth();
            
            return rightTree;
        }
        
        //Change to tree
        public String toString()
        {
            String leftS="";
            String rightS="";
            
            if (left!=null) leftS="("+left.toString()+") ";
            if (right!=null) rightS=" ["+right.toString()+"]";
            
            return leftS+value.toString()+rightS;
        }
        
    }

}
