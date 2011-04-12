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

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.drools.examples.broker.model.Company;

/**
 * A class that manages a company UI panel
 */
public class CompanyPanel extends JPanel {

    private static final int FIELD_COLUMN_SIZE = 8;

    private final Company model;

    private final JTextField currentField;
    private final JTextField previousField;

    private NumberFormat format = NumberFormat.getCurrencyInstance();

    public CompanyPanel(Company model) {
        this.model = model;
        GroupLayout formLayout = new GroupLayout(this);
        setLayout(formLayout);
        formLayout.setAutoCreateGaps(true);
        formLayout.setAutoCreateContainerGaps(true);

        JLabel companyNameField = new JLabel(model.getName());
        companyNameField.setFont(companyNameField.getFont().deriveFont(companyNameField.getFont().getSize() + 2.0F));

        JLabel currentLabel = new JLabel("Current: ");
        currentField = new JTextField(format.format( model.getCurrentPrice() ), FIELD_COLUMN_SIZE);
        currentField.setEditable(false);
        JLabel previousLabel = new JLabel("Previous:");
        previousField = new JTextField(format.format( model.getPreviousPrice() ), FIELD_COLUMN_SIZE);
        previousField.setEditable(false);

        formLayout.setHorizontalGroup(
                formLayout.createParallelGroup()
                        .addComponent(companyNameField)
                        .addGroup(formLayout.createSequentialGroup()
                                .addGap(10)
                                .addComponent(currentLabel)
                                .addGap(10)
                                .addComponent(currentField))
                        .addGroup(formLayout.createSequentialGroup()
                                .addGap(10)
                                .addComponent(previousLabel)
                                .addGap(10)
                                .addComponent(previousField))
        );
        formLayout.setVerticalGroup(
                formLayout.createSequentialGroup()
                        .addComponent(companyNameField)
                        .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addGap(10)
                                .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(currentLabel)
                                        .addComponent(previousLabel))
                                .addGap(10)
                                .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(currentField)
                                        .addComponent(previousField))
                        ));
    }

    public void updateCompany() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                currentField.setText( format.format( model.getCurrentPrice() ) );
                previousField.setText( format.format( model.getPreviousPrice() ) );
                if ( model.getCurrentPrice() > model.getPreviousPrice() ) {
                    currentField.setForeground( Color.BLUE );
                } else {
                    currentField.setForeground( Color.RED );
                }
            }
        } );
    }
}
