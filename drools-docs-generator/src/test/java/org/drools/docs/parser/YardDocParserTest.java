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
package org.drools.docs.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.drools.docs.model.YardDoc;
import org.drools.docs.model.YardDoc.YardElementDoc;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YardDocParserTest {

    private final YardDocParser parser = new YardDocParser();

    @Test
    void shouldParseYardName() throws IOException {
        YardDoc yard = parseResource("sample-yard.yml");
        assertThat(yard.getName()).isEqualTo("Insurance Base Price");
        assertThat(yard.getSpecVersion()).isEqualTo("alpha");
    }

    @Test
    void shouldParseYardInputs() throws IOException {
        YardDoc yard = parseResource("sample-yard.yml");
        assertThat(yard.getInputs()).hasSize(2);
        assertThat(yard.getInputs().get(0).getName()).isEqualTo("Age");
        assertThat(yard.getInputs().get(0).getType()).isEqualTo("number");
    }

    @Test
    void shouldParseYardDecisionTable() throws IOException {
        YardDoc yard = parseResource("sample-yard.yml");
        assertThat(yard.getElements()).hasSize(2);

        YardElementDoc dtElement = yard.getElements().get(0);
        assertThat(dtElement.getName()).isEqualTo("Base price");
        assertThat(dtElement.getLogicType()).isEqualTo("DecisionTable");
        assertThat(dtElement.getRows()).hasSize(4);
    }

    @Test
    void shouldParseYardLiteralExpression() throws IOException {
        YardDoc yard = parseResource("sample-yard.yml");

        YardElementDoc leElement = yard.getElements().get(1);
        assertThat(leElement.getName()).isEqualTo("Downpayment");
        assertThat(leElement.getLogicType()).isEqualTo("LiteralExpression");
        assertThat(leElement.getLiteralExpression()).contains("Math.max");
    }

    private YardDoc parseResource(String name) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);
        assertThat(is).isNotNull();
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return parser.parse(content);
    }
}
