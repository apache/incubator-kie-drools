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

package org.drools.compiler.integrationtests;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class ExistentialOperatorTest {

    @Test
    public void testForallAfterOr() throws Exception {
        // DROOLS-2710
        String str =
                "package redhat\n" +
                "declare Fact\n" +
                "    integer : int\n" +
                "    string1 : String\n" +
                "    string2 : String\n" +
                "end\n" +
                "rule \"Rule\"\n" +
                "when\n" +
                "Fact(string2 == \"Y\")\n" +
                "(\n" +
                "    exists (Fact(integer == 42)) or\n" +
                "    Fact(integer == 43)\n" +
                ")\n" +
                "forall (Fact(string1 == \"X\"))\n" +
                "then\n" +
                "end";

        KieBase kieBase = new KieHelper().addContent( str, ResourceType.DRL ).build();
        KieSession kieSession = kieBase.newKieSession();

        FactType factType = kieBase.getFactType("redhat", "Fact");

        Object fact = factType.newInstance();
        factType.set(fact, "string1", "X");
        factType.set(fact, "string2", "Y");
        factType.set(fact, "integer", 42);

        kieSession.insert(fact);

        int n = kieSession.fireAllRules();
        Assert.assertEquals(1, n);
    }
}
