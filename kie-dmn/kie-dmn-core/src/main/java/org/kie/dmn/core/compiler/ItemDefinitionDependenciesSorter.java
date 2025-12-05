/*
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

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNVersion;
import org.kie.dmn.model.api.ItemDefinition;

public class ItemDefinitionDependenciesSorter {
    
    private final String modelNamespace;

    public ItemDefinitionDependenciesSorter(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }
    
    /**
     * Return a new list of ItemDefinition sorted by dependencies (required dependencies comes first)
     * @param itemDefinitions list of ItemDefinitions available in the model
     * @param dmnVersion version of dmn in the current model
     */
    public List<ItemDefinition> sort(List<ItemDefinition> itemDefinitions, DMNVersion dmnVersion) {
        // In a graph A -> B -> {C, D}
        // showing that A requires B, and B requires C,D
        // then a depth-first visit would satisfy required ordering, for example a valid depth first visit is also a valid sort here: C, D, B, A.
        Collection<ItemDefinition> visited = new ArrayList<>(itemDefinitions.size());
        List<ItemDefinition> sortedItemDefinitions = new ArrayList<>(itemDefinitions.size());

        for (ItemDefinition node : itemDefinitions) {
            if (!visited.contains(node)) {
                populateSortedItemDefinitions(node, itemDefinitions, visited, sortedItemDefinitions, dmnVersion);
            }
        }

        return sortedItemDefinitions;
    }
        
    /**
     * Performs a depth first visit, but keeping a separate reference of visited/visiting nodes, _also_ to avoid potential issues of circularities.
     */
    private void populateSortedItemDefinitions(ItemDefinition node, List<ItemDefinition> allNodes, Collection<ItemDefinition> visited, List<ItemDefinition> sortedItemDefinitions, DMNVersion dmnVersion) {
        visited.add(node);
        List<ItemDefinition> neighbours = allNodes.stream()
                                                  .filter(n -> !n.getName().equals(node.getName())) // filter out `node`
                                                  .filter(n -> recurseFind(node, new QName(modelNamespace, n.getName()), modelNamespace, dmnVersion)) // I pick from allNodes, those referenced by this `node`. Only neighbours of `node`, because N is referenced by NODE.
                                                  .toList();
        for (ItemDefinition n : neighbours) {
            if (!visited.contains(n)) {
                populateSortedItemDefinitions(n, allNodes, visited, sortedItemDefinitions, dmnVersion);
            }
        }

        sortedItemDefinitions.add(node);
    }

    private static boolean recurseFind(ItemDefinition o1, QName qname2, String modelNamespace, DMNVersion dmnVersion) {
        QName typeRef = retrieveTypeRef(o1, modelNamespace, dmnVersion);
        return (typeRef != null)
                ? matchesQNameUsingNamespacePrefixes(o1, typeRef, qname2)
                : o1.getItemComponent().stream()
                .anyMatch(component -> recurseFind(component, qname2, modelNamespace, dmnVersion));
    }

    static QName retrieveTypeRef(ItemDefinition o1, String  modelNamespace, DMNVersion dmnVersion) {
        QName toReturn = o1.getTypeRef();
        if (toReturn == null
                && dmnVersion.getDmnVersion() > DMNVersion.V1_2.getDmnVersion()
                && o1.getFunctionItem() != null) {
            toReturn = o1.getFunctionItem().getOutputTypeRef();
            if (toReturn != null && toReturn.getNamespaceURI().isEmpty()) {
                toReturn = new QName(modelNamespace, toReturn.getLocalPart());
            }
        }
        return toReturn;
    }

    private static boolean matchesQNameUsingNamespacePrefixes(ItemDefinition o1, QName typeRef, QName qname2) {
        if (typeRef.equals(qname2)) {
            return true;
        }
        if (typeRef.getLocalPart().endsWith(qname2.getLocalPart())) {
            for (String nsKey : o1.recurseNsKeys()) {
                String ns = o1.getNamespaceURI(nsKey);
                if (ns == null || !ns.equals(qname2.getNamespaceURI())) {
                    continue;
                }
                String prefix = nsKey + ".";
                if (typeRef.getLocalPart().startsWith(prefix) &&
                        typeRef.getLocalPart().replace(prefix, "").equals(qname2.getLocalPart())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean directFind(ItemDefinition o1, QName qname2) {
        if ( o1.getTypeRef() != null ) {
            return matchesQNameUsingNamespacePrefixes(o1, o1.getTypeRef(), qname2);
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

    public static void displayDependencies(List<ItemDefinition> ins, String namespaceURI, DMNVersion dmnVersion) {
        for ( ItemDefinition in : ins ) {
            System.out.println(in.getName());
            List<ItemDefinition> others = new ArrayList<>(ins);
            others.remove(in);
            for ( ItemDefinition other : others ) {
                QName otherQName = new QName(namespaceURI, other.getName());
                if ( directFind(in, otherQName) ) {
                    System.out.println(" direct depends on: "+other.getName());
                } else if ( recurseFind(in, otherQName, namespaceURI, dmnVersion) ) {
                    System.out.println(" indir. depends on: "+other.getName());
                }
            }
        }
        
    }

}