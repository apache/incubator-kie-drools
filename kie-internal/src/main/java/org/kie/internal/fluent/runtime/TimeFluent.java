package org.kie.internal.fluent.runtime;

public interface TimeFluent <T> {
    T after(long duration);
    T relativeAfter(long duration);
}
