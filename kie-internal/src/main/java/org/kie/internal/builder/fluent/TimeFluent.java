package org.kie.internal.builder.fluent;

public interface TimeFluent <T> {
    T after(long duration);
    T relativeAfter(long duration);
}
