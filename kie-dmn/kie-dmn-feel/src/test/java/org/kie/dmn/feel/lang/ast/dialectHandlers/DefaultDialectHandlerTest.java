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
package org.kie.dmn.feel.lang.ast.dialectHandlers;

import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for DefaultDialectHandler static utility methods and inner classes.
 * 
 * Note: This is an abstract class. Its instance methods are tested through
 * concrete implementations (FEELDialectHandler and BFEELDialectHandler):
 * 
 * This test class focuses on:
 * - Static utility methods (isEqual, compare with fallback suppliers)
 * - Inner classes (CheckedPredicate)
 */
class DefaultDialectHandlerTest {

    @Test
    void testIsEqual_StaticMethod() {
        // Basic equality
        assertThat(DefaultDialectHandler.isEqual(null, null, () -> Boolean.FALSE, () -> null))
                .isEqualTo(Boolean.FALSE);
        assertThat(DefaultDialectHandler.isEqual(10, 10, () -> null, () -> null)).isTrue();
        assertThat(DefaultDialectHandler.isEqual(10, 20, () -> null, () -> null)).isFalse();
        assertThat(DefaultDialectHandler.isEqual("test", "test", () -> null, () -> null)).isTrue();
        assertThat(DefaultDialectHandler.isEqual("test1", "test2", () -> null, () -> null)).isFalse();
        
        // Singleton collection and element
        List<Integer> list = Arrays.asList(10);
        assertThat(DefaultDialectHandler.isEqual(list, 10, () -> null, () -> null)).isTrue();
        assertThat(DefaultDialectHandler.isEqual(10, list, () -> null, () -> null)).isTrue();
        
        // Temporal types
        assertThat(DefaultDialectHandler.isEqual(Period.ofMonths(6), Period.ofMonths(6), () -> null, () -> null))
                .isTrue();
        assertThat(DefaultDialectHandler.isEqual(LocalTime.of(14, 30), LocalTime.of(14, 30), () -> null, () -> null))
                .isTrue();
        
        // Null fallback suppliers throw exception
        assertThatThrownBy(() -> DefaultDialectHandler.isEqual(10, 20, null, () -> null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fallback suppliers must not be null");
        
        assertThatThrownBy(() -> DefaultDialectHandler.isEqual(10, 20, () -> null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fallback suppliers must not be null");
    }

    @Test
    void testCheckedPredicate() {
        BiPredicate<Object, Object> predicate = (l, r) -> l instanceof Number && r instanceof Number;
        DefaultDialectHandler.CheckedPredicate cp1 = new DefaultDialectHandler.CheckedPredicate(predicate, false);
        DefaultDialectHandler.CheckedPredicate cp2 = new DefaultDialectHandler.CheckedPredicate(predicate, true);
        
        // Equality based on predicate only (not on checked flag)
        assertThat(cp1).isEqualTo(cp2);
        assertThat(cp1.hashCode()).isEqualTo(cp2.hashCode());
        
        // Self equality
        assertThat(cp1).isEqualTo(cp1);
        
        // Not equal to different types
        assertThat(cp1).isNotEqualTo("string");
        assertThat(cp1).isNotEqualTo(null);
    }
}

