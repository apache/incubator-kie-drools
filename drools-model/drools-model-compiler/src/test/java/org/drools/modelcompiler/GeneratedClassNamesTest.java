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

package org.drools.modelcompiler;

import java.util.Set;
import java.util.UUID;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GeneratedClassNamesTest extends BaseModelTest {

    public GeneratedClassNamesTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{RUN_TYPE.PATTERN_DSL};
    }

    @Test
    public void testGeneratedClassNames() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  System.out.println(\"hello\");\n" +
                     "end";

        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");

        createKieBuilder(ks, null, releaseId, toKieFiles(new String[]{str}));
        KieContainer kcontainer = ks.newKieContainer(releaseId);

        KieModule kieModule = ((KieContainerImpl) kcontainer).getKieModuleForKBase("defaultKieBase");

        assertTrue(kieModule instanceof CanonicalKieModule);

        Set<String> generatedClassNames = ((CanonicalKieModule) kieModule).getGeneratedClassNames();
        assertGeneratedClassNames(generatedClassNames);

        KieSession ksession = kcontainer.newKieSession();

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }

    private void assertGeneratedClassNames(Set<String> generatedClassNames) {
        String[] nameFragments = new String[]{"Rules", "LambdaConsequence", "LambdaPredicate", "LambdaExtractor", "DomainClassesMetadata", "ProjectModel"};
        for (String nameFragment : nameFragments) {
            boolean contains = false;
            for (String generatedClassName : generatedClassNames) {
                if (generatedClassName.contains(nameFragment)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                fail("generatedClassNames doesn't contain [" + nameFragment + "] class. : " + generatedClassNames);
            }
        }
    }
}
