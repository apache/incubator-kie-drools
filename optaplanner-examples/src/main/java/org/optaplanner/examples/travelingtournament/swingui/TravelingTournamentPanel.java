/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.travelingtournament.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class TravelingTournamentPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/travelingtournament/swingui/travelingTournamentLogo.png";

    private final TimeTablePanel<Team, Day> teamsPanel;
    private TangoColorFactory tangoColorFactory;

    public TravelingTournamentPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        teamsPanel = new TimeTablePanel<Team, Day>();
        tabbedPane.add("Teams", new JScrollPane(teamsPanel));
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

    private TravelingTournament getTravelingTournament() {
        return (TravelingTournament) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        teamsPanel.reset();
        TravelingTournament travelingTournament = (TravelingTournament) solution;
        tangoColorFactory = new TangoColorFactory();
        defineGrid(travelingTournament);
        fillCells(travelingTournament);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(TravelingTournament travelingTournament) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("MMMMM"));
        int footprintWidth = footprint.getPreferredSize().width;

        teamsPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Day header
        for (Team team : travelingTournament.getTeamList()) {
            teamsPanel.defineColumnHeader(team, footprintWidth);
        }
        teamsPanel.defineColumnHeader(null, footprintWidth); // Unassigned

        teamsPanel.defineRowHeaderByKey(HEADER_ROW); // Team header
        for (Day day : travelingTournament.getDayList()) {
            teamsPanel.defineRowHeader(day);
        }
        teamsPanel.defineRowHeader(null); // Unassigned day
    }

    private void fillCells(TravelingTournament travelingTournament) {
        teamsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Day")));
        fillTeamCells(travelingTournament);
        fillDayCells(travelingTournament);
        fillMatchCells(travelingTournament);
    }

    private void fillTeamCells(TravelingTournament travelingTournament) {
        for (Team team : travelingTournament.getTeamList()) {
            JPanel teamPanel = createTableHeader(new JLabel(team.getLabel(), SwingConstants.CENTER));
            teamPanel.setBackground(tangoColorFactory.pickColor(team));
            teamsPanel.addColumnHeader(team, HEADER_ROW,
                    teamPanel);
        }
        teamsPanel.addColumnHeader(null, HEADER_ROW,
                createTableHeader(new JLabel("Unassigned", SwingConstants.CENTER)));
    }

    private void fillDayCells(TravelingTournament travelingTournament) {
        for (Day day : travelingTournament.getDayList()) {
            teamsPanel.addRowHeader(HEADER_COLUMN, day,
                    createTableHeader(new JLabel(day.getLabel())));
        }
        teamsPanel.addRowHeader(HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
    }

    private void fillMatchCells(TravelingTournament travelingTournament) {
        for (Match match : travelingTournament.getMatchList()) {
            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();
            teamsPanel.addCell(homeTeam, match.getDay(),
                    createButton(match, tangoColorFactory.pickColor(awayTeam), awayTeam.getLabel()));
            teamsPanel.addCell(awayTeam, match.getDay(),
                    createButton(match, tangoColorFactory.pickColor(homeTeam), homeTeam.getLabel()));
        }
    }

    private JPanel createTableHeader(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private JButton createButton(Match match, Color color, String label) {
        JButton button = SwingUtils.makeSmallButton(new JButton(new MatchAction(match, label)));
        button.setBackground(color);
        return button;
    }

    private class MatchAction extends AbstractAction {

        private Match match;

        public MatchAction(Match match, String label) {
            super(label);
            this.match = match;
        }

        public void actionPerformed(ActionEvent e) {
            // TODO this allows the user to put the TTP in an inconsistent state, from which the solver cannot start
            List<Day> dayList = getTravelingTournament().getDayList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox dayListField = new JComboBox(
                    dayList.toArray(new Object[dayList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(dayListField);
            dayListField.setSelectedItem(match.getDay());
            int result = JOptionPane.showConfirmDialog(TravelingTournamentPanel.this.getRootPane(), dayListField,
                    "Select day", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Day toDay = (Day) dayListField.getSelectedItem();
                solutionBusiness.doChangeMove(match, "day", toDay);
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
