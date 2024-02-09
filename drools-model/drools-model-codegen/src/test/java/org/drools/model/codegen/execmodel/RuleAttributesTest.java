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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleAttributesTest extends BaseModelTest {

    public RuleAttributesTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test(timeout = 5000)
    public void testNoLoop() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R no-loop when\n" +
                        "  $p : Person(age > 18)\n" +
                        "then\n" +
                        "  modify($p) { setAge($p.getAge()+1) };\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertThat(me.getAge()).isEqualTo(41);
    }

    @Test
    public void testSalience() throws Exception {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "\n" +
                        "rule R1 salience 1 when\n" +
                        "  $p : Person( age == 40 )\n" +
                        "then\n" +
                        "   insert(\"R1\");\n" +
                        "   delete($p);" +
                        "end\n" +
                        "rule R2 salience 2 when\n" +
                        "  $p : Person( name.length == 5 )\n" +
                        "then\n" +
                        "   insert(\"R2\");\n" +
                        "   delete($p);" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("R2")).isTrue();
    }

    @Test
    public void testSalienceExpressionAttribute() throws Exception {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "\n" +
                     "rule R1 salience -$p.getAge() when\n" +
                     "  $p : Person( age == 40 )\n" +
                     "then\n" +
                     "   insert(\"R1\");\n" +
                     "   delete($p);" +
                     "end\n" +
                     "rule R2 salience $p.getAge() when\n" +
                     "  $p : Person( name.length == 5 )\n" +
                     "then\n" +
                     "   insert(\"R2\");\n" +
                     "   delete($p);" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("R2")).isTrue();
    }

    @Test
    public void testExpressionEnabledAttribute() throws Exception {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R1\n" +
                     "enabled ($b)\n" +
                     "when\n" +
                     "  $b : Boolean( )\n" +
                     "  $p : Person( )\n" +
                     "then\n" +
                     "   insert(\"R1\");\n" +
                     "   delete($p);" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(Boolean.FALSE);
        Person mario = new Person("Mario", 40);
        ksession.insert(mario);
        ksession.fireAllRules();

        Collection<Object> results = getObjectsIntoList(ksession, Object.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(mario)).isTrue();
        assertThat(!results.contains("R1")).isTrue();
    }

    @Test
    public void testCrossNoLoopWithNodeSharing() throws Exception {
        String str =
                "package org.drools.compiler.loop " +
                        "rule 'Rule 1' " +
                        "  agenda-group 'Start' " +
                        "  no-loop " +
                        "  when " +
                        "      $thing1 : String() " +
                        "      $thing2 : Integer() " +
                        "  then\n" +
                        "      System.out.println( 'At 1' ); " +
                        "      update( $thing2 ); " +
                        "end " +

                        "rule 'Rule 2' " +
                        "  agenda-group 'End' " +
                        "  no-loop " +
                        "  when " +
                        "      $thing1 : String() " +
                        "      $thing2 : Integer() " +
                        "  then " +
                        "      System.out.println( 'At 2' ); " +
                        "      update( $thing2 ); " +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert( "hello" );
        ksession.insert( new Integer( 42 ) );

        // set the agenda groups in reverse order so that stack is preserved
        ksession.getAgenda().getAgendaGroup( "End" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "Start" ).setFocus();

        int x = ksession.fireAllRules( 10 );
        assertThat(x).isEqualTo(2);
    }

    @Test
    public void testCalendars() {
        String str =
                "package org.drools.compiler.integrationtests;\n" +
                        "\n" +
                        "global java.util.List list\n" +
                        " \n" +
                        "rule \"weekend\"\n" +
                        "    calendars \"weekend\"\n" +
                        "    \n" +
                        "    when\n" +
                        "    then\n" +
                        "        list.add(\"weekend\");\n" +
                        "end\n" +
                        " \n" +
                        "rule \"weekday\"\n" +
                        "    calendars \"weekday\"\n" +
                        "\n" +
                        "    when\n" +
                        "    then\n" +
                        "       list.add(\"weekday\");\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ArrayList<String> list = new ArrayList<String>();

        ksession.getCalendars().set("weekend", WEEKEND);
        ksession.getCalendars().set("weekday", WEEKDAY);
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(1);
    }

    private static final org.kie.api.time.Calendar WEEKEND = new org.kie.api.time.Calendar() {

        @Override
        public boolean isTimeIncluded(long timestamp) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);

            int day = c.get(Calendar.DAY_OF_WEEK);

            return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
        }
    };

    private static final org.kie.api.time.Calendar WEEKDAY = new org.kie.api.time.Calendar() {

        @Override
        public boolean isTimeIncluded(long timestamp) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);

            int day = c.get(Calendar.DAY_OF_WEEK);
            return day != Calendar.SATURDAY && day != Calendar.SUNDAY;
        }
    };

    @Test
    public void testAutoFocus() {
        String str =
                "package org.drools.testcoverage.functional;\n" +
                        "//generated from Decision Table\n" +
                        "// rule values at A9, header at A4\n" +
                        "rule \"a\"\n" +
                        "  when\n" +
                        "    String(this == \"lockOnActive\")\n" +
                        "  then\n" +
                        "    drools.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"a\").setFocus();\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A10, header at A4\n" +
                        "rule \"a1\"\n" +
                        "  salience 0\n" +
                        "  lock-on-active true\n" +
                        "  agenda-group \"a\"\n" +
                        "  when\n" +
                        "    String(this == \"lockOnActive2\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A11, header at A4\n" +
                        "rule \"a2\"\n" +
                        "  salience 10\n" +
                        "  lock-on-active false\n" +
                        "  agenda-group \"a\"\n" +
                        "  when\n" +
                        "    String(this == \"lockOnActive\")\n" +
                        "  then\n" +
                        "    insert(new String(\"lockOnActive2\"));\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A12, header at A4\n" +
                        "rule \"a3\"\n" +
                        "  salience 5\n" +
                        "  lock-on-active true\n" +
                        "  agenda-group \"a\"\n" +
                        "  when\n" +
                        "    String(this == \"lockOnActive\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A13, header at A4\n" +
                        "rule \"b1\"\n" +
                        "  salience 10\n" +
                        "  agenda-group \"b\"\n" +
                        "  when\n" +
                        "    String(this == \"withoutAutoFocus\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A14, header at A4\n" +
                        "rule \"b2\"\n" +
                        "  salience 5\n" +
                        "  auto-focus true\n" +
                        "  agenda-group \"b\"\n" +
                        "  when\n" +
                        "    String(this == \"autoFocus\")\n" +
                        "  then\n" +
                        "    insert(new String(\"withoutAutoFocus\"));\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A15, header at A4\n" +
                        "rule \"c1\"\n" +
                        "  salience 0\n" +
                        "  activation-group \"c\"\n" +
                        "  when\n" +
                        "    String(this == \"activationGroup\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A16, header at A4\n" +
                        "rule \"c2\"\n" +
                        "  salience 10\n" +
                        "  activation-group \"c\"\n" +
                        "  when\n" +
                        "    String(this == \"activationGroup\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n" +
                        "// rule values at A17, header at A4\n" +
                        "rule \"c3\"\n" +
                        "  salience 5\n" +
                        "  activation-group \"c\"\n" +
                        "  when\n" +
                        "    String(this == \"activationGroup\")\n" +
                        "  then\n" +
                        "end\n" +
                        "\n";

        KieSession ksession = getKieSession( str );

        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first test - we try to fire rule in agenda group which has auto focus
        // disable, we won't succeed
        final FactHandle withoutAutoFocus = ksession.insert("withoutAutoFocus");
        ksession.fireAllRules();
        assertThat(listener.size()).isEqualTo(0);

        // second test - we try to fire rule in agenda group with auto focus
        // enabled
        // it fires and it's defined consequence causes to fire second rule
        // which has no auto focus
        ksession.insert("autoFocus");
        ksession.delete(withoutAutoFocus);
        ksession.fireAllRules();
        assertThat(listener.size()).isEqualTo(2);
        final String[] expected = {"b2", "b1"};
        for (int i = 0; i < listener.size(); i++) {
            assertThat(listener.get(i)).isEqualTo(expected[i]);
        }
    }

    public static class OrderListener extends DefaultAgendaEventListener {

        private List<String> rulesFired = new ArrayList<String>();

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event) {
            rulesFired.add(event.getMatch().getRule().getName());
        }

        public int size() {
            return rulesFired.size();
        }

        public String get(final int index) {
            return rulesFired.get(index);
        }
    }

    @Test
    public void testMetadataBasics() {
        final String PACKAGE_NAME = "org.asd";
        final String RULE_NAME = "hello world";
        final String RULE_KEY = "output";
        final String RULE_VALUE = "Hello world!";
        final String rule = " package " + PACKAGE_NAME + ";\n" + 
                            " rule \"" + RULE_NAME + "\"\n" +
                            " @" + RULE_KEY + "(\"\\\"" + RULE_VALUE + "\\\"\")\n" +
                            " when\n" +
                            " then\n" +
                            "     System.out.println(\"Hello world!\");\n" +
                            " end";

        KieSession ksession = getKieSession(rule);

        final Map<String, Object> metadata = ksession.getKieBase().getRule(PACKAGE_NAME, RULE_NAME).getMetaData();

        assertThat(metadata.containsKey(RULE_KEY)).isTrue();
        assertThat(metadata.get(RULE_KEY)).isEqualTo("\"" + RULE_VALUE + "\"");
    }

    @Test
    public void testMetadataValue() {
        final String rule = " package org.test;\n" +
                            " rule R1\n" +
                            " @metaValueString(\"asd\")\n" +
                            " @metaValueCheck1(java.math.BigDecimal.ONE)\n" +
                            " @metaValueCheck2(Boolean.TRUE)\n" +
                            " @metaValueCheck3(System.out)\n" +
                            " when\n" +
                            " then\n" +
                            "     System.out.println(\"Hello world!\");\n" +
                            " end";

        KieSession ksession = getKieSession(rule);

        final Map<String, Object> metadata = ksession.getKieBase().getRule("org.test", "R1").getMetaData();

        assertThat(metadata.get("metaValueString")).isEqualTo("asd");
        assertThat(metadata.get("metaValueCheck1")).isSameAs(java.math.BigDecimal.ONE);
        assertThat(metadata.get("metaValueCheck2")).isSameAs(Boolean.TRUE);
        assertThat(metadata.get("metaValueCheck3")).isSameAs(System.out);
    }

    @Test
    public void testDynamicSalience() {
        String str =
                "global java.util.List list;\n" +
                "rule R1 salience $s.length when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    list.add($s);" +
                "end\n" +
                "rule R2 salience $i when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    list.add($i);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Object> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "ok" );
        ksession.insert( "test" );
        ksession.insert( 3 );
        ksession.insert( 1 );

        ksession.fireAllRules();
        assertThat(list).isEqualTo(Arrays.asList("test", 3, "ok", 1));
    }

    public static final int CONST_SALIENCE = 1;

    @Test
    public void testSalienceFromConstant() {
        // DROOLS-5550
        String str =
                "import " + RuleAttributesTest.class.getCanonicalName() + "\n;" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    list.add($s);" +
                "end\n" +
                "rule R2 salience RuleAttributesTest.CONST_SALIENCE when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    list.add($i);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Object> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "ok" );
        ksession.insert( 1 );

        ksession.fireAllRules();
        assertThat(list).isEqualTo(Arrays.asList(1, "ok"));
    }
}
