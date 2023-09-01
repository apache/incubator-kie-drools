package org.drools.core.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.Calendars;
import org.kie.api.time.Calendar;

public class CalendarsImpl
    implements
    Calendars,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private Map<String, Calendar> map;

    public CalendarsImpl() {
        this.map = new ConcurrentHashMap<>();
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map = (Map)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( map );
    }
    
    public Calendar get(String identifier) {
        Calendar calendar = this.map.get( identifier );
        if (calendar == null) {
            throw new UndefinedCalendarExcption(identifier);
        }
        return calendar;
    }
    
    public void set(String identifier, Calendar calendar) {
        this.map.put( identifier, calendar );
    }

}
