package org.optaplanner.examples.investment.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity()
@XStreamAlias("AssetClassAllocation")
public class AssetClassAllocation extends AbstractPersistable {

    public static long calculateSquaredStandardDeviationFemtosFromTo(AssetClassAllocation from, AssetClassAllocation to) {
        if (from == to) {
            long micros = from.getQuantifiedStandardDeviationRiskMicros();
            return micros * micros * 1000L;
        } else {
            long picos = from.getQuantifiedStandardDeviationRiskMicros() * to.getQuantifiedStandardDeviationRiskMicros();
            long correlationMillis = from.getAssetClass().getCorrelationMillisMap().get(to.getAssetClass());
            return picos * correlationMillis;
        }
    }

    private AssetClass assetClass;

    // Planning variables: changes during planning, between score calculations.
    private Long quantityMillis; // In millis (so multiplied by 1000)

    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    @PlanningVariable(valueRangeProviderRefs = { "quantityMillisRange" })
    public Long getQuantityMillis() {
        return quantityMillis;
    }

    public void setQuantityMillis(Long quantityMillis) {
        this.quantityMillis = quantityMillis;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Region getRegion() {
        return assetClass.getRegion();
    }

    public Sector getSector() {
        return assetClass.getSector();
    }

    public long getQuantifiedExpectedReturnMicros() {
        if (quantityMillis == null) {
            return 0L;
        }
        return quantityMillis * assetClass.getExpectedReturnMillis();
    }

    public long getQuantifiedStandardDeviationRiskMicros() {
        if (quantityMillis == null) {
            return 0L;
        }
        return quantityMillis * assetClass.getStandardDeviationRiskMillis();
    }

    public String getQuantityLabel() {
        if (quantityMillis == null) {
            return "";
        }
        return InvestmentNumericUtil.formatMillisAsPercentage(quantityMillis);
    }

    public String getLabel() {
        return assetClass.getName();
    }

    @Override
    public String toString() {
        return assetClass.toString();
    }

}
