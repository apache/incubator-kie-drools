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

import org.drools.core.util.HierNode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

//public class CodedHierarchyImpl<T> extends AbstractCodedHierarchyImpl<T> implements CodedHierarchy<T>, Externalizable {
public class CodedHierarchyImpl<T> extends AbstractCodedHierarchyImpl<T> implements Externalizable {

    protected transient Map<T, HierNode<T>> cache = new HashMap<T, HierNode<T>>();

    protected HierNode<T> getNode( T name ) {
        return cache.get( name );
    }

    protected HierNode<T> getNode( HierNode<T> name ) {
        return cache.get( name );
    }

    protected void add( HierNode<T> node ) {
        super.add( node );
        cache.put( node.getValue(), node );
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("*****************************************\n");

        int len = 0;
        for ( HierNode<T> node : getNodes() ) {
            len = Math.max( len, numBit( node.getBitMask() ) );
        }

        for ( HierNode<T> node : getNodes() ) {
            builder.append( node.toString( len ) ).append("\n");
        }
        builder.append( "*****************************************\n" );
        builder.append( getSortedMap() ).append("\n");
        builder.append("*****************************************\n");
        return builder.toString();
    }


}
