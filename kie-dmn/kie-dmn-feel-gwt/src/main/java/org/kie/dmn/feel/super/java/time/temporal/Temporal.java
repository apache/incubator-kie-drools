package java.time.temporal;

public interface Temporal extends TemporalAccessor{

    Temporal minus(TemporalAmount amount);
}