/**
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
package org.drools.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.drools.util.MethodUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodUtilsTest {

    public static class MyClass {

        public static int methodInt(int a) {
            return a;
        }
    }

    @Test
    public void testFindMethod() {
        Method m = MethodUtils.findMethod(Object.class, "toString", new Class[0]);
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("toString");
        assertThat(m.getParameters()).isEmpty();
    }

    @Test
    public void testFindIntMethodWithBigDecimal() {
        Method m = MethodUtils.findMethod(MyClass.class, "methodInt", new Class[]{BigDecimal.class});
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("methodInt");
        assertThat(parametersTypeName(m.getParameters())).containsExactly("int");
    }

    @Test
    public void testFindObjectMethodWithString() {
        Method m = MethodUtils.findMethod(Map.class, "get", new Class[]{String.class});
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("get");
        assertThat(parametersTypeName(m.getParameters())).containsExactly("java.lang.Object");
    }

    private Stream<String> parametersTypeName(Parameter[] parameters) {
        return Arrays.stream(parameters).map(Parameter::getType).map(Class::getCanonicalName);
    }
}