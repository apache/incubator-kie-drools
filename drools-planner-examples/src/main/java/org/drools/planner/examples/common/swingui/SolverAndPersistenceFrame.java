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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;

import org.drools.planner.examples.common.business.SolutionBusiness;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolverAndPersistenceFrame extends JFrame {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private SolutionBusiness solutionBusiness;

    private SolutionPanel solutionPanel;
    private ConstraintScoreMapDialog constraintScoreMapDialog;

    private List<Action> loadUnsolvedActionList;
    private List<Action> loadSolvedActionList;
    private Action terminateSolvingEarlyAction;
    private JCheckBox refreshScreenDuringSolvingCheckBox;
    private Action solveAction;
    private Action openAction;
    private Action saveAction;
    private Action importAction;

    private Action exportAction;
    private JProgressBar progressBar;
    private JLabel resultLabel;
    private ShowConstraintScoreMapDialogAction showConstraintScoreMapDialogAction;

    public SolverAndPersistenceFrame(SolutionBusiness solutionBusiness, SolutionPanel solutionPanel, String exampleName) {
        super(exampleName + " Drools Planner example");
        this.solutionBusiness = solutionBusiness;
        this.solutionPanel = solutionPanel;
        solutionPanel.setSolutionBusiness(solutionBusiness);
        solutionPanel.setSolverAndPersistenceFrame(this);
        registerListeners();
        constraintScoreMapDialog = new ConstraintScoreMapDialog(this);
        constraintScoreMapDialog.setSolutionBusiness(solutionBusiness);
    }

    private void registerListeners() {
        solutionBusiness.registerForBestSolutionChanges(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // This async, so it doesn't stop the solving immediately
                solutionBusiness.terminateSolvingEarly();
            }
        });
    }

    public void bestSolutionChanged() {
        Solution solution = solutionBusiness.getSolution();
        if (refreshScreenDuringSolvingCheckBox.isSelected()) {
            solutionPanel.updatePanel(solution);
            validate(); // TODO remove me?
        }
        resultLabel.setText("Latest best score: " + solution.getScore());
    }

    public void init() {
        setContentPane(createContentPane());
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createContentPane() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createButtonPanel(), BorderLayout.NORTH);
        if (solutionPanel.isWrapInScrollPane()) {
            JScrollPane solutionScrollPane = new JScrollPane(solutionPanel);
            panel.add(solutionScrollPane, BorderLayout.CENTER);
        } else {
            panel.add(solutionPanel, BorderLayout.CENTER);
        }
        panel.add(createScorePanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 0));
        panel.add(createLoadUnsolvedPanel());
        panel.add(createLoadSolvedPanel());
        panel.add(createProcessingPanel());
        return panel;
    }

    private JComponent createLoadUnsolvedPanel() {
        loadUnsolvedActionList = new ArrayList<Action>();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        for (File file : solutionBusiness.getUnsolvedFileList()) {
            Action loadUnsolvedAction = new LoadAction(file);
            loadUnsolvedActionList.add(loadUnsolvedAction);
            panel.add(new JButton(loadUnsolvedAction));
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        return scrollPane;
    }

    private JComponent createLoadSolvedPanel() {
        loadSolvedActionList = new ArrayList<Action>();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        for (File file : solutionBusiness.getSolvedFileList()) {
            Action loadSolvedAction = new LoadAction(file);
            loadSolvedActionList.add(loadSolvedAction);
            panel.add(new JButton(loadSolvedAction));
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        return scrollPane;
    }

    private class LoadAction extends AbstractAction {

        private File file;

        public LoadAction(File file) {
            super("Load " + file.getName());
            this.file = file;
        }

        public void actionPerformed(ActionEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                solutionBusiness.openSolution(file);
                setSolutionLoaded();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }

    }

    private JComponent createProcessingPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        solveAction = new SolveAction();
        solveAction.setEnabled(false);
        panel.add(new JButton(solveAction));
        terminateSolvingEarlyAction = new TerminateSolvingEarlyAction();
        terminateSolvingEarlyAction.setEnabled(false);
        panel.add(new JButton(terminateSolvingEarlyAction));
        refreshScreenDuringSolvingCheckBox = new JCheckBox("Refresh screen during solving",
                solutionPanel.isRefreshScreenDuringSolving());
        panel.add(refreshScreenDuringSolvingCheckBox);
        openAction = new OpenAction();
        openAction.setEnabled(true);
        panel.add(new JButton(openAction));
        saveAction = new SaveAction();
        saveAction.setEnabled(false);
        panel.add(new JButton(saveAction));
        importAction = new ImportAction();
        importAction.setEnabled(solutionBusiness.hasImporter());
        panel.add(new JButton(importAction));
        exportAction = new ExportAction();
        exportAction.setEnabled(false);
        panel.add(new JButton(exportAction));
        return panel;
    }

    private void setSolutionLoaded() {
        solveAction.setEnabled(true);
        saveAction.setEnabled(true);
        exportAction.setEnabled(solutionBusiness.hasExporter());
        resetScreen();
    }

    private void setSolvingState(boolean solving) {
        for (Action action : loadUnsolvedActionList) {
            action.setEnabled(!solving);
        }
        for (Action action : loadSolvedActionList) {
            action.setEnabled(!solving);
        }
        solveAction.setEnabled(!solving);
        terminateSolvingEarlyAction.setEnabled(solving);
        openAction.setEnabled(!solving);
        saveAction.setEnabled(!solving);
        importAction.setEnabled(!solving && solutionBusiness.hasImporter());
        exportAction.setEnabled(!solving && solutionBusiness.hasExporter());
        solutionPanel.setEnabled(!solving);
        progressBar.setIndeterminate(solving);
        progressBar.setStringPainted(solving);
        progressBar.setString(solving ? "Solving..." : null);
        showConstraintScoreMapDialogAction.setEnabled(!solving);
        solutionPanel.setSolvingState(solving);
    }

    private class SolveAction extends AbstractAction {

        // TODO This should be replaced with a java 6 SwingWorker once drools's hudson is on JDK 1.6
        private ExecutorService solvingExecutor = Executors.newFixedThreadPool(1);

        public SolveAction() {
            super("Solve");
        }

        public void actionPerformed(ActionEvent e) {
            setSolvingState(true);
            final Solution planningProblem = solutionBusiness.getSolution(); // In event thread
            // TODO This should be replaced with a java 6 SwingWorker once drools's hudson is on JDK 1.6
            solvingExecutor.submit(new Runnable() {
                public void run() {
                    Solution bestSolution;
                    try {
                        bestSolution = solutionBusiness.solve(planningProblem); // Not in event thread
                    } catch (final Throwable e) {
                        // Otherwise the newFixedThreadPool will eat the exception...
                        logger.error("Solving failed.", e);
                        bestSolution = null;
                    }
                    final Solution newSolution = bestSolution;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (newSolution != null) {
                                solutionBusiness.setSolution(newSolution); // In event thread
                            }
                            setSolvingState(false);
                            resetScreen();
                        }
                    });
                }
            });
        }

    }

    private class TerminateSolvingEarlyAction extends AbstractAction {

        public TerminateSolvingEarlyAction() {
            super("Terminate solving early");
        }

        public void actionPerformed(ActionEvent e) {
            // This async, so it doesn't stop the solving immediately
            solutionBusiness.terminateSolvingEarly();
        }

    }

    private class OpenAction extends AbstractAction {

        public OpenAction() {
            super("Open...");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(solutionBusiness.getUnsolvedDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".xml");
                }

                public String getDescription() {
                    return "Solver xml files";
                }
            });
            int approved = fileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.openSolution(fileChooser.getSelectedFile());
                    setSolutionLoaded();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class SaveAction extends AbstractAction {

        public SaveAction() {
            super("Save as...");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(solutionBusiness.getSolvedDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".xml");
                }

                public String getDescription() {
                    return "Solver xml files";
                }
            });
            int approved = fileChooser.showSaveDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.saveSolution(fileChooser.getSelectedFile());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class ImportAction extends AbstractAction {

        public ImportAction() {
            super("Import...");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(solutionBusiness.getImportDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() || solutionBusiness.acceptImportFile(file);
                }
                public String getDescription() {
                    return "Import files (*" + solutionBusiness.getImportFileSuffix() + ")";
                }
            });
            int approved = fileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.importSolution(fileChooser.getSelectedFile());
                    setSolutionLoaded();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class ExportAction extends AbstractAction {

        public ExportAction() {
            super("Export as...");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(solutionBusiness.getExportDataDir());
//            fileChooser.setFileFilter(new FileFilter() {
//                public boolean accept(File file) {
//                    return file.isDirectory() || file.getName().endsWith(".xml"); // TODO Not all export files are xml
//                }
//                public String getDescription() {
//                    return "Export files";
//                }
//            });
            int approved = fileChooser.showSaveDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.exportSolution(fileChooser.getSelectedFile());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        panel.add(progressBar, BorderLayout.WEST);
        resultLabel = new JLabel("No solution loaded yet");
        resultLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(resultLabel, BorderLayout.CENTER);
        showConstraintScoreMapDialogAction = new ShowConstraintScoreMapDialogAction();
        JButton constraintScoreMapButton = new JButton(showConstraintScoreMapDialogAction);
        panel.add(constraintScoreMapButton, BorderLayout.EAST);
        return panel;
    }

    private class ShowConstraintScoreMapDialogAction extends AbstractAction {

        public ShowConstraintScoreMapDialogAction() {
            super("Constraint scores");
        }

        public void actionPerformed(ActionEvent e) {
            constraintScoreMapDialog.resetContentPanel();
            constraintScoreMapDialog.setVisible(true);
        }

    }

    public void resetScreen() {
        solutionPanel.resetPanel(solutionBusiness.getSolution());
        validate();
        resultLabel.setText("Score: " + solutionBusiness.getScore());
    }

}
