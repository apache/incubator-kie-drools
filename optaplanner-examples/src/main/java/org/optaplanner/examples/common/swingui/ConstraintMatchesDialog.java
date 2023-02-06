package org.optaplanner.examples.common.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.examples.common.business.SolutionBusiness;

final class ConstraintMatchesDialog extends JDialog {

    private final SolutionBusiness solutionBusiness;

    public ConstraintMatchesDialog(SolverAndPersistenceFrame solverAndPersistenceFrame,
            SolutionBusiness solutionBusiness) {
        super(solverAndPersistenceFrame, "Constraint matches", true);
        this.solutionBusiness = solutionBusiness;
    }

    public void resetContentPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        Action okAction = new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        buttonPanel.add(new JButton(okAction));
        if (!solutionBusiness.isConstraintMatchEnabled()) {
            JPanel unsupportedPanel = new JPanel(new BorderLayout());
            JLabel unsupportedLabel = new JLabel("Constraint matches are not supported with this ScoreDirector.");
            unsupportedPanel.add(unsupportedLabel, BorderLayout.CENTER);
            unsupportedPanel.add(buttonPanel, BorderLayout.SOUTH);
            setContentPane(unsupportedPanel);
        } else {
            final List<ConstraintMatchTotal<?>> constraintMatchTotalList = solutionBusiness.getConstraintMatchTotalList();
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            final JTable table = new JTable(new ConstraintMatchTotalTableModel(constraintMatchTotalList));
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(300);
            columnModel.getColumn(1).setPreferredWidth(80);
            columnModel.getColumn(2).setPreferredWidth(80);
            columnModel.getColumn(3).setPreferredWidth(80);
            DefaultTableCellRenderer rightCellRenderer = new DefaultTableCellRenderer();
            rightCellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            columnModel.getColumn(1).setCellRenderer(rightCellRenderer);
            columnModel.getColumn(3).setCellRenderer(rightCellRenderer);
            JScrollPane tableScrollPane = new JScrollPane(table);
            tableScrollPane.setPreferredSize(new Dimension(700, 300));
            splitPane.setTopComponent(tableScrollPane);
            JPanel bottomPanel = new JPanel(new BorderLayout());
            JLabel detailLabel = new JLabel("Constraint matches of selected constraint type");
            detailLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            bottomPanel.add(detailLabel, BorderLayout.NORTH);
            final JTextArea detailTextArea = new JTextArea(10, 80);
            JScrollPane detailScrollPane = new JScrollPane(detailTextArea);
            bottomPanel.add(detailScrollPane, BorderLayout.CENTER);
            table.getSelectionModel().addListSelectionListener(
                    event -> {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow < 0) {
                            detailTextArea.setText("");
                        } else {
                            ConstraintMatchTotal<?> constraintMatchTotal = constraintMatchTotalList.get(selectedRow);
                            detailTextArea.setText(buildConstraintMatchSetText(constraintMatchTotal));
                            detailTextArea.setCaretPosition(0);
                        }
                    });
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
            splitPane.setBottomComponent(bottomPanel);
            splitPane.setResizeWeight(1.0);
            setContentPane(splitPane);
        }
        pack();
        setLocationRelativeTo(getParent());
    }

    public String buildConstraintMatchSetText(ConstraintMatchTotal<?> constraintMatchTotal) {
        Set<? extends ConstraintMatch<?>> constraintMatchSet = constraintMatchTotal.getConstraintMatchSet();
        StringBuilder text = new StringBuilder(constraintMatchSet.size() * 80);
        for (ConstraintMatch<?> constraintMatch : constraintMatchSet) {
            text.append(constraintMatch.getIndictedObjectList().toString())
                    .append(" = ")
                    .append(constraintMatch.getScore().toShortString()).append("\n");
        }
        return text.toString();
    }

    public static class ConstraintMatchTotalTableModel extends AbstractTableModel {

        private final List<ConstraintMatchTotal<?>> constraintMatchTotalList;

        public ConstraintMatchTotalTableModel(List<ConstraintMatchTotal<?>> constraintMatchTotalList) {
            this.constraintMatchTotalList = constraintMatchTotalList;
        }

        @Override
        public int getRowCount() {
            return constraintMatchTotalList.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Constraint name";
                case 1:
                    return "Constraint weight";
                case 2:
                    return "Match count";
                case 3:
                    return "Score";
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
                case 3:
                    return String.class;
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ConstraintMatchTotal<?> constraintMatchTotal = constraintMatchTotalList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return constraintMatchTotal.getConstraintName();
                case 1:
                    Score<?> constraintWeight = constraintMatchTotal.getConstraintWeight();
                    return constraintWeight == null ? "N/A" : constraintWeight.toShortString();
                case 2:
                    return constraintMatchTotal.getConstraintMatchCount();
                case 3:
                    return constraintMatchTotal.getScore().toShortString();
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }
    }

}
