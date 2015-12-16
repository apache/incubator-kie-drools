/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PassivePatternTest {

    @Test
    public void testPassiveInsert() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("2");
        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.insert(2);
        ksession.fireAllRules();
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList(1, 2)));
    }

}
