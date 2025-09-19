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
package org.drools.ruleunits.impl;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import org.drools.ruleunits.api.DataSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class SimpleRuleUnitVariableClassNotFoundTest {

    @Test
    public void testNullClassLoader() throws Exception {
        Class<?> isolatedDataSourceClass = new ByteBuddy()
                .subclass(Object.class)
                .name("org.drools.ruleunits.isolated.IsolatedDataSource")
                .defineField("data", List.class, Modifier.PRIVATE)
                .defineMethod("getData", List.class, Modifier.PUBLIC)
                .intercept(FieldAccessor.ofField("data"))
                .defineMethod("setData", void.class, Modifier.PUBLIC)
                .withParameters(List.class)
                .intercept(FieldAccessor.ofField("data"))
                .make()
                .load(null) // null parent = isolated classloader
                .getLoaded();

        assertThat(isolatedDataSourceClass.getClassLoader())
                .isNotNull()
                .isNotEqualTo(DataSource.class.getClassLoader());

        try {
            DataSource.class.getClassLoader().loadClass(isolatedDataSourceClass.getCanonicalName());
            fail("Should have thrown ClassNotFoundException");
        } catch (ClassNotFoundException e) {
        }

        String variableName = "testVariable";
        Type variableType = isolatedDataSourceClass;
        Class<?> dataSourceParameterType = isolatedDataSourceClass;
        String setter = "setData";

        SimpleRuleUnitVariable variable = new SimpleRuleUnitVariable(variableName, variableType, dataSourceParameterType, setter);

        assertThat(variable).isNotNull();
        assertThat(variable.getName()).isEqualTo(variableName);

        Field boxedVarTypeField = SimpleRuleUnitVariable.class.getDeclaredField("boxedVarType");
        boxedVarTypeField.setAccessible(true);
        Class<?> actualBoxedVarType = (Class<?>) boxedVarTypeField.get(variable);

        assertThat(actualBoxedVarType).isEqualTo(isolatedDataSourceClass);
    }

}
