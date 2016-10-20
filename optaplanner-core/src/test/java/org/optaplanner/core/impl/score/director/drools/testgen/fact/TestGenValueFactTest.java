/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.director.drools.testgen.fact;

import java.util.HashMap;

import org.junit.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.*;

public class TestGenValueFactTest {

    @Test
    public void initialization() {
        TestdataValue instance = new TestdataValue();
        TestGenValueFact fact = new TestGenValueFact(321, instance);

        assertSame(instance, fact.getInstance());
        assertEquals("testdataValue_321", fact.toString());

        StringBuilder sb = new StringBuilder(100);
        fact.printInitialization(sb);
        assertEquals("    TestdataValue testdataValue_321 = new TestdataValue();\n", sb.toString());
    }

    @Test
    public void importsAndDependencies() {
        HashMap<Object, TestGenFact> instances = new HashMap<>();
        TestdataEntity entity = new TestdataEntity();
        TestGenValueFact f1 = new TestGenValueFact(0, entity);
        TestdataValue value = new TestdataValue();
        TestGenValueFact f2 = new TestGenValueFact(1, value);

        instances.put(entity, f1);

        f1.setUp(instances);
        assertEquals(1, f1.getImports().size());
        assertTrue(f1.getImports().contains(TestdataEntity.class));
        assertEquals(0, f1.getDependencies().size());

        entity.setValue(value);
        instances.put(value, f2);

        f2.setUp(instances);
        assertEquals(1, f2.getImports().size());
        assertTrue(f2.getImports().contains(TestdataValue.class));
        assertTrue(f1.getDependencies().isEmpty());


        f1.setUp(instances);
        assertEquals(1, f1.getImports().size());
        assertTrue(f1.getImports().contains(TestdataEntity.class));
        assertEquals(1, f1.getDependencies().size());
        assertTrue(f1.getDependencies().contains(f2));
    }

}
