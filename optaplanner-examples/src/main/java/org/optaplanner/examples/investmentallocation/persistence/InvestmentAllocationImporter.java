/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investmentallocation.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.investmentallocation.domain.AssetClass;
import org.optaplanner.examples.investmentallocation.domain.InvestmentAllocationSolution;

public class InvestmentAllocationImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        InvestmentAllocationImporter importer = new InvestmentAllocationImporter();
        importer.convert("irrinki_1.csv", "irrinki_1.xml");
    }

    public InvestmentAllocationImporter() {
        super(new InvestmentAllocationDao());
    }

    public InvestmentAllocationImporter(boolean withoutDao) {
        super(withoutDao);
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new InvestmentAllocationInputBuilder();
    }

    public static class InvestmentAllocationInputBuilder extends TxtInputBuilder {

        private InvestmentAllocationSolution solution;

        public Solution readSolution() throws IOException {
            solution = new InvestmentAllocationSolution();
            solution.setId(0L);
            readAssetClassList();

            logger.info("InvestmentAllocation {} has {} asset classes.",
                    getInputId(), solution.getAssetClassList().size());
            return solution;
        }

        private void readAssetClassList() throws IOException {
            readConstantLine("ABC Institutional Investor Capital Markets Expectations;*");
            readConstantLine("Asset class;+Correlation;*");
            String headerLine = bufferedReader.readLine();
            String[] headerTokens = splitBySemicolonSeparatedValue(headerLine);
            String headerRegex = "ID;Name;Expected return;Standard deviation(;\\d+)+";
            if (!headerLine.trim().matches(headerRegex)) {
                throw new IllegalArgumentException("Read line (" + headerLine + ") is expected to be a constant regex ("
                        + headerRegex + ").");
            }
            int assetClassListSize = headerTokens.length - 4;
            List<AssetClass> assetClassList = new ArrayList<AssetClass>(assetClassListSize);
            Map<Long, AssetClass> idToAssetClassMap = new HashMap<Long, AssetClass>(assetClassListSize);
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                String[] tokens = splitBySemicolonSeparatedValue(line, 4 + assetClassListSize);
                AssetClass assetClass = new AssetClass();
                assetClass.setId(Long.parseLong(tokens[0]));
                assetClass.setName(tokens[1]);
                assetClass.setExpectedReturnNanos(parsePercentageNanos(tokens[2]));
                assetClass.setStandardDeviationRiskNanos(parsePercentageNanos(tokens[3]));

                assetClassList.add(assetClass);
                idToAssetClassMap.put(assetClass.getId(), assetClass);
            }
            if (assetClassList.size() != assetClassListSize) {
                throw new IllegalStateException("The assetClassList size (" + assetClassList.size()
                        + ") is expected to be the same as the header's assetClassListSize ("
                        + assetClassListSize + ")");
            }
            solution.setAssetClassList(assetClassList);
        }

        protected long parsePercentageNanos(String token) {
            BigDecimal nanos;
            if (token.endsWith("%")) {
                nanos = new BigDecimal(token.substring(0, token.length() - 1))
                        .multiply(new BigDecimal(10000000L));
            } else {
                nanos = new BigDecimal(token)
                        .multiply(new BigDecimal(1000000000L));
            }
            return nanos.longValueExact();
        }

    }

}
