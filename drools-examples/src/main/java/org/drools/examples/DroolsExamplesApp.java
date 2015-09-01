/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples;

import org.drools.examples.datadriventemplate.DataDrivenTemplateExample;
import org.drools.examples.decisiontable.PricingRuleDTExample;
import org.drools.examples.decisiontable.PricingRuleTemplateExample;
import org.drools.examples.fibonacci.FibonacciExample;
import org.drools.examples.golfing.GolfingExample;
import org.drools.examples.helloworld.HelloWorldExample;
import org.drools.examples.honestpolitician.HonestPoliticianExample;
import org.drools.examples.petstore.PetStoreExample;
import org.drools.examples.shopping.ShoppingExample;
import org.drools.examples.state.StateExampleUsingAgendaGroup;
import org.drools.examples.state.StateExampleUsingSalience;
import org.drools.examples.sudoku.SudokuExample;
import org.drools.examples.templates.SimpleRuleTemplateExample;
import org.drools.examples.troubleticket.TroubleTicketExample;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDSL;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDT;
import org.drools.examples.workitemconsequence.WorkItemConsequenceExample1;
import org.drools.examples.workitemconsequence.WorkItemConsequenceExample2;
import org.drools.games.adventures.TextAdventure;
import org.drools.games.pong.PongMain;
import org.drools.games.wumpus.WumpusWorldMain;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DroolsExamplesApp extends JFrame {

    public static void main(String[] args) {
        DroolsExamplesApp droolsExamplesApp = new DroolsExamplesApp();
        droolsExamplesApp.pack();
        droolsExamplesApp.setVisible(true);
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final KieContainer kieContainer;

    public DroolsExamplesApp() {
        super("JBoss BRMS examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logger.info("DroolsExamplesApp started.");
        kieContainer = createKieContainer();
    }

    private KieContainer createKieContainer() {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        return kc;
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which GUI example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("SudokuExample") {
            public void actionPerformed(ActionEvent e) {
                new SudokuExample().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PetStoreExample") {
            public void actionPerformed(ActionEvent e) {
                new PetStoreExample().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TextAdventure") {
            public void actionPerformed(ActionEvent e) {
                new TextAdventure().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Pong") {
            public void actionPerformed(ActionEvent e) {
                new PongMain().init(kieContainer, false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WumpusWorld") {
            public void actionPerformed(ActionEvent e) {
                new WumpusWorldMain().init(kieContainer, false);
            }
        }));
        
        contentPane.add(new JLabel("Which output example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("HelloWorldExample") {
            public void actionPerformed(ActionEvent e) {
                HelloWorldExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("FibonacciExample") {
            public void actionPerformed(ActionEvent e) {
                FibonacciExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("ShoppingExample") {
            public void actionPerformed(ActionEvent e) {
                ShoppingExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("HonestPoliticianExample") {
            public void actionPerformed(ActionEvent e) {
                HonestPoliticianExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("GolfingExample") {
            public void actionPerformed(ActionEvent e) {
                GolfingExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("SimpleRuleTemplateExample") {
            public void actionPerformed(ActionEvent e) {
                SimpleRuleTemplateExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExample") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDT") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDT.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDSL") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDSL.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingSalience") {
            public void actionPerformed(ActionEvent e) {
                StateExampleUsingSalience.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingAgendaGroup") {
            public void actionPerformed(ActionEvent e) {
                StateExampleUsingAgendaGroup.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PricingRuleTemplateExample") {
            public void actionPerformed(ActionEvent e) {
                PricingRuleTemplateExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PricingRuleDTExample") {
            public void actionPerformed(ActionEvent e) {
                PricingRuleDTExample.execute( kieContainer );
            }
        }));
        contentPane.add(new JButton(new AbstractAction("DataDrivenTemplateExample") {
            public void actionPerformed(ActionEvent e) {
                DataDrivenTemplateExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WorkItemConsequenceExample1") {
            public void actionPerformed(ActionEvent e) {
                WorkItemConsequenceExample1.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WorkItemConsequenceExample2") {
            public void actionPerformed(ActionEvent e) {
                WorkItemConsequenceExample2.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
