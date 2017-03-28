/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.util;

import java.util.ArrayList;
import java.util.List;

public class TokenTree {
    private Node root;
    private Node currentNode;

    public TokenTree() {
        root = new Node();
        root.children = new ArrayList<Node>();
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