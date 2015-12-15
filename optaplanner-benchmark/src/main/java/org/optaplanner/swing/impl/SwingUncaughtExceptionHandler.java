/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.swing.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

// TODO move to optaplanner-swingwb, the Swing version of optaplanner-wb (which doesn't exist yet either)
public class SwingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void register() {
        SwingUncaughtExceptionHandler exceptionHandler = new SwingUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        System.setProperty("sun.awt.exception.handler", SwingUncaughtExceptionHandler.class.getName());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Not logger.error() because it needs to show up red (and linked) in the IDE console
        System.err.append("Exception in thread \"").append(t.getName()).append("\" ");
        e.printStackTrace();
        displayException(t, e);
    }

    private void displayException(Thread t, Throwable e) {
        final JFrame exceptionFrame = new JFrame("Uncaught exception: " + e.getMessage());
        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        BufferedImage errorImage = new BufferedImage(
                errorIcon.getIconWidth(), errorIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        errorIcon.paintIcon(null, errorImage.getGraphics(), 0, 0);
        exceptionFrame.setIconImage(errorImage);
        exceptionFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(new JLabel("An uncaught exception has occurred: "), BorderLayout.NORTH);
        JTextArea stackTraceTextArea = new JTextArea(30, 80);
        stackTraceTextArea.setEditable(false);
        stackTraceTextArea.append("Exception in thread \"" + t.getName() + "\" " + e.getClass().getName()
                + ": " + e.getMessage() + "\n");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            stackTraceTextArea.append("    at " + stackTraceElement.toString() + "\n");
        }
        Throwable parentException = e;
        Throwable cause = e.getCause();
        while (cause != null && cause != parentException) {
            stackTraceTextArea.append("Caused by: " + "\" " + cause.getClass().getName()
                    + ": " + cause.getMessage() + "\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                stackTraceTextArea.append("    at " + stackTraceElement.toString() + "\n");
            }
            parentException = cause;
            cause = cause.getCause();
        }
        JScrollPane stackTraceScrollPane = new JScrollPane(stackTraceTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(stackTraceScrollPane, BorderLayout.CENTER);
        stackTraceTextArea.setCaretPosition(0); // Scroll to top
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        JButton closeButton = new JButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                exceptionFrame.setVisible(false);
                exceptionFrame.dispose();
            }
        });
        buttonPanel.add(closeButton);
        JButton exitApplicationButton = new JButton(new AbstractAction("Exit application") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        buttonPanel.add(exitApplicationButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        exceptionFrame.setContentPane(contentPanel);
        exceptionFrame.pack();
        exceptionFrame.setLocationRelativeTo(null);
        exceptionFrame.setVisible(true);
    }

}
