package java.time.chrono;

public interface ChronoZonedDateTime<D extends ChronoLocalDate> {

    default long toEpochSecond() {
        return 0;
    }
}