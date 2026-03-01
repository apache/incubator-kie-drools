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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.drools.docs.model.ConditionDoc;
import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleDoc;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlDocParserTest {

    private final DrlDocParser parser = new DrlDocParser();

    @Test
    void shouldParsePackageName() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        assertThat(pkg.getName()).isEqualTo("org.example.loans");
        assertThat(pkg.getSourceFormat()).isEqualTo(PackageDoc.SourceFormat.DRL);
    }

    @Test
    void shouldParseImports() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        assertThat(pkg.getImports()).containsExactly(
                "org.example.model.Applicant",
                "org.example.model.LoanApplication"
        );
    }

    @Test
    void shouldParseGlobals() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        assertThat(pkg.getGlobals()).hasSize(1);
        assertThat(pkg.getGlobals().get(0).getType()).isEqualTo("java.util.List");
        assertThat(pkg.getGlobals().get(0).getIdentifier()).isEqualTo("approvedApplications");
    }

    @Test
    void shouldParseTypeDeclarations() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        assertThat(pkg.getTypeDeclarations()).hasSize(2);
        assertThat(pkg.getTypeDeclarations().get(0).getName()).isEqualTo("Applicant");
        assertThat(pkg.getTypeDeclarations().get(0).getFields()).containsKeys("name", "age", "creditScore");
    }

    @Test
    void shouldParseRules() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        assertThat(pkg.getRules()).hasSize(3);

        RuleDoc firstRule = pkg.getRules().get(0);
        assertThat(firstRule.getName()).isEqualTo("Eligible Age Check");
        assertThat(firstRule.getAttributes()).containsEntry("salience", "10");
        assertThat(firstRule.getConditions()).isNotEmpty();
        assertThat(firstRule.getConsequence()).contains("System.out.println");
    }

    @Test
    void shouldParseRuleConditions() {
        PackageDoc pkg = parseResource("sample-rules.drl");
        RuleDoc rule = pkg.getRules().get(1);

        assertThat(rule.getName()).isEqualTo("Credit Score Approval");
        assertThat(rule.getConditions()).hasSizeGreaterThanOrEqualTo(1);

        ConditionDoc firstCond = rule.getConditions().get(0);
        assertThat(firstCond.getType()).isEqualTo(ConditionDoc.ConditionType.PATTERN);
        assertThat(firstCond.getObjectType()).isEqualTo("LoanApplication");
    }

    private PackageDoc parseResource(String name) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);
        assertThat(is).isNotNull();
        return parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
}
