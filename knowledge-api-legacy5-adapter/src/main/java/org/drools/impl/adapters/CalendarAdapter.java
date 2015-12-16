/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.time.Calendar;

public class CalendarAdapter implements org.drools.time.Calendar {

    private final Calendar delegate;

    public CalendarAdapter(Calendar delegate) {
        this.delegate = delegate;
    }

    public boolean isTimeIncluded(long timestamp) {
        return delegate.isTimeIncluded(timestamp);
    }

    public Calendar getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CalendarAdapter && delegate.equals(((CalendarAdapter)obj).delegate);
    }
}
