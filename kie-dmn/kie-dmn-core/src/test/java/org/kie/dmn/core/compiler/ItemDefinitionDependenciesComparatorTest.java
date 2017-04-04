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
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.model.v1_1.ItemDefinition;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;


public class ItemDefinitionDependenciesComparatorTest {
    
    private static final String TEST_NS = "https://www.drools.org/";
    private static final DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();

    private ItemDefinition build(String name, ItemDefinition... components) {
        ItemDefinition res = new ItemDefinition();
        res.setName(name);
        for ( ItemDefinition ic : components ) {
            ItemDefinition c = new ItemDefinition();
            c.setName("_" + name + "-" + ic.getName());
            c.setTypeRef(new QName(TEST_NS, ic.getName()));
            res.getItemComponent().add(c);
        }
        return res;
    }
    
    @Test
    public void testGeneric() {
        ItemDefinition a = build("a");
        
        ItemDefinition b = build("b");
        
        ItemDefinition c = build("c", a, b);
        
        ItemDefinition d = build("d", c);
        
        List<ItemDefinition> originalList = Arrays.asList(new ItemDefinition[]{d,c,b,a});
        
        List<ItemDefinition> orderedList = new ArrayList<>(originalList);
        orderedList.sort(new ItemDefinitionDependenciesComparator(TEST_NS));

        assertThat(orderedList, contains(a,b,c,d));
    }
    
    
}
