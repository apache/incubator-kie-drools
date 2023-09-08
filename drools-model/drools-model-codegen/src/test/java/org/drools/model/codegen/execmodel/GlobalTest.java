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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.InputDataTypes;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalTest extends BaseModelTest {

    public GlobalTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testGlobalInConsequence() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Result globalResult;" +
                "rule X when\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "then\n" +
                " globalResult.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.setGlobal("globalResult", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mark is 37");
    }

    @Test
    public void testGlobalInConstraint() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global java.lang.String nameG;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(nameG == name)\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("nameG", "Mark");

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mark is 37");
    }

    public static class Functions {
        public boolean lengthIs4(String s) {
            return s.length() == 4;
        }
        public int length(String s) {
            return s.length();
        }

        public boolean arrayContainsInstanceWithParameters(Object[] array, Object[] parms) throws Exception {
            if (array.length == 0) {
                return false;
            }

            for (Object o : array) {
                boolean fullmatch = true;

                for (int i = 0; fullmatch && i < parms.length; i++) {
                    if (parms[i] instanceof String && parms[i].toString().startsWith("get")) {
                        String methodName = parms[i].toString();
                        if (i + 1 >= parms.length) {
                            throw new RuntimeException("The parameter list is shorter than expected. Should be pairs of accessor method names and expected values.");
                        }

                        ++i;
                        Class<?> c = o.getClass();
                        Method m = c.getMethod(methodName, (Class[])null);
                        if (m == null) {
                            throw new RuntimeException("The method " + methodName + " does not exist on class " + o.getClass().getName() + ".");
                        }

                        Object result = m.invoke(o, (Object[])null);
                        if (result == null && parms[i] != null) {
                            fullmatch = false;
                        } else if (result != null && parms[i] == null) {
                            fullmatch = false;
                        } else if (!result.equals(parms[i])) {
                            fullmatch = false;
                        }
                    }
                }

                if (fullmatch) {
                    return true;
                }
            }

            return false;
        }

        public Double sumOf(Object[] objects) {
            Double ret = null;
            for (Object o : objects) {
              if ((o instanceof Number))
              {
                double d = Double.parseDouble(o.toString());
                if (ret == null) {
                  ret = d;
                } else {
                  ret = Double.valueOf(ret.doubleValue() + d);
                }
              }
            }
            return ret;
        }

    }

    @Test
    public void testGlobalBooleanFunction() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Functions.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Functions functions;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(functions.lengthIs4(name))\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("functions", new Functions());

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mark is 37");
    }

    @Test
    public void testGlobalFunctionOnLeft() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Functions.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Functions functions;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(functions.length(name) == 4)\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("functions", new Functions());

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mark is 37");
    }

    @Test
    public void testGlobalFunctionOnRight() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Functions.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Functions functions;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(4 == functions.length(name))\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("functions", new Functions());

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mark is 37");
    }

    public static class Family {
        public Object getPersons() {
            return new Object[]{new Person("Mario", 44), new Person("Mark", 40)};
        }
    }

    @Test
    public void testComplexGlobalFunction() {
        String str =
                "package org.mypkg;" +
                        "import " + Family.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule X when\n" +
                        "  $f : Family(functions.arrayContainsInstanceWithParameters((Object[])$f.getPersons(),\n" +
                        "              new Object[]{\"getAge\", 40}))\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new Family());

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testComplexGlobalFunctionWithShort() {
        String str =
                "package org.mypkg;" +
                        "import " + Family.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule X when\n" +
                        "  $f : Family( eval( true == functions.arrayContainsInstanceWithParameters((Object[])$f.getPersons(),\n" +
                        "              new Object[]{\"getAgeAsShort\", new Short((short) 40)}) ) )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new Family());

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testComplexGlobalFunctionWithShortEvalOnJoin() {
        String str =
                "package org.mypkg;" +
                        "import " + Family.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule X when\n" +
                        "  $f : Family()\n" +
                        "  $s : String( eval( true == functions.arrayContainsInstanceWithParameters((Object[])$f.getPersons(),\n" +
                        "              new Object[]{\"getAgeAsShort\", new Short((short) 40)}) ) )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new Family());
        ksession.insert("test");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testComplexGlobalFunctionWithShortNotFiring() {
        String str =
                "package org.mypkg;" +
                        "import " + Family.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule X when\n" +
                        "  $f : Family( eval( true == functions.arrayContainsInstanceWithParameters((Object[])$f.getPersons(),\n" +
                        "              new Object[]{\"getAgeAsShort\", new Short((short) 39)}) ) )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new Family());

        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }


    @Test
    public void testGlobalOnTypeDeclaration() throws Exception {
        String str =
                "declare MyObject end\n" +
                "global MyObject event;";

        KieSession ksession = getKieSession(str);
    }

    @Test
    public void testGlobalFunctionWithArrayInput() {
        String str =
                "package org.mypkg;" +
                        "import " + InputDataTypes.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule useSumOf when\n" +
                        "  $input : InputDataTypes( $no1Count_1 : no1Count\n" +
                        "           , $no2Count_1 : no2Count\n" +
                        "           , $no3Count_1 : no3Count\n" +
                        "           , $no4Count_1 : no4Count\n" +
                        "           , $no5Count_1 : no5Count\n" +
                        "           , $no6Count_1 : no6Count\n" +
                        "           , $no7Count_1 : no7Count\n" +
                        "           , $no8Count_1 : no8Count\n" +
                        "           , $no9Count_1 : no9Count\n" +
                        "           , $no10Count_1 : no10Count\n" +
                        "           , firings not contains \"fired\")\n" +
                        "then\n" +
                        "  $input.setNo13Count(functions.sumOf(new Object[]{$no1Count_1, $no2Count_1, $no3Count_1, $no4Count_1, $no5Count_1, $no6Count_1, $no7Count_1, $no8Count_1, $no9Count_1, $no10Count_1}).intValue());\n" +
                        "  $input.getFirings().add(\"fired\");\n" +
                        "  update($input);\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new InputDataTypes());

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testGlobalFunctionWithLargeArrayInput() {
        String str =
                "package org.mypkg;" +
                        "import " + InputDataTypes.class.getCanonicalName() + ";" +
                        "import " + Functions.class.getCanonicalName() + ";" +
                        "global Functions functions;" +
                        "rule useSumOf when\n" +
                        "  $input : InputDataTypes( " +
                        "             $no1Count_1 : no1Count\n" +
                        "           , $no2Count_1 : no2Count\n" +
                        "           , $no3Count_1 : no3Count\n" +
                        "           , $no4Count_1 : no4Count\n" +
                        "           , $no5Count_1 : no5Count\n" +
                        "           , $no6Count_1 : no6Count\n" +
                        "           , $no7Count_1 : no7Count\n" +
                        "           , $no8Count_1 : no8Count\n" +
                        "           , $no9Count_1 : no9Count\n" +
                        "           , $no10Count_1 : no10Count\n" +
                        "           , $no11Count_1 : no11Count\n" +
                        "           , $no12Count_1 : no12Count\n" +
                        "           , $no13Count_1 : no13Count\n" +
                        "           , $no14Count_1 : no14Count\n" +
                        "           , $no15Count_1 : no15Count\n" +
                        "           , $no16Count_1 : no16Count\n" +
                        "           , $no17Count_1 : no17Count\n" +
                        "           , $no18Count_1 : no18Count\n" +
                        "           , $no19Count_1 : no19Count\n" +
                        "           , $no20Count_1 : no20Count\n" +
                        "           , $no21Count_1 : no21Count\n" +
                        "           , $no22Count_1 : no22Count\n" +
                        "           , firings not contains \"fired\")\n" +
                        "then\n" +
                        "  $input.setNo13Count(functions.sumOf(new Object[]{" +
                        "       $no1Count_1, " +
                        "       $no2Count_1, " +
                        "       $no3Count_1, " +
                        "       $no4Count_1, " +
                        "       $no5Count_1, " +
                        "       $no6Count_1, " +
                        "       $no7Count_1, " +
                        "       $no8Count_1, " +
                        "       $no9Count_1, " +
                        "       $no10Count_1, " +
                        "       $no11Count_1," +
                        "       $no12Count_1," +
                        "       $no13Count_1," +
                        "       $no14Count_1," +
                        "       $no15Count_1," +
                        "       $no16Count_1," +
                        "       $no17Count_1," +
                        "       $no18Count_1," +
                        "       $no19Count_1," +
                        "       $no20Count_1," +
                        "       $no21Count_1," +
                        "       $no22Count_1" +
                        "" +
                        "}).intValue());\n" +
                        "  $input.getFirings().add(\"fired\");\n" +
                        "  update($input);\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal("functions", new Functions());
        ksession.insert(new InputDataTypes());

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testGlobalInDifferentPackage() throws InstantiationException, IllegalAccessException {
        // DROOLS-6657
        String def =
                "package org.drools.reproducer.definitions\n" +
                "declare Fact\n" +
                "  value : String\n" +
                "end\n" +
                "global java.util.List<String> globalList;\n" +
                "\n";

        String rule =
                "package org.drools.reproducer.rulesA\n" +
                "import org.drools.reproducer.definitions.*\n" +
                "\n" +
                "rule \"Rule\"\n" +
                "when\n" +
                "   Fact( value == \"FOO\")\n" +
                "then\n" +
                "    globalList.add(\"FOO matched\");\n" +
                "end\n";

        KieSession ksession = getKieSession( rule, def );
        KieBase kb = ksession.getKieBase();

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        FactType ft = kb.getFactType("org.drools.reproducer.definitions", "Fact");

        KieSession ks = kb.newKieSession();
        ks.setGlobal("globalList", new ArrayList<String>());
        Object f = ft.newInstance();
        ft.set(f, "value", "FOO");
        ks.insert(f);

        ks.fireAllRules();

        List<String> globalList = (List<String>)ks.getGlobal("globalList");
        assertThat(globalList.size()).isEqualTo(1);
        assertThat(globalList.get(0)).isEqualTo("FOO matched");
    }

    @Test
    public void testGenericOnGlobal() {
        // DROOLS-7155
        String def =
                "package org.drools.reproducer\n" +
                "global java.util.List<String> globalList;\n" +
                "rule R when\n" +
                "then\n" +
                "    globalList.add(\"test\");\n" +
                "end\n";

        KieSession ksession = getKieSession( def );
        ksession.setGlobal("globalList", new ArrayList<String>());

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testWrongGenericOnGlobal() {
        // DROOLS-7155
        String def =
                "package org.drools.reproducer\n" +
                "global java.util.List<Integer> globalList;\n" +
                "rule R when\n" +
                "then\n" +
                "    globalList.add(\"test\");\n" +
                "end\n";

        Results results = createKieBuilder(def).getResults();
        assertThat(results.getMessages(Message.Level.ERROR)).isNotEmpty();
        assertThat(results.getMessages(Message.Level.ERROR).stream().map(Message::getText)
                .anyMatch(s -> s.contains("The method add(Integer) in the type List<Integer> is not applicable for the arguments (String)"))).isTrue();
    }
}
