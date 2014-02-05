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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

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


    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final BenchmarkAggregator benchmarkAggregator;
    private final BenchmarkResultIO benchmarkResultIO;

    private List<PlannerBenchmarkResult> plannerBenchmarkResultList;
    private Map<MixedCheckBox, SingleBenchmarkResult> resultCheckBoxMapping = new HashMap<MixedCheckBox, SingleBenchmarkResult>();

    private JTextField frameStatusBar;

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
            contentPane.add(createButtonPanel(), BorderLayout.NORTH);
            contentPane.add(createBenchmarkTree(), BorderLayout.CENTER);
        }
        contentPane.add(createFrameStatusBar(), BorderLayout.SOUTH);
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

    private JComponent createFrameStatusBar() {
        frameStatusBar = new JTextField();
        frameStatusBar.setEditable(false);
        return frameStatusBar;
    }

    private JComponent createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateReportButton = new JButton(new GenerateReportAction(this));
        buttonPanel.add(generateReportButton);
        return buttonPanel;
    }

    private JComponent createBenchmarkTree() {
        CheckboxTree checkboxTree = new CheckboxTree(initBenchmarkHierarchy());
        return new JScrollPane(checkboxTree);
    }

    private void initPlannerBenchmarkResultList() {
        plannerBenchmarkResultList = benchmarkResultIO.readPlannerBenchmarkResultList(
                benchmarkAggregator.getBenchmarkDirectory());
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
                frameStatusBar.setText("No single benchmarks have been selected.");
                parentFrame.setEnabled(true);
            } else {
                frameStatusBar.setText("Generating merged report...");
                GenerateReportWorker worker = new GenerateReportWorker(parentFrame, singleBenchmarkResultList);
                worker.execute();
            }
        }
    }

    private DefaultMutableTreeNode initBenchmarkHierarchy() {
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new MixedCheckBox("Planner benchmarks"));
        for (PlannerBenchmarkResult plannerBenchmarkResult : plannerBenchmarkResultList) {
            DefaultMutableTreeNode plannerNode = new DefaultMutableTreeNode(new MixedCheckBox(plannerBenchmarkResult.getName()));
            parentNode.add(plannerNode);
            for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
                DefaultMutableTreeNode problemNode = new DefaultMutableTreeNode(new MixedCheckBox(problemBenchmarkResult.getName()));
                plannerNode.add(problemNode);
                for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                    DefaultMutableTreeNode solverNode = new DefaultMutableTreeNode(new MixedCheckBox(solverBenchmarkResult.getName()));
                    problemNode.add(solverNode);
                    for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                        if (singleBenchmarkResult.getProblemBenchmarkResult().equals(problemBenchmarkResult)) {
                            MixedCheckBox singleCheckbox = new MixedCheckBox(singleBenchmarkResult.getName());
                            DefaultMutableTreeNode singleNode = new DefaultMutableTreeNode(singleCheckbox);
                            resultCheckBoxMapping.put(singleCheckbox, singleBenchmarkResult);
                            solverNode.add(singleNode);
                        }
                    }
                }
            }
        }
        return parentNode;
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
                CustomDialog dialog = new CustomDialog(parentFrame, htmlOverviewFile);
                dialog.pack();
                dialog.setLocationRelativeTo(BenchmarkAggregatorFrame.this);
                dialog.setVisible(true);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            } finally {
                parentFrame.setEnabled(true);
                frameStatusBar.setText(null);
            }
        }

    }

    private class CustomDialog extends JDialog {

        public CustomDialog(final JFrame parentFrame, final File reportFile) {
            super(parentFrame, "Report generation finished");
            JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton openBrowserButton = new JButton("Show in browser");
            openBrowserButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openReportFile(reportFile.getAbsoluteFile(), Desktop.Action.BROWSE);
                }
            });
            contentPanel.add(openBrowserButton);

            JButton openFileButton = new JButton("Show in files");
            openFileButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openReportFile(reportFile.getParentFile(), Desktop.Action.OPEN);
                }
            });
            contentPanel.add(openFileButton);

            final JCheckBox exitCheckbox = new JCheckBox("Exit application”");
            exitCheckbox.setSelected(true);
            contentPanel.add(exitCheckbox);

            JButton closeButton = new JButton("Ok");
            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (exitCheckbox.isSelected()) {
                        parentFrame.dispose();
                    } else {
                        dispose();
                    }
                }
            });
            contentPanel.add(closeButton);
            getContentPane().add(contentPanel);
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
