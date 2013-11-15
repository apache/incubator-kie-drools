/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.examples.tennis.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.tennis.domain.Day;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class TennisPanel extends SolutionPanel {

    private final TimeTablePanel<Day, Team> timeTablePanel;

    public TennisPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        timeTablePanel = new TimeTablePanel<Day, Team>();
        tabbedPane.add("Dates", new JScrollPane(timeTablePanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public void resetPanel(Solution solution) {
        timeTablePanel.reset();
        TennisSolution tennisSolution = (TennisSolution) solution;
        defineGrid(tennisSolution);
        fillCells(tennisSolution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(TennisSolution tennisSolution) {
        JButton footprint = new JButton("999999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;

        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN);
        for (Day day : tennisSolution.getDayList() ) {
            timeTablePanel.defineColumnHeader(day, footprintWidth);
        }
        timeTablePanel.defineColumnHeaderByKey(TRAILING_HEADER_COLUMN); // Assignment count

        timeTablePanel.defineRowHeaderByKey(HEADER_ROW);
        for (Team team : tennisSolution.getTeamList()) {
            timeTablePanel.defineRowHeader(team);
        }
        timeTablePanel.defineRowHeader(null); // Unassigned
    }

    private void fillCells(TennisSolution tennisSolution) {
        timeTablePanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Team")));
        fillDayCells(tennisSolution);
        fillTeamCells(tennisSolution);
        fillUnavailabilityPenaltyCells(tennisSolution);
        fillLectureCells(tennisSolution);
    }

    private void fillDayCells(TennisSolution tennisSolution) {
        for (Day day : tennisSolution.getDayList()) {
            timeTablePanel.addColumnHeader(day, HEADER_ROW,
                    createHeaderPanel(new JLabel(day.getLabel(), SwingConstants.CENTER)));
        }
        timeTablePanel.addCornerHeader(TRAILING_HEADER_COLUMN, HEADER_ROW,
                createHeaderPanel(new JLabel("Day count")));
    }

    private void fillTeamCells(TennisSolution tennisSolution) {
        Map<Team, Integer> dayCountPerTeamMap = extractDayCountPerTeamMap(tennisSolution);
        for (Team team : tennisSolution.getTeamList()) {
            timeTablePanel.addRowHeader(HEADER_COLUMN, team,
                    createHeaderPanel(new JLabel(team.getLabel())));
            timeTablePanel.addRowHeader(TRAILING_HEADER_COLUMN, team,
                    createHeaderPanel(new JLabel(dayCountPerTeamMap.get(team) + " days")));
        }
        timeTablePanel.addRowHeader(HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
    }

    private Map<Team, Integer> extractDayCountPerTeamMap(TennisSolution tennisSolution) {
        Map<Team, Integer> dayCountPerTeamMap = new HashMap<Team, Integer>(tennisSolution.getTeamList().size());
        for (Team team : tennisSolution.getTeamList()) {
            dayCountPerTeamMap.put(team, 0);
        }
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            Team team = teamAssignment.getTeam();
            if (team != null) {
                int count = dayCountPerTeamMap.get(team);
                count++;
                dayCountPerTeamMap.put(team, count);
            }
        }
        return dayCountPerTeamMap;
    }

    private void fillUnavailabilityPenaltyCells(TennisSolution tennisSolution) {
        for (UnavailabilityPenalty unavailabilityPenalty : tennisSolution.getUnavailabilityPenaltyList()) {
            JPanel unavailabilityPanel = new JPanel();
            unavailabilityPanel.setBackground(TangoColorFactory.ALUMINIUM_4);
            timeTablePanel.addCell(unavailabilityPenalty.getDay(), unavailabilityPenalty.getTeam(),
                    unavailabilityPanel);
        }
    }

    private void fillLectureCells(TennisSolution tennisSolution) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            Color examColor = tangoColorFactory.pickColor(teamAssignment);
            timeTablePanel.addCell(teamAssignment.getDay(), teamAssignment.getTeam(),
                    createButton(teamAssignment, examColor));
        }
    }

    private JPanel createHeaderPanel(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private JButton createButton(TeamAssignment teamAssignment, Color color) {
        JButton button = new JButton("Play");
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBackground(color);
        return button;
    }

}
