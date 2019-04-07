/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.DebugProcessEventListener;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgendaFilterTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AgendaFilterTest.class);
    
    @Test
    public void testAgendaFilter() {
        // JBRULES-3374
        String drl = "package org.drools\n" +
                " \n" +
                "import org.jbpm.integrationtests.AgendaFilterTest.Message\n" +
                " \n" +
                "rule \"R1\"\n" +
                "ruleflow-group \"node1\"\n" +
                "no-loop \n" +
                "salience 3\n" +
                "    when\n" +
                "        Message( status == Message.GOODBYE, myMessage : message )\n" +
                "    then\n" +
                "       System.out.println( \"R1\"  );\n" +
                "end\n" +
                "\n" +
                "rule \"R2\"\n" +
                "ruleflow-group \"node1\"\n" +
                "no-loop \n" +
                "salience 2\n" +
                "    when\n" +
                "        m : Message( status == Message.HELLO, myMessage : message )\n" +
                "    then\n" +
                "        System.out.println( \"R2\"  );\n" +
                "        m.setMessage( \"Goodbye cruel world\" );\n" +
                "        m.setStatus( Message.GOODBYE );\n" +
                "        update( m );\n" +
                "end\n" +
                "\n" +
                "rule \"R3\"\n" +
                "ruleflow-group \"node2\"\n" +
                "no-loop \n" +
                "    when\n" +
                "        m: Message( status == Message.GOODBYE, myMessage : message )\n" +
                "    then\n" +
                "        System.out.println( \"R3\"  );\n" +
                "        m.setStatus(5);\n" +
                "        update (m);\n" +
                "        \n" +
                "end";

        String rf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                "         type=\"RuleFlow\" name=\"flow\" id=\"process-test\" package-name=\"com.sample\" >\n" +
                "\n" +
                "  <header>\n" +
                "  </header>\n" +
                "\n" +
                "  <nodes>\n" +
                "    <start id=\"1\" name=\"Start\" x=\"122\" y=\"96\" width=\"48\" height=\"48\" />\n" +
                "    <ruleSet id=\"2\" name=\"Node1\" x=\"277\" y=\"96\" width=\"80\" height=\"48\" ruleFlowGroup=\"node1\" />\n" +
                "    <ruleSet id=\"3\" name=\"Node2\" x=\"433\" y=\"98\" width=\"80\" height=\"48\" ruleFlowGroup=\"node2\" />\n" +
                "    <end id=\"4\" name=\"End\" x=\"645\" y=\"96\" width=\"48\" height=\"48\" />\n" +
                "  </nodes>\n" +
                "\n" +
                "  <connections>\n" +
                "    <connection from=\"1\" to=\"2\" />\n" +
                "    <connection from=\"2\" to=\"3\" />\n" +
                "    <connection from=\"3\" to=\"4\" />\n" +
                "  </connections>\n" +
                "\n" +
                "</process>";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource(rf.getBytes()), ResourceType.DRF );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieSession ksession = createKieSession(kbuilder.getKnowledgePackages().toArray(new KiePackage[0]));

        // go !
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        ksession.startProcess("process-test");
        
        assertEquals("Goodbye cruel world", message.getMessage());
    }

    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

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

    public static class SalienceFilter implements AgendaFilter {

        private Integer currentSalience = null;

        public boolean accept(Match activation) {
            RuleImpl rule = (RuleImpl)activation.getRule();

            if (currentSalience == null){
                currentSalience = rule.getSalience() != null ? Integer.valueOf(rule.getSalience().toString()) : 0;
            }
            boolean nocancel = currentSalience >= Integer.valueOf(rule.getSalience().toString());

            if(!nocancel){
                logger.info("cancelling -> {}", rule.getName());
            }

            return nocancel;
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource(rf.getBytes()), ResourceType.DRF );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugProcessEventListener());

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(newCancelFact(ksession, false)));
        commands.add(CommandFactory.newInsert(newCancelFact(ksession, true)));
        commands.add(CommandFactory.newStartProcess("bz761715"));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));
        commands.add(new FireAllRulesCommand(new CancelAgendaFilter()));

        ksession.execute(CommandFactory.newBatchExecution(commands));
    }

    private Object newCancelFact(KieSession ksession, boolean cancel) {
        FactType type = ksession.getKieBase().getFactType("org.jboss.qa.brms.agendafilter", "CancelFact");
        Object instance = null;
        try {
            instance = type.newInstance();

            type.set(instance, "cancel", cancel);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        }

        return instance;
    }

    public static class CancelAgendaFilter implements AgendaFilter {
        public boolean accept(Match activation) {
            return !"Cancel".equals(activation.getRule().getName());
        }
    }

    @Test
    public void testGetListeners() {
        // JBRULES-3378
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }

        KieSession ksession = kbuilder.newKieBase().newKieSession();

        ksession.getAgendaEventListeners();
        ksession.getProcessEventListeners();
        ksession.getRuleRuntimeEventListeners();

        ksession.dispose();
    }
}
