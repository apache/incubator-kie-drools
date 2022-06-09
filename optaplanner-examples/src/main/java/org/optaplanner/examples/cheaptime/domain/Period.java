package org.optaplanner.examples.cheaptime.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtPeriod")
public class Period extends AbstractPersistable {

    private final int index;
    private long powerPriceMicros;

    public Period(int id, long powerPriceMicros) {
        super(id);
        this.index = id;
        this.powerPriceMicros = powerPriceMicros;
    }

    public int getIndex() {
        return index;
    }

    public long getPowerPriceMicros() {
        return powerPriceMicros;
    }

    public void setPowerPriceMicros(long powerPriceMicros) {
        this.powerPriceMicros = powerPriceMicros;
    }

}
