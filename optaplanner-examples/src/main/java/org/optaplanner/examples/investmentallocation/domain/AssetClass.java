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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("IaAssetClass")
public class AssetClass extends AbstractPersistable {

    private String name;
    private long expectedReturnMicros; // In micro's (so multiplied by 10^6)
    private long standardDeviationRiskMicros; // In micro's (so multiplied by 10^6)

    private Map<AssetClass, Long> correlationMicrosMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpectedReturnMicros() {
        return expectedReturnMicros;
    }

    public void setExpectedReturnMicros(long expectedReturnMicros) {
        this.expectedReturnMicros = expectedReturnMicros;
    }

    public long getStandardDeviationRiskMicros() {
        return standardDeviationRiskMicros;
    }

    public void setStandardDeviationRiskMicros(long standardDeviationRiskMicros) {
        this.standardDeviationRiskMicros = standardDeviationRiskMicros;
    }

    public Map<AssetClass, Long> getCorrelationMicrosMap() {
        return correlationMicrosMap;
    }

    public void setCorrelationMicrosMap(Map<AssetClass, Long> correlationMicrosMap) {
        this.correlationMicrosMap = correlationMicrosMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getExpectedReturnLabel() {
        BigDecimal percentage = new BigDecimal(expectedReturnMicros).divide(
                new BigDecimal(1000000L), BigDecimal.ROUND_HALF_UP);
        return DecimalFormat.getPercentInstance().format(percentage);
    }

    public String getStandardDeviationRiskLabel() {
        BigDecimal percentage = new BigDecimal(standardDeviationRiskMicros).divide(
                new BigDecimal(1000000L), BigDecimal.ROUND_HALF_UP);
        return DecimalFormat.getPercentInstance().format(percentage);
    }

    public String getCorrelationLabel(AssetClass other) {
        long correlationMicros = correlationMicrosMap.get(other);
        BigDecimal percentage = new BigDecimal(correlationMicros).divide(
                new BigDecimal(1000000L), BigDecimal.ROUND_HALF_UP);
        return DecimalFormat.getPercentInstance().format(percentage);
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

}
