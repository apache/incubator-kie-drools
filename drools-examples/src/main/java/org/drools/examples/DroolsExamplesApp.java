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

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsExamplesApp extends JFrame {
    private static final long serialVersionUID = 5511989981501462030L;

    public static void main(String[] args) {
        DroolsExamplesApp droolsExamplesApp = new DroolsExamplesApp();
        droolsExamplesApp.pack();
        droolsExamplesApp.setVisible(true);
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public DroolsExamplesApp() {
        super("JBoss BRMS examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logger.info("DroolsExamplesApp started.");
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which GUI example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("SudokuExample") {
            private static final long serialVersionUID = 826335283932936110L;

            public void actionPerformed(ActionEvent e) {
                new SudokuExample().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PetStoreExample") {
            private static final long serialVersionUID = -2858687803954344152L;

            public void actionPerformed(ActionEvent e) {
                new PetStoreExample().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TextAdventure") {
            private static final long serialVersionUID = 6738368225425313447L;

            public void actionPerformed(ActionEvent e) {
                new TextAdventure().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Pong") {
            private static final long serialVersionUID = 4687421449425270200L;

            public void actionPerformed(ActionEvent e) {
                new PongMain().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WumpusWorld") {
            private static final long serialVersionUID = 821103188056845210L;

            public void actionPerformed(ActionEvent e) {
                new WumpusWorldMain().init(false);
            }
        }));
        
        contentPane.add(new JLabel("Which output example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("HelloWorldExample") {
            private static final long serialVersionUID = -5689396653133154206L;

            public void actionPerformed(ActionEvent e) {
                HelloWorldExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("FibonacciExample") {
            private static final long serialVersionUID = -8413095787165679797L;

            public void actionPerformed(ActionEvent e) {
                FibonacciExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("ShoppingExample") {
            private static final long serialVersionUID = 2394887300311853362L;

            public void actionPerformed(ActionEvent e) {
                ShoppingExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("HonestPoliticianExample") {
            private static final long serialVersionUID = -834426138171650395L;

            public void actionPerformed(ActionEvent e) {
                HonestPoliticianExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("GolfingExample") {
            private static final long serialVersionUID = -2581665804843270981L;

            public void actionPerformed(ActionEvent e) {
                GolfingExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("SimpleRuleTemplateExample") {
            private static final long serialVersionUID = -2558081417593825468L;

            public void actionPerformed(ActionEvent e) {
                SimpleRuleTemplateExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExample") {
            private static final long serialVersionUID = 7960525920219679429L;

            public void actionPerformed(ActionEvent e) {
                TroubleTicketExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDT") {
            private static final long serialVersionUID = 1147091777873824856L;

            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDT.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDSL") {
            private static final long serialVersionUID = -8665408797950741235L;

            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDSL.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingSalience") {
            private static final long serialVersionUID = 4126835375804937460L;

            public void actionPerformed(ActionEvent e) {
                StateExampleUsingSalience.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("StateExampleUsingAgendaGroup") {
            private static final long serialVersionUID = 8950581127960817265L;

            public void actionPerformed(ActionEvent e) {
                StateExampleUsingAgendaGroup.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PricingRuleTemplateExample") {
            private static final long serialVersionUID = -6068257670386662680L;

            public void actionPerformed(ActionEvent e) {
                PricingRuleTemplateExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PricingRuleDTExample") {
            private static final long serialVersionUID = -624957573008325165L;

            public void actionPerformed(ActionEvent e) {
                PricingRuleDTExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("DataDrivenTemplateExample") {
            private static final long serialVersionUID = 7306790457258874155L;

            public void actionPerformed(ActionEvent e) {
                DataDrivenTemplateExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WorkItemConsequenceExample1") {
            private static final long serialVersionUID = 6726420738656439424L;

            public void actionPerformed(ActionEvent e) {
                WorkItemConsequenceExample1.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WorkItemConsequenceExample2") {
            private static final long serialVersionUID = 7798898015603969809L;

            public void actionPerformed(ActionEvent e) {
                WorkItemConsequenceExample2.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
