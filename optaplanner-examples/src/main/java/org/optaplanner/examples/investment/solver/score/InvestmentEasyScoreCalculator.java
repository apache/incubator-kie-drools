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

package org.optaplanner.examples.investment.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.Region;
import org.optaplanner.examples.investment.domain.Sector;

public class InvestmentEasyScoreCalculator implements EasyScoreCalculator<InvestmentSolution> {

    public HardSoftLongScore calculateScore(InvestmentSolution solution) {
        long hardScore = 0L;
        long softScore = 0L;
        long squaredFemtosMaximum = solution.getParametrization().calculateSquaredStandardDeviationFemtosMaximum();
        long squaredFemtos = solution.calculateStandardDeviationSquaredFemtos();
        // Standard deviation maximum
        if (squaredFemtos > squaredFemtosMaximum) {
            hardScore -= squaredFemtos - squaredFemtosMaximum;
        }
        List<Region> regionList = solution.getRegionList();
        Map<Region, Long> regionQuantityTotalMap = new HashMap<Region, Long>(regionList.size());
        for (Region region : regionList) {
            regionQuantityTotalMap.put(region, 0L);
        }
        List<Sector> sectorList = solution.getSectorList();
        Map<Sector, Long> sectorQuantityTotalMap = new HashMap<Sector, Long>(sectorList.size());
        for (Sector sector : sectorList) {
            sectorQuantityTotalMap.put(sector, 0L);
        }
        for (AssetClassAllocation allocation : solution.getAssetClassAllocationList()) {
            Long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis != null) {
                regionQuantityTotalMap.put(allocation.getRegion(),
                        regionQuantityTotalMap.get(allocation.getRegion()) + quantityMillis);
                sectorQuantityTotalMap.put(allocation.getSector(),
                        sectorQuantityTotalMap.get(allocation.getSector()) + quantityMillis);
            }
        }
        // Region quantity maximum
        for (Region region : regionList) {
            long available = region.getQuantityMillisMaximum() - regionQuantityTotalMap.get(region);
            if (available < 0) {
                hardScore += available;
            }
        }
        // Sector quantity maximum
        for (Sector sector : sectorList) {
            long available = sector.getQuantityMillisMaximum() - sectorQuantityTotalMap.get(sector);
            if (available < 0) {
                hardScore += available;
            }
        }
        // Maximize expected return
        softScore += solution.calculateExpectedReturnMicros();
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
