/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.type;

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


public class DateFormatsImpl
    implements
    DateFormats,
    Externalizable {

    private static final long       serialVersionUID = 510l;

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
