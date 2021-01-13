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
package java.time;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public final class YearMonth implements Temporal {

    public boolean isSupported(final TemporalUnit unit) {
        return false;
    }

    public Temporal with(final TemporalField field, final long newValue) {
        return null;
    }

    public Temporal plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0;
    }

    public boolean isSupported(final TemporalField field) {
        return false;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public int getYear() {
        return 0;
    }

    public Month getMonth() {
        return null;
    }

    public YearMonth minus(TemporalAmount amountToSubtract) {
        return null;
    }
}