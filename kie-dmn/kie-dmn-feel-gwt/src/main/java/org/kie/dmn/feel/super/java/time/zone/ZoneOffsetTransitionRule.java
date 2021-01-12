/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.time.zone;

import java.io.Serializable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

public final class ZoneOffsetTransitionRule implements Serializable {

    public Month getMonth() {
        return null;
    }

    public int getDayOfMonthIndicator() {
        return 0;
    }

    public DayOfWeek getDayOfWeek() {
        return null;
    }

    public LocalTime getLocalTime() {
        return null;
    }

    public boolean isMidnightEndOfDay() {
        return true;
    }

    public TimeDefinition getTimeDefinition() {
        return null;
    }

    public ZoneOffset getStandardOffset() {
        return null;
    }

    public ZoneOffset getOffsetBefore() {
        return null;
    }

    public ZoneOffset getOffsetAfter() {
        return null;
    }

    public ZoneOffsetTransition createTransition(final int year) {
        return null;
    }

    public boolean equals(final ZoneOffsetTransitionRule otherRule) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public enum TimeDefinition {
        UTC,
        WALL,
        STANDARD;
    }
}