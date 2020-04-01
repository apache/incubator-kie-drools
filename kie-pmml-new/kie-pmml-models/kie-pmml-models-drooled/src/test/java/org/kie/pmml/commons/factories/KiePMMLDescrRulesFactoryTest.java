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

package org.kie.pmml.commons.factories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLDrooledModel;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLDescrRulesFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrRulesFactoryTest.class.getName());
    private static final String PACKAGE_NAME = "package";
    private PackageDescrBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        assertNotNull(builder.getDescr());
        assertEquals(PACKAGE_NAME, builder.getDescr().getName());
    }

    @Test
    public void declareRule() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String patternType = "TEMPERATURE";
        String agendaGroup = "agendaGroup";
        String activationGroup = "activationGroup";
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        final Map<String, List<KiePMMLOperatorValue>> orConstraints = Collections.singletonMap(patternType, kiePMMLOperatorValues);
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet)
                .withAgendaGroup(agendaGroup)
                .withActivationGroup(activationGroup)
                .withOrConstraints(orConstraints)
                .build();
        KiePMMLDescrRulesFactory.factory(builder).declareRule(rule);
        assertNotNull(builder.getDescr().getRules());
        assertEquals(1, builder.getDescr().getRules().size());
        final RuleDescr retrieved = builder.getDescr().getRules().get(0);
        assertEquals(name, retrieved.getName());
        assertEquals(2, retrieved.getAttributes().size());
        assertTrue(retrieved.getAttributes().containsKey("agenda-group"));
        assertEquals(agendaGroup, retrieved.getAttributes().get("agenda-group").getValue());
        assertTrue(retrieved.getAttributes().containsKey("activation-group"));
        assertEquals(activationGroup, retrieved.getAttributes().get("activation-group").getValue());
        printGeneratedRules();
    }

    private void printGeneratedRules() {
        try {
            String string = new DrlDumper().dump(builder.getDescr());
            logger.info(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}