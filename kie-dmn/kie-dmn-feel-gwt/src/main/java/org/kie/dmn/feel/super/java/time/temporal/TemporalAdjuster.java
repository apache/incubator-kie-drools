package java.time.temporal;

@FunctionalInterface
public interface TemporalAdjuster{

    Temporal adjustInto(Temporal temporal);
}