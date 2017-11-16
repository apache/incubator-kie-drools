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
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertTrue;

public class EntryPointTest extends CommonTestMethodBase {

    @Test
    public void testEntryPointWithVarIN() {
        final String str = "package org.drools.compiler.test;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"In\"\n" +
                "when\n" +
                "   $x : Integer()\n " +
                "then\n" +
                "   drools.getEntryPoint(\"inX\").insert( $x );\n" +
                "end\n" +
                "\n" +
                "rule \"Out\"\n" +
                "when\n" +
                "   $i : Integer() from entry-point \"inX\"\n" +
                "then\n" +
                "   list.add( $i );\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(10);

        final List res = new ArrayList();
        ksession.setGlobal("list", res);

        ksession.fireAllRules();
        ksession.dispose();
        assertTrue(res.contains(10));
    }
}
