/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.integrationtests;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.junit.Assert.assertEquals;

public class TruthManagementSystemTest {

    @Test
    public void testUpdateLogicalEvent() {
        // DROOLS-5971
        String drl =
                "dialect 'mvel'\n" +
                        "\n" +
                        "import java.util.concurrent.atomic.AtomicInteger\n" +
                        "import " + Event.class.getCanonicalName() + "\n" +
                        "import " + AdminEvent.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule 'negative integer'\n" +
                        "    when\n" +
                        "        $int: AtomicInteger(intValue < 0)\n" +
                        "    then\n" +
                        "        insertLogical(new Event(\"negative integer\"))\n" +
                        "end\n" +
                        "\n" +
                        "rule 'increment positive if no negative values'\n" +
                        "    when\n" +
                        "        $positive: AtomicInteger(intValue > 0)\n" +
                        "        not Event()\n" +
                        "    then\n" +
                        "        $positive.incrementAndGet();\n" +
                        "end\n" +
                        "\n" +
                        "rule 'update logical event'\n" +
                        "    when\n" +
                        "        AdminEvent(message == 'update logical event')\n" +
                        "        $event: Event()\n" +
                        "    then\n" +
                        "        $event.setMessage(\"Updated logical event\");\n" +
                        "        update($event);\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();

        KieSession ksession = kbase.newKieSession();

        AtomicInteger positive = new AtomicInteger(1);

        ksession.insert(new AtomicInteger(-1));
        ksession.fireAllRules();
        ksession.insert(positive);
        ksession.fireAllRules();
        ksession.insert(new AdminEvent("update logical event"));
        ksession.fireAllRules();

        assertEquals(1, positive.get());
    }

    public static class Event {
        private String message;

        public Event(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public boolean equals(Object obj) {
            return reflectionEquals(this, obj, false);
        }

        @Override
        public int hashCode() {
            return reflectionHashCode(this, false);
        }
    }

    public static class AdminEvent {
        private String message;

        public AdminEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
