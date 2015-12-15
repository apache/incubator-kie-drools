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

package org.optaplanner.examples.investment.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("InvestmentSolution")
public class InvestmentSolution extends AbstractPersistable implements Solution<HardSoftLongScore> {

    private InvestmentParametrization parametrization;
    private List<Region> regionList;
    private List<Sector> sectorList;
    private List<AssetClass> assetClassList;

    private List<AssetClassAllocation> assetClassAllocationList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftLongScoreDefinition.class})
    private HardSoftLongScore score;

    public InvestmentParametrization getParametrization() {
        return parametrization;
    }

    public void setParametrization(InvestmentParametrization parametrization) {
        this.parametrization = parametrization;
    }

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

    public List<Sector> getSectorList() {
        return sectorList;
    }

    public void setSectorList(List<Sector> sectorList) {
        this.sectorList = sectorList;
    }

    public List<AssetClass> getAssetClassList() {
        return assetClassList;
    }

    public void setAssetClassList(List<AssetClass> assetClassList) {
        this.assetClassList = assetClassList;
    }

    @PlanningEntityCollectionProperty
    public List<AssetClassAllocation> getAssetClassAllocationList() {
        return assetClassAllocationList;
    }

    public void setAssetClassAllocationList(List<AssetClassAllocation> assetClassAllocationList) {
        this.assetClassAllocationList = assetClassAllocationList;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "quantityMillisRange")
    public CountableValueRange<Long> getQuantityMillisRange() {
        return ValueRangeFactory.createLongValueRange(0L, InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS + 1L);
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(parametrization);
        facts.addAll(regionList);
        facts.addAll(sectorList);
        facts.addAll(assetClassList);
        // Do not add the planning entity's (assetClassAllocationList) because that will be done automatically
        return facts;
    }

    /**
     * Not incremental.
     */
    public long calculateExpectedReturnMicros() {
        long expectedReturnMicros = 0L;
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            expectedReturnMicros += allocation.getQuantifiedExpectedReturnMicros();
        }
        return expectedReturnMicros;
    }

    /**
     * Not incremental.
     */
    public long calculateStandardDeviationMicros() {
        long squaredFemtos = calculateStandardDeviationSquaredFemtos();
        return (long) Math.sqrt(squaredFemtos / 1000L);
    }

    /**
     * Not incremental.
     */
    public long calculateStandardDeviationSquaredFemtos() {
        long totalFemtos = 0L;
        for (AssetClassAllocation a : assetClassAllocationList) {
            for (AssetClassAllocation b : assetClassAllocationList) {
                if (a == b) {
                    totalFemtos += a.getQuantifiedStandardDeviationRiskMicros() * b.getQuantifiedStandardDeviationRiskMicros()
                            * 1000L;
                } else {
                    // Matches twice: once for (A, B) and once for (B, A)
                    long correlationMillis = a.getAssetClass().getCorrelationMillisMap().get(b.getAssetClass());
                    totalFemtos += a.getQuantifiedStandardDeviationRiskMicros() * b.getQuantifiedStandardDeviationRiskMicros()
                            * correlationMillis;
                }
            }
        }
        return totalFemtos;
    }

    public Map<Region, Long> calculateRegionQuantityMillisTotalMap() {
        Map<Region, Long> totalMap = new HashMap<Region, Long>(regionList.size());
        for (Region region : regionList) {
            totalMap.put(region, 0L);
        }
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            Long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis != null) {
                totalMap.put(allocation.getRegion(),
                        totalMap.get(allocation.getRegion()) + quantityMillis);
            }
        }
        return totalMap;
    }

    public Map<Sector, Long> calculateSectorQuantityMillisTotalMap() {
        Map<Sector, Long> totalMap = new HashMap<Sector, Long>(regionList.size());
        for (Sector sector : sectorList) {
            totalMap.put(sector, 0L);
        }
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            Long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis != null) {
                totalMap.put(allocation.getSector(),
                        totalMap.get(allocation.getSector()) + quantityMillis);
            }
        }
        return totalMap;
    }

}
