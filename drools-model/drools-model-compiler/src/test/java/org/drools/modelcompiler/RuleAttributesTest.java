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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        assertEquals( 41, me.getAge() );
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
        assertEquals(1, results.size());
        assertTrue(results.contains("R2"));
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
        assertEquals( 2, x );
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

        assertEquals(1, list.size());
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
        Assertions.assertThat(listener.size()).isEqualTo(0);

        // second test - we try to fire rule in agenda group with auto focus
        // enabled
        // it fires and it's defined consequence causes to fire second rule
        // which has no auto focus
        ksession.insert("autoFocus");
        ksession.delete(withoutAutoFocus);
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = {"b2", "b1"};
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
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
}
