/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.pmml.pmml_4_2.predictive;


import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AttributesTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "org/drools/pmml/pmml_4_2/test_headerAttribs.xml";

    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testRuleAttributes() throws Exception {
        setKSession( getModelSession( source, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        checkGeneratedRules();

        for ( Rule r : getKbase().getKiePackage( "org.drools.pmml.attribs.test" ).getRules() ) {
            assertEquals( "test-rf", ( (RuleImpl) r ).getAgendaGroup() );
            assertEquals( "test-rf", ((RuleImpl) r).getRuleFlowGroup() );
        }

    }

}
