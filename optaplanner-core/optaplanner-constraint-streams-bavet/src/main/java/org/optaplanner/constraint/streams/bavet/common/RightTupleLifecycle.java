package org.optaplanner.constraint.streams.bavet.common;

public interface RightTupleLifecycle<Tuple_ extends Tuple> {

    void insertRight(Tuple_ tuple);

    void updateRight(Tuple_ tuple);

    void retractRight(Tuple_ tuple);

}
