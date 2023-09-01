package org.kie.dmn.feel.runtime;

public interface Range {

    static enum RangeBoundary {
        OPEN, CLOSED;
    }

    RangeBoundary getLowBoundary();

    Comparable getLowEndPoint();

    Comparable getHighEndPoint();

    RangeBoundary getHighBoundary();

    Boolean includes(Object param);

}
