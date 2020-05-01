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

package org.optaplanner.examples.investment.persistence;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.investment.app.InvestmentApp;
import org.optaplanner.examples.investment.domain.AssetClass;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentParametrization;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.Region;
import org.optaplanner.examples.investment.domain.Sector;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

public class InvestmentImporter extends AbstractXlsxSolutionImporter<InvestmentSolution> {

    public static void main(String[] args) {
        SolutionConverter<InvestmentSolution> converter = SolutionConverter.createImportConverter(
                InvestmentApp.DATA_DIR_NAME, new InvestmentImporter(), InvestmentSolution.class);
        converter.convert("irrinki_1.xlsx", "irrinki_1.xml");
        converter.convert("de_smet_1.xlsx", "de_smet_1.xml");
    }

    @Override
    public XlsxInputBuilder<InvestmentSolution> createXlsxInputBuilder() {
        return new InvestmentAllocationInputBuilder();
    }

    public static class InvestmentAllocationInputBuilder extends XlsxInputBuilder<InvestmentSolution> {

        private InvestmentSolution solution;

        private Map<String, Region> regionMap;
        private Map<String, Sector> sectorMap;

        @Override
        public InvestmentSolution readSolution() throws IOException {
            solution = new InvestmentSolution();
            solution.setId(0L);
            readParametrization();
            readRegionList();
            readSectorList();
            readAssetClassList();
            createAssetClassAllocationList();

            BigInteger possibleSolutionSize = BigInteger.valueOf(solution.getAssetClassList().size()).multiply(
                    BigInteger.valueOf(InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS));
            logger.info("InvestmentAllocation {} has {} regions, {} sectors and {} asset classes"
                    + " with a search space of {}.",
                    getInputId(),
                    solution.getRegionList().size(),
                    solution.getSectorList().size(),
                    solution.getAssetClassList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        private void readParametrization() throws IOException {
            Sheet sheet = readSheet(0, "Parametrization");
            assertCellConstant(sheet.getRow(0).getCell(0), "Investment parametrization");
            InvestmentParametrization parametrization = new InvestmentParametrization();
            parametrization.setId(0L);
            parametrization.setStandardDeviationMillisMaximum(
                    parsePercentageMillis(readDoubleParameter(sheet.getRow(1), "Standard deviation maximum")));
            solution.setParametrization(parametrization);
        }

        private void readRegionList() throws IOException {
            Sheet sheet = readSheet(1, "Regions");
            Row headerRow = sheet.getRow(0);
            assertCellConstant(headerRow.getCell(0), "Name");
            assertCellConstant(headerRow.getCell(1), "Quantity maximum");
            List<Region> regionList = new ArrayList<>();
            regionMap = new LinkedHashMap<>();
            long id = 0L;
            for (Row row : sheet) {
                if (row.getRowNum() < 1) {
                    continue;
                }
                if (row.getCell(0) == null && row.getCell(1) == null) {
                    continue;
                }
                Region region = new Region();
                region.setId(id);
                id++;
                region.setName(readStringCell(row.getCell(0)));
                region.setQuantityMillisMaximum(parsePercentageMillis(readDoubleCell(row.getCell(1))));
                regionList.add(region);
                regionMap.put(region.getName(), region);
            }
            solution.setRegionList(regionList);
        }

        private void readSectorList() throws IOException {
            Sheet sheet = readSheet(2, "Sectors");
            Row headerRow = sheet.getRow(0);
            assertCellConstant(headerRow.getCell(0), "Name");
            assertCellConstant(headerRow.getCell(1), "Quantity maximum");
            List<Sector> sectorList = new ArrayList<>();
            sectorMap = new LinkedHashMap<>();
            long id = 0L;
            for (Row row : sheet) {
                if (row.getRowNum() < 1) {
                    continue;
                }
                if (row.getCell(0) == null && row.getCell(1) == null) {
                    continue;
                }
                Sector sector = new Sector();
                sector.setId(id);
                id++;
                sector.setName(readStringCell(row.getCell(0)));
                sector.setQuantityMillisMaximum(parsePercentageMillis(readDoubleCell(row.getCell(1))));
                sectorList.add(sector);
                sectorMap.put(sector.getName(), sector);
            }
            solution.setSectorList(sectorList);
        }

        private void readAssetClassList() throws IOException {
            Sheet sheet = readSheet(3, "AssetClasses");
            final int ASSET_CLASS_PROPERTIES_COUNT = 6;
            Row groupHeaderRow = sheet.getRow(0);
            assertCellConstant(groupHeaderRow.getCell(0), "Asset class");
            assertCellConstant(groupHeaderRow.getCell(ASSET_CLASS_PROPERTIES_COUNT), "Correlation");
            Row headerRow = sheet.getRow(1);
            assertCellConstant(headerRow.getCell(0), "ID");
            assertCellConstant(headerRow.getCell(1), "Name");
            assertCellConstant(headerRow.getCell(2), "Region");
            assertCellConstant(headerRow.getCell(3), "Sector");
            assertCellConstant(headerRow.getCell(4), "Expected return");
            assertCellConstant(headerRow.getCell(5), "Standard deviation");

            int assetClassListSize = headerRow.getPhysicalNumberOfCells() - ASSET_CLASS_PROPERTIES_COUNT;
            List<AssetClass> assetClassList = new ArrayList<>(assetClassListSize);
            Map<Long, AssetClass> idToAssetClassMap = new HashMap<>(assetClassListSize);
            for (int i = 0; i < assetClassListSize; i++) {
                AssetClass assetClass = new AssetClass();
                assetClass.setId(readLongCell(headerRow.getCell(ASSET_CLASS_PROPERTIES_COUNT + i)));
                assetClassList.add(assetClass);
                AssetClass old = idToAssetClassMap.put(assetClass.getId(), assetClass);
                if (old != null) {
                    throw new IllegalStateException("The assetClass id (" + assetClass.getId() + ") is not unique.");
                }
            }
            for (Row row : sheet) {
                if (row.getRowNum() < 2) {
                    continue;
                }
                if (row.getCell(0) == null && row.getCell(1) == null && row.getCell(2) == null
                        && row.getCell(3) == null && row.getCell(4) == null && row.getCell(5) == null) {
                    continue;
                }
                if (row.getPhysicalNumberOfCells() != (ASSET_CLASS_PROPERTIES_COUNT + assetClassListSize)) {
                    throw new IllegalArgumentException("The row (" + row.getRowNum() + ") has "
                            + row.getPhysicalNumberOfCells() + " cells, but is expected to have "
                            + (ASSET_CLASS_PROPERTIES_COUNT + assetClassListSize) + " cells instead.");
                }
                long id = readLongCell(row.getCell(0));
                AssetClass assetClass = idToAssetClassMap.get(id);
                if (assetClass == null) {
                    throw new IllegalStateException("The row (" + row.getRowNum()
                            + ") has an assetClass id (" + id + ") that is not in the header.");
                }
                assetClass.setName(readStringCell(row.getCell(1)));
                String regionName = readStringCell(row.getCell(2));
                Region region = regionMap.get(regionName);
                if (region == null) {
                    throw new IllegalStateException("The row (" + row.getRowNum()
                            + ") has a region (" + regionName + ") that is not in the regions sheet.");
                }
                assetClass.setRegion(region);
                String sectorName = readStringCell(row.getCell(3));
                Sector sector = sectorMap.get(sectorName);
                if (sector == null) {
                    throw new IllegalStateException("The row (" + row.getRowNum()
                            + ") has a sector (" + sectorName + ") that is not in the sectors sheet.");
                }
                assetClass.setSector(sector);
                assetClass.setExpectedReturnMillis(parsePercentageMillis(readDoubleCell(row.getCell(4))));
                assetClass.setStandardDeviationRiskMillis(parsePercentageMillis(readDoubleCell(row.getCell(5))));
                Map<AssetClass, Long> correlationMillisMap = new LinkedHashMap<>(assetClassListSize);
                for (int i = 0; i < assetClassListSize; i++) {
                    AssetClass other = assetClassList.get(i);
                    long correlationMillis = parsePercentageMillis(
                            readDoubleCell(row.getCell(ASSET_CLASS_PROPERTIES_COUNT + i)));
                    correlationMillisMap.put(other, correlationMillis);
                }
                assetClass.setCorrelationMillisMap(correlationMillisMap);
            }
            solution.setAssetClassList(assetClassList);
        }

        private void createAssetClassAllocationList() {
            List<AssetClass> assetClassList = solution.getAssetClassList();
            List<AssetClassAllocation> assetClassAllocationList = new ArrayList<>(assetClassList.size());
            for (AssetClass assetClass : assetClassList) {
                AssetClassAllocation allocation = new AssetClassAllocation();
                allocation.setId(assetClass.getId());
                allocation.setAssetClass(assetClass);
                assetClassAllocationList.add(allocation);
            }
            solution.setAssetClassAllocationList(assetClassAllocationList);
        }

        protected long parsePercentageMillis(double numericValue) {
            return (long) (numericValue * 1000.0);
        }

        protected long parsePercentageMillis(String token) {
            BigDecimal millis;
            if (token.endsWith("%")) {
                millis = new BigDecimal(token.substring(0, token.length() - 1)).multiply(new BigDecimal(10L));
            } else {
                millis = new BigDecimal(token).multiply(new BigDecimal(1000L));
            }
            return millis.longValueExact();
        }

    }

}
