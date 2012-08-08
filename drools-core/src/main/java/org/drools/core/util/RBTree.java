/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @see http://en.literateprograms.org/Red-black_tree_%28Java%29
 */
public class RBTree<K extends Comparable< ? super K>, V> implements Serializable {

    public static final boolean VERIFY_RBTREE = false;
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
                n = n.right;
            }
        }
        return n;
    }

    public enum Boundary {
        LOWER, UPPER
    }

    public static class RBTreeFastIterator<K extends Comparable< ? super K>, V> implements FastIterator {
        private Node<K, V> upperBound;
        private Node<K, V> next;

        public RBTreeFastIterator(Node<K, V> lowerBound, Node<K, V> upperBound) {
            this.next = lowerBound;
            this.upperBound = upperBound;
        }

        public Entry next(Entry object) {
            Node<K, V> temp = next;
            next = checkUpperBound( recurse( next ) );
            return temp;
        }

        public boolean isFullIterator() {
            return false;
        }

        private Node<K, V> recurse(Node<K, V> current) {
            if (current == null) {
                return null;
            }

            if (current.right != null) {
                Node<K, V> p = current.right;
                while (p.left != null) {
                    p = p.left;
                }
                return p;
            }

            Node<K, V> p = current.parent;
            Node<K, V> ch = current;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }

        public Node<K, V> checkUpperBound(Node<K, V> current) {
            if (upperBound == null) {
                return current;
            }
            return current == null || current.compareTo(upperBound) > 0 ? null : current;
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public Node<K, V> first() {
        if (root == null) {
            return null;
        }
        Node<K, V> n = root;
        while (n.left != null) {
            n = n.left;
        }
        return n;
    }

    public Node<K, V> last() {
        if (root == null) {
            return null;
        }
        Node<K, V> n = root;
        while (n.right != null) {
            n = n.right;
        }
        return n;
    }

    public FastIterator fastIterator() {
        return root == null ? FastIterator.EMPTY : new RBTreeFastIterator( first(), null );
    }

    public FastIterator range(K lowerBound,
                              boolean testLowerEqual,
                              K upperBound,
                              boolean testUpperEqual) {
        Node<K, V> lowerNearest = findNearestNode( lowerBound, testLowerEqual, Boundary.LOWER );
        Node<K, V> upperNearest = findNearestNode( upperBound, testUpperEqual, Boundary.UPPER );

        if ( lowerNearest == null || upperNearest == null ) {
            return FastIterator.EMPTY;
        }

        if ( lowerNearest.key.compareTo( upperNearest.key  ) > 0 ) {
            upperNearest = lowerNearest;
        }

        return new RBTreeFastIterator( lowerNearest, upperNearest );
    }

    public Node<K, V> findNearestNode(K key, boolean allowEqual, Boundary boundary) {
        Node<K, V> nearest = null;
        Node<K, V> n = root;

        while ( n != null ) {
            int compResult = key.compareTo( n.key );
            if ( allowEqual && compResult == 0 ) {
                return n;
            }

            boolean accepted = acceptNode(compResult, boundary);
            if ( acceptNode( compResult, boundary ) && ( nearest == null || acceptNode( n.key.compareTo( nearest.key ), boundary ) ) ) {
                nearest = n;
            }

            if ( compResult == 0 ) {
                n = boundary == Boundary.LOWER ? n.right : n.left;
            } else {
                n = accepted ^ boundary == Boundary.LOWER ? n.right : n.left;
            }
        }

        return nearest;
    }

    private boolean acceptNode(int compResult, Boundary boundary) {
        return compResult != 0 && ( compResult > 0 ^ boundary == Boundary.LOWER );
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
        insertCase5(n);
    }

    void insertCase5(Node<K, V> n) {
        n.parent.color = Color.BLACK;
        n.grandparent().color = Color.RED;
        if ( n == n.parent.left && n.parent == n.grandparent().left ) {
            rotateRight( n.grandparent() );
        } else {
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
            n.sibling().right.color = Color.BLACK;
            rotateLeft( n.parent );
        }
        else
        {
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

    public static class Node<K extends Comparable< ? super K>, V> implements Entry, Comparable<Node<K, V>> {
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
            return parent.parent;
        }

        public Node<K, V> sibling() {
            if ( this == parent.left ) return parent.right;
            else return parent.left;
        }

        public Node<K, V> uncle() {
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

        public int compareTo(Node<K, V> other) {
            return key.compareTo(other.key);
        }
    }

}
