package org.drools.builder.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.builder.DateFormats;

public class DateFormatsImpl
    implements
    DateFormats,
    Externalizable {

    private static final long       serialVersionUID = 400L;

    private Map<String, DateFormat> map;
    
    private static ThreadLocal<Map<String, DateFormat>> localMap = new ThreadLocal<Map<String, DateFormat>>() {
        protected Map<String, DateFormat> initialValue() {;
            return new HashMap<String, DateFormat>();
        };
    };    
    
    /**
     * This is here for any static classes, such as MVEL DataConverters, to access.
     * It is expected this will be set, before access.
     */
    public static ThreadLocal<DateFormats> dateFormats = new ThreadLocal<DateFormats>() {

    };      

    public DateFormatsImpl() {
        this.map = new ConcurrentHashMap<String, DateFormat>();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        map = (Map) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( map );
    }

    public DateFormat get(String identifier) {
        return this.map.get( identifier );
    }

    public void set(String identifier,
                    DateFormat calendar) {
        this.map.put( identifier,
                      calendar );
    }

    public Date parse(String identifier,
                      String date) {
        
        // DateFormat's are not thread safe, so always keep a local thread copy
        DateFormat df = this.localMap.get().get( identifier );
        if ( df == null ) {
            df = this.map.get( identifier );
            if ( df == null ) {
                throw new RuntimeException( "Unable to find DateFormat for id '" + identifier + "'" );
            }
            this.localMap.get().put( identifier, ( DateFormat ) df.clone() );
        }
        
        try {
            return df.parse( date );
        } catch ( ParseException e ) {
            throw new IllegalArgumentException("Invalid date input format: ["
                                               + date + "] using the DateFormat: [" + df + "]");
        }
    }

}
