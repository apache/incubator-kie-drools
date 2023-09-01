package org.kie.dmn.feel.util;

import java.util.ArrayList;
import java.util.List;

public class TokenTree {
    private Node root;
    private Node currentNode;

    public TokenTree() {
        root = new Node();
        root.children = new ArrayList<>();
    }

    public void addName( List<String> tokens ) {
        Node current = root;
        for( String t : tokens ) {
            Node next = findToken( current, t );
            if( next == null ) {
                next = new Node( t, current);
                current.children.add( next );
            }
            current = next;
        }
    }

    public void start( String t ) {
        currentNode = findToken( root, t );
    }

    public boolean followUp( String t, boolean commit ) {
        if( currentNode == null ) {
            // this happens when the start() call above does not
            // find a root token
            return false;
        }
        Node node = findToken( currentNode, t );
        if( commit ) {
            currentNode = node;
        }
        return node != null;
    }

    private Node findToken(Node current, String t) {
        for( Node n : current.children ) {
            if( n.token.equals( t ) ) {
                return n;
            }
        }
        return null;
    }

    private static class Node {
        public String      token;
        public Node       parent;
        public List<Node> children;

        public Node() {
            this.children = new ArrayList<>(  );
        }

        public Node(String token, Node parent) {
            this.token = token;
            this.parent = parent;
            this.children = new ArrayList<>(  );
        }
    }
}