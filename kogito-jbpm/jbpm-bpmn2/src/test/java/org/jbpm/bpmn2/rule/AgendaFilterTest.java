/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.rule;

import java.util.ArrayList;
import java.util.List;

import org.drools.commands.runtime.rule.FireAllRulesCommand;
import org.drools.core.event.DebugProcessEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.junit.jupiter.api.Assertions.fail;

public class AgendaFilterTest extends AbstractBaseTest {

    public static class Message {

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    @Test
    public void testActivationCancelled() {
        // JBRULES-3376
        String drl = "package org.jboss.qa.brms.agendafilter\n" +
                "declare CancelFact\n" +
                "   cancel : boolean = true\n" +
                "end\n" +
                "rule NoCancel\n" +
                "   ruleflow-group \"rfg\"\n" +
                "   when\n" +
                "       $fact : CancelFact ( cancel == false )\n" +
                "   then\n" +
                "       System.out.println(\"No cancel...\");\n" +
                "       modify ($fact) {\n" +
                "           setCancel(true);\n" +
                "       }\n" +
                "end\n" +
                "rule PresenceOfBothFacts\n" +
                "   ruleflow-group \"rfg\"\n" +
                "   salience -1\n" +
                "   when\n" +
                "       $fact1 : CancelFact( cancel == false )\n" +
                "       $fact2 : CancelFact( cancel == true )\n" +
                "   then\n" +
                "       System.out.println(\"Both facts!\");\n" +
                "end\n" +
                "rule PresenceOfFact\n" +
                "   ruleflow-group \"rfg\"\n" +
                "   when\n" +
                "       $fact : CancelFact( )\n" +
                "   then\n" +
                "       System.out.println(\"We have a \" + ($fact.isCancel() ? \"\" : \"non-\") + \"cancelling fact!\");\n" +
                "end\n" +
                "rule Cancel\n" +
                "   ruleflow-group \"rfg\"\n" +
                "   when\n" +
                "       $fact : CancelFact ( cancel == true )\n" +
                "   then\n" +
                "       System.out.println(\"Cancel!\");\n" +
                "end";

        String rf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                "         type=\"RuleFlow\" name=\"flow\" id=\"bz761715\" package-name=\"org.jboss.qa.brms.agendafilter\" >\n" +
                "  <header>\n" +
                "  </header>\n" +
                "  <nodes>\n" +
                "    <start id=\"1\" name=\"Start\" x=\"16\" y=\"16\" width=\"48\" height=\"48\" />\n" +
                "    <ruleSet id=\"2\" name=\"Rule\" x=\"208\" y=\"16\" width=\"80\" height=\"48\" ruleFlowGroup=\"rfg\" />\n" +
                "    <actionNode id=\"3\" name=\"Script\" x=\"320\" y=\"16\" width=\"80\" height=\"48\" >\n" +
                "        <action type=\"expression\" dialect=\"java\" >System.out.println(\"Finishing process...\");</action>\n" +
                "    </actionNode>\n" +
                "    <end id=\"4\" name=\"End\" x=\"432\" y=\"16\" width=\"48\" height=\"48\" />\n" +
                "    <actionNode id=\"5\" name=\"Script\" x=\"96\" y=\"16\" width=\"80\" height=\"48\" >\n" +
                "        <action type=\"expression\" dialect=\"java\" >System.out.println(\"Starting process...\");</action>\n" +
                "    </actionNode>\n" +
                "  </nodes>\n" +
                "  <connections>\n" +
                "    <connection from=\"5\" to=\"2\" />\n" +
                "    <connection from=\"2\" to=\"3\" />\n" +
                "    <connection from=\"3\" to=\"4\" />\n" +
                "    <connection from=\"1\" to=\"5\" />\n" +
                "  </connections>\n" +
                "</process>";

        builder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        builder.add(ResourceFactory.newByteArrayResource(rf.getBytes()), ResourceType.DRF);

        if (builder.hasErrors()) {
            fail(builder.getErrors().toString());
        }

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        kruntime.getKieSession().addEventListener(new DebugAgendaEventListener());
        kruntime.getProcessEventManager().addEventListener(new DebugProcessEventListener());

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(newCancelFact(kruntime, false)));
        commands.add(CommandFactory.newInsert(newCancelFact(kruntime, true)));
        commands.add(CommandFactory.newStartProcess("bz761715"));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));

        kruntime.getKieSession().execute(CommandFactory.newBatchExecution(commands));
    }

    private Object newCancelFact(KogitoProcessRuntime kruntime, boolean cancel) {
        FactType type = kruntime.getKieSession().getKieBase().getFactType("org.jboss.qa.brms.agendafilter", "CancelFact");
        Object instance = null;
        try {
            instance = type.newInstance();

            type.set(instance, "cancel", cancel);
        } catch (IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }

        return instance;
    }

    public static class CancelAgendaFilter implements AgendaFilter {
        public boolean accept(Match activation) {
            return !"Cancel".equals(activation.getRule().getName());
        }
    }

}
