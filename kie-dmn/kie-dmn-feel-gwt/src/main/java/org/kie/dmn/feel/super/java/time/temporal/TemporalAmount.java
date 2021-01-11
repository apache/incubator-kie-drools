
package java.time.temporal;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.util.List;


public interface TemporalAmount {

    List<TemporalUnit> getUnits();

    long get(TemporalUnit unit);

    Temporal addTo(Temporal temporal);

    Temporal subtractFrom(Temporal temporal);

}
