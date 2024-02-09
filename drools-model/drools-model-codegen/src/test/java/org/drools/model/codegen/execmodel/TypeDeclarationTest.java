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
package org.drools.model.codegen.execmodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeDeclarationTest extends BaseModelTest {

    public TypeDeclarationTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testRecursiveDeclaration() throws Exception {
        String str =
              "package org.drools.compiler\n" +
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

        KieSession ksession = getKieSession( str );
        KieBase kbase = ksession.getKieBase();

        FactType nodeType = kbase.getFactType( "org.drools.compiler", "Node" );
        Object parent = nodeType.newInstance();
        nodeType.set( parent, "value", "parent" );
        ksession.insert( parent );

        Object child = nodeType.newInstance();
        nodeType.set( child, "value", "child" );
        nodeType.set( child, "parent", parent );
        ksession.insert( child );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testGenerics() throws Exception {
        // DROOLS-4939
        String str =
              "package org.drools.compiler\n" +
              "import java.util.List\n" +
              "declare Node\n" +
              "    values: List<String>\n" +
              "end\n" +
              "rule R1 when\n" +
              "   $node: Node( values.get(0).length == 4 )\n" +
              "then\n" +
              "   System.out.println( $node );\n" +
              "end";

        KieSession ksession = getKieSession( str );
        KieBase kbase = ksession.getKieBase();

        FactType nodeType = kbase.getFactType( "org.drools.compiler", "Node" );
        Object parent = nodeType.newInstance();
        nodeType.set(parent, "values", List.of("test"));
        ksession.insert( parent );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    public interface ValuesProvider {
        Map<String, String> getValues();
    }

    @Test
    public void testGenericsMap() throws Exception {
        // DROOLS-4939
        String str =
              "package org.drools.compiler\n" +
              "import " + ValuesProvider.class.getCanonicalName() + "\n" +
              "import java.util.Map\n" +
              "declare Node extends ValuesProvider\n" +
              "    values: Map<String, String>\n" +
              "end\n" +
              "rule R1 when\n" +
              "   $node: Node( values.get(\"value\").length == 4 )\n" +
              "then\n" +
              "   System.out.println( $node );\n" +
              "end";

        KieSession ksession = getKieSession( str );
        KieBase kbase = ksession.getKieBase();

        FactType nodeType = kbase.getFactType( "org.drools.compiler", "Node" );
        Object parent = nodeType.newInstance();
        Map<String,String> map = new HashMap<>();
        map.put("value", "test");
        nodeType.set( parent, "values", map );
        ksession.insert( parent );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testSerialVersionUID() throws Exception {
        // DROOLS-5340
        String str =
                "package org.drools.compiler\n" +
                "import java.util.*\n" +
                "declare ServiceInformation\n" +
                "    @serialVersionUID( 0 )\n" +
                "    code: String @key\n" +
                "    text : String\n" +
                "    associations : List\n" +
                "end\n" +
                "\n" +
                "rule \"Match first and last name\"\n" +
                "when \n" +
                "then \n" +
                "   insert( new ServiceInformation(\"123456\", \"ServiceTest\", new ArrayList()) );\n" +
                "end";

        KieSession ksession = getKieSession( str );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testSerialVersionUIDWithAllkeys() throws Exception {
        // DROOLS-5400
        String str =
                "package org.drools.compiler\n" +
                "import java.util.*\n" +
                "declare ServiceInformation\n" +
                "    @serialVersionUID( 0 )\n" +
                "    code: String @key\n" +
                "    text : String @key\n" +
                "    associations : List @key\n" +
                "end\n" +
                "\n" +
                "rule \"Match first and last name\"\n" +
                "when \n" +
                "then \n" +
                "   insert( new ServiceInformation(\"123456\", \"ServiceTest\", new ArrayList()) );\n" +
                "end";

        KieSession ksession = getKieSession( str );
        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testPositionalWithLiteral() {
        // DROOLS-6128
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "declare Person\n" +
                "    name : String \n" +
                "    age : int \n" +
                "end" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new Person(\"Mark\", 37));\n" +
                "  insert(new Person(\"Mario\", 40));\n" +
                "end\n" +
                "rule X when\n" +
                "  Person ( \"Mark\", $age; )\n" +
                "then\n" +
                "  insert(new Result($age));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(37);
    }

    @Test
    public void testPositionalWithJoin() {
        // DROOLS-6128
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "declare Person\n" +
                "    name : String \n" +
                "    age : int \n" +
                "end" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new Person(\"Mark\", 37));\n" +
                "  insert(new Person(\"Mario\", 40));\n" +
                "end\n" +
                "rule X when\n" +
                "  $s: String()" +
                "  Person ( $s, $age; )\n" +
                "then\n" +
                "  insert(new Result($age));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "Mark" );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(37);
    }
}
