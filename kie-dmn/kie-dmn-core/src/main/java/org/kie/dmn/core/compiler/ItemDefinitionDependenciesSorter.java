/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.ItemDefinition;

public class ItemDefinitionDependenciesSorter {
    
    private final String modelNamespace;

    public ItemDefinitionDependenciesSorter(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }
    
    /**
     * Return a new list of ItemDefinition sorted by dependencies (required dependencies comes first)
     */
    public List<ItemDefinition> sort(List<ItemDefinition> ins) {
        // In a graph A -> B -> {C, D}
        // showing that A requires B, and B requires C,D
        // then a depth-first visit would satisfy required ordering, for example a valid depth first visit is also a valid sort here: C, D, B, A.
        Collection<ItemDefinition> visited = new ArrayList<>(ins.size());
        List<ItemDefinition> dfv = new ArrayList<>(ins.size());

        for (ItemDefinition node : ins) {
            if (!visited.contains(node)) {
                dfVisit(node, ins, visited, dfv);
            }
        }

        return dfv;
    }
        
    /**
     * Performs a depth first visit, but keeping a separate reference of visited/visiting nodes, _also_ to avoid potential issues of circularities.
     */
    private void dfVisit(ItemDefinition node, List<ItemDefinition> allNodes, Collection<ItemDefinition> visited, List<ItemDefinition> dfv) {
        visited.add(node);

        List<ItemDefinition> neighbours = allNodes.stream()
                                                  .filter(n -> !n.getName().equals(node.getName())) // filter out `node`
                                                  .filter(n -> recurseFind(node, new QName(modelNamespace, n.getName()))) // I pick from allNodes, those referenced by this `node`. Only neighbours of `node`, because N is referenced by NODE.
                                                  .collect(Collectors.toList());
        for (ItemDefinition n : neighbours) {
            if (!visited.contains(n)) {
                dfVisit(n, allNodes, visited, dfv);
            }
        }

        dfv.add(node);
    }
    
    private static boolean recurseFind(ItemDefinition o1, QName qname2) {
        if ( o1.getTypeRef() != null ) {
            return extFastEqUsingNSPrefix(o1, qname2);
        }
        for ( ItemDefinition ic : o1.getItemComponent() ) {
            if ( recurseFind(ic, qname2) ) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean extFastEqUsingNSPrefix(ItemDefinition o1, QName qname2) {
        if (o1.getTypeRef().equals(qname2)) {
            return true;
        }
        if (o1.getTypeRef().getLocalPart().endsWith(qname2.getLocalPart())) {
            for (String nsKey : o1.recurseNsKeys()) {
                String ns = o1.getNamespaceURI(nsKey);
                if (ns == null || !ns.equals(qname2.getNamespaceURI())) {
                    continue;
                }
                String prefix = nsKey + ".";
                if (o1.getTypeRef().getLocalPart().startsWith(prefix) &&
                    o1.getTypeRef().getLocalPart().replace(prefix, "").equals(qname2.getLocalPart())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean directFind(ItemDefinition o1, QName qname2) {
        if ( o1.getTypeRef() != null ) {
            return extFastEqUsingNSPrefix(o1, qname2);
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
