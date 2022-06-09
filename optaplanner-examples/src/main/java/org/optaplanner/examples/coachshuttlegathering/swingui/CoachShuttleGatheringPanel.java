package org.optaplanner.examples.coachshuttlegathering.swingui;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CoachShuttleGatheringPanel extends SolutionPanel<CoachShuttleGatheringSolution> {

    public static final String LOGO_PATH =
            "/org/optaplanner/examples/coachshuttlegathering/swingui/coachShuttleGatheringLogo.png";

    private CoachShuttleGatheringWorldPanel coachShuttleGatheringWorldPanel;

    public CoachShuttleGatheringPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        coachShuttleGatheringWorldPanel = new CoachShuttleGatheringWorldPanel(this);
        coachShuttleGatheringWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", coachShuttleGatheringWorldPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(CoachShuttleGatheringSolution solution) {
        coachShuttleGatheringWorldPanel.resetPanel(solution);
    }

    @Override
    public void updatePanel(CoachShuttleGatheringSolution solution) {
        coachShuttleGatheringWorldPanel.updatePanel(solution);
    }

}
