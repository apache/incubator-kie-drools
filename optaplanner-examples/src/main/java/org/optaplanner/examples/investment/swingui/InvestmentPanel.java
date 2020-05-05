/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.investment.swingui;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_EXTRA_PROPERTY_1;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_EXTRA_PROPERTY_2;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_EXTRA_PROPERTY_3;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_EXTRA_PROPERTY_4;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_EXTRA_PROPERTY_5;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW_GROUP1;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.TRAILING_HEADER_ROW;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.investment.domain.AssetClass;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentParametrization;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.Region;
import org.optaplanner.examples.investment.domain.Sector;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class InvestmentPanel extends SolutionPanel<InvestmentSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/investment/swingui/investmentLogo.png";

    private final TimeTablePanel<AssetClass, AssetClass> assetClassPanel;
    private final TimeTablePanel<Void, Region> regionPanel;
    private final TimeTablePanel<Void, Sector> sectorPanel;
    private JSpinner standardDeviationMaximumField;

    private boolean ignoreChangeEvents = false;

    public InvestmentPanel() {
        setLayout(new BorderLayout());
        add(createTableHeader(), BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        assetClassPanel = new TimeTablePanel<>();
        tabbedPane.add("Asset classes", new JScrollPane(assetClassPanel));
        regionPanel = new TimeTablePanel<>();
        tabbedPane.add("Regions", new JScrollPane(regionPanel));
        sectorPanel = new TimeTablePanel<>();
        tabbedPane.add("Sectors", new JScrollPane(sectorPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    private JPanel createTableHeader() {
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.add(new JLabel("Standard deviation maximum"));
        standardDeviationMaximumField = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.001));
        standardDeviationMaximumField.setEditor(new JSpinner.NumberEditor(standardDeviationMaximumField,
                InvestmentNumericUtil.MILLIS_PERCENT_PATTERN));
        headerPanel.add(standardDeviationMaximumField);
        standardDeviationMaximumField.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (ignoreChangeEvents) {
                    return;
                }
                long standardDeviationMillisMaximum = (long) (((Number) standardDeviationMaximumField.getValue()).doubleValue()
                        * 1000.0);
                changeStandardDeviationMillisMaximum(standardDeviationMillisMaximum);
            }
        });
        return headerPanel;
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(InvestmentSolution solution) {
        ignoreChangeEvents = true;
        assetClassPanel.reset();
        regionPanel.reset();
        sectorPanel.reset();
        InvestmentParametrization parametrization = solution.getParametrization();
        standardDeviationMaximumField.setValue((double) parametrization.getStandardDeviationMillisMaximum() / 1000.0);
        defineGrid(solution);
        fillCells(solution);
        ignoreChangeEvents = false;
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(InvestmentSolution solution) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("99999999"));
        int footprintWidth = footprint.getPreferredSize().width;

        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_1);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_2);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_3);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_4);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_5);
        for (AssetClass assetClass : solution.getAssetClassList()) {
            assetClassPanel.defineColumnHeader(assetClass, footprintWidth);
        }

        assetClassPanel.defineRowHeaderByKey(HEADER_ROW_GROUP1);
        assetClassPanel.defineRowHeaderByKey(HEADER_ROW);
        for (AssetClass assetClass : solution.getAssetClassList()) {
            assetClassPanel.defineRowHeader(assetClass);
        }
        assetClassPanel.defineRowHeaderByKey(TRAILING_HEADER_ROW); // Total

        regionPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        regionPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_1);
        regionPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_2);
        regionPanel.defineRowHeaderByKey(HEADER_ROW);
        for (Region region : solution.getRegionList()) {
            regionPanel.defineRowHeader(region);
        }

        sectorPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        sectorPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_1);
        sectorPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_2);
        sectorPanel.defineRowHeaderByKey(HEADER_ROW);
        for (Sector sector : solution.getSectorList()) {
            sectorPanel.defineRowHeader(sector);
        }
    }

    private void fillCells(InvestmentSolution solution) {
        List<AssetClass> assetClassList = solution.getAssetClassList();
        assetClassPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Asset class"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, HEADER_ROW,
                createTableHeader(new JLabel("Region"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, HEADER_ROW,
                createTableHeader(new JLabel("Sector"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, HEADER_ROW,
                createTableHeader(new JLabel("Expected return"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_4, HEADER_ROW,
                createTableHeader(new JLabel("Standard deviation risk"), null));
        JLabel quantityHeaderLabel = new JLabel("Quantity");
        quantityHeaderLabel.setForeground(TangoColorFactory.ORANGE_3);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_5, HEADER_ROW,
                createTableHeader(quantityHeaderLabel, null));
        assetClassPanel.addColumnHeader(assetClassList.get(0), HEADER_ROW_GROUP1,
                assetClassList.get(assetClassList.size() - 1), HEADER_ROW_GROUP1,
                createTableHeader(new JLabel("Correlation"), null));
        for (AssetClass assetClass : assetClassList) {
            assetClassPanel.addColumnHeader(assetClass, HEADER_ROW,
                    createTableHeader(new JLabel(assetClass.getName(), SwingConstants.CENTER),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                                    + " - Standard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass assetClass : assetClassList) {
            assetClassPanel.addRowHeader(HEADER_COLUMN, assetClass,
                    createTableHeader(new JLabel(assetClass.getName(), SwingConstants.LEFT),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                                    + " - Standard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass a : assetClassList) {
            for (AssetClass b : assetClassList) {
                assetClassPanel.addCell(a, b, new JLabel(a.getCorrelationLabel(b), SwingConstants.RIGHT));
            }
        }
        assetClassPanel.addCornerHeader(HEADER_COLUMN, TRAILING_HEADER_ROW,
                createTableHeader(new JLabel("Total"), null));
        long quantityTotalMillis = 0L;
        for (AssetClassAllocation allocation : solution.getAssetClassAllocationList()) {
            if (allocation.getQuantityMillis() != null) {
                quantityTotalMillis += allocation.getQuantityMillis();
            }
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getRegion().getName()));
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getSector().getName()));
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getExpectedReturnLabel(), SwingConstants.RIGHT));
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_4, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getStandardDeviationRiskLabel(), SwingConstants.RIGHT));
            JLabel quantityLabel = new JLabel(allocation.getQuantityLabel(), SwingConstants.RIGHT);
            quantityLabel.setForeground(TangoColorFactory.ORANGE_3);
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_5, allocation.getAssetClass(),
                    quantityLabel);
        }
        JLabel expectedReturnLabel = new JLabel(
                InvestmentNumericUtil.formatMicrosAsPercentage(solution.calculateExpectedReturnMicros()), SwingConstants.RIGHT);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, TRAILING_HEADER_ROW,
                expectedReturnLabel);
        long standardDeviationMicros = solution.calculateStandardDeviationMicros();
        JLabel standardDeviationLabel = new JLabel(InvestmentNumericUtil.formatMicrosAsPercentage(standardDeviationMicros),
                SwingConstants.RIGHT);
        if (standardDeviationMicros > solution.getParametrization().getStandardDeviationMillisMaximum() * 1000L) {
            standardDeviationLabel.setForeground(TangoColorFactory.SCARLET_3);
        }
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_4, TRAILING_HEADER_ROW,
                standardDeviationLabel);
        JLabel quantityTotalLabel = new JLabel(InvestmentNumericUtil.formatMillisAsPercentage(quantityTotalMillis),
                SwingConstants.RIGHT);
        quantityTotalLabel.setForeground(TangoColorFactory.ORANGE_3);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_5, TRAILING_HEADER_ROW, quantityTotalLabel);

        regionPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Region"), null));
        regionPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, HEADER_ROW,
                createTableHeader(new JLabel("Quantity total"), null));
        regionPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, HEADER_ROW,
                createTableHeader(new JLabel("Quantity maximum"), null));
        Map<Region, Long> regionTotalMap = solution.calculateRegionQuantityMillisTotalMap();
        for (final Region region : solution.getRegionList()) {
            regionPanel.addRowHeader(HEADER_COLUMN, region, new JLabel(region.getName()));
            long total = regionTotalMap.get(region);
            JLabel totalLabel = new JLabel(InvestmentNumericUtil.formatMillisAsPercentage(total), SwingConstants.RIGHT);
            if (total > region.getQuantityMillisMaximum()) {
                totalLabel.setForeground(TangoColorFactory.SCARLET_3);
            }
            regionPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, region,
                    totalLabel);
            final JSpinner maximumField = new JSpinner(new SpinnerNumberModel(
                    (double) region.getQuantityMillisMaximum() / 1000.0, 0.0, 1.0, 0.010));
            maximumField.setEditor(new JSpinner.NumberEditor(maximumField,
                    InvestmentNumericUtil.MILLIS_PERCENT_PATTERN));
            regionPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, region, maximumField);
            maximumField.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (ignoreChangeEvents) {
                        return;
                    }
                    long quantityMillisMaximum = (long) (((Number) maximumField.getValue()).doubleValue() * 1000.0);
                    changeRegionQuantityMillisMaximum(region, quantityMillisMaximum);
                }
            });
        }

        sectorPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Sector"), null));
        sectorPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, HEADER_ROW,
                createTableHeader(new JLabel("Quantity total"), null));
        sectorPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, HEADER_ROW,
                createTableHeader(new JLabel("Quantity maximum"), null));
        Map<Sector, Long> sectorTotalMap = solution.calculateSectorQuantityMillisTotalMap();
        for (final Sector sector : solution.getSectorList()) {
            sectorPanel.addRowHeader(HEADER_COLUMN, sector, new JLabel(sector.getName()));
            long total = sectorTotalMap.get(sector);
            JLabel totalLabel = new JLabel(InvestmentNumericUtil.formatMillisAsPercentage(total), SwingConstants.RIGHT);
            if (total > sector.getQuantityMillisMaximum()) {
                totalLabel.setForeground(TangoColorFactory.SCARLET_3);
            }
            sectorPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, sector,
                    totalLabel);
            final JSpinner maximumField = new JSpinner(new SpinnerNumberModel(
                    (double) sector.getQuantityMillisMaximum() / 1000.0, 0.0, 1.0, 0.010));
            maximumField.setEditor(new JSpinner.NumberEditor(maximumField,
                    InvestmentNumericUtil.MILLIS_PERCENT_PATTERN));
            sectorPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, sector, maximumField);
            maximumField.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (ignoreChangeEvents) {
                        return;
                    }
                    long quantityMillisMaximum = (long) (((Number) maximumField.getValue()).doubleValue() * 1000.0);
                    changeSectorQuantityMillisMaximum(sector, quantityMillisMaximum);
                }
            });
        }
    }

    private JPanel createTableHeader(JLabel label, String toolTip) {
        if (toolTip != null) {
            label.setToolTipText(toolTip);
        }
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private void changeStandardDeviationMillisMaximum(final long standardDeviationMillisMaximum) {
        doProblemFactChange(scoreDirector -> {
            InvestmentSolution solution = scoreDirector.getWorkingSolution();
            InvestmentParametrization parametrization = solution.getParametrization();
            scoreDirector.beforeProblemPropertyChanged(parametrization);
            parametrization.setStandardDeviationMillisMaximum(standardDeviationMillisMaximum);
            scoreDirector.afterProblemPropertyChanged(parametrization);
        }, true);
    }

    private void changeRegionQuantityMillisMaximum(final Region region, final long quantityMillisMaximum) {
        doProblemFactChange(scoreDirector -> {
            InvestmentSolution solution = scoreDirector.getWorkingSolution();
            for (Region workingRegion : solution.getRegionList()) {
                if (region.getId().equals(workingRegion.getId())) {
                    scoreDirector.beforeProblemPropertyChanged(workingRegion);
                    workingRegion.setQuantityMillisMaximum(quantityMillisMaximum);
                    scoreDirector.afterProblemPropertyChanged(workingRegion);
                    break;
                }
            }
        }, true);
    }

    private void changeSectorQuantityMillisMaximum(final Sector sector, final long quantityMillisMaximum) {
        doProblemFactChange(scoreDirector -> {
            InvestmentSolution solution = scoreDirector.getWorkingSolution();
            for (Sector workingSector : solution.getSectorList()) {
                if (sector.getId().equals(workingSector.getId())) {
                    scoreDirector.beforeProblemPropertyChanged(workingSector);
                    workingSector.setQuantityMillisMaximum(quantityMillisMaximum);
                    scoreDirector.afterProblemPropertyChanged(workingSector);
                    break;
                }
            }
        }, true);
    }

}
