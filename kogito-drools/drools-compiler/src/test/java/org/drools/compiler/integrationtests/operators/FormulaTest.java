/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.operators;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class FormulaTest extends CommonTestMethodBase {

    @Test
    public void testConstants() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_formulaConstantsConstraint.drl");
        KieSession ksession = kbase.newKieSession();

        final Person person = new Person();
        person.setAge(5);

        ksession.insert(person);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBoundField() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_formulaBoundFieldConstraint.drl");
        KieSession ksession = kbase.newKieSession();

        final Person person = new Person();
        person.setAge(10);

        ksession.insert(person);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals(1, ksession.fireAllRules());
    }

}
