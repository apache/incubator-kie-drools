package org.kie.api.runtime.rule;

public interface EventHandle extends FactHandle {

    long getStartTimestamp();

    long getDuration();

    long getEndTimestamp();

    boolean isExpired();

}
