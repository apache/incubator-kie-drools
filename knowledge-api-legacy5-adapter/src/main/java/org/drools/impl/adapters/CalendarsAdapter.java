/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.time.Calendar;
import org.kie.api.runtime.Calendars;

public class CalendarsAdapter implements org.drools.runtime.Calendars {

    private final Calendars delegate;

    public CalendarsAdapter(Calendars delegate) {
        this.delegate = delegate;
    }

    public Calendar get(String identifier) {
        return new CalendarAdapter(delegate.get(identifier));
    }

    @Override
    public void set(String identifier, Calendar value) {
        delegate.set(identifier, ((CalendarAdapter)value).getDelegate());
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CalendarsAdapter && delegate.equals(((CalendarsAdapter)obj).delegate);
    }
}
