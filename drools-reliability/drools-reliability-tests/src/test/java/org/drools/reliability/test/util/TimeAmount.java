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
package org.drools.reliability.test.util;

import java.util.concurrent.TimeUnit;

/**
 * Copied from org.drools.ansible.rulebook.integration.api.domain.temporal.TimeAmount
 */
public class TimeAmount {
    private final int amount;
    private final TimeUnit timeUnit;

    public TimeAmount(int amount, TimeUnit timeUnit) {
        this.amount = amount;
        this.timeUnit = timeUnit;
    }

    public int getAmount() {
        return amount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public String toString() {
        return amount + " " + timeUnit;
    }

    public static TimeAmount parseTimeAmount(String timeAmount) {
        int sepPos = timeAmount.indexOf(' ');
        if (sepPos <= 0) {
            throw new IllegalArgumentException("Invalid time amount definition: " + timeAmount);
        }
        int value = Integer.parseInt(timeAmount.substring(0, sepPos).trim());
        TimeUnit timeUnit = parseTimeUnit(timeAmount.substring(sepPos + 1).trim());
        return new TimeAmount(value, timeUnit);
    }

    private static TimeUnit parseTimeUnit(String unit) {
        if (unit.equalsIgnoreCase("millisecond") || unit.equalsIgnoreCase("milliseconds")) {
            return TimeUnit.MILLISECONDS;
        }
        if (unit.equalsIgnoreCase("second") || unit.equalsIgnoreCase("seconds")) {
            return TimeUnit.SECONDS;
        }
        if (unit.equalsIgnoreCase("minute") || unit.equalsIgnoreCase("minutes")) {
            return TimeUnit.MINUTES;
        }
        if (unit.equalsIgnoreCase("hour") || unit.equalsIgnoreCase("hours")) {
            return TimeUnit.HOURS;
        }
        if (unit.equalsIgnoreCase("day") || unit.equalsIgnoreCase("days")) {
            return TimeUnit.DAYS;
        }
        throw new IllegalArgumentException("Unknown time unit: " + unit);
    }

    public long toMillis() {
        return timeUnit.toMillis(amount);
    }
}
