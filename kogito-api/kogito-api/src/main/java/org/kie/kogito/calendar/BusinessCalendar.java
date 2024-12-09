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
package org.kie.kogito.calendar;

import java.util.Date;

/**
 * BusinessCalendar allows for defining custom definitions of working days, hours and holidays
 * to be taken under consideration when scheduling time based activities such as timers or deadlines.
 */
public interface BusinessCalendar {

    /**
     * Returns the difference, in milliseconds, between the <b>business date</b> that matches the given
     * <code>timeExpression</code>, and the current time.
     * See {@link #calculateBusinessTimeAsDate} for <b>business date</b> calculation
     *
     * @param timeExpression time expression that is supported by business calendar implementation.
     * @return duration expressed in milliseconds
     */
    long calculateBusinessTimeAsDuration(String timeExpression);

    /**
     * Returns the first <code>Date</code> that matches the given <code>timeExpression</code> and falls
     * into the business calendar working hours.
     * 
     * @param timeExpression time expression that is supported by business calendar implementation.
     * @return date when given time expression will match in the future
     */
    Date calculateBusinessTimeAsDate(String timeExpression);
}
