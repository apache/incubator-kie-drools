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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.BenchmarkResultIO;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkAggregatorFrame extends JFrame {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private BenchmarkResultIO benchmarkResultIO;
    private File benchmarkDirectory;

    private JPanel resultSelectionPanel;
    private List<PlannerBenchmarkResult> visiblePlannerBenchmarkResultList;

    public BenchmarkAggregatorFrame(File defaultBenchmarkDirectory) {
        super("Benchmark aggregator");
        benchmarkDirectory = defaultBenchmarkDirectory;
        benchmarkResultIO = new BenchmarkResultIO();
        visiblePlannerBenchmarkResultList = Collections.emptyList();
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(createContentPane());
        refreshPlannerBenchmarkResultList();
        pack();
        setLocationRelativeTo(null);
    }

    // ************************************************************************
    // TODO All code below is POC code: replace this code with production quality code
    // ************************************************************************

    private JComponent createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(createBenchmarkDirectoryPanel(), BorderLayout.NORTH);
        contentPane.add(createResultSelectionPanel(), BorderLayout.CENTER);
        contentPane.add(createButtonPanel(), BorderLayout.SOUTH);
        return contentPane;
    }

    private JComponent createBenchmarkDirectoryPanel() {
        JTextField benchmarkDirectoryField = new JTextField(80);
        benchmarkDirectoryField.setText(benchmarkDirectory.getAbsolutePath());
        benchmarkDirectoryField.setEditable(false);
        return benchmarkDirectoryField;
    }

    private JComponent createResultSelectionPanel() {
        resultSelectionPanel = new JPanel(new GridLayout(0, 1));
        return resultSelectionPanel;
    }

    private JComponent createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateReportButton = new JButton(new GenerateReportAction());
        buttonPanel.add(generateReportButton);
        return buttonPanel;
    }

    private void refreshPlannerBenchmarkResultList() {
        visiblePlannerBenchmarkResultList = benchmarkResultIO.readPlannerBenchmarkResultList(benchmarkDirectory);
        resultSelectionPanel.removeAll();
        for (PlannerBenchmarkResult result : visiblePlannerBenchmarkResultList) {
            resultSelectionPanel.add(new JLabel(result.getName()));
        }
    }

    private class GenerateReportAction extends AbstractAction {

        public GenerateReportAction() {
            super("Generate report");
        }

        public void actionPerformed(ActionEvent e) {
            generateReport();
        }

    }

    private void generateReport() {
        List<SingleBenchmarkResult> singleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        for (PlannerBenchmarkResult plannerBenchmarkResult : visiblePlannerBenchmarkResultList) {
            for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                singleBenchmarkResultList.addAll(solverBenchmarkResult.getSingleBenchmarkResultList());
            }
        }
        BenchmarkAggregator benchmarkAggregator = new BenchmarkAggregator();
        benchmarkAggregator.setBenchmarkDirectory(benchmarkDirectory);
        benchmarkAggregator.aggregate(singleBenchmarkResultList);
    }

}
