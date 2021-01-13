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

import java.io.Serializable;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.zone.ZoneRules;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class ZoneId implements Serializable {

    public static final Map<String, String> SHORT_IDS = new HashMap<>();

    public String getId() {
        return null;
    }

    public String getDisplayName(final TextStyle style, final Locale locale) {
        return "";
    }

    public ZoneRules getRules() {
        return null;
    }

    public ZoneId normalized() {
        return null;
    }

    public boolean equals(final ZoneId obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "";
    }

    public static ZoneId systemDefault() {
        return null;
    }

    public static Set<String> getAvailableZoneIds() {
        return null;
    }

    public static ZoneId of(String zoneId, Map<String, String> aliasMap) {
        return null;
    }

    public static ZoneId of(String zoneId) {
        return null;
    }

    public static ZoneId ofOffset(String prefix, ZoneOffset offset) {
        return null;
    }

    public static ZoneId from(TemporalAccessor temporal) {
        return null;
    }
}
