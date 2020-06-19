/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.AlphaNodeOrderingOption;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlphaNodeOrderingTest extends BaseModelTest {

    public AlphaNodeOrderingTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testCountBasedAlphaNodeOrdering() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1 when\n" +
                     "  $p : Person(age != 0, age != 1, age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age != 1, age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : Person(age != 3)\n" +
                     "then\n" +
                     "end\n";

        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.newKieBaseModel("kb")
             .setDefault(true)
             .setAlphaNodeOrdering(AlphaNodeOrderingOption.COUNT)
             .newKieSessionModel("ks")
             .setDefault(true);

        KieSession ksession = getKieSession(model, str);

//        ReteDumper.dumpRete(ksession);
        List<AlphaNode> alphaNodes = ReteDumper.collectNodes(ksession)
                                               .stream()
                                               .filter(AlphaNode.class::isInstance)
                                               .map(node -> (AlphaNode) node)
                                               .collect(Collectors.toList());
        assertEquals(4, alphaNodes.size());

        if (testRunType == RUN_TYPE.STANDARD_FROM_DRL) {
            // Not found a good way to assert constraints in case of externalized LambdaConstraint
            // Anyway alphaNodes.size() == 4 means that alpha nodes are reordered so effectively shared
            Optional<AlphaNode> optAlphaNot3 = alphaNodes.stream()
                                                         .filter(node -> ((MvelConstraint)node.getConstraint()).getExpression().equals("age != 3"))
                                                         .findFirst();
            assertTrue(optAlphaNot3.isPresent());

            AlphaNode alphaNot3 = optAlphaNot3.get();

            AlphaNode alphaNot2 = getNextAlphaNode(alphaNot3);
            assertEquals("age != 2", ((MvelConstraint)alphaNot2.getConstraint()).getExpression());

            AlphaNode alphaNot1 = getNextAlphaNode(alphaNot2);
            assertEquals("age != 1", ((MvelConstraint)alphaNot1.getConstraint()).getExpression());

            AlphaNode alphaNot0 = getNextAlphaNode(alphaNot1);
            assertEquals("age != 0", ((MvelConstraint)alphaNot0.getConstraint()).getExpression());
        }

        ksession.insert(new Person("Mario", 1));
        assertEquals(2, ksession.fireAllRules());
    }

    private AlphaNode getNextAlphaNode(AlphaNode current) {
        Optional<AlphaNode> optNextAlpha = Arrays.stream(current.getSinks())
                                                 .filter(AlphaNode.class::isInstance)
                                                 .map(AlphaNode.class::cast)
                                                 .findFirst();
        assertTrue(optNextAlpha.isPresent());
        AlphaNode nextAlpha = optNextAlpha.get();
        return nextAlpha;
    }

    @Test
    public void testNoopAlphaNodeOrdering() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1 when\n" +
                     "  $p : Person(age != 0, age != 1, age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age != 1, age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(age != 2, age != 3)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : Person(age != 3)\n" +
                     "then\n" +
                     "end\n";

        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.newKieBaseModel("kb")
             .setDefault(true)
             .setAlphaNodeOrdering(AlphaNodeOrderingOption.NONE)
             .newKieSessionModel("ks")
             .setDefault(true);

        KieSession ksession = getKieSession(model, str);

        ReteDumper.dumpRete(ksession);
        List<AlphaNode> alphaNodes = ReteDumper.collectNodes(ksession)
                                               .stream()
                                               .filter(AlphaNode.class::isInstance)
                                               .map(node -> (AlphaNode) node)
                                               .collect(Collectors.toList());
        assertEquals(10, alphaNodes.size());

        ksession.insert(new Person("Mario", 1));
        assertEquals(2, ksession.fireAllRules());
    }
}
