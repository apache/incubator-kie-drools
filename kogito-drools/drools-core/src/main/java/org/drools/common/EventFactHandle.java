package org.drools.common;

import org.drools.FactHandle;

public class EventFactHandle extends DefaultFactHandle {

    private static final long serialVersionUID = 5997141759543399455L;

    private long              startTimestamp;
    private long              duration;
    private boolean           expired;
    private long              activationsCount;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public EventFactHandle() {
        super();
        this.startTimestamp = 0;
        this.duration = 0;
    }

    public EventFactHandle(final int id,
                           final Object object) {
        super( id,
               object );
        this.startTimestamp = 0;
        this.duration = 0;
    }

    /**
     * Construct.
     *
     * @param id
     *            Handle id.
     */
    public EventFactHandle(final int id,
                           final Object object,
                           final long recency) {
        super( id,
               object,
               recency );
        this.startTimestamp = 0;
        this.duration = 0;
    }

    /**
     * Creates a new event fact handle.
     *
     * @param id this event fact handle ID
     * @param object the event object encapsulated in this event fact handle
     * @param recency the recency of this event fact handle
     * @param timestamp the timestamp of the occurrence of this event
     * @param duration the duration of this event. May be 0 (zero) in case this is a primitive event.
     */
    public EventFactHandle(final int id,
                           final Object object,
                           final long recency,
                           final long timestamp,
                           final long duration) {
        super( id,
               object,
               recency );
        this.startTimestamp = timestamp;
        this.duration = duration;
    }

    /**
     * @see FactHandle
     */
    public String toExternalForm() {
        return "[event fid:" + getId() + ":" + getRecency() + ":" + getObject() + "]";
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    /**
     * Always returns true, since the EventFactHandle is
     * only used for Events, and not for regular Facts
     */
    public boolean isEvent() {
        return true;
    }

    /**
     * Returns the timestamp of the occurrence of this event.
     * @return
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Returns the duration of this event. In case this is a primitive event,
     * returns 0 (zero).
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the end timestamp for this event. This is the same as:
     *
     * startTimestamp + duration
     *
     * @return
     */
    public long getEndTimestamp() {
        return this.startTimestamp + this.duration;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getActivationsCount() {
        return activationsCount;
    }

    public void increaseActivationsCount() {
        this.activationsCount++;
    }

    public void decreaseActivationsCount() {
        this.activationsCount--;
    }

    public EventFactHandle clone() {
        EventFactHandle clone = new EventFactHandle( getId(),
                                                      getObject(),
                                                      getRecency(),
                                                      startTimestamp,
                                                      duration );
        clone.activationsCount = activationsCount;
        clone.expired = expired;
        clone.setEntryPoint( getEntryPoint() );
        clone.setEqualityKey( getEqualityKey() );
        clone.setLeftTuple( getLeftTuple() );
        clone.setRightTuple( getRightTuple() );
        clone.setObjectHashCode( getObjectHashCode() );
        return clone;
    }
}
