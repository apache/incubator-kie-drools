/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.core.datatype;

import java.util.Arrays;

import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ListDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataTypeResolverTest {

    private enum Champions {
        BETIS,
        DEPORTIVO,
        REAL_SOCIEDAD,
        VALENCIA
    }

    @Test
    public void testDataType() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertTrue(DataTypeResolver.fromObject("pepe") instanceof StringDataType);
        assertTrue(DataTypeResolver.fromObject(true) instanceof BooleanDataType);
        assertTrue(DataTypeResolver.fromObject(4) instanceof IntegerDataType);
        assertTrue(DataTypeResolver.fromObject(23.2f) instanceof FloatDataType);
        assertTrue(DataTypeResolver.fromObject(Arrays.asList("1", "2", "3")) instanceof ListDataType);
        assertTrue(DataTypeResolver.fromObject(new byte[0]) instanceof ObjectDataType);
        assertTrue(DataTypeResolver.fromObject(Champions.BETIS) instanceof EnumDataType);
        assertTrue(DataTypeResolver.fromType("String", cl) instanceof StringDataType);
        assertTrue(DataTypeResolver.fromType("Boolean", cl) instanceof BooleanDataType);
        assertTrue(DataTypeResolver.fromType("Integer", cl) instanceof IntegerDataType);
        assertTrue(DataTypeResolver.fromType("Float", cl) instanceof FloatDataType);
        assertTrue(DataTypeResolver.fromType("java.util.List", cl) instanceof ListDataType);
        assertTrue(DataTypeResolver.fromType("Object", cl) instanceof ObjectDataType);

    }
}
