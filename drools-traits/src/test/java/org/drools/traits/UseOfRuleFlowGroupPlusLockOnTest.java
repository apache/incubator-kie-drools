package org.drools.traits;

import org.drools.core.common.DefaultAgenda;
import org.drools.core.reteoo.ReteDumper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class UseOfRuleFlowGroupPlusLockOnTest {

    private static final String drl = "package com.sample\n"
            + "import " + Person.class.getCanonicalName() + " ;\n"
            + "import " + Cheese.class.getCanonicalName() + " ;\n"
            + "rule R1\n"
            + "ruleflow-group \"group1\"\n"
            + "lock-on-active true\n"
            + "when\n"
            + "   $p : Person()\n" + "then\n"
            + "   $p.setName(\"John\");\n"
            + "   update ($p);\n"
            + "end\n"
            + " "
            + "rule R2\n"
            + "ruleflow-group \"group1\"\n"
            + "lock-on-active true\n"
            + "when\n"
            + "   $p : Person( name == null )\n"
            + "   forall ( Cheese ( type == \"cheddar\" ))\n"
            + "then\n"
            + "end\n";

    @Test
    public void test() {
        KieHelper kieHelper = new KieHelper().addContent(drl, ResourceType.DRL);
        KieBase kbase = kieHelper.build();
        KieSession ksession = kbase.newKieSession();
        ReteDumper.dumpRete(ksession);
        ksession.addEventListener( new DebugAgendaEventListener() );
        try {
            ksession.insert(new Person());
            ksession.insert(new Cheese("eidam"));
            ((DefaultAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");
            int rulesFired = ksession.fireAllRules();
            Assert.assertEquals(1, rulesFired);


        } finally {
            ksession.dispose();
        }
    }

    public class Cheese {

        private String type;
        private int price;

        public Cheese() {
        }

        public Cheese(String type) {
            this.type = type;
        }

        public int getPrice() {
            return this.price;
        }

        public String getType() {
            return this.type;
        }

        public void setPrice(final int price) {
            this.price = price;
        }
    }

    public class Person {

        private String name;

        public Person() {
        }

        public Person(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "[Person name='" + this.name + "']";
        }
    }
}
