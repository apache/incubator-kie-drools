/*
 * Copyright 2013 JBoss Inc
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

package org.drools.util;

import java.util.*;

public class HierarchySorter<K> {


    public List<K> sort( Map<K,Collection<K>> hierarchy ) {
        Node<K,K> root = new Node<K,K>( null );
        Map<K, Node<K,K>> map = new HashMap<K, Node<K,K>>();
        for ( K element : hierarchy.keySet() ) {
            K key = element;

            Node<K,K> node = map.get( key );
            if ( node == null ) {
                node = new Node( key,
                        element );
                map.put( key,
                        node );
            } else if ( node.getData() == null ) {
                node.setData( element );
            }
            Collection<K> px = hierarchy.get( key );
            if ( px.isEmpty() ) {
                root.addChild( node );
            } else {
                for ( K parentElement : px ) {

                    K superKey = parentElement;

                    Node<K,K> superNode = map.get( superKey );
                    if ( superNode == null ) {
                        superNode = new Node<K,K>( superKey );
                        map.put( superKey,
                                superNode );
                    }
                    if ( ! superNode.children.contains( node ) ) {
                        superNode.addChild( node );
                    }
                }

            }

        }

        Iterator<Node<K,K>> iter = map.values().iterator();
        while ( iter.hasNext() ) {
            Node<K,K> n = iter.next();
            if ( n.getData() == null ) root.addChild( n );

        }

        List<K> sortedList = new LinkedList<K>();
        root.accept( sortedList );

        return sortedList;
    }

    /**
     * Utility class for the sorting algorithm
     *
     * @param <T>
     */
    private static class Node<K,T> {
        private K        key;
        private T             data;
        private List<Node<K,T>> children;

        public Node(K key) {
            this.key = key;
            this.children = new LinkedList<Node<K,T>>();
        }

        public Node(K key,
                    T content) {
            this( key );
            this.data = content;
        }

        public void addChild(Node<K,T> child) {
            this.children.add( child );
        }

        public List<Node<K,T>> getChildren() {
            return children;
        }

        public K getKey() {
            return key;
        }

        public T getData() {
            return data;
        }

        public void setData(T content) {
            this.data = content;
        }

        public void accept(List<T> list) {
            if ( this.data != null ) {
                if ( list.contains( this.data ) ) {
                    list.remove( this.data );
                }
                list.add( this.data );
            }

            for ( int j = 0; j < children.size(); j++ ) {
                if ( children.get( j ) != this ) {
                    children.get( j ).accept( list );
                }
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key='" + key + '\'' +
                    '}';
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            Node node = (Node) o;

            if ( !key.equals( node.key ) ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}
