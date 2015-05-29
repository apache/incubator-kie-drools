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

import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("IaAssetClass")
public class AssetClass extends AbstractPersistable {

    private String name;
    private long expectedReturnNanos; // In nano's (so multiplied by 10^9)
    private long standardDeviationRiskNanos; // In nano's (so multiplied by 10^9)

    private Map<AssetClass, Long> correlationNanosMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpectedReturnNanos() {
        return expectedReturnNanos;
    }

    public void setExpectedReturnNanos(long expectedReturnNanos) {
        this.expectedReturnNanos = expectedReturnNanos;
    }

    public long getStandardDeviationRiskNanos() {
        return standardDeviationRiskNanos;
    }

    public void setStandardDeviationRiskNanos(long standardDeviationRiskNanos) {
        this.standardDeviationRiskNanos = standardDeviationRiskNanos;
    }

    public Map<AssetClass, Long> getCorrelationNanosMap() {
        return correlationNanosMap;
    }

    public void setCorrelationNanosMap(Map<AssetClass, Long> correlationNanosMap) {
        this.correlationNanosMap = correlationNanosMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String toString() {
        return id + "-" + name;
    }

}
