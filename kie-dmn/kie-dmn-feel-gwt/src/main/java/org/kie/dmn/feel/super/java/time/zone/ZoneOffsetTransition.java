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

public final class ZoneOffsetTransition
        implements Comparable<ZoneOffsetTransition>,
                   Serializable {

    public Instant getInstant() {
        return null;
    }

    public long toEpochSecond() {
        return 0L;
    }

    public LocalDateTime getDateTimeBefore() {
        return null;
    }

    public LocalDateTime getDateTimeAfter() {
        return null;
    }

    public ZoneOffset getOffsetBefore() {
        return null;
    }

    public ZoneOffset getOffsetAfter() {
        return null;
    }

    public Duration getDuration() {
        return null;
    }

    public boolean isGap() {
        return true;
    }

    public boolean isOverlap() {
        return true;
    }

    public boolean isValidOffset(final ZoneOffset offset) {
        return true;
    }

    public int compareTo(final ZoneOffsetTransition transition) {
        return 0;
    }

    public boolean equals(final ZoneOffsetTransition other) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
