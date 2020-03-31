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

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KiePMMLDescrRulesFactoryTest {

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
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        final Map<String, List<KiePMMLOperatorValue>> orConstraints = Collections.singletonMap(patternType, kiePMMLOperatorValues);
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet)
                .withOrConstraints(orConstraints)
                .build();
        KiePMMLDescrRulesFactory.factory(builder).declareRule(rule);
        assertNotNull(builder.getDescr().getRules());
        assertEquals(1, builder.getDescr().getRules().size());
        assertEquals(name, builder.getDescr().getRules().get(0).getName());
    }
}