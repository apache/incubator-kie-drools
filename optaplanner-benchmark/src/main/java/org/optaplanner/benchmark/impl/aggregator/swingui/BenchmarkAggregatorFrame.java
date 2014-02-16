/*
 * Copyright 2014 JBoss Inc
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkAggregatorFrame extends JFrame {

    public static void createAndDisplay(PlannerBenchmarkFactory plannerBenchmarkFactory) {
        SwingUncaughtExceptionHandler.register();
        SwingUtils.fixateLookAndFeel();
        PlannerBenchmarkConfig plannerBenchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        BenchmarkAggregator benchmarkAggregator = new BenchmarkAggregator();
        benchmarkAggregator.setBenchmarkDirectory(plannerBenchmarkConfig.getBenchmarkDirectory());
        BenchmarkReportConfig benchmarkReportConfig = plannerBenchmarkConfig.getBenchmarkReportConfig();
        if (benchmarkReportConfig == null) {
            benchmarkReportConfig = new BenchmarkReportConfig();
        }
        benchmarkAggregator.setBenchmarkReportConfig(benchmarkReportConfig);

        BenchmarkAggregatorFrame benchmarkAggregatorFrame = new BenchmarkAggregatorFrame(benchmarkAggregator);
        benchmarkAggregatorFrame.init();
        benchmarkAggregatorFrame.setVisible(true);
    }

    private static final String DETAIL_TEMPLATE_PLANNER_BENCHMARK = "Average score: %s%nAverage problem scale: %d";
    private static final String DETAIL_TEMPLATE_PROBLEM_BENCHMARK = "Problem scale: %d%nUsed memory: %s";
    private static final String DETAIL_TEMPLATE_SINGLE_BENCHMARK = "Score: %s%nPlanning entity"
            + " count: %d%nUsed memory: %s%nTime millis spend: %d ms";
    private static final String DETAIL_TEMPLATE_SOLVER_BENCHMARK = "Average score: %s%nTotal score: %s%n"
            + "Average time millis spend: %d ms%nTotal winning score difference: %s";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final BenchmarkAggregator benchmarkAggregator;
    private final BenchmarkResultIO benchmarkResultIO;

    private List<PlannerBenchmarkResult> plannerBenchmarkResultList;
    private Map<MixedCheckBox, SingleBenchmarkResult> resultCheckBoxMapping = new LinkedHashMap<MixedCheckBox, SingleBenchmarkResult>();

    private CheckBoxTree checkBoxTree;
    private JTextArea detailTextArea;
    private JProgressBar generateProgressBar;

    public BenchmarkAggregatorFrame(BenchmarkAggregator benchmarkAggregator) {
        super("Benchmark aggregator");
        this.benchmarkAggregator = benchmarkAggregator;
        benchmarkResultIO = new BenchmarkResultIO();
        plannerBenchmarkResultList = Collections.emptyList();
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initPlannerBenchmarkResultList();
        setContentPane(createContentPane());
        setPreferredSize(new Dimension(500, 400));
        pack();
        setLocationRelativeTo(null);
    }

    private JComponent createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        if (plannerBenchmarkResultList.isEmpty()) {
            contentPane.add(createNoPlannerFoundTextField(), BorderLayout.CENTER);
        } else {
            contentPane.add(createTopButtonPanel(), BorderLayout.NORTH);
            contentPane.add(createBenchmarkTreePanel(), BorderLayout.CENTER);
        }
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JButton expandNodesButton = new JButton(new ExpandAllNodesAction());
        buttonPanel.add(expandNodesButton);
        JButton collapseNodesButton = new JButton(new CollapseAllNodesAction());
        buttonPanel.add(collapseNodesButton);
        generateProgressBar = new JProgressBar();
        buttonPanel.add(generateProgressBar);
        return buttonPanel;
    }

    private JComponent createBenchmarkTreePanel() {
        JPanel benchmarkTreePanel = new JPanel(new BorderLayout());
        CheckBoxTree checkBoxTree = createCheckBoxTree();
        benchmarkTreePanel.add(new JScrollPane(checkBoxTree), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateReportButton = new JButton(new GenerateReportAction(this));
        buttonPanel.add(generateReportButton);
        benchmarkTreePanel.add(buttonPanel, BorderLayout.SOUTH);
        benchmarkTreePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return benchmarkTreePanel;
    }

    private CheckBoxTree createCheckBoxTree() {
        CheckBoxTree resultCheckBoxTree = new CheckBoxTree(initBenchmarkHierarchy());
        resultCheckBoxTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath treeSelectionPath = e.getNewLeadSelectionPath();
                if (treeSelectionPath != null) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeSelectionPath.getLastPathComponent();
                    MixedCheckBox checkBox = (MixedCheckBox) treeNode.getUserObject();
                    detailTextArea.setText(checkBox.getDetail());
                    detailTextArea.setCaretPosition(0);
                }
            }
        });
        checkBoxTree = resultCheckBoxTree;
        return resultCheckBoxTree;
    }

    private void initPlannerBenchmarkResultList() {
        plannerBenchmarkResultList = benchmarkResultIO.readPlannerBenchmarkResultList(
                benchmarkAggregator.getBenchmarkDirectory());
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            plannerBenchmarkResult.accumulateResults(
                    benchmarkAggregator.getBenchmarkReportConfig().buildBenchmarkReport(plannerBenchmarkResult));
        }
    }

    private class GenerateReportAction extends AbstractAction {

        private JFrame parentFrame;

        public GenerateReportAction(JFrame parentFrame) {
            super("Generate report");
            this.parentFrame = parentFrame;
        }

        public void actionPerformed(ActionEvent e) {
            parentFrame.setEnabled(false);
            generateReport();
        }

        private void generateReport() {
            List<SingleBenchmarkResult> singleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
            for (Map.Entry<MixedCheckBox, SingleBenchmarkResult> entry : resultCheckBoxMapping.entrySet()) {
                if (MixedCheckBoxStatus.CHECKED == entry.getKey().getStatus()) {
                    singleBenchmarkResultList.add(entry.getValue());
                }
            }
            if (singleBenchmarkResultList.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No single benchmarks have been selected.", "Warning", JOptionPane.WARNING_MESSAGE);
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

    private class ExpandAllNodesAction extends AbstractAction {

        public ExpandAllNodesAction() {
            super("Expand all", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("expandAll.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkBoxTree.expandAllNodes();
        }
    }

    private class CollapseAllNodesAction extends AbstractAction {

        public CollapseAllNodesAction() {
            super("Collapse all", new ImageIcon(BenchmarkAggregatorFrame.class.getResource("collapseAll.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkBoxTree.collapseAllNodes();
        }
    }

    private DefaultMutableTreeNode initBenchmarkHierarchy() {
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new MixedCheckBox("Planner benchmarks"));
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            DefaultMutableTreeNode plannerNode = new DefaultMutableTreeNode(createPlannerBenchmarkCheckBox(plannerBenchmarkResult));
            parentNode.add(plannerNode);
            for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                DefaultMutableTreeNode solverNode = new DefaultMutableTreeNode(createSolverBenchmarkCheckBox(solverBenchmarkResult));
                plannerNode.add(solverNode);
                for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
                    DefaultMutableTreeNode problemNode = new DefaultMutableTreeNode(createProblemBenchmarkCheckBox(problemBenchmarkResult));
                    solverNode.add(problemNode);
                    for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                        if (singleBenchmarkResult.getProblemBenchmarkResult().equals(problemBenchmarkResult)) {
                            DefaultMutableTreeNode singleNode = new DefaultMutableTreeNode(createSingleBenchmarkCheckBox(singleBenchmarkResult));
                            problemNode.add(singleNode);
                        }
                    }
                }
            }
        }
        return parentNode;
    }

    private MixedCheckBox createPlannerBenchmarkCheckBox(PlannerBenchmarkResult plannerBenchmarkResult) {
        String plannerBenchmarkDetail = String.format(DETAIL_TEMPLATE_PLANNER_BENCHMARK, plannerBenchmarkResult.getAverageScore(),
                plannerBenchmarkResult.getAverageProblemScale());
        return new MixedCheckBox(plannerBenchmarkResult.getName(), plannerBenchmarkDetail);
    }

    private MixedCheckBox createSolverBenchmarkCheckBox(SolverBenchmarkResult solverBenchmarkResult) {
        String solverCheckBoxName = solverBenchmarkResult.getName() + " (" + solverBenchmarkResult.getRanking() + ")";
        String solverBenchmarkDetail = String.format(DETAIL_TEMPLATE_SOLVER_BENCHMARK, solverBenchmarkResult.getAverageScore(),
                solverBenchmarkResult.getTotalScore(), solverBenchmarkResult.getAverageTimeMillisSpend(),
                solverBenchmarkResult.getTotalWinningScoreDifference());
        return new MixedCheckBox(solverCheckBoxName, solverBenchmarkDetail);
    }

    private MixedCheckBox createProblemBenchmarkCheckBox(ProblemBenchmarkResult problemBenchmarkResult) {
        String problemBenchmarkDetail = String.format(DETAIL_TEMPLATE_PROBLEM_BENCHMARK, problemBenchmarkResult.getProblemScale(),
                toEmptyStringIfNull(problemBenchmarkResult.getAverageUsedMemoryAfterInputSolution()));
        return new MixedCheckBox(problemBenchmarkResult.getName(), problemBenchmarkDetail);
    }

    private MixedCheckBox createSingleBenchmarkCheckBox(SingleBenchmarkResult singleBenchmarkResult) {
        String singleCheckBoxName = singleBenchmarkResult.getName() + " (" + singleBenchmarkResult.getRanking() + ")";
        String singleBenchmarkDetail = String.format(DETAIL_TEMPLATE_SINGLE_BENCHMARK, singleBenchmarkResult.getScore(),
                singleBenchmarkResult.getPlanningEntityCount(), toEmptyStringIfNull(singleBenchmarkResult.getUsedMemoryAfterInputSolution()),
                singleBenchmarkResult.getTimeMillisSpend());
        MixedCheckBox singleBenchmarkCheckBox = new MixedCheckBox(singleCheckBoxName, singleBenchmarkDetail);
        resultCheckBoxMapping.put(singleBenchmarkCheckBox, singleBenchmarkResult);
        return singleBenchmarkCheckBox;
    }

    private String toEmptyStringIfNull(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private class GenerateReportWorker extends SwingWorker<File, Void> {

        private JFrame parentFrame;
        private List<SingleBenchmarkResult> singleBenchmarkResultList;

        public GenerateReportWorker(JFrame parentFrame, List<SingleBenchmarkResult> singleBenchmarkResultList) {
            this.parentFrame = parentFrame;
            this.singleBenchmarkResultList = singleBenchmarkResultList;
        }

        @Override
        protected File doInBackground() {
            return benchmarkAggregator.aggregate(singleBenchmarkResultList);
        }

        @Override
        protected void done() {
            try {
                File htmlOverviewFile = get();
                ReportFinishedDialog dialog = new ReportFinishedDialog(parentFrame, htmlOverviewFile);
                dialog.pack();
                dialog.setLocationRelativeTo(BenchmarkAggregatorFrame.this);
                dialog.setVisible(true);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            } finally {
                parentFrame.setEnabled(true);
                detailTextArea.setText(null);
                generateProgressBar.setIndeterminate(false);
                generateProgressBar.setString(null);
                generateProgressBar.setStringPainted(false);
            }
        }

    }

    private class ReportFinishedDialog extends JDialog {

        public ReportFinishedDialog(final JFrame parentFrame, final File reportFile) {
            super(parentFrame, "Report generation finished");
            JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));

            final JCheckBox exitCheckBox = new JCheckBox("Exit application”");
            exitCheckBox.setSelected(true);

            JButton openBrowserButton = new JButton("Show in browser");
            openBrowserButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openReportFile(reportFile.getAbsoluteFile(), Desktop.Action.BROWSE);
                    if (exitCheckBox.isSelected()) {
                        parentFrame.dispose();
                    }
                }
            });
            contentPanel.add(openBrowserButton);

            JButton openFileButton = new JButton("Show in files");
            openFileButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openReportFile(reportFile.getParentFile(), Desktop.Action.OPEN);
                    if (exitCheckBox.isSelected()) {
                        parentFrame.dispose();
                    }
                }
            });
            contentPanel.add(openFileButton);

            JButton closeButton = new JButton("Ok");
            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (exitCheckBox.isSelected()) {
                        parentFrame.dispose();
                    } else {
                        dispose();
                    }
                }
            });
            contentPanel.add(closeButton);

            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            mainPanel.add(exitCheckBox, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            getContentPane().add(mainPanel);
            pack();
        }

        private void openReportFile(File file, Desktop.Action action) {
            Desktop desktop = Desktop.getDesktop();
            try {
                switch (action) {
                    case OPEN:
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            desktop.open(file);
                        }
                        break;
                    case BROWSE:
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(file.toURI());
                        }
                        break;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

    }

}
