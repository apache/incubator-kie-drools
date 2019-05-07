/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.rule.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.models.datamodel.rule.RuleModel.DEFAULT_TYPE;
import static org.junit.Assert.assertEquals;

public class InterpolationVariableCollectorTest {

    @Test
    public void merge() {
        final HashMap<InterpolationVariable, Integer> map = new HashMap<>();

        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "birthday"), 1);
        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "dateOfDeath"), 2);

        final Map<InterpolationVariable, Integer> result = new InterpolationVariableCollector(map).getMap();
        assertEquals(1, result.size());
        final InterpolationVariable var = result.keySet().iterator().next();
        assertEquals("var", var.getVarName());
        assertEquals(DEFAULT_TYPE, var.getDataType());
    }

    @Test
    public void orderTopDown() {
        final HashMap<InterpolationVariable, Integer> map = new HashMap<>();

        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "birthday"), 0);
        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "dateOfDeath"), 1);
        map.put(new InterpolationVariable("p",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "dateOfDeath"), 2);

        final Map<InterpolationVariable, Integer> result = new InterpolationVariableCollector(map).getMap();
        assertEquals(2, result.size());
        final Iterator<Integer> iterator = result.values().iterator();
        assertEquals(1, (int) iterator.next());
        assertEquals(0, (int) iterator.next());
    }

    @Test
    /**
     * The map numbers can go from higher to lower or from lower to higher.
     * After merge make sure the numbers are not skipping any.
     */
    public void orderBottomUp() {
        final HashMap<InterpolationVariable, Integer> map = new HashMap<>();

        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "birthday"), 2);
        map.put(new InterpolationVariable("var",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "dateOfDeath"), 1);
        map.put(new InterpolationVariable("p",
                                          DataType.TYPE_DATE,
                                          "Person",
                                          "dateOfDeath"), 0);

        final Map<InterpolationVariable, Integer> result = new InterpolationVariableCollector(map).getMap();
        assertEquals(2, result.size());
        final Iterator<Integer> iterator = result.values().iterator();
        assertEquals(0, (int) iterator.next());
        assertEquals(1, (int) iterator.next());
    }
}