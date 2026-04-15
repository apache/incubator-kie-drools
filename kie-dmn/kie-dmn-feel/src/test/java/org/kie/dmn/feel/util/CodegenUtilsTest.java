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

package org.kie.dmn.feel.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.custom.FormattedZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CodegenUtilsTest {

    @Test
    void testGetVariableDeclaratorWithZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 3, 15, 10, 30, 45, 123456789, ZoneId.of("America/New_York"));
        String variableName = "testZdt";
        
        VariableDeclarationExpr result = CodegenUtils.getVariableDeclaratorWithZonedDateTime(variableName, zonedDateTime);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains(variableName);
        assertThat(result.toString()).contains("ZonedDateTime.of");
        assertThat(result.toString()).contains("2024");
        assertThat(result.toString()).contains("3");
        assertThat(result.toString()).contains("15");
        assertThat(result.toString()).contains("10");
        assertThat(result.toString()).contains("30");
        assertThat(result.toString()).contains("45");
        assertThat(result.toString()).contains("123456789");
        assertThat(result.toString()).contains("America/New_York");
    }

}