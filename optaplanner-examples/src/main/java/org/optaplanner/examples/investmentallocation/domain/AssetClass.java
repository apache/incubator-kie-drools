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

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investmentallocation.domain.util.InvestmentAllocationNumericUtil;

@XStreamAlias("AssetClass")
public class AssetClass extends AbstractPersistable {

    private String name;
    private long expectedReturnMillis; // In milli's (so multiplied by 1000)
    private long standardDeviationRiskMillis; // In milli's (so multiplied by 1000)

    private Map<AssetClass, Long> correlationMillisMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpectedReturnMillis() {
        return expectedReturnMillis;
    }

    public void setExpectedReturnMillis(long expectedReturnMillis) {
        this.expectedReturnMillis = expectedReturnMillis;
    }

    public long getStandardDeviationRiskMillis() {
        return standardDeviationRiskMillis;
    }

    public void setStandardDeviationRiskMillis(long standardDeviationRiskMillis) {
        this.standardDeviationRiskMillis = standardDeviationRiskMillis;
    }

    public Map<AssetClass, Long> getCorrelationMillisMap() {
        return correlationMillisMap;
    }

    public void setCorrelationMillisMap(Map<AssetClass, Long> correlationMillisMap) {
        this.correlationMillisMap = correlationMillisMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getExpectedReturnLabel() {
        return InvestmentAllocationNumericUtil.formatMillisAsPercentage(expectedReturnMillis);
    }

    public String getStandardDeviationRiskLabel() {
        return InvestmentAllocationNumericUtil.formatMillisAsPercentage(standardDeviationRiskMillis);
    }

    public String getCorrelationLabel(AssetClass other) {
        long correlationMillis = correlationMillisMap.get(other);
        return InvestmentAllocationNumericUtil.formatMillisAsNumber(correlationMillis);
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

}
