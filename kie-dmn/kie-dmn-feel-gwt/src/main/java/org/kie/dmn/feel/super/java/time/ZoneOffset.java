package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.time.zone.ZoneRules;

import org.kie.dmn.model.api.GwtIncompatible;

public class ZoneOffset
        extends ZoneId{

    public static final ZoneOffset UTC = null;

    public static ZoneOffset ofTotalSeconds(int totalSeconds) {
        return null;
    }

    public static ZoneOffset ofHours(int hours) {
        return null;
    }

    public String getId() {
        return null;
    }

    public ZoneRules getRules() {
        return null;
    }

    @GwtIncompatible
    void write(final DataOutput out) throws IOException {

    }
}