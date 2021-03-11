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

package org.drools.mvel.compiler.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.TerminalNodeIterator;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.Iterator;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TerminalNodeIteratorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TerminalNodeIteratorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testTerminalNodeListener() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule5 when\n" + // this will result in two terminal nodes
                     "    Object() or\n" +
                     "    Object()\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        List<String> nodes = new ArrayList<String>();
        Iterator it = TerminalNodeIterator.iterator(kbase);
        for ( TerminalNode node = (TerminalNode) it.next(); node != null; node = (TerminalNode) it.next() ) {
            nodes.add( ((RuleTerminalNode) node).getRule().getName() );
        }

        assertEquals( 6,
                      nodes.size() );
        assertTrue( nodes.contains( "rule1" ) );
        assertTrue( nodes.contains( "rule2" ) );
        assertTrue( nodes.contains( "rule3" ) );
        assertTrue( nodes.contains( "rule4" ) );
        assertTrue( nodes.contains( "rule5" ) );

        int first = nodes.indexOf( "rule5" );
        int second = nodes.lastIndexOf( "rule5" );
        assertTrue( first != second );
    }

}
