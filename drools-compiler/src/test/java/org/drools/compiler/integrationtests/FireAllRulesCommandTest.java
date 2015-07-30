/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;

public class FireAllRulesCommandTest {
    @Test
    public void oneRuleFiredTest() {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString());

        assertEquals(1, fired);
    }

    @Test
    public void fiveRulesFiredTest() {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        commands.add(CommandFactory.newInsert(new Cheese("gruyere")));
        commands.add(CommandFactory.newInsert(new Cheese("cheddar")));
        commands.add(CommandFactory.newInsert(new Cheese("stinky")));
        commands.add(CommandFactory.newInsert(new Cheese("limburger")));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString());

        assertEquals(5, fired);
    }

    @Test
    public void zeroRulesFiredTest() {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert("not cheese"));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString());

        assertEquals(0, fired);
    }

    @Test
    public void oneRuleFiredWithDefinedMaxTest() {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        FireAllRulesCommand farc = (FireAllRulesCommand) CommandFactory.newFireAllRules(10);
        farc.setOutIdentifier("num-rules-fired");
        commands.add(farc);

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString());

        assertEquals(1, fired);
    }

    @Test
    public void infiniteLoopTerminatesAtMaxTest() {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " update($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        FireAllRulesCommand farc = (FireAllRulesCommand) CommandFactory.newFireAllRules(10);
        farc.setOutIdentifier("num-rules-fired");
        commands.add(farc);

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString());

        assertEquals(10, fired);
    }

    private StatelessKnowledgeSession getSession(String drl) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
        }
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase.newStatelessKnowledgeSession();
    }

}
