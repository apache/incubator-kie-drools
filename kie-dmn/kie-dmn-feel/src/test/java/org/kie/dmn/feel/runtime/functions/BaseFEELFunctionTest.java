/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;

import static org.junit.jupiter.api.Assertions.*;

class BaseFEELFunctionTest {

    private EvaluationContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = CodegenTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void getAllFunctionCandidateMethod() {
        BaseFEELFunction toTest = AllFunction.INSTANCE;
        Object[] parameters = {List.of(true, false)};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(List.class, parametersRetrieved[0].getType());

        parameters = new Object[]{ true, false };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(Object.class.arrayType(), parametersRetrieved[0].getType());
    }
}