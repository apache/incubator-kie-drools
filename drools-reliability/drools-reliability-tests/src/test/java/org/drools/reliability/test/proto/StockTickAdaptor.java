package org.drools.reliability.test.proto;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.test.domain.StockTick;

@ProtoAdapter(StockTick.class)
public class StockTickAdaptor {

    @ProtoFactory
    StockTick create(String company, long duration) {
        return new StockTick(company, duration);
    }

    @ProtoField(1)
    String getCompany(StockTick stockTick) {
        return stockTick.getCompany();
    }

    @ProtoField(number = 2, defaultValue = "0")
    long getDuration(StockTick stockTick) {
        return stockTick.getDuration();
    }
}
