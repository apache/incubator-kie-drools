package org.kie.internal.fluent.runtime;

public interface TimeFluent <T> {
    //T at(long time);
    T after(long duration);
    T relativeAfter(long duration);
}
