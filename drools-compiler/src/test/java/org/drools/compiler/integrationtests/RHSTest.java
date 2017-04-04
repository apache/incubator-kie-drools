/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.ClassObjectFilter;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RHSTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger( RHSTest.class );

    @Test
    public void testUnderscoreDoubleMultiplicationCastedToInt() {
        // In Java it's legal to do:
        int a = 42;
        int b = ((int) (a * 1_000.0));

        // But if we do the same thing in drools:
        String str =
                "import org.drools.compiler.Cheese\n" +
                "rule R when\n" +
                "  Cheese( $p : price)\n" +
                "then\n" +
                "  int b = (int) ($p * 1_000.0);\n" +
                "end\n";

        // TODO Currently fails with "$p cannot be resolved to a variable"
        // which is the wrong error message in Drools, even if it's ECJ's fault
        // If you believe Drools error messages (I did), this is a misleading error message wastes a lot of time

        // Removing the underscore (Java 7 syntax) fixes it, but drools says it supports Java 8 and lower syntax in the RHS.
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();


        Cheese c1 = new Cheese( "gauda", 42 );
        FactHandle fh2 = ksession.insert( c1 );
        ksession.fireAllRules();
    }

}
