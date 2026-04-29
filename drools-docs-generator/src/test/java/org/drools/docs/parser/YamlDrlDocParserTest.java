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

import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleDoc;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YamlDrlDocParserTest {

    private final YamlDrlDocParser parser = new YamlDrlDocParser();

    @Test
    void shouldParseYamlDrlPackage() throws IOException {
        PackageDoc pkg = parseResource("sample-rules.drl.yaml");
        assertThat(pkg.getName()).isEqualTo("org.example.drinks");
        assertThat(pkg.getSourceFormat()).isEqualTo(PackageDoc.SourceFormat.YAML_DRL);
    }

    @Test
    void shouldParseYamlImports() throws IOException {
        PackageDoc pkg = parseResource("sample-rules.drl.yaml");
        assertThat(pkg.getImports()).containsExactly(
                "org.example.model.Person",
                "org.example.model.DrinkResult"
        );
    }

    @Test
    void shouldParseYamlRules() throws IOException {
        PackageDoc pkg = parseResource("sample-rules.drl.yaml");
        assertThat(pkg.getRules()).hasSize(2);

        RuleDoc first = pkg.getRules().get(0);
        assertThat(first.getName()).isEqualTo("Can Drink - Adult");
        assertThat(first.getConditions()).isNotEmpty();
        assertThat(first.getConditions().get(0).getObjectType()).isEqualTo("Person");
    }

    private PackageDoc parseResource(String name) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);
        assertThat(is).isNotNull();
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return parser.parse(content);
    }
}
