/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.aggregator.swingui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.aggregator.swingui.MixedCheckBox.MixedCheckBoxStatus;
import org.optaplanner.benchmark.impl.result.BenchmarkResultIO;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.swing.impl.SwingUncaughtExceptionHandler;
import org.optaplanner.swing.impl.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkAggregatorFrame extends JFrame {

    /**
     * Reads an XML benchmark configuration from the classpath
     * and uses that {@link PlannerBenchmarkConfig} to do an aggregation.
     * @param benchmarkConfigResource never null, same one as in {@link PlannerBenchmarkFactory#createFromXmlResource(String)}
     */
    public static void createAndDisplayFromXmlResource(String benchmarkConfigResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource);
        createAndDisplay(benchmarkConfig);
    }

    /**
     * Reads an Freemarker template from the classpath that generates an XML benchmark configuration
     * and uses that {@link PlannerBenchmarkConfig} to do an aggregation.
     * @param templateResource never null, same one as in {@link PlannerBenchmarkFactory#createFromFreemarkerXmlResource(String)}
     */
    public static void createAndDisplayFromFreemarkerXmlResource(String templateResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource);
        createAndDisplay(benchmarkConfig);
    }

    /**
     * Uses a {@link PlannerBenchmarkConfig} to do an aggregation.
     * @param benchmarkConfig never null
     */
    public static void createAndDisplay(PlannerBenchmarkConfig benchmarkConfig) {
        SwingUncaughtExceptionHandler.register();
        SwingUtils.fixateLookAndFeel();
        BenchmarkAggregator benchmarkAggregator = new BenchmarkAggregator();
        benchmarkAggregator.setBenchmarkDirectory(benchmarkConfig.getBenchmarkDirectory());
        BenchmarkReportConfig benchmarkReportConfig = benchmarkConfig.getBenchmarkReportConfig();
        if (benchmarkReportConfig != null) {
            // Defensive copy
            benchmarkReportConfig = new BenchmarkReportConfig(benchmarkReportConfig);
        } else {
            benchmarkReportConfig = new BenchmarkReportConfig();
        }
        benchmarkAggregator.setBenchmarkReportConfig(benchmarkReportConfig);

        BenchmarkAggregatorFrame benchmarkAggregatorFrame = new BenchmarkAggregatorFrame(benchmarkAggregator);
        benchmarkAggregatorFrame.init();
        benchmarkAggregatorFrame.setVisible(true);
    }

    /**
     * @param benchmarkFactory never null
     * @deprecated in favor of {@link #createAndDisplayFromXmlResource(String)}
     * or {@link #createAndDisplay(PlannerBenchmarkConfig)}.
     */
    @Deprecated
    public static void createAndDisplay(PlannerBenchmarkFactory benchmarkFactory) {
        createAndDisplay(benchmarkFactory.getPlannerBenchmarkConfig());
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final BenchmarkAggregator benchmarkAggregator;
    private final BenchmarkResultIO benchmarkResultIO;
    private final MillisecondsSpentNumberFormat millisecondsSpentNumberFormat;

    private List<PlannerBenchmarkResult> plannerBenchmarkResultList;
    private Map<SingleBenchmarkResult, DefaultMutableTreeNode> resultCheckBoxMapping = new LinkedHashMap<>();
    private Map<SolverBenchmarkResult, String> solverBenchmarkResultNameMapping = new HashMap<>();

    private CheckBoxTree checkBoxTree;
    private JTextArea detailTextArea;
    private JProgressBar generateProgressBar;
    private JButton generateReportButton;
    private JButton renameNodeButton;

    private boolean exitApplicationWhenReportFinished = true;

    public BenchmarkAggregatorFrame(BenchmarkAggregator benchmarkAggregator) {
        super("Benchmark aggregator");
        this.benchmarkAggregator = benchmarkAggregator;
        benchmarkResultIO = new BenchmarkResultIO();
        plannerBenchmarkResultList = Collections.emptyList();
        Locale locale = benchmarkAggregator.getBenchmarkReportConfig().determineLocale();
        millisecondsSpentNumberFormat = new MillisecondsSpentNumberFormat(locale);
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initPlannerBenchmarkResultList();
        setContentPane(createContentPane());
        pack();
        setLocationRelativeTo(null);
    }

    private JComponent createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(createTopButtonPanel(), BorderLayout.NORTH);
        contentPane.add(createBenchmarkTreePanel(), BorderLayout.CENTER);
        contentPane.add(createDetailTextArea(), BorderLayout.SOUTH);
        return contentPane;
    }

    private JComponent createNoPlannerFoundTextField() {
        String infoMessage = "No planner benchmarks have been found in the benchmarkDirectory ("
                + benchmarkAggregator.getBenchmarkDirectory() + ").";
        JTextPane textPane = new JTextPane();

        textPane.setEditable(false);
        textPane.setText(infoMessage);

        // center info message
        StyledDocument styledDocument = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(center, true);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(),
                center, false);
        return textPane;
    }

    private JComponent createDetailTextArea() {
        JPanel detailPanel = new JPanel(new BorderLayout());
        JLabel detailLabel = new JLabel("Details");
        detailPanel.add(detailLabel, BorderLayout.NORTH);
        detailTextArea = new JTextArea(5, 80);
        detailTextArea.setEditable(false);
        JScrollPane detailScrollPane = new JScrollPane(detailTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        detailPanel.add(detailScrollPane, BorderLayout.SOUTH);
        return detailPanel;
    }

    private JComponent createTopButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
        buttonPanel.add(new JButton(new ExpandNodesAction()));
        buttonPanel.add(new JButton(new CollapseNodesAction()));
        buttonPanel.add(new JButton(new MoveNodeAction(true)));
        buttonPanel.add(new JButton(new MoveNodeAction(false)));
        renameNodeButton = new JButton(new RenameNodeAction());
        renameNodeButton.setEnabled(false);
        buttonPanel.add(renameNodeButton);
        buttonPanel.add(new JButton(new SwitchLevelsAction(false)));
        return buttonPanel;
    }

    private JComponent createBenchmarkTreePanel() {
        JPanel benchmarkTreePanel = new JPanel(new BorderLayout());
        benchmarkTreePanel.add(new JScrollPane(plannerBenchmarkResultList.isEmpty() ? createNoPlannerFoundTextField() : createCheckBoxTree(),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        JPanel buttonPanelWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        generateReportButton = new JButton(new GenerateReportAction(this));
        generateReportButton.setEnabled(false);
        buttonPanel.add(generateReportButton);
        generateProgressBar = new JProgressBar();
        buttonPanel.add(generateProgressBar);
        buttonPanelWrapper.add(buttonPanel);
        benchmarkTreePanel.add(buttonPanelWrapper, BorderLayout.SOUTH);
        benchmarkTreePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return benchmarkTreePanel;
    }

    private CheckBoxTree createCheckBoxTree() {
        final CheckBoxTree resultCheckBoxTree = new CheckBoxTree(initBenchmarkHierarchy(true));
        resultCheckBoxTree.addTreeSelectionListener(e -> {
            TreePath treeSelectionPath = e.getNewLeadSelectionPath();
            if (treeSelectionPath != null) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeSelectionPath.getLastPathComponent();
                MixedCheckBox checkBox = (MixedCheckBox) treeNode.getUserObject();
                detailTextArea.setText(checkBox.getDetail());
                detailTextArea.setCaretPosition(0);
                renameNodeButton.setEnabled(checkBox.getBenchmarkResult() instanceof PlannerBenchmarkResult
                        || checkBox.getBenchmarkResult() instanceof SolverBenchmarkResult);
            }
        });
        resultCheckBoxTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Enable button if checked singleBenchmarkResults exist
                generateReportButton.setEnabled(!resultCheckBoxTree.getSelectedSingleBenchmarkNodes().isEmpty());
            }
        });
        checkBoxTree = resultCheckBoxTree;
        return resultCheckBoxTree;
    }

    private void initPlannerBenchmarkResultList() {
        SolverConfigContext configContext = new SolverConfigContext();
        plannerBenchmarkResultList = benchmarkResultIO.readPlannerBenchmarkResultList(configContext,
                benchmarkAggregator.getBenchmarkDirectory());
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            plannerBenchmarkResult.accumulateResults(
                    benchmarkAggregator.getBenchmarkReportConfig().buildBenchmarkReport(plannerBenchmarkResult));
        }
    }

    private class GenerateReportAction extends AbstractAction {

        private final BenchmarkAggregatorFrame parentFrame;

        public GenerateReportAction(BenchmarkAggregatorFrame parentFrame) {
            super("Generate report");
            this.parentFrame = parentFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            parentFrame.setEnabled(false);
            generateReport();
        }

        private void generateReport() {
            List<SingleBenchmarkResult> singleBenchmarkResultList = new ArrayList<>();
            for (Map.Entry<SingleBenchmarkResult, DefaultMutableTreeNode> entry : resultCheckBoxMapping.entrySet()) {
                if (((MixedCheckBox) entry.getValue().getUserObject()).getStatus() == MixedCheckBoxStatus.CHECKED) {
                    singleBenchmarkResultList.add(entry.getKey());
                }
            }
            if (singleBenchmarkResultList.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No single benchmarks have been selected.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                parentFrame.setEnabled(true);
            } else {
                generateProgressBar.setIndeterminate(true);
                generateProgressBar.setStringPainted(true);
                generateProgressBar.setString("Generating...");
                GenerateReportWorker worker = new GenerateReportWorker(parentFrame, singleBenchmarkResultList);
                worker.execute();
            }
        }
    }

    private class ExpandNodesAction extends AbstractAction {

        public ExpandNodesAction() {
            super("Expand", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("expand.png")));
            setEnabled(!plannerBenchmarkResultList.isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkBoxTree.expandNodes();
        }
    }

    private class CollapseNodesAction extends AbstractAction {

        public CollapseNodesAction() {
            super("Collapse", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("collapse.png")));
            setEnabled(!plannerBenchmarkResultList.isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkBoxTree.collapseNodes();
        }
    }

    private class MoveNodeAction extends AbstractAction {

        private boolean directionUp;

        public MoveNodeAction(boolean directionUp) {
            super(directionUp ? "Move up" : "Move down", new ImageIcon(BenchmarkAggregatorFrame.class.getResource(
                    directionUp ? "moveUp.png" : "moveDown.png")));
            this.directionUp = directionUp;
            setEnabled(!plannerBenchmarkResultList.isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkBoxTree.getSelectionPath() != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) checkBoxTree.getSelectionPath().getLastPathComponent();
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                if (parentNode != null) {
                    DefaultMutableTreeNode immediateSiblingNode = directionUp ? (DefaultMutableTreeNode) parentNode.getChildBefore(selectedNode)
                            : (DefaultMutableTreeNode) parentNode.getChildAfter(selectedNode);
                    if (immediateSiblingNode != null) {
                        parentNode.insert(immediateSiblingNode, parentNode.getIndex(selectedNode));
                        ((DefaultTreeModel) checkBoxTree.getModel()).nodeStructureChanged(parentNode);
                        checkBoxTree.setSelectionPath(new TreePath(selectedNode.getPath()));
                    }
                }
            }
        }
    }

    private class RenameNodeAction extends AbstractAction {

        public RenameNodeAction() {
            super("Rename", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("rename.png")));
            setEnabled(!plannerBenchmarkResultList.isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkBoxTree.getSelectionPath() != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) checkBoxTree.getSelectionPath().getLastPathComponent();
                MixedCheckBox mixedCheckBox = (MixedCheckBox) selectedNode.getUserObject();
                if (mixedCheckBox.getBenchmarkResult() instanceof PlannerBenchmarkResult
                        || mixedCheckBox.getBenchmarkResult() instanceof SolverBenchmarkResult) {
                    RenameNodeDialog renameNodeDialog = new RenameNodeDialog(selectedNode);
                    renameNodeDialog.pack();
                    renameNodeDialog.setLocationRelativeTo(BenchmarkAggregatorFrame.this);
                    renameNodeDialog.setVisible(true);
                }
            }
        }
    }

    private class SwitchLevelsAction extends AbstractAction {

        private boolean solverLevelFirst;

        public SwitchLevelsAction(boolean solverLevelFirst) {
            super("Switch levels", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("switchTree.png")));
            this.solverLevelFirst = solverLevelFirst;
            setEnabled(!plannerBenchmarkResultList.isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode treeRoot = initBenchmarkHierarchy(solverLevelFirst);
            DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
            checkBoxTree.setModel(treeModel);
            treeModel.nodeStructureChanged(treeRoot);
            solverLevelFirst = !solverLevelFirst;
            checkBoxTree.setSelectedSingleBenchmarkNodes(new HashSet<>());
            for (Map.Entry<SingleBenchmarkResult, DefaultMutableTreeNode> entry : resultCheckBoxMapping.entrySet()) {
                if (((MixedCheckBox) entry.getValue().getUserObject()).getStatus() == MixedCheckBoxStatus.CHECKED) {
                    checkBoxTree.getSelectedSingleBenchmarkNodes().add(entry.getValue());
                }
            }
            checkBoxTree.updateHierarchyCheckBoxStates();
        }
    }

    private class RenameNodeDialog extends JDialog {

        public RenameNodeDialog(final DefaultMutableTreeNode treeNode) {
            super(BenchmarkAggregatorFrame.this, "Rename node");
            final MixedCheckBox mixedCheckBox = (MixedCheckBox) treeNode.getUserObject();
            final Object benchmarkResult = mixedCheckBox.getBenchmarkResult();

            JPanel mainPanel = new JPanel(new BorderLayout());
            String benchmarkResultTextFieldText = null;
            if (benchmarkResult instanceof SolverBenchmarkResult) {
                benchmarkResultTextFieldText = solverBenchmarkResultNameMapping.get(benchmarkResult);
            }
            final JTextField benchmarkResultNameTextField = new JTextField(benchmarkResultTextFieldText == null ? benchmarkResult.toString()
                    : benchmarkResultTextFieldText, 30);
            mainPanel.add(benchmarkResultNameTextField, BorderLayout.WEST);
            AbstractAction renamedAction = new AbstractAction("Rename") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newBenchmarkResultName = benchmarkResultNameTextField.getText();
                    if (StringUtils.isEmpty(newBenchmarkResultName)) {
                        JOptionPane.showMessageDialog(BenchmarkAggregatorFrame.this,
                                "New benchmark's name cannot be empty.",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (benchmarkResult instanceof PlannerBenchmarkResult) {
                            ((PlannerBenchmarkResult) benchmarkResult).setName(newBenchmarkResultName);
                            mixedCheckBox.setText(newBenchmarkResultName);
                            ((DefaultTreeModel) checkBoxTree.getModel()).nodeChanged(treeNode);
                        } else if (benchmarkResult instanceof SolverBenchmarkResult) {
                            mixedCheckBox.setText(newBenchmarkResultName + " (" + ((SolverBenchmarkResult) benchmarkResult).getRanking() + ")");
                            ((DefaultTreeModel) checkBoxTree.getModel()).nodeChanged(treeNode);
                            solverBenchmarkResultNameMapping.put((SolverBenchmarkResult) benchmarkResult, newBenchmarkResultName);
                        }
                        dispose();
                    }
                }
            };
            benchmarkResultNameTextField.addActionListener(renamedAction);
            JButton confirmRenameButton = new JButton(renamedAction);
            mainPanel.add(confirmRenameButton, BorderLayout.EAST);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setContentPane(mainPanel);
        }
    }

    private DefaultMutableTreeNode initBenchmarkHierarchy(boolean solverFirst) {
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new MixedCheckBox("Planner benchmarks"));
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            DefaultMutableTreeNode plannerNode = new DefaultMutableTreeNode(createPlannerBenchmarkCheckBox(plannerBenchmarkResult));
            parentNode.add(plannerNode);
            if (solverFirst) {
                for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                    DefaultMutableTreeNode solverNode = new DefaultMutableTreeNode(createSolverBenchmarkCheckBox(solverBenchmarkResult));
                    plannerNode.add(solverNode);
                    for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
                        DefaultMutableTreeNode problemNode = new DefaultMutableTreeNode(createProblemBenchmarkCheckBox(problemBenchmarkResult));
                        solverNode.add(problemNode);
                        initSingleBenchmarkNodes(solverBenchmarkResult, problemBenchmarkResult, problemNode);
                    }
                }
            } else {
                for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
                    DefaultMutableTreeNode problemNode = new DefaultMutableTreeNode(createProblemBenchmarkCheckBox(problemBenchmarkResult));
                    plannerNode.add(problemNode);
                    for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                        DefaultMutableTreeNode solverNode = new DefaultMutableTreeNode(createSolverBenchmarkCheckBox(solverBenchmarkResult));
                        problemNode.add(solverNode);
                        initSingleBenchmarkNodes(solverBenchmarkResult, problemBenchmarkResult, solverNode);
                    }
                }
            }
        }
        return parentNode;
    }

    private void initSingleBenchmarkNodes(SolverBenchmarkResult solverBenchmarkResult, ProblemBenchmarkResult problemBenchmarkResult, DefaultMutableTreeNode problemNode) {
        for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
            if (singleBenchmarkResult.getProblemBenchmarkResult().equals(problemBenchmarkResult)) {
                DefaultMutableTreeNode singleBenchmarkNode = resultCheckBoxMapping.get(singleBenchmarkResult);
                if (singleBenchmarkNode != null) {
                    problemNode.add(singleBenchmarkNode);
                } else {
                    DefaultMutableTreeNode singleNode = new DefaultMutableTreeNode(createSingleBenchmarkCheckBox(singleBenchmarkResult));
                    problemNode.add(singleNode);
                    resultCheckBoxMapping.put(singleBenchmarkResult, singleNode);
                }
            }
        }
    }

    private MixedCheckBox createPlannerBenchmarkCheckBox(PlannerBenchmarkResult plannerBenchmarkResult) {
        String plannerBenchmarkDetail = String.format(
                "Average score: %s%n"
                + "Average problem scale: %d",
                plannerBenchmarkResult.getAverageScore(),
                plannerBenchmarkResult.getAverageProblemScale());
        return new MixedCheckBox(plannerBenchmarkResult.getName(), plannerBenchmarkDetail, plannerBenchmarkResult);
    }

    private MixedCheckBox createSolverBenchmarkCheckBox(SolverBenchmarkResult solverBenchmarkResult) {
        String solverCheckBoxName = solverBenchmarkResult.getName() + " (" + solverBenchmarkResult.getRanking() + ")";
        String solverBenchmarkDetail = String.format(
                "Total score: %s%n"
                + "Average score: %s%n"
                + "Total winning score difference: %s"
                + "Average time spent: %s%n",
                solverBenchmarkResult.getTotalScore(),
                solverBenchmarkResult.getAverageScore(),
                solverBenchmarkResult.getTotalWinningScoreDifference(),
                solverBenchmarkResult.getAverageTimeMillisSpent() == null
                        ? "" : millisecondsSpentNumberFormat.format(solverBenchmarkResult.getAverageTimeMillisSpent()));
        solverBenchmarkResultNameMapping.put(solverBenchmarkResult, solverBenchmarkResult.getName());
        return new MixedCheckBox(solverCheckBoxName, solverBenchmarkDetail, solverBenchmarkResult);
    }

    private MixedCheckBox createProblemBenchmarkCheckBox(ProblemBenchmarkResult problemBenchmarkResult) {
        String problemBenchmarkDetail = String.format(
                "Entity count: %d%n"
                + "Problem scale: %d%n"
                + "Used memory: %s",
                problemBenchmarkResult.getEntityCount(),
                problemBenchmarkResult.getProblemScale(),
                toEmptyStringIfNull(problemBenchmarkResult.getAverageUsedMemoryAfterInputSolution()));
        return new MixedCheckBox(problemBenchmarkResult.getName(), problemBenchmarkDetail);
    }

    private MixedCheckBox createSingleBenchmarkCheckBox(SingleBenchmarkResult singleBenchmarkResult) {
        String singleCheckBoxName = singleBenchmarkResult.getName() + " (" + singleBenchmarkResult.getRanking() + ")";
        String singleBenchmarkDetail = String.format(
                "Score: %s%n"
                + "Used memory: %s%n"
                + "Time spent: %s",
                singleBenchmarkResult.getAverageScore(),
                toEmptyStringIfNull(singleBenchmarkResult.getUsedMemoryAfterInputSolution()),
                millisecondsSpentNumberFormat.format(singleBenchmarkResult.getTimeMillisSpent()));
        return new MixedCheckBox(singleCheckBoxName, singleBenchmarkDetail, singleBenchmarkResult);
    }

    private String toEmptyStringIfNull(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private class GenerateReportWorker extends SwingWorker<File, Void> {

        private final BenchmarkAggregatorFrame parentFrame;
        private List<SingleBenchmarkResult> singleBenchmarkResultList;

        public GenerateReportWorker(BenchmarkAggregatorFrame parentFrame, List<SingleBenchmarkResult> singleBenchmarkResultList) {
            this.parentFrame = parentFrame;
            this.singleBenchmarkResultList = singleBenchmarkResultList;
        }

        @Override
        protected File doInBackground() {
            return benchmarkAggregator.aggregate(singleBenchmarkResultList, solverBenchmarkResultNameMapping);
        }

        @Override
        protected void done() {
            try {
                File htmlOverviewFile = get();
                ReportFinishedDialog dialog = new ReportFinishedDialog(parentFrame, htmlOverviewFile);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("The report generation was interrupted.", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("The report generation failed.", e.getCause());
            } finally {
                detailTextArea.setText(null);
                generateProgressBar.setIndeterminate(false);
                generateProgressBar.setString(null);
                generateProgressBar.setStringPainted(false);
            }
        }

    }

    private class ReportFinishedDialog extends JDialog {

        private final BenchmarkAggregatorFrame parentFrame;
        private final File reportFile;

        private JCheckBox exitCheckBox;

        public ReportFinishedDialog(BenchmarkAggregatorFrame parentFrame, File reportFile) {
            super(parentFrame, "Report generation finished");
            this.parentFrame = parentFrame;
            this.reportFile = reportFile;
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    refresh();
                }
            });

            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            exitCheckBox = new JCheckBox("Exit application", exitApplicationWhenReportFinished);
            mainPanel.add(exitCheckBox, BorderLayout.NORTH);
            mainPanel.add(createButtonPanel(), BorderLayout.CENTER);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            getContentPane().add(mainPanel);
        }

        private JPanel createButtonPanel() {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

            AbstractAction openBrowserAction = new AbstractAction("Show in browser") {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        desktop.browse(reportFile.getAbsoluteFile().toURI());
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed showing reportFile (" + reportFile
                                + ") in the default browser.", e);
                    }
                    finishDialog();
                }
            };
            openBrowserAction.setEnabled(desktop != null && desktop.isSupported(Desktop.Action.BROWSE));
            buttonPanel.add(new JButton(openBrowserAction));

            AbstractAction openFileAction = new AbstractAction("Show in files") {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        desktop.open(reportFile.getParentFile());
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed showing reportFile (" + reportFile
                                + ") in the file explorer.", e);
                    }
                    finishDialog();
                }
            };
            openFileAction.setEnabled(desktop != null && desktop.isSupported(Desktop.Action.OPEN));
            buttonPanel.add(new JButton(openFileAction));

            AbstractAction closeAction = new AbstractAction("Ok") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    finishDialog();
                }
            };
            buttonPanel.add(new JButton(closeAction));

            return buttonPanel;
        }

        private void finishDialog() {
            exitApplicationWhenReportFinished = exitCheckBox.isSelected();
            if (exitApplicationWhenReportFinished) {
                parentFrame.dispose();
            } else {
                dispose();
                parentFrame.refresh();
            }
        }

    }

    private void refresh() {
        initPlannerBenchmarkResultList();
        solverBenchmarkResultNameMapping = new HashMap<>();
        resultCheckBoxMapping = new LinkedHashMap<>();
        checkBoxTree.setSelectedSingleBenchmarkNodes(new HashSet<>());
        DefaultMutableTreeNode newCheckBoxRootNode = initBenchmarkHierarchy(true);
        DefaultTreeModel treeModel = new DefaultTreeModel(newCheckBoxRootNode);
        checkBoxTree.setModel(treeModel);
        treeModel.nodeStructureChanged(newCheckBoxRootNode);
        setEnabled(true);
    }

}
