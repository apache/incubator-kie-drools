package org.drools.model.codegen.execmodel.domain;

import java.util.Calendar;
import java.util.Date;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Duration("duration")
public class StockTickEx extends StockFact {

    private long timeFieldEx;
    private Calendar dueDate;

    public StockTickEx( String company ) {
        super( company );
    }

    public StockTickEx( String company, long duration ) {
        super( company, duration );
    }

    public long getTimeFieldExAsLong() {
        return timeFieldEx;
    }

    public Date getTimeFieldExAsDate() {
        return new Date(timeFieldEx);
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public StockTickEx setTimeFieldEx( long timeFieldEx ) {
        this.timeFieldEx = timeFieldEx;
        return this;
    }

    public boolean getIsSetDueDate() {
        return null != dueDate;
    }

    public boolean getIsSetTimeFieldEx() {
        return 0 != timeFieldEx;
    }
}
