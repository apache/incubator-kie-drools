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

package org.optaplanner.examples.investmentallocation.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investmentallocation.domain.util.InvestmentAllocationMicrosUtil;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("IaInvestmentAllocationSolution")
public class InvestmentAllocationSolution extends AbstractPersistable implements Solution<HardSoftLongScore> {

    private List<AssetClass> assetClassList;

    private List<AssetClassAllocation> assetClassAllocationList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftLongScoreDefinition.class})
    private HardSoftLongScore score;

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

    @ValueRangeProvider(id = "quantityMicrosRange")
    public CountableValueRange<Long> getQuantityMicrosRange() {
        return ValueRangeFactory.createLongValueRange(0L, InvestmentAllocationMicrosUtil.MAXIMUM_QUANTITY_MICROS + 1L);
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(assetClassList);
        // Do not add the planning entity's (assetClassAllocationList) because that will be done automatically
        return facts;
    }

}
