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

package org.drools.examples.conway;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.drools.examples.conway.ui.ConwayGUI;

public class AbstractRunConway {
    public static final int AGENDAGROUP = 0;
    public static final int RULEFLOWGROUP = 1;
    
    public static void start(final int executionControl) {
        final ConwayGUI gui = new ConwayGUI(executionControl);
        final String appTitle = ConwayApplicationProperties.getProperty( "app.title" );
        final JFrame f = new JFrame( appTitle );
        f.setResizable( false );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getContentPane().add( BorderLayout.CENTER,
                                gui );

        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                gui.dispose();
            }
        } );
        f.pack();
        f.setVisible( true );
    }
}
