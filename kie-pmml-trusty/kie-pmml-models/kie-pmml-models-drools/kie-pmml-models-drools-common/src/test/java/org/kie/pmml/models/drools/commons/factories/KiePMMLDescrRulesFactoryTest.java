/**
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
package org.kie.pmml.models.drools.commons.factories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLDescrRulesFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrRulesFactoryTest.class.getName());
    private static final String PACKAGE_NAME = "package";
    private PackageDescrBuilder builder;

    @BeforeEach
    public void setUp() throws Exception {
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        assertThat(builder.getDescr()).isNotNull();
        assertThat(builder.getDescr().getName()).isEqualTo(PACKAGE_NAME);
    }

    @Test
    void declareRule() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String patternType = "TEMPERATURE";
        String agendaGroup = "agendaGroup";
        String activationGroup = "activationGroup";
        List<KiePMMLFieldOperatorValue> orConstraints = Arrays.asList(new KiePMMLFieldOperatorValue(patternType, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                new KiePMMLFieldOperatorValue(patternType, BOOLEAN_OPERATOR.AND, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null));
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList())
                .withAgendaGroup(agendaGroup)
                .withActivationGroup(activationGroup)
                .withOrConstraints(orConstraints)
                .build();
        KiePMMLDescrRulesFactory.factory(builder).declareRule(rule);
        assertThat(builder.getDescr().getRules()).isNotNull();
        assertThat(builder.getDescr().getRules()).hasSize(1);
        final RuleDescr retrieved = builder.getDescr().getRules().get(0);
        assertThat(retrieved.getName()).isEqualTo(name);
        assertThat(retrieved.getAttributes()).hasSize(2);
        assertThat(retrieved.getAttributes()).containsKey("agenda-group");
        assertThat(retrieved.getAttributes().get("agenda-group").getValue()).isEqualTo(agendaGroup);
        assertThat(retrieved.getAttributes()).containsKey("activation-group");
        assertThat(retrieved.getAttributes().get("activation-group").getValue()).isEqualTo(activationGroup);
    }
}