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

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drools.planner.examples.cloudbalancing.app.CloudBalancingApp;
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
        droolsPlannerExamplesApp.setVisible(true);
    }

    public DroolsPlannerExamplesApp() {
        super("Drools Planner examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which example do you want to see?"));
        contentPane.add(new JButton(new AbstractAction("N queens") {
            public void actionPerformed(ActionEvent e) {
                new NQueensApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Miss Manners 2009") {
            public void actionPerformed(ActionEvent e) {
                new Manners2009App().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Traveling salesman problem (TSP)") {
            public void actionPerformed(ActionEvent e) {
                new TspApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Traveling tournament problem (TTP)") {
            public void actionPerformed(ActionEvent e) {
                new SmartTravelingTournamentApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Curriculum course timetabling (ITC2007 track3)") {
            public void actionPerformed(ActionEvent e) {
                new CurriculumCourseApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Examination timetabling (ITC2007 track1)") {
            public void actionPerformed(ActionEvent e) {
                new ExaminationApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Patient admission schedule (hospital bed planning)") {
            public void actionPerformed(ActionEvent e) {
                new PatientAdmissionScheduleApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Nurse rostering (INRC2010)") {
            public void actionPerformed(ActionEvent e) {
                new NurseRosteringApp().init(false);
            }
        }));
        contentPane.add(new JButton(new AbstractAction("Cloud balancing") {
            public void actionPerformed(ActionEvent e) {
                new CloudBalancingApp().init(false);
            }
        }));
        return contentPane;
    }

}
