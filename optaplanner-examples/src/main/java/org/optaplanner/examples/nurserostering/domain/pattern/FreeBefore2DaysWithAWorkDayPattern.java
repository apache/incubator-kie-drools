/*
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

package org.optaplanner.examples.nurserostering.domain.pattern;

import java.time.DayOfWeek;

public class FreeBefore2DaysWithAWorkDayPattern extends Pattern {

    private DayOfWeek freeDayOfWeek;

    public FreeBefore2DaysWithAWorkDayPattern() {
    }

    public FreeBefore2DaysWithAWorkDayPattern(long id, String code) {
        super(id, code);
    }

    public FreeBefore2DaysWithAWorkDayPattern(long id, String code, DayOfWeek freeDayOfWeek) {
        this(id, code);
        this.freeDayOfWeek = freeDayOfWeek;
    }

    public DayOfWeek getFreeDayOfWeek() {
        return freeDayOfWeek;
    }

    public void setFreeDayOfWeek(DayOfWeek freeDayOfWeek) {
        this.freeDayOfWeek = freeDayOfWeek;
    }

    @Override
    public String toString() {
        return "Free on " + freeDayOfWeek + " followed by a work day within 2 days";
    }

}
