/*
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.broker.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * A panel to log information
 * 
 */
public class LogPanel {

    private final JPanel    panel;
    private final JTextArea log;

    public LogPanel() {
        panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        log = new JTextArea();
        log.setEditable( false );
        
        JScrollPane areaScrollPane = new JScrollPane(log);
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add( areaScrollPane,
                   BorderLayout.CENTER );
    }

    public void log(final String text) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                log.append( text + "\n" );
            }
        } );
    }

    public JPanel getPanel() {
        return panel;
    }

}
