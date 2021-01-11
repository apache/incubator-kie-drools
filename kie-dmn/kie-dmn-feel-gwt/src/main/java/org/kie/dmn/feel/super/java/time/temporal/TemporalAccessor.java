
package java.time.temporal;

public interface TemporalAccessor {

    boolean isSupported(TemporalField field);

    default int get(TemporalField field) {
        return 0;
    }

    default <R> R query(TemporalQuery<R> query) {
        return null;
    }
}
