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

package java.time.temporal;

import java.io.Serializable;

public final class ValueRange implements Serializable {

    public boolean isFixed() {
        return true;
    }

    public long getMinimum() {
        return 0L;
    }

    public long getLargestMinimum() {
        return 0L;
    }

    public long getSmallestMaximum() {
        return 0L;
    }

    public long getMaximum() {
        return 0L;
    }

    public boolean isIntValue() {
        return true;
    }

    public boolean isValidValue(final long value) {
        return true;
    }

    public boolean isValidIntValue(final long value) {
        return true;
    }

    public long checkValidValue(final long value, final TemporalField field) {
        return 0L;
    }

    public int checkValidIntValue(final long value, final TemporalField field) {
        return 0;
    }

    public boolean equals(final ValueRange obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
