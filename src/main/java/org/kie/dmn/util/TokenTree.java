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

package org.kie.dmn.util;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class TokenTree {
    private Node root;
    private Node currentNode;

    public TokenTree() {
        root = new Node();
        root.children = new ArrayList<Node>();
    }

    public void addName( List<Token> tokens ) {
        System.out.println(" ++ tokens = "+tokens);
        Node current = root;
        for( Token t : tokens ) {
            Node next = findToken( current, t );
            if( next == null ) {
                next = new Node( t, current);
                root.children.add( next );
            }
            current = next;
        }
    }

    public void start( Token t ) {
        currentNode = findToken( root, t );
    }

    public boolean followUp( Token t ) {
        if( currentNode == null ) {
            // this happens when the start() call above does not
            // find a root token
            return false;
        }
        currentNode = findToken( currentNode, t );
        return currentNode != null;
    }

    private Node findToken(Node current, Token t) {
        for( Node n : current.children ) {
            if( n.token.getType() == t.getType() && n.token.getText().equals( t.getText() ) ) {
                return n;
            }
        }
        return null;
    }

    private static class Node {
        public Token      token;
        public Node       parent;
        public List<Node> children;

        public Node() {
            this.children = new ArrayList<>(  );
        }

        public Node(Token token, Node parent) {
            this.token = token;
            this.parent = parent;
            this.children = new ArrayList<>(  );
        }
    }
}