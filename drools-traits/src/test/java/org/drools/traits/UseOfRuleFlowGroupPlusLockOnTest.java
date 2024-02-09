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
package org.drools.traits;

import org.drools.kiesession.agenda.DefaultAgenda;
import org.drools.core.reteoo.ReteDumper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class UseOfRuleFlowGroupPlusLockOnTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UseOfRuleFlowGroupPlusLockOnTest.class);

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
        if (LOGGER.isDebugEnabled()) {
            ReteDumper.dumpRete(ksession);
        }
        ksession.addEventListener( new DebugAgendaEventListener() );
        try {
            ksession.insert(new Person());
            ksession.insert(new Cheese("eidam"));
            ((DefaultAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
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
