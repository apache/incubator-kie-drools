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

package org.drools.tutorials.banking;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BankingExamplesApp extends JFrame {

	private static final long serialVersionUID = 4139198307676955555L;

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
			private static final long serialVersionUID = -5392823353007946183L;

			public void actionPerformed(ActionEvent e) {
                BankingExample1.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample2") {
			private static final long serialVersionUID = -808299533709717562L;

			public void actionPerformed(ActionEvent e) {
                BankingExample2.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample3") {
			private static final long serialVersionUID = 8664543708864863721L;

			public void actionPerformed(ActionEvent e) {
                BankingExample3.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample4") {
			private static final long serialVersionUID = -6338034649624286831L;

			public void actionPerformed(ActionEvent e) {
                BankingExample4.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample5") {
			private static final long serialVersionUID = 9073459862429160302L;

			public void actionPerformed(ActionEvent e) {
                BankingExample5.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("BankingExample6") {
			private static final long serialVersionUID = 6381220686035667623L;

			public void actionPerformed(ActionEvent e) {
                BankingExample6.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
