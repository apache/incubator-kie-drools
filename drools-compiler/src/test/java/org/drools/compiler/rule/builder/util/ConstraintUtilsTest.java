/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.rule.builder.util;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.drools.compiler.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.drools.compiler.rule.builder.util.ConstraintUtils.normalizeConstraintExpression;

public class ConstraintUtilsTest {

    @Test
    public void testNormalizeConstraintExpression() {
        String[][] testExpressions = {
                                      {"name==\"Toshiya\"", "\"Toshiya\" == name"},
                                      {"name == \"Toshiya\"", "name == \"Toshiya\""},
                                      {"name!=\"Toshiya\"", "\"Toshiya\" != name"},
                                      {"name==$customerName", "$customerName == name"},
                                      {"age==10", "10 == age"},
                                      {"age<10", "10 > age"}, // inverse operator
                                      {"age>10", "10 < age"}, // inverse operator
                                      {"age<=10", "10 >= age"}, // inverse operator
                                      {"age>=10", "10 <= age"}, // inverse operator
                                      {"10==age || 20==age", "10==age || 20==age"}, // don't normalize
                                      {"10==age||20==age", "10==age||20==age"}, // don't normalize
                                      {"10<age && 20>age", "10<age && 20>age"}, // don't normalize
                                      {"10<age&&20>age", "10<age&&20>age"}, // don't normalize
                                      {"eval(20 == age)", "eval(20 == age)"}, // don't normalize
                                      {"(20 == age)", "(20 == age)"}, // don't normalize
        };

        Arrays.stream(testExpressions).forEach(expr -> {
            assertEquals(expr[0], normalizeConstraintExpression(expr[1], Person.class));
        });
    }

    @Test
    public void testDisableNormalization() throws Exception {
        Field field = ConstraintUtils.class.getDeclaredField( "ENABLE_NORMALIZE" );
        field.setAccessible(true);
        field.set(null, false);
        try {
            assertEquals("\"Toshiya\" == name", normalizeConstraintExpression("\"Toshiya\" == name", Person.class));
        } finally {
            field.set(null, true);
        }
    }
}
