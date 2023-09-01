package org.drools.testcoverage.common.model;

import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
public class StockTickEvent extends StockTick {

    public StockTickEvent() {
        super();
    }

    public StockTickEvent(final long seq,
                          final String company,
                          final double price,
                          final long time) {
        super(seq, company, price, time);
    }

    public StockTickEvent(final long seq,
                          final String company,
                          final double price,
                          final long time,
                          final long duration) {
        super(seq, company, price, time, duration);
    }
}
