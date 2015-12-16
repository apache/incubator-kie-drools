/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
        this.map = new ConcurrentHashMap<String, Calendar>();
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
