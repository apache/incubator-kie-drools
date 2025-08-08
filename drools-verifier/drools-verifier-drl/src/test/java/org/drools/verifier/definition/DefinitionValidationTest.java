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
package org.drools.verifier.definition;

import org.drools.io.ClassPathResource;
import org.drools.verifier.TestBase;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class DefinitionValidationTest extends TestBase {

    @Test
    void testMissingDefinitionsAndFields() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource("DefinitionVerificationRules.drl", DefinitionValidationTest.class), ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource("MissingDefinitionTest.drl", DefinitionValidationTest.class), ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            fail("Could not run verifier");
        }
        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();


        result.getBySeverity(Severity.ERROR).stream().forEach(System.out::println);

        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(3);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(0);

        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Missing type definition for fact type: Employee"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'department' used in rule but not declared in definition of type 'Employee'"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Missing type definition for fact type: Vehicle"))).isTrue();

    }

    @Test
    void testMissingFields() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource("DefinitionVerificationRules.drl", DefinitionValidationTest.class), ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource("UndefinedFieldUsageTest.drl", DefinitionValidationTest.class), ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            fail("Could not run verifier");
        }
        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();

        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(5);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(0);

        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'department' used in rule but not declared in definition of type 'Person'"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'experience' used in rule but not declared in definition of type 'Person'"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'salary' used in rule but not declared in definition of type 'Person'"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'year' used in rule but not declared in definition of type 'Vehicle'"))).isTrue();
        assertThat(result.getBySeverity(Severity.ERROR).stream().anyMatch(m -> m.getMessage().contains("Field 'owner' used in rule but not declared in definition of type 'Vehicle'"))).isTrue();

    }

    @Test
    void testValidFileWithNestedFacts() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource("DefinitionVerificationRules.drl", DefinitionValidationTest.class), ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource("DefinitionValidationTest.drl", DefinitionValidationTest.class), ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            fail("Could not run verifier");
        }
        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();

        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(0);
    }
}
