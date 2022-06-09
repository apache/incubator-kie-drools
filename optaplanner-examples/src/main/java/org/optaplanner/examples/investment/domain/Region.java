package org.optaplanner.examples.investment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Region")
public class Region extends AbstractPersistable {

    private String name;
    private Long quantityMillisMaximum; // In millis (so multiplied by 1000)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantityMillisMaximum() {
        return quantityMillisMaximum;
    }

    public void setQuantityMillisMaximum(Long quantityMillisMaximum) {
        this.quantityMillisMaximum = quantityMillisMaximum;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getQuantityMaximumLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(quantityMillisMaximum);
    }

    @Override
    public String toString() {
        return name;
    }

}
