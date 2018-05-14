/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class DroolsContextTest extends BaseModelTest {

    public DroolsContextTest(final RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testDroolsContext() {
        final String str =
                "global java.util.List list\n" +
                        "global java.util.List list2\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(list2.add(kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testDroolsContextInString() {
        final String str =
                "global java.util.List list\n" +
                        "global java.util.List list2\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(list2.add(\"something\" + kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testDroolsContextWithoutReplacingStrings() {
        final String str =
                "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(\"this kcontext shoudln't be replaced\");\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals("this kcontext shoudln't be replaced", list.get(0));
    }

    @Test
    public void testRuleContext() {
        final String str =
                "import " + FactWithRuleContext.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "    $factWithRuleContext: FactWithRuleContext() \n" +
                        "then\n" +
                        " list.add($factWithRuleContext.getRuleName(kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new FactWithRuleContext());
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals("R", list.iterator().next());
    }

    public static class FactWithRuleContext {
        public String getRuleName(final org.kie.api.runtime.rule.RuleContext ruleContext) {
            return ruleContext.getRule().getName();
        }
    }
}
