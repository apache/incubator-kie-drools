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
package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

public final class TimeUtil {

    private TimeUtil() { }

    public static long unitToLong( long value, TimeUnit unit ) {
        if (unit == null) {
            return value;
        }
        switch (unit) {
            case DAYS: value *= 24;
            case HOURS: value *= 60;
            case MINUTES: value *= 60;
            case SECONDS: value *= 1000;
            case MILLISECONDS: return value;
        }
        throw new IllegalArgumentException( "Time Unit " + unit + " is not supported" );
    }
}
