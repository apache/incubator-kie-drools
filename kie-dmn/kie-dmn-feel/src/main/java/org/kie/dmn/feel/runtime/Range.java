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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Range {

    enum RangeBoundary {
        OPEN, CLOSED;
    }

    RangeBoundary getLowBoundary();

    Comparable getLowEndPoint();

    Comparable getHighEndPoint();

    RangeBoundary getHighBoundary();

    Boolean includes(Object param);

    boolean isWithUndefined();


    static Comparable getStart(Range result) {
        if(result.getLowEndPoint() instanceof BigDecimal) {
            BigDecimal start = (BigDecimal) result.getLowEndPoint();
            start = result.getLowBoundary() == Range.RangeBoundary.OPEN ? start.add(BigDecimal.ONE) : start;
            return start;
        } else if (result.getLowEndPoint() instanceof LocalDate) {
            LocalDate start = (LocalDate) result.getLowEndPoint();
            start = result.getLowBoundary() == Range.RangeBoundary.OPEN ? start.plusDays(1) : start;
            return start;
        }
        return result.getLowEndPoint();
    }

    static Comparable getEnd(Range result) {
        if (result.getHighEndPoint() instanceof BigDecimal) {
            BigDecimal end = (BigDecimal) result.getHighEndPoint();
            end = result.getHighBoundary() == Range.RangeBoundary.OPEN ? end.subtract(BigDecimal.ONE) : end;
            return end;
        } else if (result.getHighEndPoint() instanceof LocalDate) {
            LocalDate end = (LocalDate) result.getHighEndPoint();
            end = result.getHighBoundary() == Range.RangeBoundary.OPEN ? end.minusDays(1) : end;
            return end;
        }
        return result.getHighEndPoint();
    }
}
