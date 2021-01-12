
package java.time.temporal;

public interface TemporalAccessor {

    boolean isSupported(TemporalField field);

    default ValueRange range(TemporalField field) {
        return null;
    }

    default int get(TemporalField field) {
        return 0;
    }

    long getLong(TemporalField field);

    default <R> R query(TemporalQuery<R> query) {
        return null;
    }
}
