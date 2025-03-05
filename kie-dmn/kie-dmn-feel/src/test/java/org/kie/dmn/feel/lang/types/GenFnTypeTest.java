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

package org.kie.dmn.feel.lang.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.AbsFunction;
import org.kie.dmn.feel.runtime.functions.AnyFunction;
import org.kie.dmn.feel.runtime.FEELFunction.Param;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

class GenFnTypeTest {

    private GenFnType genFnType;
    private FEELFunction mockFunction;
    private AbsFunction absFunctionInstance;
    private AnyFunction anyFunctionInstance;

    @BeforeEach
    void setUp() {
        absFunctionInstance = AbsFunction.INSTANCE;
        anyFunctionInstance = AnyFunction.INSTANCE;

        genFnType = new GenFnType(
                Arrays.asList(new SomeType(), new AnotherType()),
                new SomeType()
        );
        mockFunction = Mockito.mock(FEELFunction.class);
    }

    @Test
    public void testIsInstanceOfWithCompatibleFunction() {
        List<List<Param>> params = new ArrayList<>();
        Mockito.when(mockFunction.getParameters()).thenReturn(params);
        Mockito.when(mockFunction.isCompatible(Mockito.any(), Mockito.any())).thenReturn(true);

        assertThat(genFnType.isInstanceOf(mockFunction)).isTrue();
    }

    @Test
    public void testIsInstanceOfWithNoParameters() {
        assertThat(genFnType.isInstanceOf(absFunctionInstance)).isFalse();
    }

    @Test
    public void testIsInstanceOfWithNonMatchingParameters() {
        FEELFnResult<Boolean> feelFn = anyFunctionInstance.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE});
        assertThat(genFnType.isInstanceOf(feelFn)).isFalse();
    }

    @Test
    public void testIsAssignableValueWithNullValue() {
        assertThat(genFnType.isAssignableValue(null)).isTrue();
    }

    @Test
    public void testIsAssignableValue_withNonNullValue() {
        Type evaluatedTypeArg = BuiltInType.NUMBER;
        Type functionReturnType = BuiltInType.NUMBER;
        GenFnType genFnType = new GenFnType(
                List.of(evaluatedTypeArg), functionReturnType
        );
        Object value = "Hello";
        boolean result = genFnType.isAssignableValue(value);
        assertThat(result).isFalse();
    }

    @Test
    public void testConformsToWithSignature() {
        GenFnType matchingGenFnType = new GenFnType(
                Collections.singletonList(new SomeType()),
                new SomeType()
        );
        assertThat(genFnType.conformsTo(matchingGenFnType)).isFalse();
    }

    @Test
    public void testConformsToWithFunctionType() {
        assertThat(genFnType.conformsTo(BuiltInType.FUNCTION)).isTrue();
    }

    @Test
    void testCheckSignatures_withMatchingSignatures() {
        List<Type> argsGen = Arrays.asList(new SomeType(), new AnotherType());
        List<Type> paramTypes = Arrays.asList(new SomeType(), new AnotherType());
        List<String> paramNames = Arrays.asList("param1", "param2");
        List<List<Param>> params = createParams(paramTypes, paramNames);

        assertThat(GenFnType.checkSignatures(params, argsGen)).isTrue();
    }

    @Test
    void testCheckSignatures_withNonMatchingSignatures() {
        List<Type> argsGen = Arrays.asList(new SomeType(), new AnotherType());
        List<Type> paramTypes = Arrays.asList(new SomeType(), new YetAnotherType());
        List<String> paramNames = Arrays.asList("param1", "param2");
        List<List<Param>> params = createParams(paramTypes, paramNames);

        assertThat(GenFnType.checkSignatures(params, argsGen)).isFalse();
    }

    @Test
    void testCheckSignatures_withSignatureSizeMismatch() {
        List<Type> argsGen = Arrays.asList(new SomeType(), new AnotherType());
        List<Type> paramTypes = List.of(new SomeType());
        List<String> paramNames = List.of("param1");
        List<List<Param>> params = createParams(paramTypes, paramNames);

        assertThat(GenFnType.checkSignatures(params, argsGen)).isFalse();
    }

    @Test
    void testCheckSignatures_withEmptyParams() {
        List<Type> argsGen = Arrays.asList(new SomeType(), new AnotherType());
        List<List<Param>> params = List.of();

        assertThat(GenFnType.checkSignatures(params, argsGen)).isFalse();
    }

    @Test
    void testCheckSignatures_withEmptyArgsGen() {
        List<Type> argsGen = List.of();
        List<Type> paramTypes = Arrays.asList(new SomeType(), new AnotherType());
        List<String> paramNames = Arrays.asList("param1", "param2");
        List<List<Param>> params = createParams(paramTypes, paramNames);

        assertThat(GenFnType.checkSignatures(params, argsGen)).isFalse();
    }

    @Test
    void testCheckSignatures_withMatchingEmptySignature() {
        List<Type> argsGen = List.of();
        List<List<Param>> params = List.of();

        assertThat(GenFnType.checkSignatures(params, argsGen)).isFalse();
    }

    static class SomeType implements Type {
        @Override
        public String getName() {
            return "SomeType";
        }

        @Override
        public boolean isInstanceOf(Object o) {
            return o instanceof SomeType;
        }

        @Override
        public boolean isAssignableValue(Object value) {
            return value instanceof SomeType;
        }

        @Override
        public boolean conformsTo(Type t) {
            return t instanceof SomeType;
        }
    }

    static class AnotherType implements Type {
        @Override
        public String getName() {
            return "AnotherType";
        }

        @Override
        public boolean isInstanceOf(Object o) {
            return o instanceof AnotherType;
        }

        @Override
        public boolean isAssignableValue(Object value) {
            return value instanceof AnotherType;
        }

        @Override
        public boolean conformsTo(Type t) {
            return t instanceof AnotherType;
        }
    }

    static class YetAnotherType implements Type {
        @Override
        public String getName() {
            return "YetAnotherType";
        }

        @Override
        public boolean isInstanceOf(Object o) {
            return o instanceof YetAnotherType;
        }

        @Override
        public boolean isAssignableValue(Object value) {
            return value instanceof YetAnotherType;
        }

        @Override
        public boolean conformsTo(Type t) {
            return t instanceof YetAnotherType;
        }
    }

    private List<List<Param>> createParams(List<Type> types, List<String> names) {
        if (types.size() != names.size()) {
            throw new IllegalArgumentException("The number of types and names must match");
        }

        List<List<Param>> params = new ArrayList<>();
        List<Param> paramList = new ArrayList<>();

        for (int i = 0; i < types.size(); i++) {
            paramList.add(new Param(names.get(i), types.get(i)));
        }

        params.add(paramList);
        return params;
    }
}