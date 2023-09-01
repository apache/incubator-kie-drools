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
package org.drools.model.codegen.execmodel.inlinecast;

import java.util.Collection;
import java.util.List;

import org.drools.model.codegen.execmodel.BaseModelTest;
import org.drools.model.codegen.execmodel.domain.InternationalAddress;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class InlineCastTest extends BaseModelTest {

    public InlineCastTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testInlineCastThis() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $o : Object( this#Person.name == \"Mark\" )\n" +
                "then\n" +
                "  $r.setValue(\"Found: \" + $o);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( "Mark" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Found: Mark");
    }

    @Test
    public void testInlineCastProjectionThis() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p : Object( $name : this#Person.name )\n" +
                "then\n" +
                "  $r.setValue(\"Found: \" + $name + \" $p class: \" + $p.getClass().getCanonicalName());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( "Mark" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Found: Mark $p class: " + Person.class.getCanonicalName());
    }

    @Test
    public void testInlineCastProjectionThisExplicit() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p : Object( this instanceof Person, $name : this#Person.name )\n" +
                "then\n" +
                "  $r.setValue(\"Found: \" + $name + \" $p class: \" + $p.getClass().getCanonicalName());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( "Mark" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Found: Mark $p class: " + Person.class.getCanonicalName());
    }


    public interface DecisionTable extends Expression {
        List<OutputClause> getOutput();
    }

    public interface OutputClause extends DMNElement {

    }

    public interface Expression extends DMNElement {

    }

    public interface DMNElement extends DMNModelInstrumentedBase {

    }

    public interface DMNModelInstrumentedBase {
        DMNModelInstrumentedBase getParent();
    }
    
    @Ignore("This test is not testing anything")
    @Test
    public void testExplicitCast() {
        String str =
                "import " + OutputClause.class.getCanonicalName() + "\n;" +
                        "import " + DecisionTable.class.getCanonicalName() + "\n;" +
                        "rule r\n" +
                        "when\n" +
                        "  $oc : OutputClause( parent instanceof DecisionTable, parent.output.size > 1 )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();
    }

    @Ignore("This test is not testing anything")
    @Test
    public void testInlineCastParent() {
        String str =
                "import " + OutputClause.class.getCanonicalName() + "\n;" +
                        "import " + DecisionTable.class.getCanonicalName() + "\n;" +
                        "rule r\n" +
                        "when\n" +
                        "  $oc : OutputClause( parent#DecisionTable.output.size > 1 )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();
    }

    @Test
    public void testInlineCastProjection() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                "import " + InternationalAddress.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person( $a : address#InternationalAddress.state )\n" +
                "then\n" +
                "  insert($a);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        InternationalAddress a = new InternationalAddress("address", "Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.iterator().next()).isEqualTo("Italy");
    }

    @Test
    public void testInlineCastProjectionOnMethod() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                "import " + InternationalAddress.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person( $a : address#InternationalAddress.getState() )\n" +
                "then\n" +
                "  insert($a);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        InternationalAddress a = new InternationalAddress("address", "Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.iterator().next()).isEqualTo("Italy");
    }


    @Test
    public void testInlineCastForAField() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "import " + InternationalAddress.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address#InternationalAddress.state.length == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        InternationalAddress a = new InternationalAddress("address", "Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testInlineCastForAFieldWithFQN() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address#" + InternationalAddress.class.getCanonicalName() + ".state.length == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        InternationalAddress a = new InternationalAddress("address", "Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testInlineCastForAFieldAndMixMethodCall() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "import " + InternationalAddress.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address#InternationalAddress.getState().length == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        InternationalAddress a = new InternationalAddress("address", "Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testInlineCastSingle() {
        String str = "import " + ICAbstractA.class.getCanonicalName() + ";" +
                "import " + ICAbstractB.class.getCanonicalName() + ";" +
                "import " + ICAbstractC.class.getCanonicalName() + ";" +
                "import " + ICA.class.getCanonicalName() + ";" +
                "import " + ICB.class.getCanonicalName() + ";" +
                "import " + ICC.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $a : ICA( someB#ICB.onlyConcrete() == \"Hello\" )\n" + // Notice this is chaining MVEL field accessors, because inlinecast chaining methodcalls does not work even on DRL: getSomeB()#ICB.getSomeD()#ICC.onlyConcrete()
                "then\n" +
                "  insert(\"matched\");\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ICA a = new ICA();
        ICB b = new ICB();
        ICC c = new ICC();
        b.setSomeC(c);
        a.setSomeB(b);

        ksession.insert(a);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testInlineCastMultiple() {
        String str = "import " + ICAbstractA.class.getCanonicalName() + ";" +
                     "import " + ICAbstractB.class.getCanonicalName() + ";" +
                     "import " + ICAbstractC.class.getCanonicalName() + ";" +
                     "import " + ICA.class.getCanonicalName() + ";" +
                     "import " + ICB.class.getCanonicalName() + ";" +
                     "import " + ICC.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $a : ICA( someB#ICB.someC#ICC.onlyConcrete() == \"Hello\" )\n" + // Notice this is chaining MVEL field accessors, because inlinecast chaining methodcalls does not work even on DRL: getSomeB()#ICB.getSomeD()#ICC.onlyConcrete()
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ICA a = new ICA();
        ICB b = new ICB();
        ICC c = new ICC();
        b.setSomeC(c);
        a.setSomeB(b);

        ksession.insert(a);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }
}