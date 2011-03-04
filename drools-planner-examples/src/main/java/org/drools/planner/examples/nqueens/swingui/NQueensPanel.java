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

package org.drools.planner.examples.nqueens.swingui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.drools.planner.core.move.Move;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nqueens.domain.Queen;
import org.drools.planner.examples.nqueens.solver.move.YChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class NQueensPanel extends SolutionPanel {

    private static final String QUEEN_IMAGE_PATH = "/org/drools/planner/examples/nqueens/swingui/queenImage.png";

    private ImageIcon queenImageIcon;

    public NQueensPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        queenImageIcon = new ImageIcon(getClass().getResource(QUEEN_IMAGE_PATH));
    }

    private NQueens getNQueens() {
        return (NQueens) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        NQueens nQueens = getNQueens();
        int n = nQueens.getN();
        List<Queen> queenList = nQueens.getQueenList();
        setLayout(new GridLayout(n, n));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Queen queen = queenList.get(j);
                if (queen.getY() == i) {
                    JButton button = new JButton(new QueenAction(queen));
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                    add(button);
                } else {
                    JPanel panel = new JPanel();
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.DARK_GRAY),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                    Color background = (((i + j) % 2) == 0) ? Color.WHITE : Color.GRAY;
                    panel.setBackground(background);
                    add(panel);
                }
            }
        }
    }

    private class QueenAction extends AbstractAction {

        private Queen queen;

        public QueenAction(Queen queen) {
            super("[" + queen.getId() + "]", queenImageIcon);
            this.queen = queen;
        }

        public void actionPerformed(ActionEvent e) {
            List<Integer> yList = getNQueens().createNList();
            JComboBox yListField = new JComboBox(yList.toArray());
            yListField.setSelectedItem(queen.getY());
            int result = JOptionPane.showConfirmDialog(NQueensPanel.this.getRootPane(), yListField, "Select y",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                int toY = (Integer) yListField.getSelectedItem();
                Move move = new YChangeMove(queen, toY);
                solutionBusiness.doMove(move);
                workflowFrame.updateScreen();
            }
        }

    }

}
