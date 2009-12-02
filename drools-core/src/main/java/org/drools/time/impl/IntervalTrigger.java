/**
 * 
 */
package org.drools.time.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.time.Trigger;

public class IntervalTrigger
    implements
    Trigger {
    private Date next;
    private long period;

    public IntervalTrigger() {

    }

    public IntervalTrigger(long currentTS,
                           long delay,
                           long period) {
        this.next = new Date( currentTS + delay );
        this.period = period;
    }

    public Date hasNextFireTime() {
        return next;
    }

    public Date nextFireTime() {
        Date date = next;
        if ( this.period != 0 ) {
            // repeated fires for the given period
            // FIXME: this is not safe for serialization
            next = new Date( next.getTime() + this.period );
        } else {
            next = null;
        }
        return date;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.next = (Date) in.readObject();
        this.period = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.next );
        out.writeLong( this.period );
    }

}