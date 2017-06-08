/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DataTypeIsValidValueTest {

    @Parameterized.Parameters(name = "dataType={0}, value={1}, isValid={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {DataType.DataTypes.NUMERIC_BIGDECIMAL, "1", true},
                {DataType.DataTypes.NUMERIC_BIGDECIMAL, "1.0", true},
                {DataType.DataTypes.NUMERIC_BIGDECIMAL, "a", false},
                {DataType.DataTypes.NUMERIC_BIGINTEGER, "1", true},
                {DataType.DataTypes.NUMERIC_BIGINTEGER, "1.0", false},
                {DataType.DataTypes.NUMERIC_BIGINTEGER, "a", false},
                {DataType.DataTypes.NUMERIC_BYTE, "1", true},
                {DataType.DataTypes.NUMERIC_BYTE, "1.0", false},
                {DataType.DataTypes.NUMERIC_BYTE, "a", false},
                {DataType.DataTypes.NUMERIC_DOUBLE, "1", true},
                {DataType.DataTypes.NUMERIC_DOUBLE, "1.0", true},
                {DataType.DataTypes.NUMERIC_DOUBLE, "a", false},
                {DataType.DataTypes.NUMERIC_DOUBLE, "1d", true},
                {DataType.DataTypes.NUMERIC_FLOAT, "1", true},
                {DataType.DataTypes.NUMERIC_FLOAT, "1.0", true},
                {DataType.DataTypes.NUMERIC_FLOAT, "a", false},
                {DataType.DataTypes.NUMERIC_FLOAT, "1f", true},
                {DataType.DataTypes.NUMERIC_INTEGER, "1", true},
                {DataType.DataTypes.NUMERIC_INTEGER, "1.0", false},
                {DataType.DataTypes.NUMERIC_INTEGER, "a", false},
                {DataType.DataTypes.NUMERIC_LONG, "1", true},
                {DataType.DataTypes.NUMERIC_LONG, "1.0", false},
                {DataType.DataTypes.NUMERIC_LONG, "a", false},
                {DataType.DataTypes.NUMERIC_SHORT, "1", true},
                {DataType.DataTypes.NUMERIC_SHORT, "1", true},
                {DataType.DataTypes.NUMERIC_SHORT, "a", false},
                {DataType.DataTypes.BOOLEAN, "1", false},
                {DataType.DataTypes.BOOLEAN, "true", true},
                {DataType.DataTypes.BOOLEAN, "false", true}
        });
    }

    @Parameterized.Parameter(0)
    public DataType.DataTypes dataType;

    @Parameterized.Parameter(1)
    public String value;

    @Parameterized.Parameter(2)
    public boolean isValid;

    @Test
    public void testIsNumeric() {
        Assertions.assertThat(dataType.isValidValue(value)).isEqualTo(isValid);
    }
}