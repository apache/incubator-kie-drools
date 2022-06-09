package org.optaplanner.examples.tsp.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TspListPanel extends JPanel implements Scrollable {

    public static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 600);

    private static final Color HEADER_COLOR = TangoColorFactory.BUTTER_1;

    private final TspPanel tspPanel;

    public TspListPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        setLayout(new GridLayout(0, 1));
    }

    public void resetPanel(TspSolution tspSolution) {
        removeAll();
        if (tspSolution.getVisitList().size() > 1000) {
            JLabel tooBigLabel = new JLabel("The dataset is too big to show.");
            add(tooBigLabel);
            return;
        }
        Domicile domicile = tspSolution.getDomicile();
        add(new JLabel(domicile.getLocation().toString()));
        // TODO If the model contains the nextVisit like in vehicle routing, use that instead
        Map<Standstill, Visit> nextVisitMap = new LinkedHashMap<>();
        List<Visit> unassignedVisitList = new ArrayList<>();
        for (Visit visit : tspSolution.getVisitList()) {
            if (visit.getPreviousStandstill() != null) {
                nextVisitMap.put(visit.getPreviousStandstill(), visit);
            } else {
                unassignedVisitList.add(visit);
            }
        }
        Visit lastVisit = null;
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            addVisitButton(tspSolution, visit);
            lastVisit = visit;
        }
        if (lastVisit != null) {
            JPanel backToDomicilePanel = new JPanel(new GridLayout(1, 2));
            backToDomicilePanel.add(new JLabel("Back to " + domicile.getLocation()));
            JLabel distanceLabel = new JLabel(
                    lastVisit.getDistanceTo(domicile) + " " + tspSolution.getDistanceUnitOfMeasurement());
            distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            backToDomicilePanel.add(distanceLabel);
            add(backToDomicilePanel);
        }
        add(new JLabel("Unassigned"));
        for (Visit visit : unassignedVisitList) {
            addVisitButton(tspSolution, visit);
        }
    }

    protected void addVisitButton(TspSolution tspSolution, Visit visit) {
        JPanel visitPanel = new JPanel(new GridLayout(1, 2));
        JButton button = new JButton(new VisitAction(visit));
        visitPanel.add(button);
        String distanceLabelString;
        if (visit.getPreviousStandstill() == null) {
            distanceLabelString = "Unassigned";
        } else {
            distanceLabelString = visit.getDistanceFromPreviousStandstill() + " " + tspSolution.getDistanceUnitOfMeasurement();
        }
        JLabel distanceLabel = new JLabel(distanceLabelString);
        distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        visitPanel.add(distanceLabel);
        add(visitPanel);
    }

    public void updatePanel(TspSolution tspSolution) {
        resetPanel(tspSolution);
    }

    private class VisitAction extends AbstractAction {

        private Visit visit;

        public VisitAction(Visit visit) {
            super(visit.getLocation().toString());
            this.visit = visit;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TspSolution tspSolution = tspPanel.getSolution();
            JComboBox previousStandstillListField = new JComboBox();
            for (Standstill previousStandstill : tspSolution.getVisitList()) {
                previousStandstillListField.addItem(previousStandstill);
            }
            previousStandstillListField.addItem(tspSolution.getDomicile());
            previousStandstillListField.setSelectedItem(visit.getPreviousStandstill());
            int result = JOptionPane.showConfirmDialog(TspListPanel.this.getRootPane(), previousStandstillListField,
                    "Visit " + visit.getLocation() + " after", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Standstill toStandstill = (Standstill) previousStandstillListField.getSelectedItem();
                tspPanel.changePreviousStandstill(visit, toStandstill);
                tspPanel.getWorkflowFrame().resetScreen();
            }
        }

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (getParent().getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (getParent().getHeight() > getPreferredSize().height);
        }
        return false;
    }

}
