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

package org.optaplanner.examples.common.swingui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstraintMatchesDialog extends JDialog {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final SolutionBusiness solutionBusiness;

    public ConstraintMatchesDialog(SolverAndPersistenceFrame solverAndPersistenceFrame,
            SolutionBusiness solutionBusiness) {
        super(solverAndPersistenceFrame, "Constraint matches", true);
        this.solutionBusiness = solutionBusiness;
    }

    public void resetContentPanel() {
        if (!solutionBusiness.isConstraintMatchEnabled()) {
            setContentPane(new JLabel("Constraint matches are not supported with this ScoreDirector."));
        } else {
            final List<ConstraintMatchTotal> constraintMatchTotalList
                    = solutionBusiness.getConstraintMatchTotalList();
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            final JTable table = new JTable(new ConstraintMatchTotalTableModel(constraintMatchTotalList));
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(160);
            columnModel.getColumn(1).setPreferredWidth(300);
            columnModel.getColumn(2).setPreferredWidth(80);
            columnModel.getColumn(3).setPreferredWidth(80);
            columnModel.getColumn(4).setPreferredWidth(80);
            JScrollPane tableScrollPane = new JScrollPane(table);
            tableScrollPane.setPreferredSize(new Dimension(700, 300));
            splitPane.setTopComponent(tableScrollPane);
            final JTextArea detailTextArea = new JTextArea(10, 80);
            JScrollPane detailScrollPane = new JScrollPane(detailTextArea);
            splitPane.setBottomComponent(detailScrollPane);
            table.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent event) {
                            int selectedRow = table.getSelectedRow();
                            if (selectedRow < 0) {
                                detailTextArea.setText("");
                            } else {
                                ConstraintMatchTotal constraintMatchTotal
                                        = constraintMatchTotalList.get(selectedRow);
                                detailTextArea.setText(buildConstraintMatchSetText(constraintMatchTotal));
                                detailTextArea.setCaretPosition(0);
                            }
                        }
                    }
            );
            splitPane.setResizeWeight(1.0);
            setContentPane(splitPane);
        }
        pack();
        setLocationRelativeTo(getParent());
    }

    public String buildConstraintMatchSetText(ConstraintMatchTotal constraintMatchTotal) {
        Set<? extends ConstraintMatch> constraintMatchSet = constraintMatchTotal.getConstraintMatchSet();
        StringBuilder text = new StringBuilder(constraintMatchSet.size() * 80);
        for (ConstraintMatch constraintMatch : constraintMatchSet) {
            text.append(constraintMatch.toString()).append("\n");
        }
        return text.toString();
    }

    public static class ConstraintMatchTotalTableModel extends AbstractTableModel {

        private List<ConstraintMatchTotal> constraintMatchTotalList;

        public ConstraintMatchTotalTableModel(List<ConstraintMatchTotal> constraintMatchTotalList) {
            this.constraintMatchTotalList = constraintMatchTotalList;
        }

        public int getRowCount() {
            return constraintMatchTotalList.size();
        }

        public int getColumnCount() {
            return 5;
        }

        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Constraint package";
                case 1:
                    return "Constraint name";
                case 2:
                    return "Score level";
                case 3:
                    return "Match count";
                case 4:
                    return "Weight total";
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Number.class;
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            ConstraintMatchTotal constraintMatchTotal = constraintMatchTotalList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return constraintMatchTotal.getConstraintPackage();
                case 1:
                    return constraintMatchTotal.getConstraintName();
                case 2:
                    return constraintMatchTotal.getScoreLevel();
                case 3:
                    return constraintMatchTotal.getConstraintMatchSet().size();
                case 4:
                    return constraintMatchTotal.getWeightTotalAsNumber();
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }
    }

}
