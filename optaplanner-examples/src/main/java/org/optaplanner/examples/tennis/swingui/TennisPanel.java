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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final TimeTablePanel<Day, Team> datesPanel;
    private final TimeTablePanel<Team, Team> confrontationsPanel;

    public TennisPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        datesPanel = new TimeTablePanel<Day, Team>();
        tabbedPane.add("Dates", new JScrollPane(datesPanel));
        confrontationsPanel = new TimeTablePanel<Team, Team>();
        tabbedPane.add("Confrontations", new JScrollPane(confrontationsPanel));
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
        datesPanel.reset();
        confrontationsPanel.reset();
        TennisSolution tennisSolution = (TennisSolution) solution;
        defineGrid(tennisSolution);
        fillCells(tennisSolution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(TennisSolution tennisSolution) {
        JButton footprint = new JButton("999999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;

        datesPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        for (Day day : tennisSolution.getDayList() ) {
            datesPanel.defineColumnHeader(day, footprintWidth);
        }
        datesPanel.defineColumnHeaderByKey(TRAILING_HEADER_COLUMN); // Assignment count

        datesPanel.defineRowHeaderByKey(HEADER_ROW);
        for (Team team : tennisSolution.getTeamList()) {
            datesPanel.defineRowHeader(team);
        }
        datesPanel.defineRowHeader(null); // Unassigned

        confrontationsPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        for (Team team : tennisSolution.getTeamList()) {
            confrontationsPanel.defineColumnHeader(team);
        }
        confrontationsPanel.defineRowHeaderByKey(HEADER_ROW);
        for (Team team : tennisSolution.getTeamList()) {
            confrontationsPanel.defineRowHeader(team);
        }
    }

    private void fillCells(TennisSolution tennisSolution) {
        datesPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Team")));
        fillDayCells(tennisSolution);
        fillTeamCells(tennisSolution);
        fillUnavailabilityPenaltyCells(tennisSolution);
        fillLectureCells(tennisSolution);
        fillConfrontationCells(tennisSolution);
    }

    private void fillDayCells(TennisSolution tennisSolution) {
        for (Day day : tennisSolution.getDayList()) {
            datesPanel.addColumnHeader(day, HEADER_ROW,
                    createHeaderPanel(new JLabel(day.getLabel(), SwingConstants.CENTER)));
        }
        datesPanel.addCornerHeader(TRAILING_HEADER_COLUMN, HEADER_ROW,
                createHeaderPanel(new JLabel("Day count")));
    }

    private void fillTeamCells(TennisSolution tennisSolution) {
        Map<Team, Integer> teamToDayCountMap = extractTeamToDayCountMap(tennisSolution);
        for (Team team : tennisSolution.getTeamList()) {
            datesPanel.addRowHeader(HEADER_COLUMN, team,
                    createHeaderPanel(new JLabel(team.getLabel())));
            datesPanel.addRowHeader(TRAILING_HEADER_COLUMN, team,
                    createHeaderPanel(new JLabel(teamToDayCountMap.get(team) + " days")));
            confrontationsPanel.addColumnHeader(team, HEADER_ROW,
                    createHeaderPanel(new JLabel(team.getLabel())));
            confrontationsPanel.addRowHeader(HEADER_COLUMN, team,
                    createHeaderPanel(new JLabel(team.getLabel())));
        }
        datesPanel.addRowHeader(HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
    }

    private Map<Team, Integer> extractTeamToDayCountMap(TennisSolution tennisSolution) {
        Map<Team, Integer> teamToDayCountMap = new HashMap<Team, Integer>(tennisSolution.getTeamList().size());
        for (Team team : tennisSolution.getTeamList()) {
            teamToDayCountMap.put(team, 0);
        }
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            Team team = teamAssignment.getTeam();
            if (team != null) {
                int count = teamToDayCountMap.get(team);
                count++;
                teamToDayCountMap.put(team, count);
            }
        }
        return teamToDayCountMap;
    }

    private void fillUnavailabilityPenaltyCells(TennisSolution tennisSolution) {
        for (UnavailabilityPenalty unavailabilityPenalty : tennisSolution.getUnavailabilityPenaltyList()) {
            JPanel unavailabilityPanel = new JPanel();
            unavailabilityPanel.setBackground(TangoColorFactory.ALUMINIUM_4);
            datesPanel.addCell(unavailabilityPenalty.getDay(), unavailabilityPenalty.getTeam(),
                    unavailabilityPanel);
        }
    }

    private void fillLectureCells(TennisSolution tennisSolution) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            Team team = teamAssignment.getTeam();
            Color teamColor = team == null ? TangoColorFactory.SCARLET_1 : tangoColorFactory.pickColor(team);
            datesPanel.addCell(teamAssignment.getDay(), team,
                    createButton(teamAssignment, teamColor));
        }
    }

    private void fillConfrontationCells(TennisSolution tennisSolution) {
        List<Team> teamList = tennisSolution.getTeamList();
        List<Day> dayList = tennisSolution.getDayList();
        Map<Day, List<TeamAssignment>> dayToTeamAssignmentListMap = new HashMap<Day, List<TeamAssignment>>(
                dayList.size());
        for (Day day : dayList) {
            dayToTeamAssignmentListMap.put(day, new ArrayList<TeamAssignment>());
        }
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            dayToTeamAssignmentListMap.get(teamAssignment.getDay()).add(teamAssignment);
        }
        // TODO replace by better collection of guava
        Map<List<Team>, Integer> teamPairToConfrontationCountMap = new HashMap<List<Team>, Integer>();
        for (Team left : teamList) {
            for (Team right : teamList) {
                if (left != right) {
                    List<Team> teamPair = new ArrayList<Team>(2);
                    teamPair.add(left);
                    teamPair.add(right);
                    teamPairToConfrontationCountMap.put(teamPair, 0);
                }
            }
        }
        for (List<TeamAssignment> teamAssignmentSubList : dayToTeamAssignmentListMap.values()) {
            for (TeamAssignment left : teamAssignmentSubList) {
                for (TeamAssignment right : teamAssignmentSubList) {
                    if (left.getTeam() != right.getTeam()) {
                        List<Team> teamPair = new ArrayList<Team>(2);
                        teamPair.add(left.getTeam());
                        teamPair.add(right.getTeam());
                        int confrontationCount = teamPairToConfrontationCountMap.get(teamPair);
                        confrontationCount++;
                        teamPairToConfrontationCountMap.put(teamPair, confrontationCount);
                    }
                }
            }
        }
        for (Map.Entry<List<Team>, Integer> teamPairToConfrontationCount : teamPairToConfrontationCountMap.entrySet()) {
            List<Team> teamPair = teamPairToConfrontationCount.getKey();
            int confrontationCount = teamPairToConfrontationCount.getValue();
            confrontationsPanel.addCell(teamPair.get(0), teamPair.get(1),
                    createHeaderPanel(new JLabel(Integer.toString(confrontationCount))));
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
