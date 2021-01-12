
package java.time;

import java.io.Serializable;
import java.time.format.TextStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Locale;

public final class ZoneOffset
        extends ZoneId
        implements TemporalAccessor,
                   TemporalAdjuster,
                   Comparable<ZoneOffset>,
                   Serializable {

    public static final ZoneOffset UTC = ZoneOffset.ofTotalSeconds(0);
    public static final ZoneOffset MIN = ZoneOffset.ofTotalSeconds(-1);
    public static final ZoneOffset MAX = ZoneOffset.ofTotalSeconds(1);

    public int getTotalSeconds() {
        return 0;
    }

    public String getId() {
        return null;
    }

    public ZoneRules getRules() {
        return null;
    }

    public boolean isSupported(final TemporalField field) {
        return true;
    }

    public ValueRange range(final TemporalField field) {
        return null;
    }

    public int get(final TemporalField field) {
        return 0;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public <R> R query(final TemporalQuery<R> query) {
        return null;
    }

    public Temporal adjustInto(final Temporal temporal) {
        return null;
    }

    public int compareTo(final ZoneOffset other) {
        return 0;
    }

    public boolean equals(final Object obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public String getDisplayName(final TextStyle style, final Locale locale) {
        return null;
    }

    public ZoneId normalized() {
        return null;
    }

    public static ZoneOffset of(String offsetId) {
        return null;
    }

    public static ZoneOffset ofHours(int hours) {
        return null;
    }

    public static ZoneOffset ofHoursMinutes(int hours, int minutes) {
        return null;
    }

    public static ZoneOffset ofHoursMinutesSeconds(int hours, int minutes, int seconds) {
        return null;
    }

    public static ZoneOffset from(TemporalAccessor temporal) {
        return null;
    }

    public static ZoneOffset ofTotalSeconds(int totalSeconds) {
        return null;
    }
}
