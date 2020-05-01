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

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AssetClass")
public class AssetClass extends AbstractPersistable {

    private String name;
    private Region region;
    private Sector sector;
    private long expectedReturnMillis; // In millis (so multiplied by 1000)
    private long standardDeviationRiskMillis; // In millis (so multiplied by 1000)

    private Map<AssetClass, Long> correlationMillisMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
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
        return InvestmentNumericUtil.formatMillisAsPercentage(expectedReturnMillis);
    }

    public String getStandardDeviationRiskLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(standardDeviationRiskMillis);
    }

    public String getCorrelationLabel(AssetClass other) {
        long correlationMillis = correlationMillisMap.get(other);
        return InvestmentNumericUtil.formatMillisAsNumber(correlationMillis);
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

}
