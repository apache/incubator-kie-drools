/*
 * Copyright 2015 JBoss Inc
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
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeDeclarationTest {

    @Test
    public void testRecursiveDeclaration() throws Exception {
        String rule = "package org.drools.compiler\n" +
                      "declare Node\n" +
                      "    value: String\n" +
                      "    parent: Node\n" +
                      "end\n" +
                      "rule R1 when\n" +
                      "   $parent: Node( value == \"parent\" )\n" +
                      "   $child: Node( $value : value, parent == $parent )\n" +
                      "then\n" +
                      "   System.out.println( $value );\n" +
                      "end";

        KieBase kbase = new KieHelper().addContent(rule, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        FactType nodeType = kbase.getFactType( "org.drools.compiler", "Node" );
        Object parent = nodeType.newInstance();
        nodeType.set( parent, "value", "parent" );
        ksession.insert( parent );

        Object child = nodeType.newInstance();
        nodeType.set( child, "value", "child" );
        nodeType.set( child, "parent", parent );
        ksession.insert( child );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }

    @Test
    public void testCircularDeclaration() throws Exception {
        String rule = "package org.drools.compiler.test\n" +
                      "declare FactA\n" +
                      "    fieldB: FactB\n" +
                      "end\n" +
                      "declare FactB\n" +
                      "    fieldA: FactA\n" +
                      "end\n" +
                      "rule R1 when\n" +
                      "   $fieldA : FactA( $fieldB : fieldB )\n" +
                      "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                      "then\n" +
                      "end";

        KieBase kbase = new KieHelper().addContent(rule, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        FactType aType = kbase.getFactType( "org.drools.compiler.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.compiler.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }

    @Test
    public void testCircularDeclarationWithExtension() throws Exception {
        // DROOLS-640
        String drl = "package org.drools.compiler.test\n" +
                     "declare FactA\n" +
                     "    fieldB: FactB\n" +
                     "end\n" +
                     "declare FactB extends FactA end\n" +
                     "rule R1 when\n" +
                     "   $a : FactA( )\n" +
                     "   $b : FactB( this == $a.fieldB )\n" +
                     "then\n" +
                     "end";

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        FactType aType = kbase.getFactType( "org.drools.compiler.test", "FactA" );
        FactType bType = kbase.getFactType( "org.drools.compiler.test", "FactB" );
        Object a = aType.newInstance();
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }

	public static class BaseClass {
		String baseField;
		public String getBaseField() {
			return baseField;
		}
		public void setBaseField(String baseField) {
			this.baseField = baseField;
		}
	}

    @Test
    public void testDeclarationWithPojoExtension() throws Exception {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "declare Fact extends org.drools.compiler.integrationtests.TypeDeclarationTest.BaseClass\n" +
                     "    field: String\n" +
                     "end\n" +
                     "rule R1 when\n" +
                     "   $fact : Fact( field == baseField )\n" +
                     "then\n" +
                     "end";

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        FactType factType = kbase.getFactType( "org.drools.compiler.integrationtests", "Fact" );
        Object fact = factType.newInstance();
        factType.set( fact, "field", "foo" );
        //factType.set( fact, "baseField", "foo" ); // This doesn't work, so case it to the base type
		((TypeDeclarationTest.BaseClass) fact).setBaseField("foo");
        ksession.insert( fact );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }

    @Test
    public void testDeclarationWithPojoExtensionDifferentPackage() throws Exception {
        String drlBase = "package org.drools.compiler.integrationtests\n";
        String drl = "package org.drools.compiler.test\n" +
                     "declare Fact extends org.drools.compiler.integrationtests.TypeDeclarationTest.BaseClass\n" +
                     "    field: String\n" +
                     "end\n" +
                     "rule R1 when\n" +
                     "   $fact : Fact( field == baseField )\n" +
                     "then\n" +
                     "end";

        KieBase kbase = new KieHelper().addContent(drlBase, ResourceType.DRL).addContent(drl, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        FactType factType = kbase.getFactType( "org.drools.compiler.test", "Fact" );
        Object fact = factType.newInstance();
        factType.set( fact, "field", "foo" );
        //factType.set( fact, "baseField", "foo" ); // This doesn't work, so case it to the base type
		((TypeDeclarationTest.BaseClass) fact).setBaseField("foo");
        ksession.insert( fact );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }

}
