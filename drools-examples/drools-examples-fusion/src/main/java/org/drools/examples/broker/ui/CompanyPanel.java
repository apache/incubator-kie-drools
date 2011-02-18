/**
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

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.drools.examples.broker.model.Company;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A class that manages a company UI panel
 * 
 * @author etirelli
 */
public class CompanyPanel {

    private final Company model;

    private final JLabel  current;
    private final JLabel  previous;
    private final JPanel  panel;

    private NumberFormat  format = NumberFormat.getCurrencyInstance();

    public CompanyPanel(Company model) {
        this.model = model;
        FormLayout layout = new FormLayout( "right:pref:grow, 6dlu, right:pref:grow", // columns
                                            "pref, 3dlu, pref, 3dlu, pref" ); // rows

        PanelBuilder builder = new PanelBuilder( layout );
        CellConstraints cc = new CellConstraints();
        builder.addSeparator( model.getName(),
                              cc.xyw( 1,
                                      1,
                                      3 ) );
        builder.addLabel( "Current :",
                          cc.xy( 1,
                                 3 ) );
        current = builder.addLabel( format.format( model.getCurrentPrice() ),
                                    cc.xy( 3,
                                           3 ) );
        builder.addLabel( "Previous :",
                          cc.xy( 1,
                                 5 ) );
        previous = builder.addLabel( format.format( model.getPreviousPrice() ),
                                     cc.xy( 3,
                                            5 ) );
        this.panel = builder.getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void updatePanel() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                current.setText( format.format( model.getCurrentPrice() ) );
                previous.setText( format.format( model.getPreviousPrice() ) );
                if ( model.getCurrentPrice() > model.getPreviousPrice() ) {
                    current.setForeground( Color.BLUE );
                } else {
                    current.setForeground( Color.RED );
                }
            }
        } );
    }
}
