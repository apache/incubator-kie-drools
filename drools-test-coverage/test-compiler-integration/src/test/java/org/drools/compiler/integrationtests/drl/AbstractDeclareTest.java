/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.List;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDeclareTest {
    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractDeclareTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testDeclaresWithArrayFields() {
        final String drl = "package org.drools.compiler.integrationtests.drl; \n" +
                "import " + Person.class.getName() + ";\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Cheese\n" +
                "   name : String = \"ched\" \n" +
                "end \n" +
                "" +
                "declare X\n" +
                "    fld \t: String   = \"xx\"                                      @key \n" +
                "    achz\t: Cheese[] \n" +
                "    astr\t: String[] " + " = new String[] {\"x\", \"y11\" } \n" +
                "    aint\t: int[] \n" +
                "    sint\t: short[] \n" +
                "    bint\t: byte[] \n" +
                "    lint\t: long[] \n" +
                "    dint\t: double[] \n" +
                "    fint\t: float[] \n" +
                "    zint\t: Integer[] " + " = new Integer[] {2,3}                   @key \n" +
                "    aaaa\t: String[][] \n" +
                "    bbbb\t: int[][] \n" +
                "    aprs\t: Person[] " + " = new Person[] { } \n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "\n" +
                "then\n" +
                "    X x = new X( \"xx\", \n" +
                "                 new Cheese[0], \n" +
                "                 new String[] { \"x\", \"y22\" }, \n" +
                "                 new int[] { 7, 9 }, \n" +
                "                 new short[] { 3, 4 }, \n" +
                "                 new byte[] { 1, 2 }, \n" +
                "                 new long[] { 100L, 200L }, \n" +
                "                 new double[] { 3.2, 4.4 }, \n" +
                "                 new float[] { 3.2f, 4.4f }, \n" +
                "                 new Integer[] { 2, 3 }, \n" +
                "                 new String[2][3], \n" +
                "                 new int[5][3], \n" +
                "                 null \n" +
                "    ); \n" +
                "   insert( x );\n" +
                "   " +
                "   X x2 = new X(); \n" +
                "   x2.setAint( new int[2] ); \n " +
                "   x2.getAint()[0] = 7; \n" +
                "   insert( x2 );\n" +
                "   " +
                "   if ( x.hashCode() == x2.hashCode() ) list.add( \"hash\" );  \n" +
                "   " +
                "   if( x.equals( x2 ) ) list.add( \"equals\" );  \n" +
                "   " +
                "   list.add( x.getAint(  )[0] );  \n" +
                "end \n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "    X( astr != null,               \n" +
                "       astr.length > 0,            \n" +
                "       astr[0] == \"x\",           \n" +
                "       $x : astr[1],               \n" +
                "       aint[0] == 7  )             \n" +
                "then\n" +
                " System.out.println(\"Fired!!!!!!!!!!!!!!\" + $x); \n" +
                "    list.add( $x );\n" +
                "end \n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal( "list", list );

            ksession.fireAllRules();
            System.out.println(list);
            assertThat(list.contains("hash")).isTrue();
            assertThat(list.contains("equals")).isTrue();
            assertThat(list.contains(7)).isTrue();
            // The X instances are considered equal so when using EQUALITY, the second insert doesn't insert anything,
            // therefore, the fire produced by the second insert should be checked just with IDENTITY.
            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list.contains("y11")).isTrue();
            }
            assertThat(list.contains("y22")).isTrue();
        } finally {
            ksession.dispose();
        }
    }
}
