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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public final class ZoneRules implements Serializable {

    public static ZoneRules of(ZoneOffset baseStandardOffset, ZoneOffset baseWallOffset, List<ZoneOffsetTransition> standardOffsetTransitionList, List<ZoneOffsetTransition> transitionList, List<ZoneOffsetTransitionRule> lastRules) {
        return null;
    }

    public static ZoneRules of(ZoneOffset offset) {
        return null;
    }

    public boolean isFixedOffset() {
        return true;
    }

    public ZoneOffset getOffset(Instant instant) {
        return null;
    }

    public ZoneOffset getOffset(LocalDateTime localDateTime) {
        return null;
    }

    public List<ZoneOffset> getValidOffsets(LocalDateTime localDateTime) {
        return null;
    }

    public ZoneOffsetTransition getTransition(LocalDateTime localDateTime) {
        return null;
    }

    public ZoneOffset getStandardOffset(Instant instant) {
        return null;
    }

    public Duration getDaylightSavings(Instant instant) {
        return null;
    }

    public boolean isDaylightSavings(Instant instant) {
        return true;
    }

    public boolean isValidOffset(LocalDateTime localDateTime, ZoneOffset offset) {
        return true;
    }

    public ZoneOffsetTransition nextTransition(Instant instant) {
        return null;
    }

    public ZoneOffsetTransition previousTransition(Instant instant) {
        return null;
    }

    public List<ZoneOffsetTransition> getTransitions() {
        return null;
    }

    public List<ZoneOffsetTransitionRule> getTransitionRules() {
        return null;
    }

    public boolean equals(ZoneRules otherRules) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
