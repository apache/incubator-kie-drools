package org.drools.mvelcompiler;

import java.util.List;
import java.util.Map;

import org.drools.Person;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MvelCompilerTest implements CompilerTest {

    @Test
    public void testConvertPropertyToAccessor() {
        String expectedJavaCode = "{ $p.getParent().getParent().getName(); }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parent.getParent().name; } ",
             expectedJavaCode);

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().parent.name; } ",
             expectedJavaCode);

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parent.parent.name; } ",
             expectedJavaCode);

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().getParent().getName(); } ",
             expectedJavaCode);
    }

    @Test
    public void testAccessorInArguments() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ insert(\"Modified person age to 1 for: \" + $p.name); }",
             "{ insert(\"Modified person age to 1 for: \" + $p.getName()); } ");
    }

    @Test
    public void testEnumField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ key = $p.gender.getKey(); } ",
             "{ int key = $p.getGender().getKey(); }");
    }

    @Test
    public void testEnumConstant() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ key = Gender.FEMALE.getKey(); } ",
             "{ int key = Gender.FEMALE.getKey(); }");
    }

    @Test
    public void testPublicField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parentPublic.getParent().name; } ",
             "{ $p.parentPublic.getParent().getName(); }");

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().parentPublic.name; } ",
             "{ $p.getParent().parentPublic.getName(); }");
    }

    @Test
    public void testUncompiledMethod() {
        test("{ System.out.println(\"Hello World\"); }",
             "{ System.out.println(\"Hello World\"); }");
    }

    @Test
    public void testStringLength() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name.length; }",
             "{ $p.getName().length(); }");
    }

    @Test
    public void testAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ Person np = $p; np = $p; }",
             "{ org.drools.Person np = $p; np = $p; }");
    }

    @Test
    public void testAssignmentUndeclared() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ np = $p; }",
             "{ org.drools.Person np = $p; }");
    }

    @Test
    public void testSetter() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name = \"Luca\"; }",
             "{ $p.setName(\"Luca\"); }");
    }

    @Test
    public void testBoxingSetter() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.ageAsInteger = 20; }",
             "{ $p.setAgeAsInteger(20); }");
    }

    @Test
    public void testSetterBigDecimal() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.salary = $p.salary + 50000; }",
             "{ $p.setSalary($p.getSalary().add(new java.math.BigDecimal(50000))); }");
    }

    @Test
    public void testSetterPublicField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.nickName = \"Luca\"; } ",
             "{ $p.nickName = \"Luca\"; } ");
    }

    @Test
    public void withoutSemicolonAndComment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{             " +
                     "delete($person) // some comment\n" +
                     "delete($pet) // another comment\n" +
                     "}",
             "{             " +
                     "delete($person);\n" +
                     "delete($pet);\n" +
                     "}");
    }

    @Test
    public void testInitializerArrayAccess() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "l = new ArrayList(); " +
                     "l.add(\"first\"); " +
                     "System.out.println(l[0]); " +
                     "}",
             "{ " +
                     "java.util.ArrayList l = new ArrayList(); " +
                     "l.add(\"first\"); " +
                     "System.out.println(l.get(0)); " +
                     "}");
    }


    @Test
    public void testMapGet() {
        test(ctx -> ctx.addDeclaration("m", Map.class),
             "{ " +
                     "m[\"key\"];\n" +
                     "}",
             "{ " +
                     "m.get(\"key\");\n" +
                     "}");
    }

    @Test
    public void testMapGetAsField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "$p.items[\"key3\"];\n" +
                     "}",
             "{ " +
                     "$p.getItems().get(\"key3\");\n" +
                     "}");
    }

    @Test
    public void testMapGetInMethodCall() {
        test(ctx -> ctx.addDeclaration("m", Map.class),
             "{ " +
                     "System.out.println(m[\"key\"]);\n" +
                     "}",
             "{ " +
                     "System.out.println(m.get(\"key\"));\n" +
                     "}");
    }


    @Test
    public void testMapSet() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "$p.items[\"key3\"] = \"value3\";\n" +
                     "}",
             "{ " +
                     "$p.getItems().put(\"key3\", java.lang.String.valueOf(\"value3\")); " +
                     "}");
    }

    @Test
    public void testMapSetWithVariable() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "String key3 = \"key3\";\n" +
                     "$p.items[key3] = \"value3\";\n" +
                     "}",
             "{ " +
                     "java.lang.String key3 = \"key3\";\n" +
                     "$p.getItems().put(key3, java.lang.String.valueOf(\"value3\")); " +
                     "}");
    }

    @Test
    public void testMapSetWithVariableCoercionString() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "$p.items[\"key\"] = 2;\n" +
                     "}",
             "{ " +
                     "$p.getItems().put(\"key\", java.lang.String.valueOf(2)); " +
                     "}");
    }

    @Test
    public void testMapPutWithVariableCoercionString() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "$p.items[\"key\"] = 2;\n" +
                     "}",
             "{ " +
                     "$p.getItems().put(\"key\", java.lang.String.valueOf(2)); " +
                     "}");
    }

    @Test
    public void testMapSetWithMapGetAsValue() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
                 ctx.addDeclaration("n", Integer.class);
             },
             "{" +
                     "    $p.getItems().put(\"key4\", n);\n" +
                     "}",
             "{ " +
                     "    $p.getItems().put(\"key4\", java.lang.String.valueOf(n));\n" +
                     "}");
    }

    @Test
    public void testMapSetToNewMap() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "Map newhashmap = new HashMap();\n" +
                     "$p.items = newhashmap;\n" +
                     "}",
             "{ " +
                     "java.util.Map newhashmap = new HashMap(); \n" +
                     "$p.setItems(newhashmap); " +
                     "}");
    }

    @Test
    public void testInitializerMap() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "m = new HashMap();\n" +
                     "m.put(\"key\", 2);\n" +
                     "System.out.println(m[\"key\"]);\n" +
                     "}",
             "{ " +
                     "java.util.HashMap m = new HashMap();\n" +
                     "m.put(\"key\", 2);\n" +
                     "System.out.println(m.get(\"key\"));\n" +
                     "}");
    }

    @Test
    public void testMixArrayMap() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    m = new HashMap();\n" +
                     "    l = new ArrayList();\n" +
                     "    l.add(\"first\");\n" +
                     "    m.put(\"content\", l);\n" +
                     "    System.out.println(((ArrayList)m[\"content\"])[0]);\n" +
                     "    list.add(((ArrayList)m[\"content\"])[0]);\n" +
                     "}",
             "{ " +
                     "    java.util.HashMap m = new HashMap();\n" +
                     "    java.util.ArrayList l = new ArrayList();\n" +
                     "    l.add(\"first\");\n" +
                     "    m.put(\"content\", l);\n" +
                     "    System.out.println(((java.util.ArrayList) m.get(\"content\")).get(0));\n" +
                     "    list.add(((java.util.ArrayList) m.get(\"content\")).get(0));\n" +
                     "}");
    }

    @Test
    public void testBigDecimal() {
        test("{ " +
                     "    BigDecimal sum = 0;\n" +
                     "    BigDecimal money = 10;\n" +
                     "    sum += money;\n" +
                     "    sum -= money;\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal sum = java.math.BigDecimal.valueOf(0);\n" +
                     "    java.math.BigDecimal money = java.math.BigDecimal.valueOf(10);\n" +
                     "    sum = sum.add(money);\n" +
                     "    sum = sum.subtract(money);\n" +
                     "}");
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { name = \"Luca\", age = 35 }; }",
             "{ $p.setName(\"Luca\"); $p.setAge(35); }",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
    }

    @Test
    public void testModifySemiColon() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { setAge(1); }; }",
             "{ $p.setAge(1); }",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
    }

    @Test
    public void testModifyWithAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { age = $p.age+1 }; }",
             "{ $p.setAge($p.getAge() + 1); }",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
    }

    @Test
    public void testWithSemiColon() {
        test("{ with( $l = new ArrayList()) { $l.add(2); }; }",
             "{ java.util.ArrayList $l = new ArrayList(); $l.add(2); }",
             result -> assertThat(allUsedBindings(result), is(empty())));
    }

    @Test
    public void testWithWithAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ with($p = new Person()) { age = $p.age+1 }; }",
             "{ org.drools.Person $p = new Person(); $p.setAge($p.getAge() + 1); }",
             result -> assertThat(allUsedBindings(result), is(empty())));
    }

    @Test
    public void testVariableDeclarationUntyped() {
        test(ctx -> ctx.addDeclaration("$map", Map.class),
             " { Map pMap = map.get( $r.getName() ); }",
             " { java.util.Map pMap = (java.util.Map) (map.get($r.getName())); }");
    }

    @Test
    public void testSimpleVariableDeclaration() {
        test(" { int i; }",
             " { int i; }");
    }

    @Test
    public void testModifyInsideIfBlock() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{    " +
                     "         if ($p.getParent() != null) {\n" +
                     "              $p.setName(\"with_parent\");\n" +
                     "         } else {\n" +
                         "         modify ($p) {\n" +
                         "            name = \"without_parent\"" +
                         "         }\n" +
                     "         }" +
                     "      }", "" +
                     "{ " +
                     "  if ($p.getParent() != null) { " +
                     "      $p.setName(\"with_parent\"); " +
                     "  } else { " +
                     "      $p.setName(\"without_parent\"); " +
                     "  } " +
                     "}",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
    }

    @Test
    public void testWithOrdering() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "        with( s0 = new Person() ) {\n" +
                     "            age = 0\n" +
                     "        }\n" +
                     "        insertLogical(s0);\n" +
                     "        with( s1 = new Person() ) {\n" +
                     "            age = 1\n" +
                     "        }\n" +
                     "        insertLogical(s1);\n " +
                     "     }",

             "{ " +
                             "org.drools.Person s1 = new Person(); " +
                             "org.drools.Person s0 = new Person(); " +
                             "insertLogical(s0); " +
                             "insertLogical(s1); " +
                             "s0.setAge(0); " +
                             "s1.setAge(1); " +
                          "}");
    }

    @Test
    public void testModifyOrdering() {
        test(ctx -> ctx.addDeclaration("$person", Person.class),
             "{" +
                     "        Address $newAddress = new Address();\n" +
                     "        $newAddress.setCity( \"Brno\" );\n" +
                     "        insert( $newAddress );\n" +
                     "        modify( $person ) {\n" +
                     "          setAddress( $newAddress )\n" +
                     "        }" +
                            "}",

             "{ " +
                             "org.drools.Address $newAddress = new Address(); " +
                             "$newAddress.setCity(\"Brno\"); " +
                             "insert($newAddress); " +
                             "$person.setAddress($newAddress); " +
                          "}");
    }

    @Test
    public void forIterationWithSubtype() {
        test(ctx -> ctx.addDeclaration("$people", List.class),
             "{" +
                     "    for (Person p : $people ) {\n" +
                     "        System.out.println(\"Person: \" + p);\n" +
                     "    }\n" +
                     "}",
             "{\n" +
                     "    for (Object _p : $people) {\n" +
                     "        Person p = (Person) _p;\n" +
                     "        {\n " +
                     "              System.out.println(\"Person: \" + p);\n" +
                     "        }\n" +
                     "    }\n" +
                     "}"
        );
    }

    @Test
    public void forIterationWithSubtypeNested() {
        test(ctx -> {
                 ctx.addDeclaration("$people", List.class);
                 ctx.addDeclaration("$addresses", List.class);
             },
             "{" +
                     "    for (Person p : $people ) {\n" +
                     "       System.out.println(\"Simple statement\");\n" +
                     "       for (Address a : $addresses ) {\n" +
                     "           System.out.println(\"Person: \" + p + \" address: \" + a);\n" +
                     "       }\n" +
                     "    }\n" +
                     "}",
             "{\n" +
                     "    for (Object _p : $people) {\n" +
                     "        Person p = (Person) _p;\n" +
                     "        {\n " +
                     "           System.out.println(\"Simple statement\");\n" +
                     "           for (Object _a : $addresses) {\n" +
                     "               Address a = (Address) _a;\n" +
                     "                   {\n " +
                     "                       System.out.println(\"Person: \" + p + \" address: \" + a);\n" +
                     "                }\n" +
                     "            }\n" +
                     "        }\n" +
                     "    }\n" +
                     "}"
        );
    }
}