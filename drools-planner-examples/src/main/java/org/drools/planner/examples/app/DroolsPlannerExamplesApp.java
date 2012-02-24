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

package org.drools.planner.examples.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.drools.planner.examples.cloudbalancing.app.CloudBalancingApp;
import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.machinereassignment.app.MachineReassignmentApp;
import org.drools.planner.examples.nqueens.app.NQueensApp;
import org.drools.planner.examples.nurserostering.app.NurseRosteringApp;
import org.drools.planner.examples.pas.app.PatientAdmissionScheduleApp;
import org.drools.planner.examples.travelingtournament.app.smart.SmartTravelingTournamentApp;
import org.drools.planner.examples.examination.app.ExaminationApp;
import org.drools.planner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.drools.planner.examples.manners2009.app.Manners2009App;
import org.drools.planner.examples.tsp.app.TspApp;

public class DroolsPlannerExamplesApp extends JFrame {

    public static void main(String[] args) {
        DroolsPlannerExamplesApp droolsPlannerExamplesApp = new DroolsPlannerExamplesApp();
        droolsPlannerExamplesApp.pack();
        droolsPlannerExamplesApp.setLocationRelativeTo(null);
        droolsPlannerExamplesApp.setVisible(true);
    }
    
    private JTextArea descriptionTextArea;

    public DroolsPlannerExamplesApp() {
        super("Drools Planner examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Which example do you want to see?", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
        contentPane.add(titleLabel, BorderLayout.NORTH);
        JScrollPane examplesScrollPane = new JScrollPane(createExamplesPanel());
        examplesScrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        examplesScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        contentPane.add(examplesScrollPane, BorderLayout.CENTER);
        contentPane.add(createDescriptionPanel(), BorderLayout.SOUTH);
        return contentPane;
    }

    private JPanel createExamplesPanel() {
        JPanel examplesPanel = new JPanel();
        examplesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GroupLayout layout = new GroupLayout(examplesPanel);
        examplesPanel.setLayout(layout);
        JPanel toyExamplesPanel = createToyExamplesPanel();
        JPanel realExamplesPanel = createRealExamplesPanel();
        JPanel difficultExamplesPanel = createDifficultExamplesPanel();
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(toyExamplesPanel)
                .addGap(10)
                .addComponent(realExamplesPanel)
                .addGap(10)
                .addComponent(difficultExamplesPanel));
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(toyExamplesPanel)
                .addComponent(realExamplesPanel)
                .addComponent(difficultExamplesPanel));
        return examplesPanel;
    }

    private JPanel createToyExamplesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Toy examples");
        titledBorder.setTitleColor(TangoColors.CHAMELEON_3);
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(createExampleButton("N queens", "No 2 queens can attack each other.",
                "/org/drools/planner/examples/nqueens/swingui/queenImage.png",
                new Runnable() {
                    public void run() {
                        new NQueensApp().init(false);
                    }
                }));
        panel.add(createExampleButton("Cloud balancing", "Assign processes to servers.", null, new Runnable() {
            public void run() {
                new CloudBalancingApp().init(false);
            }
        }));
        panel.add(createExampleButton("Miss Manners 2009", "Assign guests to tables.", null, new Runnable() {
            public void run() {
                new Manners2009App().init(false);
            }
        }));
        panel.add(createExampleButton("Traveling salesman problem", "Find the shortest route to visit all cities.",
                null, new Runnable() {
            public void run() {
                new TspApp().init(false);
            }
        }));
        return panel;
    }

    private JPanel createRealExamplesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Real examples");
        titledBorder.setTitleColor(TangoColors.BUTTER_3);
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(createExampleButton("Curriculum course timetabling", "(ITC2007 track3)", null, new Runnable() {
            public void run() {
                new CurriculumCourseApp().init(false);
            }
        }));
        panel.add(createExampleButton("Machine reassignment", "(ROADEF 2012)", null, new Runnable() {
            public void run() {
                new MachineReassignmentApp().init(false);
            }
        }));
        panel.add(createExampleButton("Patient admission scheduling", "Hospital bed planning", null, new Runnable() {
            public void run() {
                new PatientAdmissionScheduleApp().init(false);
            }
        }));
        panel.add(createExampleButton("Nurse rostering", "(INRC2010)", null, new Runnable() {
            public void run() {
                new NurseRosteringApp().init(false);
            }
        }));
        return panel;
    }

    private JPanel createDifficultExamplesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Difficult examples");
        titledBorder.setTitleColor(TangoColors.SCARLET_3);
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(createExampleButton("Traveling tournament problem", "(TTP)", null, new Runnable() {
            public void run() {
                new SmartTravelingTournamentApp().init(false);
            }
        }));
        panel.add(createExampleButton("Examination timetabling", "(ITC2007 track1)", null, new Runnable() {
            public void run() {
                new ExaminationApp().init(false);
            }
        }));
        // TODO TrainDesign is still in working progress
//        contentPane.add(createExampleButton("Train design", "(RAS2011)", new Runnable() {
//            public void run() {
//                new TrainDesignApp().init(false);
//            }
//        }));
        return panel;
    }
    
    private JButton createExampleButton(final String title, final String description, String iconResource,
            final Runnable runnable) {
        ImageIcon icon = iconResource == null ? null : new ImageIcon(getClass().getResource(iconResource));
        JButton button = new JButton(new AbstractAction(title, icon) {
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.addMouseListener(new MouseAdapter() {
            
            public void mouseEntered(MouseEvent e) {
                descriptionTextArea.setText(description);
            }
            
            public void mouseExited(MouseEvent e) {
                descriptionTextArea.setText("");
            }
            
        });
        return button;
    }

    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description"), BorderLayout.NORTH);
        descriptionTextArea = new JTextArea(8, 80);
        descriptionTextArea.setEditable(false);
        descriptionPanel.add(new JScrollPane(descriptionTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        return descriptionPanel;
    }

}
