/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class ListenersTest extends BaseModelTest {

    public ListenersTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testInsert() {
        String str =
                "rule R\n" +
                "when\n" +
                "  $i: Integer()\n" +
                "then\n" +
                "  insert(\"\" + $i);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        List<String> results = new ArrayList<>();

        final RuleRuntimeEventListener workingMemoryListener = new RuleRuntimeEventListener() {
            public void objectInserted( ObjectInsertedEvent event) {
                if (event.getObject() instanceof String) {
                    results.add( event.getRule().getName() );
                }
            }

            public void objectUpdated( ObjectUpdatedEvent event) {
            }

            public void objectDeleted( ObjectDeletedEvent event) {
            }

        };

        ksession.addEventListener( workingMemoryListener );

        ksession.insert(42);
        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, results.size());
        assertEquals("R", results.get(0));
    }
}
