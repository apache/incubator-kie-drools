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
