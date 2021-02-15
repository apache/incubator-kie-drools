/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2;

import org.jbpm.bpmn2.objects.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgendaFilterTest extends JbpmBpmn2TestCase {

    @Test
    public void testNoFilter() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AgendaFilter.bpmn2", "BPMN2-AgendaFilter.drl");

        Order order = new Order();
        order.setId("ORDER-1");
        kruntime.getKieSession().insert(order);

        kruntime.startProcess("Ruleflow");

        assertTrue(order.isValid());
    }

    @Test
    public void testWithFilter() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AgendaFilter.bpmn2", "BPMN2-AgendaFilter.drl");

        Order order = new Order();
        order.setId("ORDER-1");
        kruntime.getKieSession().insert(order);

        kruntime.startProcess("Ruleflow", match -> false);

        assertFalse(order.isValid());
    }
}
