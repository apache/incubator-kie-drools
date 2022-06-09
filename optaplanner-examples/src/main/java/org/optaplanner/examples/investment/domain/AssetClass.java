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
