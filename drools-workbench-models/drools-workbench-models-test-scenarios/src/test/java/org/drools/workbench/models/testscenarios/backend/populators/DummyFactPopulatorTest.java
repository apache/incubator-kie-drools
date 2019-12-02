/*
 * Copyright 2005 JBoss Inc
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

package org.drools.workbench.models.testscenarios.backend.populators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.workbench.models.testscenarios.backend.Person;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.junit.Test;

import static org.drools.workbench.models.testscenarios.backend.populators.DummyFactPopulator.factDataToObjects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DummyFactPopulatorTest {

    @Test
    public void testPopulateFacts() throws Exception {
        FactData factData = new FactData(
                "Person",
                "p1",
                Arrays.<Field>asList(
                        new FieldData(
                                "name",
                                "mic"),
                        new FieldData(
                                "age",
                                "=30 + 3")),
                false);

        Map<String, Object> populatedData = factDataToObjects(getTypeResolver(), factData);

        assertTrue(populatedData.containsKey("p1"));

        Person person = (Person) populatedData.get("p1");
        assertEquals("mic", person.getName());
        assertEquals(33, person.getAge());
    }

    private TypeResolver getTypeResolver() {
        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(), getClassLoader());
        resolver.addImport("org.drools.workbench.models.testscenarios.backend.Person");
        return resolver;
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader;
    }
}
