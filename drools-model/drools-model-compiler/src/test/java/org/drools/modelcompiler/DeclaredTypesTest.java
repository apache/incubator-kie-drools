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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeclaredTypesTest extends BaseModelTest {

    public DeclaredTypesTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testPojo() throws Exception {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "\n" +
                "declare POJOPerson\n" +
                "    name : String\n" +
                "    surname : String\n" +
                "    age :  int\n" +
                "end\n" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "   POJOPerson p = new POJOPerson();\n" +
                "   p.setName($p.getName());\n" +
                "   insert(new Result(p));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        Result r = results.iterator().next();
        Object result = r.getValue();
        Class<?> resultClass = result.getClass();
        Method name = resultClass.getMethod("getName");
        assertEquals("defaultpkg.POJOPerson", resultClass.getName());
        assertEquals("Mark", name.invoke(result));

        Constructor<?>[] constructors = resultClass.getConstructors();
        assertEquals(2, constructors.length);

        Object instance1 = resultClass.newInstance();
        Constructor<?> ctor = resultClass.getConstructor(String.class, String.class, int.class);
        Object luca = ctor.newInstance("Luca", null, 32);
        Method getName = resultClass.getMethod("getName");
        Method getAge = resultClass.getMethod("getAge");

        assertEquals("Luca", getName.invoke(luca));
        assertEquals(32, getAge.invoke(luca));

        assertEquals("POJOPerson( name=Luca, surname=null, age=32 )", luca.toString());
    }

    @Test
    public void testPojoInDifferentPackages() throws Exception {
        String ruleWithPojo =
                "package org.drools.pojo.model;" +
                "\n" +
                "declare POJOPerson\n" +
                "    name : String\n" +
                "    surname : String\n" +
                "    age :  int\n" +
                "end\n";

        String rule =
                "package org.drools.pojo;\n" +
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import org.drools.pojo.model.*;" +
                "\n" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "   POJOPerson p = new POJOPerson();\n" +
                "   p.setName($p.getName());\n" +
                "   insert(p);\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : POJOPerson( name.length == 4 )\n" +
                "then\n" +
                "   insert(new Result($p));\n" +
                "end\n";

        KieSession ksession = getKieSession(rule, ruleWithPojo);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        Result r = results.iterator().next();
        Object result = r.getValue();
        Class<?> resultClass = result.getClass();
        Method name = resultClass.getMethod("getName");
        assertEquals("org.drools.pojo.model.POJOPerson", resultClass.getName());
        assertEquals("Mark", name.invoke(result));

        Constructor<?>[] constructors = resultClass.getConstructors();
        assertEquals(2, constructors.length);

        Object instance1 = resultClass.newInstance();
        Constructor<?> ctor = resultClass.getConstructor(String.class, String.class, int.class);
        Object luca = ctor.newInstance("Luca", null, 32);
        Method getName = resultClass.getMethod("getName");
        Method getAge = resultClass.getMethod("getAge");

        assertEquals("Luca", getName.invoke(luca));
        assertEquals(32, getAge.invoke(luca));

        assertEquals("POJOPerson( name=Luca, surname=null, age=32 )", luca.toString());
    }

    @Test
    public void testPojoReferencingEachOthers() throws Exception {
        String factA =
                "package org.kie.test;" +
                        "\n" +
                        "declare FactA\n" +
                        "    fieldB: FactB\n" +
                        "end\n";

        String factB =
                "package org.kie.test;" +
                        "\n" +
                        "declare FactB\n" +
                        "    fieldA: FactA\n" +
                        "end\n";

        String rule =
                "package org.kie.test\n" +
                        "rule R1 when\n" +
                        "   $fieldA : FactA( $fieldB : fieldB )\n" +
                        "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(rule, factA, factB);

        ksession.fireAllRules();
    }

    @Test
    public void testDeclaredTypeInLhs() throws Exception {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "\n" +
                "declare POJOPerson\n" +
                "    name : String\n" +
                "    surname : String\n" +
                "    age :  int\n" +
                "end\n" +
                "rule R1 when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "   POJOPerson p = new POJOPerson();\n" +
                "   p.setName($p.getName());\n" +
                "   insert(p);\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : POJOPerson( name.length == 4 )\n" +
                "then\n" +
                "   insert(new Result($p));\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        Result r = results.iterator().next();
        Object result = r.getValue();
        Class<?> resultClass = result.getClass();
        Method name = resultClass.getMethod("getName");
        assertEquals("defaultpkg.POJOPerson", resultClass.getName());
        assertEquals("Mark", name.invoke(result));

        Constructor<?>[] constructors = resultClass.getConstructors();
        assertEquals(2, constructors.length);

        Object instance1 = resultClass.newInstance();
        Constructor<?> ctor = resultClass.getConstructor(String.class, String.class, int.class);
        Object luca = ctor.newInstance("Luca", null, 32);
        Method getName = resultClass.getMethod("getName");
        Method getAge = resultClass.getMethod("getAge");

        assertEquals("Luca", getName.invoke(luca));
        assertEquals(32, getAge.invoke(luca));

        assertEquals("POJOPerson( name=Luca, surname=null, age=32 )", luca.toString());
    }

    public static class MyNumber {

        private final int value;

        public MyNumber(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public boolean isEven() {
            return value % 2 == 0;
        }

        @Override
        public String toString() {
            return "MyNumber [value=" + value + "]";
        }
    }

    @Test
    public void testPojoPredicateIsUsedAsConstraint() {
        String str = "import " + MyNumber.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  MyNumber(even, $value : value)" +
                     "then\n" +
                     "  insert($value);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new MyNumber(2));
        ksession.fireAllRules();

        Collection<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));
        assertFalse(results.contains(1)); // This is because MyNumber(1) would fail for "even" predicate/getter used here in pattern as a constraint.
    }

    @Test
    public void testPojoPredicateIsUsedAsConstraintOK() {
        String str = "import " + MyNumber.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $n : MyNumber(even, $value : value)" +
                     "then\n" +
                     "  insert($value);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new MyNumber(2));
        ksession.fireAllRules();

        Collection<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));
        assertFalse(results.contains(1)); // This is because MyNumber(1) would fail for "even" predicate/getter used here in pattern as a constraint.
    }

    @Test
    public void testBindingOfPredicateIsNotUsedAsConstraint() {
        String str = "import " + MyNumber.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  MyNumber($even : even, $value : value)" +
                     "then\n" +
                     "  insert($value);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new MyNumber(2));
        ksession.fireAllRules();

        Collection<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertTrue(results.contains(2));
        assertTrue(results.contains(1)); // This is because MyNumber(1) would simply bind for "even" predicate/getter to $even variable, and not used as a constraint.
    }

    @Test
    public void testDeclaredWithAllPrimitives() {
        String str = "declare DeclaredAllPrimitives\n" +
                     "    my_byte    : byte    \n" +
                     "    my_short   : short   \n" +
                     "    my_int     : int     \n" +
                     "    my_long    : long    \n" +
                     "    my_float   : float   \n" +
                     "    my_double  : double  \n" +
                     "    my_char    : char    \n" +
                     "    my_boolean : boolean \n" +
                     "end\n" +
                     "rule R\n" +
                     "when\n" +
                     "then\n" +
                     "  insert(new DeclaredAllPrimitives((byte) 1, (short) 1, 1, 1L, 1f, 1d, 'x', true));\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.fireAllRules();

        List<Object> results = getObjectsIntoList(ksession, Object.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testFactType() throws Exception {
        // DROOLS-4784
        String str =
                "package org.test;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "declare Name\n" +
                "    VALUE : String\n" +
                "end\n" +
                "rule R when\n" +
                "    Name($v : VALUE == \"Mario\")\n" +
                "then\n" +
                "    insert($v);" +
                "end";

        KieSession ksession = getKieSession( str );

        FactType nameType = ksession.getKieBase().getFactType("org.test", "Name");
        Object name = nameType.newInstance();
        nameType.set(name, "VALUE", "Mario");

        ksession.insert(name);
        ksession.fireAllRules();

        assertEquals( "Mario", nameType.get( name, "VALUE" ) );

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next() );

        EntryPointNode epn = (( KnowledgeBaseImpl ) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        Iterator<ObjectTypeNode> otns = epn.getObjectTypeNodes().values().iterator();
        ObjectTypeNode otn = otns.next();
        if (otn.toString().contains( "InitialFact" )) {
            otn = otns.next();
        }
        AlphaNode alpha = (AlphaNode)otn.getSinks()[0];
        AlphaNodeFieldConstraint constraint = alpha.getConstraint();
        int index = (( IndexableConstraint ) constraint).getFieldExtractor().getIndex();
        assertTrue( index >= 0 );
    }

    @Test
    public void testFactTypeNotUsedInRule() throws Exception {
        String str =
                "package org.test;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "declare Name\n" +
                "    value : String\n" +
                "end\n" +
                "declare ExtendedName extends Name\n" +
                "end\n" +
                "rule R when\n" +
                "    Name($v : value == \"Mario\")\n" +
                "then\n" +
                "    insert($v);" +
                "end";

        KieSession ksession = getKieSession( str );

        FactType nameType = ksession.getKieBase().getFactType("org.test", "ExtendedName");
        Object name = nameType.newInstance();
        nameType.set(name, "value", "Mario");

        ksession.insert(name);
        ksession.fireAllRules();

        assertEquals( "Mario", nameType.get( name, "value" ) );

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next() );
    }

    @Test
    public void testTypeDeclarationsInheritance() throws Exception {
        String str =
                "declare Person\n" +
                "    id : int @key\n" +
                "    name : String\n" +
                "end\n" +
                "declare Employee extends Person\n" +
                "    salary : double\n" +
                "end\n" +
                "rule \"TestConstructors\" when\n" +
                "    then\n" +
                "        Person p1 = new Person();\n" +
                "        Person p2 = new Person(9);\n" +
                "        Person p3 = new Person(99, \"myname\");\n" +
                "\n" +
                "        Employee e1 = new Employee();\n" +
                "        Employee e2 = new Employee(9);\n" +
                "        Employee e3 = new Employee(99, \"myname\", 100.00);\n" +
                "\n" +
                "        Person pe = new Employee();\n" +
                "end";

        KieSession ksession = getKieSession( str );
    }

    @Test
    public void testEnum() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "declare enum PersonAge\n" +
                "    ELEVEN(11);\n" +
                "\n" +
                "    key: int\n" +
                "end\n" +
                "\n" +
                "rule \"0_SomeRule\"\n" +
                "    when\n" +
                "            $p : Person ()\n" +
                "    then\n" +
                "            $p.setAge(PersonAge.ELEVEN.getKey());\n" +
                "            insert(new Result($p));\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario"));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        Person p = (Person) results.iterator().next().getValue();
        assertEquals(11, p.getAge());
    }

    @Test
    public void testDeclaredSlidingWindowOnEventInTypeDeclaration() throws Exception {
        String str =
                "package org.test;\n" +
                "declare MyPojo\n" +
                "  @serialVersionUID( 42 )\n" +
                "end\n" +
                "rule R when then insert(new MyPojo()); end\n";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();

        Object pojo = getObjectsIntoList(ksession, Object.class).iterator().next();
        Field f = pojo.getClass().getDeclaredField( "serialVersionUID" );
        f.setAccessible( true );
        assertEquals(42L, (long)f.get( pojo ));
    }

    @Test
    public void testNestedDateConstraint() throws Exception {
        String str =
                "package org.test;\n" +
                "declare Fact\n" +
                "    n : Nested\n" +
                "end\n" +
                "declare Nested\n" +
                "    d : java.util.Date\n" +
                "end\n" +
                "\n" +
                "rule \"with nested date\" when\n" +
                "    Fact(n.d >= \"01-Jan-2020\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );
        KieBase kbase = ksession.getKieBase();

        FactType factType = kbase.getFactType("org.test", "Fact");
        FactType nestedType = kbase.getFactType("org.test", "Nested");

        Object f1 = factType.newInstance();
        Object n1 = nestedType.newInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        nestedType.set(n1, "d", df.parse("01-Jan-2020"));
        factType.set(f1, "n", n1);

        ksession.insert(f1);
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testExtendPojo() throws Exception {
        String str =
                "package org.test;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "declare MyPerson extends Person\n" +
                "    style : String\n" +
                "end\n" +
                "\n" +
                "rule \"with nested date\" when\n" +
                "    MyPerson(name == \"Mario\", style == \"Steampunk\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );
        KieBase kbase = ksession.getKieBase();

        FactType factType = kbase.getFactType("org.test", "MyPerson");
        assertEquals( String.class, factType.getField( "name" ).getType() );
        assertEquals( String.class, factType.getField( "style" ).getType() );

        Object f1 = factType.newInstance();
        factType.set(f1, "name", "Mario");
        factType.set(f1, "style", "Steampunk");

        ksession.insert(f1);
        assertEquals( 1, ksession.fireAllRules() );
    }
}