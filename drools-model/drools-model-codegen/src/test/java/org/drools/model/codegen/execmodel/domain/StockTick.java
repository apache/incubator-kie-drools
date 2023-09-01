package org.drools.model.codegen.execmodel.domain;

import java.util.Calendar;
import java.util.Date;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Duration("duration")
public class StockTick extends StockFact {

    private long timeField;
    private Calendar dueDate;

    public StockTick( String company ) {
        super( company );
    }

    public StockTick( String company, long duration ) {
        super( company, duration );
    }

    public long getTimeFieldAsLong() {
        return timeField;
    }

    public Date getTimeFieldAsDate() {
        return new Date(timeField);
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public StockTick setTimeField( long timeField ) {
        this.timeField = timeField;
        return this;
    }

    public boolean getIsSetDueDate() {
        return null != dueDate;
    }

    public boolean getIsSetTimeField() {
        return 0 != timeField;
    }
}
