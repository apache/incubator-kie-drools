package org.drools.compiler;

import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
public class StockTickEvent extends StockTick {

    public StockTickEvent() {
        super();
    }

    public StockTickEvent(long seq,
                          String company,
                          double price,
                          long time) {
        super(seq, company, price, time);
    }

    public StockTickEvent(long seq,
                          String company,
                          double price,
                          long time,
                          long duration ) {
        super(seq, company, price, time, duration);
    }

}
