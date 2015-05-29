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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity()
@XStreamAlias("IaAssetClassAllocation")
public class AssetClassAllocation extends AbstractPersistable {

    private AssetClass assetClass;

    // Planning variables: changes during planning, between score calculations.
    private Long quantityNanos; // In nano's (so multiplied by 10^9)

    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    @PlanningVariable(valueRangeProviderRefs = {"quantityNanosRange"})
    public Long getQuantityNanos() {
        return quantityNanos;
    }

    public void setQuantityNanos(Long quantityNanos) {
        this.quantityNanos = quantityNanos;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getLabel() {
        return assetClass.getName();
    }

    @Override
    public String toString() {
        return assetClass.toString();
    }

}
