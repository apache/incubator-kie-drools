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

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.kie.dmn.feel.runtime.FEELFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.kie.dmn.feel.runtime.functions.BuiltInFunctions.FUNCTIONS;

public class BuiltInFunctionsTest {

    @Test
    public void getFunctions() {
        // This test is aimed at verify that all the "INSTANCE" fields are correctly populated, referring to the same class they are defined in
        Set< Class<? extends FEELFunction>> verifiedClasses = Stream.of(FUNCTIONS).map(this::validateFunction)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        assertEquals(FUNCTIONS.length, verifiedClasses.size());
    }

    private Class<? extends FEELFunction> validateFunction(FEELFunction toValidate) {
        Class<? extends FEELFunction> aClass = toValidate.getClass();
        try {
            Field instance = aClass.getDeclaredField("INSTANCE");
            assertEquals(aClass, instance.getDeclaringClass());
            return aClass;
        } catch (NoSuchFieldException e) {
            fail("No INSTANCE field found for " + aClass);
            return null;
        }
    }
}