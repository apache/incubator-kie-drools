/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.compiler.definitions;


import java.util.Collection;

import org.drools.core.definitions.rule.impl.GlobalImpl;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class KnowledgePackageMetaDataTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KnowledgePackageMetaDataTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private String drl ="" +
            "package org.drools.mvel.compiler.test.definitions \n" +
            "import java.util.List; \n" +
            "\n" +
            "global Integer N; \n" +
            "global List list; \n" +
            "\n" +
            "function void fun1() {}\n" +
            "\n" +
            "function String fun2( int j ) { return null; } \n" +
            "\n" +
            "declare Person\n" +
            "  name : String\n" +
            "  age  : int\n" +
            "end\n" +
            "\n" +
            "declare Foo extends Person\n" +
            "   bar : String\n" +
            "end \n" +
            "\n" +
            "query qry1() \n" +
            "  Foo()\n" +
            "end\n" +
            "\n" +
            "query qry2( String x )\n" +
            "  x := String()\n" +
            "end\n" +
            "\n" +
            "rule \"rule1\"\n" +
            "when\n" +
            "then\n" +
            "end\n" +
            "\n" +
            "rule \"rule2\"\n" +
            "when\n" +
            "then\n" +
            "end";

    @Test
    public void testMetaData() {
        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KiePackage pack = kBase.getKiePackage( "org.drools.mvel.compiler.test.definitions" );

        assertNotNull( pack );

        if (!kieBaseTestConfiguration.isExecutableModel()) {
            // With executable model functions becomes plain static methods and then now longer distinguishable from any other static method
            assertEquals(2, pack.getFunctionNames().size());
            assertTrue(pack.getFunctionNames().contains("fun1"));
            assertTrue(pack.getFunctionNames().contains("fun2"));
        }

        assertEquals( 2, pack.getGlobalVariables().size() );
        GlobalImpl g1 = new GlobalImpl( "N", "java.lang.Integer" );
        GlobalImpl g2 = new GlobalImpl( "list", "java.util.List" );
        assertTrue( pack.getGlobalVariables().contains( g1 ) );
        assertTrue( pack.getGlobalVariables().contains( g2 ) );

        assertEquals( 2, pack.getFactTypes().size() );
        FactType type;
        for ( int j = 0; j < 2; j++ ) {
            type = pack.getFactTypes().iterator().next();
            if ( type.getName().equals( "org.drools.mvel.compiler.test.definitions.Person" ) ) {
                assertEquals( 2, type.getFields().size() );
            } else if (type.getName().equals( "org.drools.mvel.compiler.test.definitions.Foo" ) ) {
                assertEquals( "org.drools.mvel.compiler.test.definitions.Person", type.getSuperClass() );

                FactField fld = type.getField( "bar" );
                assertEquals( 2, fld.getIndex() );
                assertEquals( String.class, fld.getType() );
            } else {
                fail("Unexpected fact type " + type);
            }
        }

        assertEquals( 2, pack.getQueries().size() );
        for ( Query q : pack.getQueries() ) {
            assertTrue( q.getName().equals( "qry1" ) || q.getName().equals( "qry2" ) );
        }

        assertEquals( 4, pack.getRules().size() );
        assertTrue( pack.getRules().containsAll( pack.getQueries() ) );
    }
}
