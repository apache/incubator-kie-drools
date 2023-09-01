/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.test.domain;

import java.util.Calendar;
import java.util.Date;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Duration("duration")
@Expires("60s")
public class StockTick extends StockFact {

    private long timeField;
    private Calendar dueDate;

    public StockTick(String company ) {
        super( company );
    }

    public StockTick(String company, long duration ) {
        super( company, duration );
    }

    public long getTimeFieldAsLong() {
        return timeField;
    }

    public Date getTimeFieldAsDate() {
        return new Date(timeField);
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public StockTick setTimeField( long timeField ) {
        this.timeField = timeField;
        return this;
    }

    public boolean getIsSetDueDate() {
        return null != dueDate;
    }

    public boolean getIsSetTimeField() {
        return 0 != timeField;
    }

    @Override
    public String toString() {
        return "StockTick [getCompany()=" + getCompany() + ", getDuration()=" + getDuration() + ", timeField=" + timeField + ", dueDate=" + dueDate + "]";
    }

}
