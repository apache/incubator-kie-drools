/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilsTest {

    private static class ServiceExample {

        @SuppressWarnings("unused")
        public int primitiveType(String s, int a) {
            return a;
        }

        @SuppressWarnings("unused")
        public Float primitiveType(String s, Float a) {
            return a;
        }

        public String getStringWithContext(String prefix, KogitoProcessContext context) {
            return prefix + "-" + context.getVariable("boy");
        }

        public String getStringWithContext(KogitoProcessContext context) {
            return (String) context.getVariable("boy");
        }
    }

    @Test
    public void testGetMethod() throws ReflectiveOperationException {
        ServiceExample instance = new ServiceExample();
        Method m = ReflectionUtils
                .getMethod(
                        Thread.currentThread().getContextClassLoader(),
                        ServiceExample.class,
                        "primitiveType",
                        Arrays.asList("String", "Integer"));
        assertThat(m.invoke(instance, "pepe", 2)).isEqualTo(Integer.valueOf(2));
        m = ReflectionUtils
                .getMethod(
                        Thread.currentThread().getContextClassLoader(),
                        ServiceExample.class,
                        "primitiveType",
                        Arrays.asList("String", "Float"));
        assertThat(m.invoke(instance, "pepe", 2.0f)).isEqualTo(Float.valueOf(2.0f));

        KogitoProcessContext context = new KogitoProcessContextImpl(null) {
            @Override
            public Object getVariable(String variableName) {
                return variableName;
            }
        };
        m = ReflectionUtils.getMethod(Thread.currentThread().getContextClassLoader(),
                ServiceExample.class, "getStringWithContext", Arrays.asList("String"));
        assertThat(m.invoke(instance, "dummy", context)).isEqualTo("dummy-boy");

        m = ReflectionUtils.getMethod(Thread.currentThread().getContextClassLoader(),
                ServiceExample.class, "getStringWithContext", Collections.emptyList());
        assertThat(m.invoke(instance, context)).isEqualTo("boy");
    }

}
