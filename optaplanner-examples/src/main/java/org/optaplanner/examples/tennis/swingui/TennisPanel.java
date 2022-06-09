package org.optaplanner.examples.tennis.swingui;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.TRAILING_HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.CommonIcons;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.tennis.domain.Day;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TennisPanel extends SolutionPanel<TennisSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/tennis/swingui/tennisLogo.png";

    private final TimeTablePanel<Day, Team> datesPanel;
    private final TimeTablePanel<Team, Team> confrontationsPanel;

    public TennisPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        datesPanel = new TimeTablePanel<>();
        tabbedPane.add("Dates", new JScrollPane(datesPanel));
        confrontationsPanel = new TimeTablePanel<>();
        tabbedPane.add("Confrontations", new JScrollPane(confrontationsPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(TennisSolution tennisSolution) {
        datesPanel.reset();
        confrontationsPanel.reset();
        defineGrid(tennisSolution);
        fillCells(tennisSolution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(TennisSolution tennisSolution) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("999999"));
        int footprintWidth = footprint.getPreferredSize().width;

        datesPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        for (Day day : tennisSolution.getDayList()) {
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
        datesPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Team")));
        fillDayCells(tennisSolution);
        fillTeamCells(tennisSolution);
        fillUnavailabilityPenaltyCells(tennisSolution);
        fillTeamAssignmentCells(tennisSolution);
        fillConfrontationCells(tennisSolution);
    }

    private void fillDayCells(TennisSolution tennisSolution) {
        for (Day day : tennisSolution.getDayList()) {
            datesPanel.addColumnHeader(day, HEADER_ROW,
                    createTableHeader(new JLabel(day.getLabel(), SwingConstants.CENTER)));
        }
        datesPanel.addCornerHeader(TRAILING_HEADER_COLUMN, HEADER_ROW,
                createTableHeader(new JLabel("Day count")));
    }

    private void fillTeamCells(TennisSolution tennisSolution) {
        Map<Team, Integer> teamToDayCountMap = extractTeamToDayCountMap(tennisSolution);
        for (Team team : tennisSolution.getTeamList()) {
            datesPanel.addRowHeader(HEADER_COLUMN, team,
                    createTableHeader(new JLabel(team.getLabel())));
            datesPanel.addRowHeader(TRAILING_HEADER_COLUMN, team,
                    createTableHeader(new JLabel(teamToDayCountMap.get(team) + " days")));
            confrontationsPanel.addColumnHeader(team, HEADER_ROW,
                    createTableHeader(new JLabel(team.getLabel())));
            confrontationsPanel.addRowHeader(HEADER_COLUMN, team,
                    createTableHeader(new JLabel(team.getLabel())));
        }
        datesPanel.addRowHeader(HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
    }

    private Map<Team, Integer> extractTeamToDayCountMap(TennisSolution tennisSolution) {
        Map<Team, Integer> teamToDayCountMap = new HashMap<>(tennisSolution.getTeamList().size());
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

    private void fillTeamAssignmentCells(TennisSolution tennisSolution) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (Team team : tennisSolution.getTeamList()) {
            tangoColorFactory.pickColor(team);
        }
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
        Map<Day, List<TeamAssignment>> dayToTeamAssignmentListMap = new HashMap<>(
                dayList.size());
        for (Day day : dayList) {
            dayToTeamAssignmentListMap.put(day, new ArrayList<>());
        }
        for (TeamAssignment teamAssignment : tennisSolution.getTeamAssignmentList()) {
            dayToTeamAssignmentListMap.get(teamAssignment.getDay()).add(teamAssignment);
        }
        Map<List<Team>, Integer> teamPairToConfrontationCountMap = new HashMap<>();
        for (Team left : teamList) {
            for (Team right : teamList) {
                if (left != right) {
                    List<Team> teamPair = Arrays.asList(left, right);
                    teamPairToConfrontationCountMap.put(teamPair, 0);
                }
            }
        }
        for (List<TeamAssignment> teamAssignmentSubList : dayToTeamAssignmentListMap.values()) {
            for (TeamAssignment left : teamAssignmentSubList) {
                if (left.getTeam() != null) {
                    for (TeamAssignment right : teamAssignmentSubList) {
                        if (right.getTeam() != null && left.getTeam() != right.getTeam()) {
                            List<Team> teamPair = Arrays.asList(left.getTeam(), right.getTeam());
                            int confrontationCount = teamPairToConfrontationCountMap.get(teamPair);
                            confrontationCount++;
                            teamPairToConfrontationCountMap.put(teamPair, confrontationCount);
                        }
                    }
                }
            }
        }
        for (Map.Entry<List<Team>, Integer> teamPairToConfrontationCount : teamPairToConfrontationCountMap.entrySet()) {
            List<Team> teamPair = teamPairToConfrontationCount.getKey();
            int confrontationCount = teamPairToConfrontationCount.getValue();
            confrontationsPanel.addCell(teamPair.get(0), teamPair.get(1),
                    createTableHeader(new JLabel(Integer.toString(confrontationCount))));
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

    private JButton createButton(TeamAssignment teamAssignment, Color color) {
        JButton button = SwingUtils.makeSmallButton(new JButton(new TeamAssignmentAction(teamAssignment)));
        button.setBackground(color);
        if (teamAssignment.isPinned()) {
            button.setIcon(CommonIcons.PINNED_ICON);
        }
        return button;
    }

    private class TeamAssignmentAction extends AbstractAction {

        private TeamAssignment teamAssignment;

        public TeamAssignmentAction(TeamAssignment teamAssignment) {
            super("Play");
            this.teamAssignment = teamAssignment;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 2));
            listFieldsPanel.add(new JLabel("Team:"));
            List<Team> teamList = getSolution().getTeamList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox teamListField = new JComboBox(
                    teamList.toArray(new Object[teamList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(teamListField);
            teamListField.setSelectedItem(teamAssignment.getTeam());
            listFieldsPanel.add(teamListField);
            listFieldsPanel.add(new JLabel("Pinned:"));
            JCheckBox pinnedField = new JCheckBox("cannot move during solving");
            pinnedField.setSelected(teamAssignment.isPinned());
            listFieldsPanel.add(pinnedField);
            int result = JOptionPane.showConfirmDialog(TennisPanel.this.getRootPane(), listFieldsPanel,
                    "Select team", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Team toTeam = (Team) teamListField.getSelectedItem();
                if (teamAssignment.getTeam() != toTeam) {
                    doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector
                            .changeVariable(teamAssignment, "team", ta -> ta.setTeam(toTeam)));
                }
                boolean toPinned = pinnedField.isSelected();
                if (teamAssignment.isPinned() != toPinned) {
                    doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector
                            .changeProblemProperty(teamAssignment, ta -> ta.setPinned(toPinned)));
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
