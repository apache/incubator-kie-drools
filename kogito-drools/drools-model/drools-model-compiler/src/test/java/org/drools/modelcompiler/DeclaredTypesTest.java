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
import java.lang.reflect.Method;
import java.util.Collection;

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

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

        Collection<Result> results = getObjects(ksession, Result.class);
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

        Collection<Result> results = getObjects(ksession, Result.class);
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

        Collection<Result> results = getObjects(ksession, Result.class);
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

        Collection<Integer> results = getObjects(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjects(ksession, Integer.class);
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

        Collection<Integer> results = getObjects(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjects(ksession, Integer.class);
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

        Collection<Integer> results = getObjects(ksession, Integer.class);
        assertTrue(results.contains(2));

        ksession.insert(new MyNumber(1));
        ksession.fireAllRules();

        results = getObjects(ksession, Integer.class);
        assertTrue(results.contains(2));
        assertTrue(results.contains(1)); // This is because MyNumber(1) would simply bind for "even" predicate/getter to $even variable, and not used as a constraint.  
    }
}