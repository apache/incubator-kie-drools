package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DroolsContextTest extends BaseModelTest {

    public DroolsContextTest(final RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testDroolsContext() {
        final String str =
                "global java.util.List list\n" +
                        "global java.util.List list2\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(list2.add(kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testDroolsContextInString() {
        final String str =
                "global java.util.List list\n" +
                        "global java.util.List list2\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(list2.add(\"something\" + kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testDroolsContextWithoutReplacingStrings() {
        final String str =
                "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(\"this kcontext shoudln't be replaced\");\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("this kcontext shoudln't be replaced");
    }

    @Test
    public void testRuleContext() {
        final String str =
                "import " + FactWithRuleContext.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "    $factWithRuleContext: FactWithRuleContext() \n" +
                        "then\n" +
                        " list.add($factWithRuleContext.getRuleName(kcontext));\n" +
                        "end";

        final KieSession ksession = getKieSession(str);

        final List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.insert(new FactWithRuleContext());
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.iterator().next()).isEqualTo("R");
    }

    public static class FactWithRuleContext {
        public String getRuleName(final org.kie.api.runtime.rule.RuleContext ruleContext) {
            return ruleContext.getRule().getName();
        }
    }
}
