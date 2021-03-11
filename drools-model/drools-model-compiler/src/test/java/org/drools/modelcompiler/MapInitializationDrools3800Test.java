package org.drools.modelcompiler;

import java.util.Map;
import java.util.Objects;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapInitializationDrools3800Test extends BaseModelTest {

    public MapInitializationDrools3800Test(BaseModelTest.RUN_TYPE testRunType) {
        super(testRunType);
    }

    public static boolean calc(Map<String, Object> params) {
        return Objects.equals(params.get("src"), params.get("target"));
    }

    public static class Fact {
        private String name;
        private String result;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    @Test
    public void testMapInitialization() {
        StringBuilder r = new StringBuilder();
        r.append("package ").append(getClass().getPackage().getName()).append("\n");
        r.append("\n");
        r.append("import ").append(Fact.class.getCanonicalName()).append("\n");
        r.append("import static ").append(getClass().getName()).append(".calc").append("\n");
        r.append("\n");
        r.append("rule rule1").append("\n");
        r.append("  when").append("\n");
        r.append("    $fact: Fact(").append("\n");
        r.append("      result != \"OK\", calc([\"src\":name, \"target\":\"TEST\"])").append("\n");
        r.append("    )").append("\n");
        r.append("  then").append("\n");
        r.append("    modify ($fact) {").append("\n");
        r.append("      setResult(\"OK\")").append("\n");
        r.append("    }").append("\n");
        r.append("end").append("\n");

        KieSession ksession = getKieSession(r.toString() );


        Fact fact = new Fact();
        fact.setName("TEST");
        ksession.insert(fact);

        ksession.fireAllRules();

        assertThat(fact.getResult(), is("OK"));
    }

    @Test
    public void testPropertyReactivityHanging() {
        // DROOLS-3849
        String rule =
                "package " + getClass().getPackage().getName() + "\n" +
                "import " + Fact.class.getCanonicalName() + "\n" +
                "import static " + getClass().getName() + ".calc\n" +
                "\n" +
                "rule rule1\n" +
                "  when\n" +
                "    $fact: Fact(\n" +
                "      calc([\"src\":name, \"target\":\"TEST\"])\n" +
                "    )\n" +
                "  then\n" +
                "    modify ($fact) {\n" +
                "        setResult(\"OK\")\n" +
                "    }\n" +
                "end";

        KieSession ksession = getKieSession(rule);

        Fact fact = new Fact();
        fact.setName("TEST");
        ksession.insert(fact);

        assertEquals(1, ksession.fireAllRules(3));

        assertThat(fact.getResult(), is("OK"));
    }
}
