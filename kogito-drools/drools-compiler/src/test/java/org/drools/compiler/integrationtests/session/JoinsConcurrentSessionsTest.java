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

package org.drools.compiler.integrationtests.session;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.integrationtests.facts.AnEnum;
import org.drools.compiler.integrationtests.facts.ChildFact1;
import org.drools.compiler.integrationtests.facts.ChildFact2;
import org.drools.compiler.integrationtests.facts.ChildFact3WithEnum;
import org.drools.compiler.integrationtests.facts.ChildFact4WithFirings;
import org.drools.compiler.integrationtests.facts.RootFact;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class JoinsConcurrentSessionsTest extends AbstractConcurrentSessionTest {

    public JoinsConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase) {
        super(enforcedJitting, serializeKieBase);
    }

    @Test
    public void test5() throws InterruptedException {
        final String drlTemplate =
                " import org.drools.compiler.integrationtests.facts.*;\n" +
                        " rule \"${ruleName}\"\n" +
                        " dialect \"java\"\n" +
                        " when\n" +
                        "     $rootFact : RootFact( )\n" +
                        "     $childFact1 : ChildFact1( parentId == $rootFact.id )\n" +
                        "     $childFact2 : ChildFact2( parentId == $childFact1.id )\n" +
                        "     $childFact3 : ChildFact3WithEnum( \n" +
                        "         parentId == $childFact2.id, \n" +
                        "         enumValue == ${enumValue}, \n" +
                        "         $enumValue : enumValue )\n" +
                        "     $childFact4 : ChildFact4WithFirings( \n" +
                        "         parentId == $childFact1.id, \n" +
                        "         $evaluationName : evaluationName, \n" +
                        "         firings not contains \"${ruleName}\" )\n" +
                        " then\n" +
                        "     $childFact4.setEvaluationName(String.valueOf($enumValue));\n" +
                        "     $childFact4.getFirings().add(\"${ruleName}\");\n" +
                        "     update($childFact4);\n" +
                        " end\n";

        final String drl1 = drlTemplate.replace("${ruleName}", "R1").replace("${enumValue}", "AnEnum.FIRST");
        final String drl2 = drlTemplate.replace("${ruleName}", "R2").replace("${enumValue}", "AnEnum.SECOND");

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                final List<Object> facts = getFacts();
                for (Object fact : facts) {
                    kieSession.insert(fact);
                }
                kieSession.fireAllRules();

                for (Object fact : facts) {
                    if (fact instanceof ChildFact4WithFirings) {
                        final ChildFact4WithFirings childFact4 = (ChildFact4WithFirings) fact;
                        if (childFact4.getFirings().size() != 1) {
                            return false;
                        } else if (childFact4.getFirings().get(0).equals("R1") && !childFact4.getEvaluationName().equals(String.valueOf(AnEnum.FIRST))) {
                            return false;
                        } else if (childFact4.getFirings().get(0).equals("R2") && !childFact4.getEvaluationName().equals(String.valueOf(AnEnum.SECOND))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }, drl1, drl2 );
    }

    private List<Object> getFacts() {
        int initialId = 1;
        final RootFact rootFact = new RootFact(initialId);

        final ChildFact1 childFact1First = new ChildFact1(initialId + 1, rootFact.getId());
        final ChildFact2 childFact2First = new ChildFact2(initialId + 3, childFact1First.getId());
        final ChildFact3WithEnum childFact3First = new ChildFact3WithEnum(initialId + 4, childFact2First.getId(), AnEnum.FIRST);
        final ChildFact4WithFirings childFact4First = new ChildFact4WithFirings(initialId + 2, childFact1First.getId());

        initialId = 6;
        final ChildFact1 childFact1Second = new ChildFact1(initialId, rootFact.getId());
        final ChildFact2 childFact2Second = new ChildFact2(initialId + 2, childFact1Second.getId());
        final ChildFact3WithEnum childFact3Second = new ChildFact3WithEnum(initialId + 3, childFact2Second.getId(), AnEnum.SECOND);
        final ChildFact4WithFirings childFact4Second = new ChildFact4WithFirings(initialId + 1, childFact1Second.getId());

        // Intentional order and IDs
        final List<Object> facts = new ArrayList<>();
        facts.add(rootFact);

        facts.add(childFact1First);
        facts.add(childFact4First);
        facts.add(childFact2First);
        facts.add(childFact3First);

        facts.add(childFact1Second);
        facts.add(childFact4Second);
        facts.add(childFact2Second);
        facts.add(childFact3Second);
        return facts;
    }

}
