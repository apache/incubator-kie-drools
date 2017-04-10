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

import java.util.Comparator;

import javax.xml.namespace.QName;

import org.kie.dmn.model.v1_1.ItemDefinition;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 *
 */
public class ItemDefinitionDependenciesComparator implements Comparator<ItemDefinition> {
    
    private final String modelNamespace;

    public ItemDefinitionDependenciesComparator(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }
    
    /**
     * Note: this comparator imposes orderings that are inconsistent with equals.
     */
    @Override
    public int compare(ItemDefinition o1, ItemDefinition o2) {
        QName qname1 = new QName(modelNamespace, o1.getName());
        QName qname2 = new QName(modelNamespace, o2.getName());
        if ( recurseFind(o1, qname2) ){
            return 1;
        } else if ( recurseFind(o2, qname1) ) {
            return -1;
        } else { 
            return 0;
        }
    }

    private boolean recurseFind(ItemDefinition o1, QName qname2) {
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

}
