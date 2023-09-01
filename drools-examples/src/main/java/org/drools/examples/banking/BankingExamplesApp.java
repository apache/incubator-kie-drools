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
