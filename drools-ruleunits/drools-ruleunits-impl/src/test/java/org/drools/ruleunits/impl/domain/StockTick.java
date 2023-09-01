package org.drools.ruleunits.impl.domain;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Duration("duration")
@Expires("10s")
public class StockTick {

    private final String company;
    private final long duration;

    public StockTick(String company) {
        this.company = company;
        this.duration = 0;
    }

    public StockTick(String company, long duration) {
        this.company = company;
        this.duration = duration;
    }

    public String getCompany() {
        return company;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "StockTick [company=" + company + ", duration=" + duration + "]";
    }

}
