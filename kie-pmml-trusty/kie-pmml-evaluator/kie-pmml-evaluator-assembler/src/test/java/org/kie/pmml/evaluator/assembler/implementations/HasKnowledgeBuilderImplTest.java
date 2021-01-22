/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.evaluator.assembler.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.*;

public class HasKnowledgeBuilderImplTest {

    private static final String CLASS_SOURCE_NAME = "ClassSource";
    private static final String CLASS_SOURCE = String.format("public class %s {}" , CLASS_SOURCE_NAME);
    private KnowledgeBuilderImpl knowledgeBuilder;
    private HasKnowledgeBuilderImpl hasKnowledgeBuilder;

    @Before
    public void setup() {
        knowledgeBuilder = new KnowledgeBuilderImpl();
        hasKnowledgeBuilder = new HasKnowledgeBuilderImpl(knowledgeBuilder);
    }

    @Test
    public void getClassLoader() {
        ClassLoader retrieved = hasKnowledgeBuilder.getClassLoader();
        assertNotNull(retrieved);
        assertEquals(knowledgeBuilder.getRootClassLoader(),retrieved);
        assertTrue(retrieved instanceof ProjectClassLoader);
    }

    @Test
    public void compileAndLoadClass() {
        Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(CLASS_SOURCE_NAME, CLASS_SOURCE);
        Class<?> retrieved = hasKnowledgeBuilder.compileAndLoadClass(sourcesMap, CLASS_SOURCE_NAME);
        assertNotNull(retrieved);
        assertEquals(CLASS_SOURCE_NAME, retrieved.getName());
    }

    @Test
    public void compileAndLoadClassMultipleTimes() {
        Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(CLASS_SOURCE_NAME, CLASS_SOURCE);
        IntStream.range(0,3).forEach(value -> {
            try {
                hasKnowledgeBuilder.compileAndLoadClass(sourcesMap, CLASS_SOURCE_NAME);
            } catch (Throwable t) {
                fail(t.getMessage());
            }
        });
    }

}