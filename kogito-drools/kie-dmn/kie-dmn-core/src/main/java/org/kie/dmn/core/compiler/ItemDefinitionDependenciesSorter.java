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

package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.dmn.model.v1_1.ItemDefinition;

public class ItemDefinitionDependenciesSorter {
    
    private final String modelNamespace;

    public ItemDefinitionDependenciesSorter(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }
    
    /**
     * Return a new list of ItemDefinition sorted by dependencies (required dependencies comes first)
     */
    public List<ItemDefinition> sort(List<ItemDefinition> ins) {
        // this method approximates a topological sort. 
        
        List<ItemDefinition> todos = new ArrayList<>(ins);
        List<ItemDefinition> ordered = new ArrayList<>(ins.size());
        
        while ( todos.size() > 0 ) {
            ItemDefinition c1 = todos.get(0);
            for ( int i = 1; i < todos.size(); i++) {
                ItemDefinition other = todos.get(i);
                QName otherQName = new QName(modelNamespace, other.getName());
                if ( recurseFind(c1, otherQName) ) {
                    c1 = other;
                }
            }
            ordered.add(c1);
            todos.remove(c1);
        }
        return ordered;
    }
    
    private static boolean recurseFind(ItemDefinition o1, QName qname2) {
        if ( o1.getTypeRef() != null ) {
            if ( o1.getTypeRef().equals(qname2) ) {
                return true;
            }
        }
        for ( ItemDefinition ic : o1.getItemComponent() ) {
            if ( recurseFind(ic, qname2) ) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean directFind(ItemDefinition o1, QName qname2) {
        if ( o1.getTypeRef() != null ) {
            if ( o1.getTypeRef().equals(qname2) ) {
                return true;
            }
        }
        for ( ItemDefinition ic : o1.getItemComponent() ) {
            if ( ic.getTypeRef() == null ) {
                // anon inner type
                if ( directFind(ic, qname2) ) {
                    return true;
                }
            } else if ( ic.getTypeRef().equals(qname2) ) {
                return true;
            }
        }
        return false;
    }
    
    public static void displayDependencies(List<ItemDefinition> ins, String namespaceURI) {
        for ( ItemDefinition in : ins ) {
            System.out.println(in.getName());
            List<ItemDefinition> others = new ArrayList<>(ins);
            others.remove(in);
            for ( ItemDefinition other : others ) {
                QName otherQName = new QName(namespaceURI, other.getName());
                if ( directFind(in, otherQName) ) {
                    System.out.println(" direct depends on: "+other.getName());
                } else if ( recurseFind(in, otherQName) ) {
                    System.out.println(" indir. depends on: "+other.getName());
                }
            }
        }
        
    }

}
