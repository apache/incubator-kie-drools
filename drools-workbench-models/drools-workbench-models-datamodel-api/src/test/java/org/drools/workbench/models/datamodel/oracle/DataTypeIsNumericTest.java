/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.datamodel.oracle;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DataTypeIsNumericTest {

    @Parameterized.Parameters(name = "Type={0}, isNumeric={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { DataType.TYPE_NUMERIC, true },
                { DataType.TYPE_NUMERIC_BYTE, true },
                { DataType.TYPE_NUMERIC_SHORT, true },
                { DataType.TYPE_NUMERIC_INTEGER, true },
                { DataType.TYPE_NUMERIC_LONG, true },
                { DataType.TYPE_NUMERIC_BIGINTEGER, true },
                { DataType.TYPE_NUMERIC_FLOAT, true },
                { DataType.TYPE_NUMERIC_DOUBLE, true },
                { DataType.TYPE_NUMERIC_BIGDECIMAL, true },
                { DataType.TYPE_BOOLEAN, false },
                { DataType.TYPE_COLLECTION, false },
                { DataType.TYPE_COMPARABLE, false },
                { DataType.TYPE_DATE, false },
                { DataType.TYPE_FINAL_OBJECT, false },
                { DataType.TYPE_OBJECT, false },
                { DataType.TYPE_STRING, false },
                { DataType.TYPE_THIS, false },
                { DataType.TYPE_VOID, false },
        });
    }

    @Parameterized.Parameter(0)
    public String dataType;

    @Parameterized.Parameter(1)
    public boolean isNumeric;

    @Test
    public void testIsNumeric() {
        Assertions.assertThat(DataType.isNumeric(dataType)).isEqualTo(isNumeric);
    }

}