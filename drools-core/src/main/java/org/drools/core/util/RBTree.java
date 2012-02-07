package org.drools.core.util;

import java.util.Arrays;

import org.junit.Test;

public class RBTree<K extends Comparable< ? super K>, V>
{

    public static final boolean VERIFY_RBTREE = true;
    private static final int    INDENT_STEP   = 4;

    public Node<K, V>           root;

    public RBTree() {
        root = null;
        verifyProperties();
    }

    public void verifyProperties() {
        if ( VERIFY_RBTREE ) {
            verifyProperty1( root );
            verifyProperty2( root );
            // Property 3 is implicit
            verifyProperty4( root );
            verifyProperty5( root );
        }
    }

    private static void verifyProperty1(Node< ? , ? > n) {
        assert nodeColor( n ) == Color.RED || nodeColor( n ) == Color.BLACK;
        if ( n == null ) return;
        verifyProperty1( n.left );
        verifyProperty1( n.right );
    }

    private static void verifyProperty2(Node< ? , ? > root) {
        assert nodeColor( root ) == Color.BLACK;
    }

    private static Color nodeColor(Node< ? , ? > n) {
        return n == null ? Color.BLACK : n.color;
    }

    private static void verifyProperty4(Node< ? , ? > n) {
        if ( nodeColor( n ) == Color.RED ) {
            assert nodeColor( n.left ) == Color.BLACK;
            assert nodeColor( n.right ) == Color.BLACK;
            assert nodeColor( n.parent ) == Color.BLACK;
        }
        if ( n == null ) return;
        verifyProperty4( n.left );
        verifyProperty4( n.right );
    }

    private static void verifyProperty5(Node< ? , ? > root) {
        verifyProperty5Helper( root, 0, -1 );
    }

    private static int verifyProperty5Helper(Node< ? , ? > n,
                                             int blackCount,
                                             int pathBlackCount) {
        if ( nodeColor( n ) == Color.BLACK ) {
            blackCount++;
        }
        if ( n == null ) {
            if ( pathBlackCount == -1 ) {
                pathBlackCount = blackCount;
            } else {
                assert blackCount == pathBlackCount;
            }
            return pathBlackCount;
        }
        pathBlackCount = verifyProperty5Helper( n.left, blackCount, pathBlackCount );
        pathBlackCount = verifyProperty5Helper( n.right, blackCount, pathBlackCount );
        return pathBlackCount;
    }

    private Node<K, V> lookupNode(K key) {
        Node<K, V> n = root;
        while ( n != null ) {
            int compResult = key.compareTo( n.key );
            if ( compResult == 0 ) {
                return n;
            } else if ( compResult < 0 ) {
                n = n.left;
            } else {
                assert compResult > 0;
                n = n.right;
            }
        }
        return n;
    }

    public enum Boundary {
        LOWER, UPPER
    };

    public static class RangeFastIterator<K extends Comparable< ? super K>, V>
            implements
            FastIterator {
        private Node<K, V> lowerBound;
        private Node<K, V> upperBound;
        private Node<K, V> next;

        final static int   LEFT  = 0;
        final static int   RIGHT = 1;
        final static int   UP    = 2;
        final static int   DONE  = 3;

        private int[]      stack;
        private int        depth;

        private boolean    leftV;

        private int        direction;

        public RangeFastIterator(Node<K, V> lowerNearest,
                                 Node<K, V> upperNearest) {
            super();
            this.lowerBound = lowerNearest;
            this.upperBound = upperNearest;
            this.next = lowerNearest;
            this.direction = LEFT;
            this.stack = new int[10];
            depth = 0;
            this.leftV = true;
            this.stack[depth] = LEFT;
        }

        public Entry next(Entry object) {
            Entry temp = next;

            if ( next != null ) {
                if ( direction == DONE) {
                    next = null;
                } else {
                    recurse();
                }
            }

            return temp;
        }

        public boolean isFullIterator() {
            // TODO Auto-generated method stub
            return false;
        }

        public void recurse() {

            if ( depth == stack.length - 1 ) {
                // increase the stack if we have used up all space
                stack = new int[depth * 3 ];
                stack = Arrays.copyOf( stack, stack.length * 3 );
            }
            
            if ( direction == LEFT ) {
                
                // LEFT
                // We can only go left if it is > lowerBound
                if ( next.left != null ) {
                    if ( leftV ) {
                        int compResult = lowerBound.key.compareTo( next.left.key );
                        if ( compResult < 0 ) {
                            // I't above lowerBound, recurse until below upperBound
                            compResult = upperBound.key.compareTo( next.left.key );
                            if ( compResult < 0 ) {
                                next = next.left;
                                depth++;                                 
                                recurse();
                                return;
                            }  else {                          
                                next = next.left;
                                depth++;
                                return;
                            }
                        }                         
                    } else {
                        // Keep going left until we are below upperBound
                        next = next.left;
                        depth++;                        
                        int compResult = upperBound.key.compareTo( next.key );
                        if ( compResult < 0 ) {
                            recurse();
                        }  
                        return;
                    }
                }
                // try right
                direction = RIGHT;
                recurse();
                return;
            } else if ( direction == RIGHT ) {
                // RIGHT
                if ( next.right != null ) {                    
                    
                    // can't go left, so try going right, as long as it's equal to or below the upper bound
                    int compResult = upperBound.key.compareTo( next.right.key );
                    if ( compResult >= 0 ) {
                        // don't end if it == 0, as we still need to include lessthan children
                        // return this node and attempt left later
                        stack[depth] = RIGHT; // reset current to RIGHT
                        next = next.right;
                        direction = LEFT;
                        depth++;                        
                        return;
                    } else {
                        next = next.right;
                        stack[depth] = RIGHT; // reset current to RIGHT
                        direction = LEFT;
                        depth++;
                        recurse();
                        return;
                    }
                } else {
                    // At leaf, so set RIGHT as done, so we can go up correctly
                    stack[depth] = RIGHT;
                    if ( next == upperBound ) {
                        // we've arrived at upper bound, so finish
                        direction = DONE;
                        next = null;
                        return;
                    }                       
                }
                direction = UP;                             
                recurse();
                return;
            } else if ( direction == UP ) {
                // UP
                if ( depth != 0 ) {
                    stack[depth] = LEFT; // reset stack position, before going back up
                    depth--; // go up one
                    if ( stack[depth] == LEFT ) {
                        next = next.parent;
                        // First check if we've reached our upper bound
                        if ( next == upperBound ) {
                            // we've arrived back at upper bound, so finish
                            direction = DONE;
                            next = null;
                            return;
                        }
                        
                        // we haven't done right yet, so try that
                        direction = RIGHT;
                        recurse(); // we know this one has been done, so got straight to right
                        return;
                    } else {
                        while ( depth >= 0 && stack[depth] == RIGHT ) {
                            // keep going up while the parent has had it's right tried
                            next = next.parent;
                            // we've arrived back at upper bound, so finish
                            if ( next == upperBound ) {
                                // we've arrived at upper bound, so finish
                                direction = DONE;
                                next = null;
                                return;
                            }                            
                            stack[depth] = LEFT; // reset stack position, before going back up                            
                            depth--;
                        }
                        if ( depth == -1 ) {
                            leftV = false;                         
                            
                            // back to stack start, move stack up one if from left, two if from the right   
                            if ( next.parent.key.compareTo( next.key ) < 0 ) {
                                // came up from the right
                                next = next.parent;
                            }
                            
                            direction = UP;                            
                            depth = 0;  
                            recurse();
                            return;
                        } else {
                            next = next.parent;     
                            // we've arrived at upper bound, so finish
                            if ( next == upperBound ) {
                                // we've arrived at upper bound, so finish
                                direction = DONE;
                                next = null;
                                return;
                            }
                            
                            // at position where right hasn't been tried, so try right
                            direction = RIGHT;
                            stack[depth] = direction;
                            recurse();                            
                        } 
                        return;
                    }
                } else {
                    next = next.parent;
                    leftV = false;                    
                    
                     // If we went left up, we'll need check lower bounds and go up again
                     int compResult = lowerBound.key.compareTo( next.key);
                     if ( compResult > 0 ) {
                        // under lower bounds, keep going up
                        recurse();
                    } else {
                        direction = RIGHT;
                        stack[depth] = direction;
                        return;                        
                    }
                }                
            }
        }     
    }

    public FastIterator range(K lowerBound,
                              boolean testLowerEqual,
                              K upperBound,
                              boolean testUpperEqual) {
        Node<K, V> lowerNearest = findNearestNode( lowerBound, testLowerEqual, Boundary.LOWER );
        Node<K, V> upperNearest = findNearestNode( upperBound, testUpperEqual, Boundary.UPPER );        
        
        if ( lowerNearest.key.compareTo( upperNearest.key  ) > 0 ) {
            upperNearest = lowerNearest;
        }        

        return new RangeFastIterator( lowerNearest, upperNearest );

    }

    public void rangeUperBounded(K upperBound,
                                 boolean testUpperEqual) {
        Node<K, V> upperNearest = findNearestNode( upperBound, testUpperEqual, Boundary.UPPER );
    }

    public void rangeLowerBounded(K upperBound,
                                  boolean testUpperEqual) {
        Node<K, V> upperNearest = findNearestNode( upperBound, testUpperEqual, Boundary.UPPER );
    }

    public Node<K, V> findNearestNode(K key,
                                      boolean testEqual,
                                      Boundary boundary) {
        Node<K, V> n = root;

        boolean bounded = false;
        int compResult = 0;
        int lastResult = 0;
        Node<K, V> nearest = null;
        while ( n != null ) {
            compResult = key.compareTo( n.key );
            if ( testEqual && compResult == 0 ) {
                nearest = n;
                break;
            } else if ( compResult < 0 ) {
                if ( lastResult > 0 ) {
                    bounded = true;
                }
                nearest = n;
                n = n.left;
            } else {// assert compResult > 0;
                if ( lastResult < 0 ) {
                    bounded = true;
                }
                nearest = n;
                n = n.right;
            }
            lastResult = compResult;
        }

        if ( bounded ) {
            if ( boundary == Boundary.UPPER ) {
                while ( compResult < 0 ) {
                    nearest = nearest.parent;
                    compResult = key.compareTo( nearest.key );
                }
            } else {
                while ( compResult > 0 ) {
                    nearest = nearest.parent;
                    compResult = key.compareTo( nearest.key );
                }
            }
        }

        return nearest;
    }

    public V lookup(K key) {
        Node<K, V> n = lookupNode( key );
        return n == null ? null : n.value;
    }

    private void rotateLeft(Node<K, V> n) {
        Node<K, V> r = n.right;
        replaceNode( n, r );
        n.right = r.left;
        if ( r.left != null ) {
            r.left.parent = n;
        }
        r.left = n;
        n.parent = r;
    }

    private void rotateRight(Node<K, V> n) {
        Node<K, V> l = n.left;
        replaceNode( n, l );
        n.left = l.right;
        if ( l.right != null ) {
            l.right.parent = n;
        }
        l.right = n;
        n.parent = l;
    }

    private void replaceNode(Node<K, V> oldn,
                             Node<K, V> newn) {
        if ( oldn.parent == null ) {
            root = newn;
        } else {
            if ( oldn == oldn.parent.left ) oldn.parent.left = newn;
            else oldn.parent.right = newn;
        }
        if ( newn != null ) {
            newn.parent = oldn.parent;
        }
    }

    public void insert(K key,
                       V value) {
        Node<K, V> insertedNode = new Node<K, V>( key, value, Color.RED, null, null );
        if ( root == null ) {
            root = insertedNode;
        } else {
            Node<K, V> n = root;
            while ( true ) {
                int compResult = key.compareTo( n.key );
                if ( compResult == 0 ) {
                    n.value = value;
                    return;
                } else if ( compResult < 0 ) {
                    if ( n.left == null ) {
                        n.left = insertedNode;
                        break;
                    } else {
                        n = n.left;
                    }
                } else {
                    assert compResult > 0;
                    if ( n.right == null ) {
                        n.right = insertedNode;
                        break;
                    } else {
                        n = n.right;
                    }
                }
            }
            insertedNode.parent = n;
        }
        insertCase1( insertedNode );
        verifyProperties();
    }

    private void insertCase1(Node<K, V> n) {
        if ( n.parent == null ) n.color = Color.BLACK;
        else insertCase2( n );
    }

    private void insertCase2(Node<K, V> n) {
        if ( nodeColor( n.parent ) == Color.BLACK ) return; // Tree is still valid
        else insertCase3( n );
    }

    void insertCase3(Node<K, V> n) {
        if ( nodeColor( n.uncle() ) == Color.RED ) {
            n.parent.color = Color.BLACK;
            n.uncle().color = Color.BLACK;
            n.grandparent().color = Color.RED;
            insertCase1( n.grandparent() );
        } else {
            insertCase4( n );
        }
    }

    void insertCase4(Node<K, V> n) {
        if ( n == n.parent.right && n.parent == n.grandparent().left ) {
            rotateLeft( n.parent );
            n = n.left;
        } else if ( n == n.parent.left && n.parent == n.grandparent().right ) {
            rotateRight( n.parent );
            n = n.right;
        }
        insertCase5( n );
    }

    void insertCase5(Node<K, V> n) {
        n.parent.color = Color.BLACK;
        n.grandparent().color = Color.RED;
        if ( n == n.parent.left && n.parent == n.grandparent().left ) {
            rotateRight( n.grandparent() );
        } else {
            assert n == n.parent.right && n.parent == n.grandparent().right;
            rotateLeft( n.grandparent() );
        }
    }

    public void delete(K key) {
        Node<K, V> n = lookupNode( key );
        if ( n == null ) return; // Key not found, do nothing
        if ( n.left != null && n.right != null ) {
            // Copy key/value from predecessor and then delete it instead
            Node<K, V> pred = maximumNode( n.left );
            n.key = pred.key;
            n.value = pred.value;
            n = pred;
        }

        assert n.left == null || n.right == null;
        Node<K, V> child = (n.right == null) ? n.left : n.right;
        if ( nodeColor( n ) == Color.BLACK ) {
            n.color = nodeColor( child );
            deleteCase1( n );
        }
        replaceNode( n, child );

        if ( nodeColor( root ) == Color.RED ) {
            root.color = Color.BLACK;
        }

        verifyProperties();
    }

    private static <K extends Comparable< ? super K>, V> Node<K, V> maximumNode(Node<K, V> n) {
        assert n != null;
        while ( n.right != null ) {
            n = n.right;
        }
        return n;
    }

    private void deleteCase1(Node<K, V> n) {
        if ( n.parent == null ) return;
        else deleteCase2( n );
    }

    private void deleteCase2(Node<K, V> n) {
        if ( nodeColor( n.sibling() ) == Color.RED ) {
            n.parent.color = Color.RED;
            n.sibling().color = Color.BLACK;
            if ( n == n.parent.left ) rotateLeft( n.parent );
            else rotateRight( n.parent );
        }
        deleteCase3( n );
    }

    private void deleteCase3(Node<K, V> n) {
        if ( nodeColor( n.parent ) == Color.BLACK &&
             nodeColor( n.sibling() ) == Color.BLACK &&
             nodeColor( n.sibling().left ) == Color.BLACK &&
             nodeColor( n.sibling().right ) == Color.BLACK )
        {
            n.sibling().color = Color.RED;
            deleteCase1( n.parent );
        }
        else deleteCase4( n );
    }

    private void deleteCase4(Node<K, V> n) {
        if ( nodeColor( n.parent ) == Color.RED &&
             nodeColor( n.sibling() ) == Color.BLACK &&
             nodeColor( n.sibling().left ) == Color.BLACK &&
             nodeColor( n.sibling().right ) == Color.BLACK )
        {
            n.sibling().color = Color.RED;
            n.parent.color = Color.BLACK;
        }
        else deleteCase5( n );
    }

    private void deleteCase5(Node<K, V> n) {
        if ( n == n.parent.left &&
             nodeColor( n.sibling() ) == Color.BLACK &&
             nodeColor( n.sibling().left ) == Color.RED &&
             nodeColor( n.sibling().right ) == Color.BLACK )
        {
            n.sibling().color = Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight( n.sibling() );
        }
        else if ( n == n.parent.right &&
                  nodeColor( n.sibling() ) == Color.BLACK &&
                  nodeColor( n.sibling().right ) == Color.RED &&
                  nodeColor( n.sibling().left ) == Color.BLACK )
        {
            n.sibling().color = Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft( n.sibling() );
        }
        deleteCase6( n );
    }

    private void deleteCase6(Node<K, V> n) {
        n.sibling().color = nodeColor( n.parent );
        n.parent.color = Color.BLACK;
        if ( n == n.parent.left ) {
            assert nodeColor( n.sibling().right ) == Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft( n.parent );
        }
        else
        {
            assert nodeColor( n.sibling().left ) == Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight( n.parent );
        }
    }

    public void print() {
        printHelper( root, 0 );
    }

    private static void printHelper(Node< ? , ? > n,
                                    int indent) {
        if ( n == null ) {
            System.out.print( "<empty tree>" );
            return;
        }

        if ( n.right != null ) {
            printHelper( n.right, indent + INDENT_STEP );
        }

        for ( int i = 0; i < indent; i++ ) {
            System.out.print( " " );
        }

        if ( n.color == Color.BLACK ) {
            System.out.println( n.key );
        } else {
            System.out.println( "<" + n.key + ">" );
        }

        if ( n.left != null ) {
            printHelper( n.left, indent + INDENT_STEP );
        }
    }

    public enum Color {
        RED, BLACK
    }

    public static class Node<K extends Comparable< ? super K>, V>
            implements
            Entry
    {
        public K          key;
        public V          value;
        public Node<K, V> left;
        public Node<K, V> right;
        public Node<K, V> parent;
        public Color      color;

        public Node(K key,
                    V value,
                    Color nodeColor,
                    Node<K, V> left,
                    Node<K, V> right) {
            this.key = key;
            this.value = value;
            this.color = nodeColor;
            this.left = left;
            this.right = right;
            if ( left != null ) left.parent = this;
            if ( right != null ) right.parent = this;
            this.parent = null;
        }

        public Node<K, V> grandparent() {
            assert parent != null; // Not the root node
            assert parent.parent != null; // Not child of root
            return parent.parent;
        }

        public Node<K, V> sibling() {
            assert parent != null; // Root node has no sibling
            if ( this == parent.left ) return parent.right;
            else return parent.left;
        }

        public Node<K, V> uncle() {
            assert parent != null; // Root node has no uncle
            assert parent.parent != null; // Children of root have no uncle
            return parent.sibling();
        }

        public String toString() {
            return "Node key=" + key + " value=" + value;
        }

        public void setNext(Entry next) {
            // TODO Auto-generated method stub

        }

        public Entry getNext() {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
