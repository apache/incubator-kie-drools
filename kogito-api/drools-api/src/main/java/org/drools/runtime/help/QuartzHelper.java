package org.drools.runtime.help;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.time.Calendar;

public class QuartzHelper {

    public static Calendar quartzCalendarAdapter(org.quartz.Calendar calendar) {
        return new QuartzCalendarAdapter( calendar );
    }

    public static class QuartzCalendarAdapter
        implements
        Calendar,
        Externalizable {
        private org.quartz.Calendar calendar;

        public QuartzCalendarAdapter(org.quartz.Calendar calendar) {
            this.calendar = calendar;
        }

        public boolean isTimeIncluded(long timestamp) {
            return this.calendar.isTimeIncluded( timestamp );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.calendar = (org.quartz.Calendar) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.calendar );
        }

    }

}
