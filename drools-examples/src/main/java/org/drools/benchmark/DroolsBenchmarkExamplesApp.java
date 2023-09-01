package org.drools.benchmark;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import org.drools.benchmark.waltz.WaltzBenchmark;
import org.drools.benchmark.waltzdb.WaltzDbBenchmark;

public class DroolsBenchmarkExamplesApp extends JFrame {

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
        contentPane.add(new JButton(new AbstractAction("WaltzBenchmark") {
            public void actionPerformed(ActionEvent e) {
                WaltzBenchmark.main(new String[0]);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("WaltzDbBenchmark") {
            public void actionPerformed(ActionEvent e) {
                WaltzDbBenchmark.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
