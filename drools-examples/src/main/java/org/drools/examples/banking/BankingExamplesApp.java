/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.examples.banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BankingExamplesApp extends JFrame {

    public static void main(String[] args) {
        BankingExamplesApp bankingExamplesApp = new BankingExamplesApp();
        bankingExamplesApp.pack();
        bankingExamplesApp.setVisible(true);
    }

    public BankingExamplesApp() {
        super("JBoss BRMS Banking tutorial");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which tutorial do you want to see in the output?"));
        contentPane.add(new JButton(new AbstractAction("BankingExample1") {
            public void actionPerformed(ActionEvent e) {
                BankingExample1.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample2") {
            public void actionPerformed(ActionEvent e) {
                BankingExample2.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample3") {
            public void actionPerformed(ActionEvent e) {
                BankingExample3.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample4") {
            public void actionPerformed(ActionEvent e) {
                BankingExample4.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample5") {
            public void actionPerformed(ActionEvent e) {
                BankingExample5.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample6") {
            public void actionPerformed(ActionEvent e) {
                BankingExample6.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
