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
package java.time.format;

import java.util.Calendar;

public enum TextStyle {

    FULL(Calendar.LONG_FORMAT, 0),
    FULL_STANDALONE(Calendar.LONG_STANDALONE, 0),
    SHORT(Calendar.SHORT_FORMAT, 1),
    SHORT_STANDALONE(Calendar.SHORT_STANDALONE, 1),
    NARROW(Calendar.NARROW_FORMAT, 1),
    NARROW_STANDALONE(Calendar.NARROW_STANDALONE, 1);

    private TextStyle(int calendarStyle, int zoneNameStyleIndex) {
    }

    public boolean isStandalone() {
        return (ordinal() & 1) == 1;
    }

    public TextStyle asStandalone() {
        return TextStyle.values()[ordinal() | 1];
    }

    public TextStyle asNormal() {
        return TextStyle.values()[ordinal() & ~1];
    }

    int toCalendarStyle() {
        return 0;
    }

    int zoneNameStyleIndex() {
        return 0;
    }
}
