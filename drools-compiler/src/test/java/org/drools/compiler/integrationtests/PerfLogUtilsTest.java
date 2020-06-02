/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.util.PerfLogUtils;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class PerfLogUtilsTest extends CommonTestMethodBase {

    @Test
    public void testCrossProduct() {
        try {
            PerfLogUtils.setEnabled(true);
            PerfLogUtils.setThreshold(-1);

            String str =
                    "import " + Address.class.getCanonicalName() + "\n" +
                         "import " + Person.class.getCanonicalName() + "\n" +
                         "rule R1\n" +
                         "when\n" +
                         "  $p1 : Person()\n" +
                         "  $p2 : Person(age > $p1.age)\n" +
                         "then\n" +
                         "end\n";

            KieBase kbase = loadKnowledgeBaseFromString(str);

            List<Person> personList = IntStream.range(0, 100).mapToObj(i -> new Person("John" + i, i)).collect(Collectors.toList());

//            for (int i = 0; i < 100; i++) {
                KieSession ksession = kbase.newKieSession();
                personList.stream().forEach(ksession::insert);

                long start = System.nanoTime();
                ksession.fireAllRules();
                System.out.println("  total elapsedMicro : " + (System.nanoTime() - start) / 1000);
                ksession.dispose();
//            }
        } finally {
            PerfLogUtils.setEnabled(false);
            PerfLogUtils.setThreshold(500);
        }

    }
}
