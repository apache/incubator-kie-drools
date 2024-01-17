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
package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.drools.Person;
import org.drools.util.MethodUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MvelCompilerTest implements CompilerTest {

    @Test
    public void testAssignmentIncrement() {
        test(ctx -> ctx.addDeclaration("i", Integer.class),
             "{ i += 10 } ",
             "{ i += 10; }");
    }


    @Test
    public void testAssignmentIncrementInFieldWithPrimitive() {
        test(ctx -> ctx.addDeclaration("p", Person.class),
             "{ p.age += 10 } ",
             "{ p.setAge(p.getAge() + 10); }");
    }

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
    public void testConvertPropertyToAccessorForEach() {
        String expectedJavaCode =  "{ for (org.drools.Address a : $p.getAddresses()) {\n" +
                "  results.add(a.getCity());\n" +
                "}\n }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ for(Address a: $p.addresses){\n" +
                     "  results.add(a.city);\n" +
                     "}\n }",
             expectedJavaCode);
    }

    @Test
    public void testConvertIfConditionAndStatements() {
        String expectedJavaCode =  "{\n if ($p.getAddresses() != null) {\n" +
                "  results.add($p.getName());\n" +
                "} else {\n" +
                "results.add($p.getAge());\n" +
                "}\n }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ if($p.addresses != null){\n" +
                     "  results.add($p.name);\n" +
                     "} else {\n " +
                     "  results.add($p.age);" +
                     "} }",
             expectedJavaCode);
    }

    @Test
    public void testPromoteBigDecimalToIntValueInsideIf() {
        String expectedJavaCode =  "{\n" +
                "    if ($p.isEven($p.getSalary().intValue()) && $p.isEven($m.intValue())) {\n" +
                "    }\n" +
                " }";

        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
                 ctx.addDeclaration("$m", BigDecimal.class);
             },
             "{ if($p.isEven($p.salary) && $p.isEven($m)){\n" +
                     "} }",
             expectedJavaCode);
    }

    @Test
    public void testPromoteBigDecimalToIntValueInsideIfWithStaticMethod() {
        String expectedJavaCode = "{\n" +
                "    if (isEven($p.getSalary().intValue()) && isEven($m.intValue())) {\n" +
                "    }\n" +
                " }";

        test(ctx -> {
                 ctx.addDeclaration("$m", BigDecimal.class);

                 Class<Person> personClass = Person.class;
                 ctx.addDeclaration("$p", personClass);
                 ctx.addStaticMethod("isEven", MethodUtils.findMethod(personClass, "isEven", new Class[]{int.class}));
             },
             "{ if(isEven($p.salary) && isEven($m)){\n" +
                     "} }",
             expectedJavaCode);
    }


    @Test
    public void testConvertPropertyToAccessorWhile() {
        String expectedJavaCode =  "{\n while ($p.getAddresses() != null) {\n" +
                "  results.add($p.getName());\n" +
                "}\n" +
                " }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ while($p.addresses != null){\n" +
                     "  results.add($p.name);\n" +
                     "}"+
                     "}",
             expectedJavaCode);
    }

    @Test
    public void testConvertPropertyToAccessorDoWhile() {
        String expectedJavaCode =  "{\n do {\n" +
                "  results.add($p.getName());\n" +
                "} while ($p.getAddresses() != null);\n" +
                " }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ do {\n" +
                     "  results.add($p.name);\n" +
                     "} while($p.addresses != null);"+
                     "}",
             expectedJavaCode);
    }

    @Test
    public void testConvertPropertyToAccessorFor() {
        String expectedJavaCode =  "{\n for (int i = 0; i < $p.getAddresses(); i++) {\n" +
                "  results.add($p.getName());\n" +
                "}\n" +
                " }";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ for(int i = 0; i < $p.addresses; i++) {\n" +
                     "  results.add($p.name);\n" +
                     "} "+
                     "}",
             expectedJavaCode);
    }

    @Test
    public void testConvertPropertyToAccessorSwitch() {
        String expectedJavaCode =  "{\n " +
                "        switch($p.getName()) {\n" +
                "            case \"Luca\":\n" +
                "                results.add($p.getName());\n" +
                "        }\n}";

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{\n " +
                     "        switch($p.name) {\n" +
                     "            case \"Luca\":\n" +
                     "                results.add($p.name);\n" +
                     "        }\n}",
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
             "{ $p.setSalary($p.getSalary().add(new java.math.BigDecimal(50000), java.math.MathContext.DECIMAL128)); }");
    }

    @Test
    public void testSetterBigDecimalConstant() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.salary = 50000; }",
             "{ $p.setSalary(new java.math.BigDecimal(50000)); }");
    }

    @Test
    public void testSetterBigDecimalConstantFromLong() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.salary = 50000L; }",
             "{ $p.setSalary(new java.math.BigDecimal(50000L)); }");
    }

    @Test
    public void testSetterStringWithBigDecimal() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name = BigDecimal.valueOf(1); }",
             "{ $p.setName((BigDecimal.valueOf(1)).toString()); }");
    }

    @Test
    public void testSetterStringWithBigDecimalFromField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name = $p.salary; }",
             "{ $p.setName(($p.getSalary()).toString()); }");
    }

    @Test
    public void testSetterStringWithBigDecimalFromVariable() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
                 ctx.addDeclaration("$m", BigDecimal.class);
             },
             "{ $p.name = $m; }",
             "{ $p.setName(($m).toString()); }");
    }

    @Test
    public void testSetterStringWithBigDecimalFromBigDecimalLiteral() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
             },
             "{ $p.name = 10000B; }",
             "{ $p.setName((new java.math.BigDecimal(\"10000\")).toString()); }");
    }

    @Test
    public void testSetterStringWithBigDecimalFromBigDecimalConstant() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
             },
             "{ $p.name = BigDecimal.ZERO; }",
             "{ $p.setName((BigDecimal.ZERO).toString()); }");
    }

    @Test
    public void testSetterStringWithNull() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
             },
             "{ $p.name = null; }",
             "{ $p.setName(null); }");
    }

    @Test
    public void testSetterBigDecimalConstantModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { salary = 50000 }; }",
             "{ { $p.setSalary(new java.math.BigDecimal(50000)); } drools.update($p);}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testSetterBigDecimalLiteralModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { salary = 50000B }; }",
             "{ { $p.setSalary(new java.math.BigDecimal(\"50000\")); } drools.update($p);}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testSetterBigDecimalLiteralModifyNegative() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { salary = -50000B }; }",
             "{ { $p.setSalary(new java.math.BigDecimal(\"-50000\")); } drools.update($p);}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testBigDecimalModulo() {
        test(ctx -> ctx.addDeclaration("$b1", BigDecimal.class),
             "{ java.math.BigDecimal result = $b1 % 2; }",
             "{ java.math.BigDecimal result = $b1.remainder(new java.math.BigDecimal(2), java.math.MathContext.DECIMAL128); }");
    }

    @Test
    public void testBigDecimalModuloPromotion() {
        test("{ BigDecimal result = 12 % 10; }",
             "{ java.math.BigDecimal result = new java.math.BigDecimal(12 % 10); }");
    }

    @Test
    public void testBigDecimalModuloWithOtherBigDecimal() {
        test(ctx -> {
                 ctx.addDeclaration("$b1", BigDecimal.class);
                 ctx.addDeclaration("$b2", BigDecimal.class);
             },
             "{ java.math.BigDecimal result = $b1 % $b2; }",
             "{ java.math.BigDecimal result = $b1.remainder($b2, java.math.MathContext.DECIMAL128); }");
    }

    @Test
    public void testBigDecimalModuloOperationSumMultiply() {
        test(ctx -> {
                 ctx.addDeclaration("bd1", BigDecimal.class);
                 ctx.addDeclaration("bd2", BigDecimal.class);
                 ctx.addDeclaration("$p", Person.class);
             },
             "{ $p.salary = $p.salary + (bd1.multiply(bd2)); }",
             "{ $p.setSalary($p.getSalary().add(bd1.multiply(bd2), java.math.MathContext.DECIMAL128));\n }");
    }

    @Test
    public void testDoNotConvertAdditionInStringConcatenation() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                          "     list.add(\"before \" + $p + \", money = \" + $p.salary); " +
                          "     modify ( $p )  { salary = 50000 };  " +
                          "     list.add(\"after \" + $p + \", money = \" + $p.salary); " +
                          "}",
             "{\n " +
                         "      list.add(\"before \" + $p + \", money = \" + $p.getSalary()); " +
                         "      { $p.setSalary(new java.math.BigDecimal(50000)); }" +
                         "      list.add(\"after \" + $p + \", money = \" + $p.getSalary()); " +
                         "      drools.update($p);" +
                         "}\n",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void modifyInsideIfTrueBlock() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "    if (true) { " +
                     "        modify ( $p )  { salary = 50000 };  " +
                     "    }" +
                     "}",
             "{\n" +
                     "    if (true) {" +
                     "        { $p.setSalary(new java.math.BigDecimal(50000)); }" +
                     "        drools.update($p);" +
                     "    }" +
                     "}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void modifyInsideIfFalseBlock() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "    if (false) { " +
                     "        modify ( $p )  { salary = 50000 };  " +
                     "    }" +
                     "}",
             "{\n" +
                     "    if (false) {" +
                     "        { $p.setSalary(new java.math.BigDecimal(50000)); }" +
                     "        drools.update($p);" +
                     "    }" +
                     "}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void emptyModifyInsideIfBlockAndSetterOutside() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "    $p.salary = 50000;" +
                     "    if (false) { " +
                     "        modify ( $p )  { };  " +
                     "    }" +
                     "}",
             "{\n" +
                     "    $p.setSalary(new java.math.BigDecimal(50000));" +
                     "    if (false) {" +
                     "        { }" +
                     "        drools.update($p);" +
                     "    }" +
                     "}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testSetterBigIntegerLiteral() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
             },
             "{ $p.ageAsInteger = 10000I; }",
             "{ $p.setAgeAsInteger(new java.math.BigInteger(\"10000\")); }");
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
                     "java.util.ArrayList l = new java.util.ArrayList(); " +
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
    public void testMapSetWithConstant() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{" +
                     "$p.items[\"key3\"] = \"value3\";\n" +
                     "}",
             "{ " +
                     "$p.getItems().put(\"key3\", java.lang.String.valueOf(\"value3\")); " +
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
                     "java.util.Map newhashmap = new java.util.HashMap(); \n" +
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
                     "java.util.HashMap m = new java.util.HashMap();\n" +
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
                     "    java.util.HashMap m = new java.util.HashMap();\n" +
                     "    java.util.ArrayList l = new java.util.ArrayList();\n" +
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
                     "    java.math.BigDecimal sum = new java.math.BigDecimal(0);\n" +
                     "    java.math.BigDecimal money = new java.math.BigDecimal(10);\n" +
                     "    sum = sum.add(money, java.math.MathContext.DECIMAL128);\n" +
                     "    sum = sum.subtract(money, java.math.MathContext.DECIMAL128);\n" +
                     "}");
    }

    @Test
    public void bigDecimalLessThan() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    BigDecimal ten = 10;\n" +
                     "    if(zero < ten) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    java.math.BigDecimal ten = new java.math.BigDecimal(10);\n" +
                     "    if (zero.compareTo(ten) < 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void bigDecimalLessThanOrEqual() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    BigDecimal ten = 10;\n" +
                     "    if(zero <= ten) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    java.math.BigDecimal ten = new java.math.BigDecimal(10);\n" +
                     "    if (zero.compareTo(ten) <= 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void bigDecimalGreaterThan() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    BigDecimal ten = 10;\n" +
                     "    if(zero > ten) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    java.math.BigDecimal ten = new java.math.BigDecimal(10);\n" +
                     "    if (zero.compareTo(ten) > 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void bigDecimalGreaterThanOrEqual() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    BigDecimal ten = 10;\n" +
                     "    if(zero >= ten) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    java.math.BigDecimal ten = new java.math.BigDecimal(10);\n" +
                     "    if (zero.compareTo(ten) >= 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void bigDecimalEquals() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    if(zero == 23) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    if (zero.compareTo(new java.math.BigDecimal(23)) == 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void bigDecimalNotEquals() {
        test("{ " +
                     "    BigDecimal zero = 0;\n" +
                     "    if(zero != 23) {}\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal zero = new java.math.BigDecimal(0);\n" +
                     "    if (zero.compareTo(new java.math.BigDecimal(23)) != 0) {\n" +
                     "    }\n" +
                     "}");
    }

    @Test
    public void testBigDecimalCompoundOperatorOnField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    $p.salary += 50000B;\n" +
                     "}",
             "{ " +
                     "    $p.setSalary($p.getSalary().add(new java.math.BigDecimal(\"50000\"), java.math.MathContext.DECIMAL128));\n" +
                     "}");
    }

    @Test
    public void testBigDecimalCompoundOperatorWithOnField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    $p.salary += $p.salary;\n" +
                     "}",
             "{ " +
                     "    $p.setSalary($p.getSalary().add($p.getSalary(), java.math.MathContext.DECIMAL128));\n" +
                     "}");
    }

    @Test
    public void testBigDecimalArithmetic() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    java.math.BigDecimal operation = $p.salary + $p.salary;\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal operation = $p.getSalary().add($p.getSalary(), java.math.MathContext.DECIMAL128);\n" +
                     "}");
    }

    @Test
    public void testBigDecimalArithmeticWithValueOfDouble() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
            "{ " +
                    "    $p.salary = $p.salary + BigDecimal.valueOf(0.5);\n" +
                    "}",
            "{ " +
                    "    $p.setSalary($p.getSalary().add(BigDecimal.valueOf(0.5), java.math.MathContext.DECIMAL128));\n" +
                    "}");
    }

    @Test
    public void testBigDecimalArithmeticWithConversionLiteral() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    java.math.BigDecimal operation = $p.salary + 10B;\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal operation = $p.getSalary().add(new java.math.BigDecimal(\"10\"), java.math.MathContext.DECIMAL128);\n" +
                     "}");
    }

    @Test
    public void testBigDecimalArithmeticWithConversionFromInteger() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    java.math.BigDecimal operation = $p.salary + 10;\n" +
                     "}",
             "{ " +
                     "    java.math.BigDecimal operation = $p.getSalary().add(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128);\n" +
                     "}");
    }

    @Test
    public void testBigDecimalAssignmentToInt() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.age = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setAge($p.getSalary().intValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToIntegerBoxed() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.integerBoxed = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setIntegerBoxed($p.getSalary().intValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToLong() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.longValue = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setLongValue($p.getSalary().longValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToLongBoxed() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.longBoxed = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setLongBoxed($p.getSalary().longValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToShort() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.shortValue = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setShortValue($p.getSalary().shortValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToShortBoxed() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.shortBoxed = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setShortBoxed($p.getSalary().shortValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToDouble() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.doubleValue = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setDoubleValue($p.getSalary().doubleValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToDoubleBoxed() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.doubleBoxed = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setDoubleBoxed($p.getSalary().doubleValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToFloat() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.floatValue = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setFloatValue($p.getSalary().floatValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalAssignmentToFloatBoxed() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
                "{ " +
                        "    $p.floatBoxed = $p.salary;\n" +
                        "}",
                "{ " +
                        "    $p.setFloatBoxed($p.getSalary().floatValue());\n" +
                        "}");
    }

    @Test
    public void testBigDecimalPromotionAllFourOperations() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ " +
                     "    BigDecimal result = 0B;" +
                     "    result += 50000;\n" +
                     "    result -= 10000;\n" +
                     "    result /= 10;\n" +
                     "    result *= 10;\n" +
                     "    (result *= $p.salary);\n" +
                     "    $p.salary = result;" +
                     "}",
             "{ " +
                     "        java.math.BigDecimal result = new java.math.BigDecimal(\"0\");\n" +
                     "        result = result.add(new java.math.BigDecimal(50000), java.math.MathContext.DECIMAL128);\n" +
                     "        result = result.subtract(new java.math.BigDecimal(10000), java.math.MathContext.DECIMAL128);\n" +
                     "        result = result.divide(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128);\n" +
                     "        result = result.multiply(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128);\n" +
                     "        result = result.multiply($p.getSalary(), java.math.MathContext.DECIMAL128);\n" +
                     "        $p.setSalary(result);\n" +
                     "}");
    }

    @Test
    public void testPromotionOfIntToBigDecimal() {
        test("{ " +
                     "    BigDecimal result = 0B;" +
                     "    int anotherVariable = 20;" +
                     "    result += anotherVariable;\n" + // 20
                     "}",
             "{ " +
                     "        java.math.BigDecimal result = new java.math.BigDecimal(\"0\");\n" +
                     "        int anotherVariable = 20;\n" +
                     "        result = result.add(new java.math.BigDecimal(anotherVariable), java.math.MathContext.DECIMAL128);\n" +
                     "}");
    }

    @Test
    public void testPromotionOfIntToBigDecimalOnField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class), "{ " +
                     "    int anotherVariable = 20;" +
                     "    $p.salary += anotherVariable;" +
                     "}",
             "{ " +
                     "        int anotherVariable = 20;\n" +
                     "        $p.setSalary($p.getSalary().add(new java.math.BigDecimal(anotherVariable), java.math.MathContext.DECIMAL128));\n" +
                     "}");
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { name = \"Luca\", age = 35 }; }",
             "{\n {\n $p.setName(\"Luca\");\n $p.setAge(35);\n } drools.update($p);\n }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyMap() {
        test(ctx -> {
                 ctx.addDeclaration("$p", Person.class);
                 ctx.addDeclaration("$p2", Person.class);
             },
             "{ modify ( $p )  { items = $p2.items }; }",
             "{\n {\n $p.setItems($p2.getItems());\n } drools.update($p);\n }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifySemiColon() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { setAge(1); }; }",
             "{ { $p.setAge(1); } drools.update($p);}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyWithAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { age = $p.age+1 }; }",
             "{ { $p.setAge($p.getAge() + 1); } drools.update($p); }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyWithMethodCall() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { addresses.clear() }; }",
             "{ { $p.getAddresses().clear(); } drools.update($p);}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testAddCastToMapGet() {
        test(ctx -> ctx.addDeclaration("$map", Map.class),
             " { Map pMap = map.get( \"whatever\" ); }",
             " { java.util.Map pMap = (java.util.Map) (map.get(\"whatever\")); }");
    }

    @Test
    public void testAddCastToMapGetOfDeclaration() {
        test(ctx -> {
                 ctx.addDeclaration("map", Map.class);
                 ctx.addDeclaration("$p", Person.class);
             },
             " { Map pMap = map.get( $p.getName() ); }",
             " { java.util.Map pMap = (java.util.Map) (map.get($p.getName())); }");
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
                     "  } else {\n " +
                     "      {\n" +
                     "          $p.setName(\"without_parent\");\n" +
                     "      }\n" +
                     "      drools.update($p);\n" +
                     "  } " +
                     "}",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
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
                     "org.drools.Address $newAddress = new org.drools.Address(); " +
                     "$newAddress.setCity(\"Brno\"); " +
                     "insert($newAddress);\n" +
                     "{ " +
                     "  $person.setAddress($newAddress);\n" +
                     "}\n" +
                     "drools.update($person);\n" +
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

    @Test
    public void testMultiLineStringLiteral() {
        test(" { java.lang.String s = \"\"\"\n string content\n \"\"\"; }",
             " { java.lang.String s = \"\"\"\n string content\n \"\"\"; }");
    }

    @Test
    public void testReadMapField() {
        test(ctx -> ctx.addDeclaration("$p", Map.class),
             " { String name = $p.name; }",
             " { java.lang.String name = (java.lang.String) ($p.get(\"name\")); }");
    }

    @Test
    public void testWriteMapField() {
        test(ctx -> ctx.addDeclaration("$p", Map.class),
             " { $p.name = \"Mario\"; }",
             " { $p.put(\"name\", \"Mario\"); }");
    }

    @Test
    public void testReadAndWriteMapField() {
        test(ctx -> ctx.addDeclaration("$a", Map.class).addDeclaration("$b", Map.class),
             " { $a.name = $b.name; }",
             " { $a.put(\"name\", $b.get(\"name\")); }");
    }
}