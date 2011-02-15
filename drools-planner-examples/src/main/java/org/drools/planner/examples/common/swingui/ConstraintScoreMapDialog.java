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

package org.drools.planner.examples.common.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.drools.planner.examples.common.business.ScoreDetail;
import org.drools.planner.examples.common.business.SolutionBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstraintScoreMapDialog extends JDialog {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected WorkflowFrame workflowFrame;
    protected SolutionBusiness solutionBusiness;

    public ConstraintScoreMapDialog(WorkflowFrame workflowFrame) {
        super(workflowFrame, "Constraint scores", true);
        this.workflowFrame = workflowFrame;
    }

    public void setSolutionBusiness(SolutionBusiness solutionBusiness) {
        this.solutionBusiness = solutionBusiness;
    }

    public void resetContentPanel() {
        final List<ScoreDetail> scoreDetailList = solutionBusiness.getScoreDetailList();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        final JTable table = new JTable(new ScoreDetailTableModel(scoreDetailList));
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
                            ScoreDetail scoreDetail = scoreDetailList.get(selectedRow);
                            detailTextArea.setText(scoreDetail.buildConstraintOccurrenceListText());
                        }
                    }
                }
        );
        splitPane.setResizeWeight(1.0);
        setContentPane(splitPane);
        pack();
        setLocationRelativeTo(getParent());
    }

    public static class ScoreDetailTableModel extends AbstractTableModel {

        private List<ScoreDetail> scoreDetailList;

        public ScoreDetailTableModel(List<ScoreDetail> scoreDetailList) {
            this.scoreDetailList = scoreDetailList;
        }

        public int getRowCount() {
            return scoreDetailList.size();
        }

        public int getColumnCount() {
            return 4;
        }

        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Rule id";
                case 1:
                    return "Constraint type";
                case 2:
                    return "# occurrences";
                case 3:
                    return "Score total";
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Enum.class;
                case 2:
                    return Integer.class;
                case 3:
                    return Double.class;
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            ScoreDetail scoreDetail = scoreDetailList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return scoreDetail.getRuleId();
                case 1:
                    return scoreDetail.getConstraintType();
                case 2:
                    return scoreDetail.getOccurrenceSize();
                case 3:
                    return scoreDetail.getScoreTotal();
                default:
                    throw new IllegalStateException("The columnIndex (" + columnIndex + ") is invalid.");
            }
        }
    }

    private static class ShowButtonTableRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return (JButton) value;
        }

    }

}
