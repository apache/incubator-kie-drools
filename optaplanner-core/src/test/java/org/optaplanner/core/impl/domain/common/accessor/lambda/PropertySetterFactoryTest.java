/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor.lambda;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class PropertySetterFactoryTest {

    private String propertyName;
    private Object value;

    public PropertySetterFactoryTest(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        params.add(new Object[]{"integerProperty", 5});
        params.add(new Object[]{"shortProperty", (short) 5});
        params.add(new Object[]{"byteProperty", (byte) 5});
        params.add(new Object[]{"booleanProperty", true});
        params.add(new Object[]{"floatProperty", 5.5f});
        params.add(new Object[]{"doubleProperty", 5.5});
        params.add(new Object[]{"objectProperty", "test"});
        params.add(new Object[]{"charProperty", 'a'});
        params.add(new Object[]{"arrayProperty", new char[]{'a', 'b'}});

        return params;
    }

    @Test
    public void testSetterFactories() throws NoSuchFieldException, IllegalAccessException {
        Class<?> declaringClass = MockClassWithSetters.class;
        Field field = declaringClass.getDeclaredField(propertyName);
        Class<?> propertyType = field.getType();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Method setterMethod = ReflectionHelper.getSetterMethod(declaringClass, propertyType, propertyName);
        BiConsumer propertySetterFunction =
                PropertySetterFactory.createSetterFunction(setterMethod, propertyType, lookup);
        assertNotNull(propertySetterFunction);

        MockClassWithSetters mockBean = new MockClassWithSetters();
        propertySetterFunction.accept(mockBean, value);

        assertEquals(value, field.get(mockBean));
    }

    protected static class MockClassWithSetters {

        protected int integerProperty;
        protected boolean booleanProperty;
        protected String objectProperty;
        protected double doubleProperty;
        protected float floatProperty;
        protected short shortProperty;
        protected char charProperty;
        protected byte byteProperty;
        protected char[] arrayProperty;

        public void setIntegerProperty(int integerProperty) {
            this.integerProperty = integerProperty;
        }

        public void setBooleanProperty(boolean booleanProperty) {
            this.booleanProperty = booleanProperty;
        }

        public void setObjectProperty(String objectProperty) {
            this.objectProperty = objectProperty;
        }

        public void setDoubleProperty(double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }

        public void setFloatProperty(float floatProperty) {
            this.floatProperty = floatProperty;
        }

        public void setShortProperty(short shortProperty) {
            this.shortProperty = shortProperty;
        }

        public void setCharProperty(char charProperty) {
            this.charProperty = charProperty;
        }

        public void setByteProperty(byte byteProperty) {
            this.byteProperty = byteProperty;
        }

        public void setArrayProperty(char[] arrayProperty) {
            this.arrayProperty = arrayProperty;
        }
    }
}
