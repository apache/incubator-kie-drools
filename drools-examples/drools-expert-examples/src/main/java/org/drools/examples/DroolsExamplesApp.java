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

import org.drools.examples.pacman.PacmanExample;
import org.drools.examples.sudoku.SudokuExample;
import org.drools.examples.troubleticket.TroubleTicketExample;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDSL;
import org.drools.examples.troubleticket.TroubleTicketExampleWithDT;

public class DroolsExamplesApp extends JFrame {

    public static void main(String[] args) {
        DroolsExamplesApp droolsExamplesApp = new DroolsExamplesApp();
        droolsExamplesApp.pack();
        droolsExamplesApp.setVisible(true);
    }

    public DroolsExamplesApp() {
        super("Drools examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which GUI example do you want to see?"));
        contentPane.add(new JButton(new AbstractAction("SudokuExample") {
            public void actionPerformed(ActionEvent e) {
                SudokuExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("PacmanExample") {
            public void actionPerformed(ActionEvent e) {
                PacmanExample.main(new String[0]);
            }
        }));
        contentPane.add(new JLabel("Which output example do you want to see?"));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExample") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExample.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDT") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDT.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("TroubleTicketExampleWithDSL") {
            public void actionPerformed(ActionEvent e) {
                TroubleTicketExampleWithDSL.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
