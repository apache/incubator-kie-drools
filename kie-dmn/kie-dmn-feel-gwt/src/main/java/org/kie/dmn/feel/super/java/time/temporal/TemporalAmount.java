
package java.time.temporal;

import java.util.List;

public interface TemporalAmount {

    List<TemporalUnit> getUnits();

    long get(TemporalUnit unit);

    Temporal addTo(Temporal temporal);

    Temporal subtractFrom(Temporal temporal);
}
