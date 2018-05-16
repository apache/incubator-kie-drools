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

package org.drools.compiler.integrationtests.drl;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.State;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.junit.Assert.assertEquals;

public class NestingTest extends CommonTestMethodBase {

    @Test
    public void testNesting() throws Exception {
        final Person p = new Person();
        p.setName("Michael");

        final Address add1 = new Address();
        add1.setStreet("High");

        final Address add2 = new Address();
        add2.setStreet("Low");

        final List l = new ArrayList();
        l.add(add1);
        l.add(add2);

        p.setAddresses(l);

        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr desc = parser.parse(new InputStreamReader(getClass().getResourceAsStream("nested_fields.drl")));
        final List packageAttrs = desc.getAttributes();
        assertEquals(1, desc.getRules().size());
        assertEquals(1, packageAttrs.size());

        final RuleDescr rule = desc.getRules().get(0);
        final Map<String, AttributeDescr> ruleAttrs = rule.getAttributes();
        assertEquals(1, ruleAttrs.size());

        assertEquals("mvel", ruleAttrs.get("dialect").getValue());
        assertEquals("dialect", ruleAttrs.get("dialect").getName());

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(desc));
        KieSession session = createKnowledgeSession(kbase);

        session.insert(p);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        session.fireAllRules();
    }

    @Test
    public void testNestedConditionalElements() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_NestedConditionalElements.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final State state = new State("SP");
        ksession.insert(state);

        final Person bob = new Person("Bob");
        bob.setStatus(state.getState());
        bob.setLikes("stilton");
        ksession.insert(bob);

        ksession.fireAllRules();

        assertEquals(0, list.size());

        ksession.insert(new Cheese(bob.getLikes(), 10));
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

}
