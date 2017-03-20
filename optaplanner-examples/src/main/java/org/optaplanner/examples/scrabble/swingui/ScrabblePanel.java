/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.swingui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.scrabble.domain.ScrabbleCell;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class ScrabblePanel extends SolutionPanel<ScrabbleSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/scrabble/swingui/scrabbleLogo.png";

    private final TimeTablePanel<Integer, Integer> gridPanel;

    public ScrabblePanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        gridPanel = new TimeTablePanel<>();
        tabbedPane.add("Grid", new JScrollPane(gridPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(ScrabbleSolution solution) {
        gridPanel.reset();
        defineGrid(solution);
        fillCells(solution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(ScrabbleSolution solution) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("99"));
        int footprintWidth = footprint.getPreferredSize().width;

        gridPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        for (int x = 0; x < solution.getGridWidth(); x++) {
            gridPanel.defineColumnHeader(x, footprintWidth);
        }

        gridPanel.defineRowHeaderByKey(HEADER_ROW);
        for (int y = 0; y < solution.getGridHeight(); y++) {
            gridPanel.defineRowHeader(y);
        }
        gridPanel.defineRowHeader(null); // Unassigned
    }

    private void fillCells(ScrabbleSolution solution) {
        fillXCells(solution);
        fillYCells(solution);
        fillTeamAssignmentCells(solution);
    }

    private void fillXCells(ScrabbleSolution solution) {
        for (int x = 0; x < solution.getGridWidth(); x++) {
            gridPanel.addColumnHeader(x, HEADER_ROW,
                    createTableHeader(new JLabel(Integer.toString(x), SwingConstants.CENTER)));
        }
    }

    private void fillYCells(ScrabbleSolution solution) {
        for (int y = 0; y < solution.getGridHeight(); y++) {
            gridPanel.addRowHeader(HEADER_COLUMN, y,
                    createTableHeader(new JLabel(Integer.toString(y))));
        }
        gridPanel.addRowHeader(HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
    }

    private void fillTeamAssignmentCells(ScrabbleSolution solution) {
        for (ScrabbleCell cell : solution.getCellList()) {
            for (Character c : cell.getCharacterSet()) {
                gridPanel.addCell(cell.getX(), cell.getY(), new JLabel(c.toString(), SwingConstants.CENTER));
            }
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

}
