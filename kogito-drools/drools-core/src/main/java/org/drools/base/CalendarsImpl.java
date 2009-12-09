package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.runtime.Calendars;
import org.drools.runtime.Globals;
import org.drools.spi.GlobalResolver;
import org.drools.time.Calendar;

public class CalendarsImpl
    implements
    Calendars,
    Externalizable {

    private static final long serialVersionUID = 400L;

    private Map<String, Calendar> map;

    public CalendarsImpl() {
        this.map = new ConcurrentHashMap<String, Calendar>();
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map = (Map)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( map );
    }     
    
    public Calendar get(String identifier) {
        return this.map.get( identifier );
    }
    
    public void set(String identifier, Calendar calendar) {
        this.map.put( identifier, calendar );
    }

}
