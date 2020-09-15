/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.domain;

import java.util.Calendar;
import java.util.Date;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Duration("duration")
public class StockTickEx extends StockFact {

    private long timeFieldEx;
    private Calendar dueDate;

    public StockTickEx( String company ) {
        super( company );
    }

    public StockTickEx( String company, long duration ) {
        super( company, duration );
    }

    public long getTimeFieldExAsLong() {
        return timeFieldEx;
    }

    public Date getTimeFieldExAsDate() {
        return new Date(timeFieldEx);
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public StockTickEx setTimeFieldEx( long timeFieldEx ) {
        this.timeFieldEx = timeFieldEx;
        return this;
    }

    public boolean getIsSetDueDate() {
        return null != dueDate;
    }

    public boolean getIsSetTimeFieldEx() {
        return 0 != timeFieldEx;
    }
}
