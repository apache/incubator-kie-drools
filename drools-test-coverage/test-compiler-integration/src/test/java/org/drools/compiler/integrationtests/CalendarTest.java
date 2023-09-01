package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CalendarTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CalendarTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void test() {
        // BZ-1007385
        final String drl = "package org.drools.compiler.integrationtests;\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("calendar-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ArrayList<String> list = new ArrayList<>();

            ksession.getCalendars().set("weekend", WEEKEND);
            ksession.getCalendars().set("weekday", WEEKDAY);
            ksession.setGlobal("list", list);

            ksession.fireAllRules();
            ksession.dispose();

            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    private static final org.kie.api.time.Calendar WEEKEND = timestamp -> {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        final int day = c.get(Calendar.DAY_OF_WEEK);

        return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
    };

    private static final org.kie.api.time.Calendar WEEKDAY = timestamp -> {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        final int day = c.get(Calendar.DAY_OF_WEEK);
        return day != Calendar.SATURDAY && day != Calendar.SUNDAY;
    };

}
