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

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class MvelOperatorsTest extends BaseModelTest {

    public MvelOperatorsTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testIn() {
        String str =
            "rule R when\n" +
            "    String(this in (\"a\", \"b\"))" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "b" );
        ksession.insert( "b" );
        assertEquals(1, ksession.fireAllRules());
    }
}
