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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

public class BindTest extends CommonTestMethodBase {

    @Test
    public void testFactBindings() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FactBindings.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final RuleRuntimeEventListener wmel = mock(RuleRuntimeEventListener.class);
        ksession.addEventListener(wmel);

        final Person bigCheese = new Person("big cheese");
        final Cheese cheddar = new Cheese("cheddar", 15);
        bigCheese.setCheese(cheddar);

        final FactHandle bigCheeseHandle = ksession.insert(bigCheese);
        final FactHandle cheddarHandle = ksession.insert(cheddar);
        ksession.fireAllRules();

        final ArgumentCaptor<ObjectUpdatedEvent> arg = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectUpdatedEvent.class);
        verify(wmel, times(2)).objectUpdated(arg.capture());

        org.kie.api.event.rule.ObjectUpdatedEvent event = arg.getAllValues().get(0);
        assertSame(cheddarHandle, event.getFactHandle());
        assertSame(cheddar, event.getOldObject());
        assertSame(cheddar, event.getObject());

        event = arg.getAllValues().get(1);
        assertSame(bigCheeseHandle, event.getFactHandle());
        assertSame(bigCheese, event.getOldObject());
        assertSame(bigCheese, event.getObject());
    }

    @Test
    public void testBindingToMissingField() throws Exception {
        // JBRULES-3047
        String rule1 = "package org.drools.compiler\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "    Integer( $i : noSuchField ) \n";
        rule1 += "    eval( $i > 0 )\n";
        rule1 += "then \n";
        rule1 += "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()), ResourceType.DRL);

        if (!kbuilder.hasErrors()) {
            fail("this should have errors");
        }
    }

    @Test
    public void testFieldBindingOnWrongFieldName() {
        //JBRULES-2527

        String str = "";
        str += "package org.drools.compiler\n";
        str += "import org.drools.compiler.Person\n";
        str += "global java.util.List mlist\n";
        str += "rule rule1 \n";
        str += "when\n";
        str += "   Person( $f : invalidFieldName, eval( $f != null ) )\n";
        str += "then\n";
        str += "end\n";

        testBingWrongFieldName(str);

        str = "";
        str += "package org.drools.compiler\n";
        str += "import org.drools.compiler.Person\n";
        str += "global java.util.List mlist\n";
        str += "rule rule1 \n";
        str += "when\n";
        str += "   Person( $f : invalidFieldName, name == ( $f ) )\n";
        str += "then\n";
        str += "end\n";

        testBingWrongFieldName(str);
    }

    private void testBingWrongFieldName(final String drl) {
        try {
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

            if (!kbuilder.hasErrors()) {
                fail("KnowledgeBuilder should have errors");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Exception should not be thrown ");
        }
    }

    @Test
    public void testBindingsOnConnectiveExpressions() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_bindings.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(new Cheese("stilton", 15));

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals("stilton", results.get(0));
        assertEquals(15, results.get(1));
    }

    @Test
    public void testAutomaticBindings() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_AutoBindings.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("bob", "stilton");
        final Cheese stilton = new Cheese("stilton", 12);
        ksession.insert(bob);
        ksession.insert(stilton);

        ksession.fireAllRules();
        assertEquals(1, list.size());

        assertEquals(bob, list.get(0));
    }
}
