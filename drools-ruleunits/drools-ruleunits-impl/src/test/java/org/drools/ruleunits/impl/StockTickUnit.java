package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessing;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.impl.domain.StockTick;

@EventProcessing(EventProcessingType.STREAM)
@Clock(ClockType.PSEUDO)
public class StockTickUnit implements RuleUnitData {
    private final DataStream<StockTick> stockTicks;
    private final List<StockTick> results = new ArrayList<>();

    public StockTickUnit() {
        this(DataSource.createStream());
    }

    public StockTickUnit(DataStream<StockTick> stockTicks) {
        this.stockTicks = stockTicks;
    }

    public DataStream<StockTick> getStockTicks() {
        return stockTicks;
    }

    public List<StockTick> getResults() {
        return results;
    }
}
