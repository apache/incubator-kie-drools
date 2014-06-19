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

package org.drools.benchmark;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drools.benchmark.manners.MannersBenchmark;
import org.drools.benchmark.waltz.WaltzBenchmark;
import org.drools.benchmark.waltzdb.WaltzDbBenchmark;

public class DroolsBenchmarkExamplesApp extends JFrame {

	private static final long serialVersionUID = -8611457695691657705L;

	public static void main(String[] args) {
        DroolsBenchmarkExamplesApp droolsBenchmarkExamplesApp = new DroolsBenchmarkExamplesApp();
        droolsBenchmarkExamplesApp.pack();
        droolsBenchmarkExamplesApp.setVisible(true);
    }

    public DroolsBenchmarkExamplesApp() {
        super("JBoss BRMS Benchmark examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which benchmark do you want to see in the output?"));
        contentPane.add(new JButton(new AbstractAction("MannersBenchmark") {
			private static final long serialVersionUID = 7519505832576886598L;

			public void actionPerformed(ActionEvent e) {
                MannersBenchmark.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WaltzBenchmark") {
			private static final long serialVersionUID = 805017998264074606L;

			public void actionPerformed(ActionEvent e) {
                WaltzBenchmark.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WaltzDbBenchmark") {
			private static final long serialVersionUID = -1870160785132166475L;

			public void actionPerformed(ActionEvent e) {
                WaltzDbBenchmark.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
