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

package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.DecisionTableFactory;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

public class UnicodeInXLSTest {

	@Test
    public void testUnicodeXLSDecisionTable() throws FileNotFoundException {

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("unicode.xls", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            System.out.println(DecisionTableFactory.loadFromInputStream(getClass().getResourceAsStream("unicode.xls"), dtconf));
            fail("Cannot build XLS decision table containing utf-8 characters\n" + kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        List<Člověk> dospělí = new ArrayList<Člověk>();
        commands.add(CommandFactory.newSetGlobal("dospělí", dospělí));
        Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(CommandFactory.newInsert(Řehoř));
        commands.add(CommandFactory.newFireAllRules());

        ksession.execute(CommandFactory.newBatchExecution(commands));

        // people with age greater than 18 should be added to list of adults
        assertNotNull(kbase.getRule("org.drools.decisiontable", "přidej k dospělým"));
        assertEquals(dospělí.size(), 5);
        assertEquals(dospělí.iterator().next().getJméno(), "Řehoř");

        assertNotNull(kbase.getRule("org.drools.decisiontable", "привет мир"));
        assertNotNull(kbase.getRule("org.drools.decisiontable", "你好世界"));
        assertNotNull(kbase.getRule("org.drools.decisiontable", "hallå världen"));
        assertNotNull(kbase.getRule("org.drools.decisiontable", "مرحبا العالم"));

        ksession.dispose();
    }
	
    public static class Člověk {

        private int věk;
        private String jméno;

        public void setVěk(int věk) {
            this.věk = věk;
        }

        public int getVěk() {
            return věk;
        }

        public void setJméno(String jméno) {
            this.jméno = jméno;
        }

        public String getJméno() {
            return jméno;
        }
    }
}
