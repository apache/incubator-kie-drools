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

package org.optaplanner.examples.common.swingui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SolverAndPersistenceFrame<Solution_> extends JFrame {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public static final ImageIcon OPTA_PLANNER_ICON = new ImageIcon(
            SolverAndPersistenceFrame.class.getResource("optaPlannerIcon.png"));

    private final SolutionBusiness<Solution_> solutionBusiness;
    private final ImageIcon indictmentHeatMapTrueIcon;
    private final ImageIcon indictmentHeatMapFalseIcon;
    private final ImageIcon refreshScreenDuringSolvingTrueIcon;
    private final ImageIcon refreshScreenDuringSolvingFalseIcon;

    private SolutionPanel<Solution_> solutionPanel;
    private ConstraintMatchesDialog constraintMatchesDialog;

    private JList<QuickOpenAction> quickOpenUnsolvedJList;
    private JList<QuickOpenAction> quickOpenSolvedJList;
    private Action openAction;
    private Action saveAction;
    private Action importAction;
    private Action exportAction;
    private Action[] extraActions;
    private JToggleButton refreshScreenDuringSolvingToggleButton;
    private JToggleButton indictmentHeatMapToggleButton;
    private Action solveAction;
    private JButton solveButton;
    private Action terminateSolvingEarlyAction;
    private JButton terminateSolvingEarlyButton;
    private JPanel middlePanel;
    private JProgressBar progressBar;
    private JTextField scoreField;
    private ShowConstraintMatchesDialogAction showConstraintMatchesDialogAction;


    public SolverAndPersistenceFrame(SolutionBusiness<Solution_> solutionBusiness,
            SolutionPanel<Solution_> solutionPanel, CommonApp.ExtraAction<Solution_>[] extraActions) {
        super(solutionBusiness.getAppName() + " OptaPlanner example");
        this.solutionBusiness = solutionBusiness;
        this.solutionPanel = solutionPanel;
        setIconImage(OPTA_PLANNER_ICON.getImage());
        solutionPanel.setSolutionBusiness(solutionBusiness);
        solutionPanel.setSolverAndPersistenceFrame(this);
        this.extraActions = new Action[extraActions.length];
        for (int i = 0; i < extraActions.length; i++) {
            BiConsumer<SolutionBusiness<Solution_>, SolutionPanel<Solution_>> consumer
                    = extraActions[i].getConsumer();
            this.extraActions[i] = new AbstractAction(extraActions[i].getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    consumer.accept(SolverAndPersistenceFrame.this.solutionBusiness,
                            SolverAndPersistenceFrame.this.solutionPanel);
                }
            };
        }
        indictmentHeatMapTrueIcon = new ImageIcon(getClass().getResource("indictmentHeatMapTrueIcon.png"));
        indictmentHeatMapFalseIcon = new ImageIcon(getClass().getResource("indictmentHeatMapFalseIcon.png"));
        refreshScreenDuringSolvingTrueIcon = new ImageIcon(getClass().getResource("refreshScreenDuringSolvingTrueIcon.png"));
        refreshScreenDuringSolvingFalseIcon = new ImageIcon(getClass().getResource("refreshScreenDuringSolvingFalseIcon.png"));
        registerListeners();
        constraintMatchesDialog = new ConstraintMatchesDialog(this, solutionBusiness);
    }

    private void registerListeners() {
        solutionBusiness.registerForBestSolutionChanges(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // This async, so it doesn't stop the solving immediately
                solutionBusiness.terminateSolvingEarly();
            }
        });
    }

    public void bestSolutionChanged() {
        Solution_ solution = solutionBusiness.getSolution();
        Score score = solutionBusiness.getScore();
        if (refreshScreenDuringSolvingToggleButton.isSelected()) {
            solutionPanel.updatePanel(solution);
            validate(); // TODO remove me?
        }
        refreshScoreField(score);
    }

    public void init(Component centerForComponent) {
        setContentPane(createContentPane());
        pack();
        setLocationRelativeTo(centerForComponent);
    }

    private JComponent createContentPane() {
        JComponent quickOpenPanel = createQuickOpenPanel();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createToolBar(), BorderLayout.NORTH);
        mainPanel.add(createMiddlePanel(), BorderLayout.CENTER);
        mainPanel.add(createScorePanel(), BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quickOpenPanel, mainPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.2);
        return splitPane;
    }

    private JComponent createQuickOpenPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createQuickOpenUnsolvedPanel(), createQuickOpenSolvedPanel());
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JComponent createQuickOpenUnsolvedPanel() {
        quickOpenUnsolvedJList = new JList<>(new DefaultListModel<>());
        List<File> unsolvedFileList = solutionBusiness.getUnsolvedFileList();
        return createQuickOpenPanel(quickOpenUnsolvedJList, "Unsolved dataset shortcuts", unsolvedFileList);
    }

    private JComponent createQuickOpenSolvedPanel() {
        quickOpenSolvedJList = new JList<>(new DefaultListModel<>());
        List<File> solvedFileList = solutionBusiness.getSolvedFileList();
        return createQuickOpenPanel(quickOpenSolvedJList, "Solved dataset shortcuts", solvedFileList);
    }

    private JComponent createQuickOpenPanel(JList<QuickOpenAction> listPanel, String title, List<File> fileList) {
        listPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPanel.addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int selectedIndex = listPanel.getSelectedIndex();
            if (selectedIndex < 0) {
                return;
            }
            QuickOpenAction action = listPanel.getModel().getElementAt(selectedIndex);
            action.actionPerformed(new ActionEvent(listPanel, -1, null));
        });

        refreshQuickOpenPanel(listPanel, fileList);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setMinimumSize(new Dimension(100, 80));
        // Size fits into screen resolution 1024*768
        scrollPane.setPreferredSize(new Dimension(180, 200));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(scrollPane, BorderLayout.CENTER);
        return titlePanel;
    }

    private void refreshQuickOpenPanel(JList<QuickOpenAction> listPanel, List<File> fileList) {
        DefaultListModel<QuickOpenAction> model = (DefaultListModel<QuickOpenAction>) listPanel.getModel();
        model.clear();

        for (File file : fileList) {
            QuickOpenAction action = new QuickOpenAction(file);
            model.addElement(action);
            listPanel.clearSelection();
        }
    }

    private class QuickOpenAction extends AbstractAction {

        private File file;

        public QuickOpenAction(File file) {
            super(file.getName().replaceAll("\\.xml$", ""));
            this.file = file;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                solutionBusiness.openSolution(file);
                setSolutionLoaded(e.getSource());
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }

        @Override
        public String toString() {
            return getValue(Action.NAME).toString();
        }

    }

    private JComponent createToolBar() {
        // JToolBar looks ugly in Nimbus LookAndFeel
        JPanel toolBar = new JPanel();
        GroupLayout toolBarLayout = new GroupLayout(toolBar);
        toolBar.setLayout(toolBarLayout);

        JButton importButton;
        if (solutionBusiness.hasImporter()) {
            importAction = new ImportAction();
            importButton = new JButton(importAction);
        } else {
            importButton = null;
        }
        openAction = new OpenAction();
        openAction.setEnabled(true);
        JButton openButton = new JButton(openAction);
        saveAction = new SaveAction();
        saveAction.setEnabled(false);
        JButton saveButton = new JButton(saveAction);
        JButton exportButton;
        if (solutionBusiness.hasExporter()) {
            exportAction = new ExportAction();
            exportAction.setEnabled(false);
            exportButton = new JButton(exportAction);
        } else {
            exportButton = null;
        }
        JButton[] extraButtons = new JButton[extraActions.length];
        for (int i = 0; i < extraActions.length; i++) {
            extraButtons[i] = new JButton(extraActions[i]);
        }

        progressBar = new JProgressBar(0, 100);

        JPanel solvePanel = new JPanel(new CardLayout());
        solveAction = new SolveAction();
        solveAction.setEnabled(false);
        solveButton = new JButton(solveAction);
        terminateSolvingEarlyAction = new TerminateSolvingEarlyAction();
        terminateSolvingEarlyAction.setEnabled(false);
        terminateSolvingEarlyButton = new JButton(terminateSolvingEarlyAction);
        terminateSolvingEarlyButton.setVisible(false);
        solvePanel.add(solveButton, "solveAction");
        solvePanel.add(terminateSolvingEarlyButton, "terminateSolvingEarlyAction");
        solveButton.setMinimumSize(terminateSolvingEarlyButton.getMinimumSize());
        solveButton.setPreferredSize(terminateSolvingEarlyButton.getPreferredSize());

        GroupLayout.SequentialGroup horizontalGroup = toolBarLayout.createSequentialGroup();
        if (solutionBusiness.hasImporter()) {
            horizontalGroup.addComponent(importButton);
        }
        horizontalGroup.addComponent(openButton);
        horizontalGroup.addComponent(saveButton);
        if (solutionBusiness.hasExporter()) {
            horizontalGroup.addComponent(exportButton);
        }
        for (JButton extraButton : extraButtons) {
            horizontalGroup.addComponent(extraButton);
        }
        horizontalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        horizontalGroup.addComponent(solvePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
        horizontalGroup.addComponent(progressBar, 20, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
        toolBarLayout.setHorizontalGroup(horizontalGroup);
        GroupLayout.ParallelGroup verticalGroup = toolBarLayout.createParallelGroup(GroupLayout.Alignment.CENTER);
        if (solutionBusiness.hasImporter()) {
            verticalGroup.addComponent(importButton);
        }
        verticalGroup.addComponent(openButton);
        verticalGroup.addComponent(saveButton);
        if (solutionBusiness.hasExporter()) {
            verticalGroup.addComponent(exportButton);
        }
        for (JButton extraButton : extraButtons) {
            verticalGroup.addComponent(extraButton);
        }
        verticalGroup.addComponent(solvePanel);
        verticalGroup.addComponent(progressBar);
        toolBarLayout.setVerticalGroup(verticalGroup);
        return toolBar;
    }

    private class SolveAction extends AbstractAction {

        public SolveAction() {
            super("Solve", new ImageIcon(SolverAndPersistenceFrame.class.getResource("solveAction.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setSolvingState(true);
            Solution_ problem = solutionBusiness.getSolution();
            new SolveWorker(problem).execute();
        }

    }

    protected class SolveWorker extends SwingWorker<Solution_, Void> {

        protected final Solution_ problem;

        public SolveWorker(Solution_ problem) {
            this.problem = problem;
        }

        @Override
        protected Solution_ doInBackground() throws Exception {
            return solutionBusiness.solve(problem);
        }

        @Override
        protected void done() {
            try {
                Solution_ bestSolution = get();
                solutionBusiness.setSolution(bestSolution);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Solving was interrupted.", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Solving failed.", e.getCause());
            } finally {
                setSolvingState(false);
                resetScreen();
            }
        }

    }

    private class TerminateSolvingEarlyAction extends AbstractAction {

        public TerminateSolvingEarlyAction() {
            super("Terminate solving early",
                    new ImageIcon(SolverAndPersistenceFrame.class.getResource("terminateSolvingEarlyAction.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            terminateSolvingEarlyAction.setEnabled(false);
            progressBar.setString("Terminating...");
            // This async, so it doesn't stop the solving immediately
            solutionBusiness.terminateSolvingEarly();
        }

    }

    private class OpenAction extends AbstractAction {

        private static final String NAME = "Open...";
        private JFileChooser fileChooser;

        public OpenAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("openAction.png")));
            fileChooser = new JFileChooser(solutionBusiness.getSolvedDataDir());
            final String inputFileExtension = solutionBusiness.getSolutionFileIO().getOutputFileExtension();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith("." + inputFileExtension);
                }

                @Override
                public String getDescription() {
                    return "Solution files (*." + inputFileExtension + ")";
                }
            });
            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int approved = fileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.openSolution(fileChooser.getSelectedFile());
                    setSolutionLoaded(e.getSource());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class SaveAction extends AbstractAction {

        private static final String NAME = "Save as...";
        private JFileChooser fileChooser;

        public SaveAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("saveAction.png")));
            fileChooser = new JFileChooser(solutionBusiness.getSolvedDataDir());
            final String outputFileExtension = solutionBusiness.getSolutionFileIO().getOutputFileExtension();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith("." + outputFileExtension);
                }

                @Override
                public String getDescription() {
                    return "Solution files (*." + outputFileExtension + ")";
                }
            });
            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final String outputFileExtension = solutionBusiness.getSolutionFileIO().getOutputFileExtension();
            fileChooser.setSelectedFile(new File(solutionBusiness.getSolvedDataDir(),
                    FilenameUtils.getBaseName(solutionBusiness.getSolutionFileName()) + "." + outputFileExtension));
            int approved = fileChooser.showSaveDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.saveSolution(fileChooser.getSelectedFile());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
                refreshQuickOpenPanel(quickOpenUnsolvedJList, solutionBusiness.getUnsolvedFileList());
                refreshQuickOpenPanel(quickOpenSolvedJList, solutionBusiness.getSolvedFileList());
                SolverAndPersistenceFrame.this.validate();
            }
        }

    }

    private class ImportAction extends AbstractAction {

        private static final String NAME = "Import...";
        private JFileChooser fileChooser;

        public ImportAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("importAction.png")));
            if (!solutionBusiness.hasImporter()) {
                fileChooser = null;
                return;
            }
            fileChooser = new JFileChooser(solutionBusiness.getImportDataDir());
            boolean firstFilter = true;
            for (final AbstractSolutionImporter importer : solutionBusiness.getImporters()) {
                FileFilter filter;
                if (importer.isInputFileDirectory()) {
                    filter = new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isDirectory();
                        }

                        @Override
                        public String getDescription() {
                            return "Import directory";
                        }
                    };
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                } else {
                    filter = new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isDirectory() || importer.acceptInputFile(file);
                        }

                        @Override
                        public String getDescription() {
                            return "Import files (*." + importer.getInputFileSuffix() + ")";
                        }
                    };
                }
                fileChooser.addChoosableFileFilter(filter);
                if (firstFilter) {
                    fileChooser.setFileFilter(filter);
                    firstFilter = false;
                }
            }
            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int approved = fileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.importSolution(file);
                    setSolutionLoaded(e.getSource());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class ExportAction extends AbstractAction {

        private static final String NAME = "Export as...";
        private final JFileChooser fileChooser;

        public ExportAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("exportAction.png")));
            if (!solutionBusiness.hasExporter()) {
                fileChooser = null;
                return;
            }
            fileChooser = new JFileChooser(solutionBusiness.getExportDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith("." + solutionBusiness.getExportFileSuffix());
                }

                @Override
                public String getDescription() {
                    return "Export files (*." + solutionBusiness.getExportFileSuffix() + ")";
                }
            });
            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setSelectedFile(new File(solutionBusiness.getExportDataDir(),
                    FilenameUtils.getBaseName(solutionBusiness.getSolutionFileName())
                            + "." + solutionBusiness.getExportFileSuffix()
            ));
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

    private JPanel createMiddlePanel() {
        middlePanel = new JPanel(new CardLayout());
        JPanel usageExplanationPanel = new JPanel(new BorderLayout(5, 5));
        ImageIcon usageExplanationIcon = new ImageIcon(getClass().getResource(solutionPanel.getUsageExplanationPath()));
        JLabel usageExplanationLabel = new JLabel(usageExplanationIcon);
        // Allow splitPane divider to be moved to the right
        usageExplanationLabel.setMinimumSize(new Dimension(100, 100));
        usageExplanationPanel.add(usageExplanationLabel, BorderLayout.CENTER);
        JPanel descriptionPanel = new JPanel(new BorderLayout(2, 2));
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        descriptionPanel.add(new JLabel("Example description"), BorderLayout.NORTH);
        JTextArea descriptionTextArea = new JTextArea(8, 70);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setText(solutionBusiness.getAppDescription());
        descriptionPanel.add(new JScrollPane(descriptionTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        usageExplanationPanel.add(descriptionPanel, BorderLayout.SOUTH);
        middlePanel.add(usageExplanationPanel, "usageExplanationPanel");
        JComponent wrappedSolutionPanel;
        if (solutionPanel.isWrapInScrollPane()) {
            wrappedSolutionPanel = new JScrollPane(solutionPanel);
        } else {
            wrappedSolutionPanel = solutionPanel;
        }
        middlePanel.add(wrappedSolutionPanel, "solutionPanel");
        return middlePanel;
    }

    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout(5, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        showConstraintMatchesDialogAction = new ShowConstraintMatchesDialogAction();
        showConstraintMatchesDialogAction.setEnabled(false);
        buttonPanel.add(new JButton(showConstraintMatchesDialogAction));
        indictmentHeatMapToggleButton = new JToggleButton(
                solutionPanel.isUseIndictmentColor() ? indictmentHeatMapTrueIcon : indictmentHeatMapFalseIcon,
                solutionPanel.isUseIndictmentColor());
        indictmentHeatMapToggleButton.setEnabled(false);
        indictmentHeatMapToggleButton.setToolTipText("Show indictment heat map");
        indictmentHeatMapToggleButton.addActionListener(e -> {
            boolean selected = indictmentHeatMapToggleButton.isSelected();
            indictmentHeatMapToggleButton.setIcon(selected ?
                    indictmentHeatMapTrueIcon : indictmentHeatMapFalseIcon);
            solutionPanel.setUseIndictmentColor(selected);
            resetScreen();
        });
        buttonPanel.add(indictmentHeatMapToggleButton);
        scorePanel.add(buttonPanel, BorderLayout.WEST);
        scoreField = new JTextField("Score:");
        scoreField.setEditable(false);
        scoreField.setForeground(Color.BLACK);
        scoreField.setBorder(BorderFactory.createLoweredBevelBorder());
        scorePanel.add(scoreField, BorderLayout.CENTER);
        refreshScreenDuringSolvingToggleButton = new JToggleButton(refreshScreenDuringSolvingTrueIcon, true);
        refreshScreenDuringSolvingToggleButton.setToolTipText("Refresh screen during solving");
        refreshScreenDuringSolvingToggleButton.addActionListener(e -> {
            refreshScreenDuringSolvingToggleButton.setIcon(refreshScreenDuringSolvingToggleButton.isSelected() ?
                    refreshScreenDuringSolvingTrueIcon : refreshScreenDuringSolvingFalseIcon);
        });
        scorePanel.add(refreshScreenDuringSolvingToggleButton, BorderLayout.EAST);
        return scorePanel;
    }

    private class ShowConstraintMatchesDialogAction extends AbstractAction {

        public ShowConstraintMatchesDialogAction() {
            super("Constraint matches", new ImageIcon(SolverAndPersistenceFrame.class.getResource("showConstraintMatchesDialogAction.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            constraintMatchesDialog.resetContentPanel();
            constraintMatchesDialog.setVisible(true);
        }

    }

    public void setSolutionLoaded(Object eventSource) {
        if (eventSource != quickOpenUnsolvedJList) {
            quickOpenUnsolvedJList.clearSelection();
        }
        if (eventSource != quickOpenSolvedJList) {
            quickOpenSolvedJList.clearSelection();
        }
        setTitle(solutionBusiness.getAppName() + " - " + solutionBusiness.getSolutionFileName());
        ((CardLayout) middlePanel.getLayout()).show(middlePanel, "solutionPanel");
        setSolvingState(false);
        resetScreen();
    }

    private void setSolvingState(boolean solving) {
        quickOpenUnsolvedJList.setEnabled(!solving);
        quickOpenSolvedJList.setEnabled(!solving);
        if (solutionBusiness.hasImporter()) {
            importAction.setEnabled(!solving);
        }
        openAction.setEnabled(!solving);
        saveAction.setEnabled(!solving);
        if (solutionBusiness.hasExporter()) {
            exportAction.setEnabled(!solving);
        }
        solveAction.setEnabled(!solving);
        solveButton.setVisible(!solving);
        terminateSolvingEarlyAction.setEnabled(solving);
        terminateSolvingEarlyButton.setVisible(solving);
        if (solving) {
            terminateSolvingEarlyButton.requestFocus();
        } else {
            solveButton.requestFocus();
        }
        solutionPanel.setEnabled(!solving);
        progressBar.setIndeterminate(solving);
        progressBar.setStringPainted(solving);
        progressBar.setString(solving ? "Solving..." : null);
        indictmentHeatMapToggleButton.setEnabled(solutionPanel.isIndictmentHeatMapEnabled() && !solving);
        showConstraintMatchesDialogAction.setEnabled(!solving);
    }

    public void resetScreen() {
        Solution_ solution = solutionBusiness.getSolution();
        Score score = solutionBusiness.getScore();
        solutionPanel.resetPanel(solution);
        validate();
        refreshScoreField(score);
    }

    public void refreshScoreField(Score score) {
        scoreField.setForeground(determineScoreFieldForeground(score));
        scoreField.setText("Latest best score: " + score);
    }

    private Color determineScoreFieldForeground(Score<?> score) {
        if (!score.isSolutionInitialized()) {
            return TangoColorFactory.SCARLET_3;
        } else if (!(score instanceof FeasibilityScore)) {
            return Color.BLACK;
        } else {
            FeasibilityScore<?> feasibilityScore = (FeasibilityScore<?>) score;
            return feasibilityScore.isFeasible() ? TangoColorFactory.CHAMELEON_3 : TangoColorFactory.ORANGE_3;
        }
    }

}
