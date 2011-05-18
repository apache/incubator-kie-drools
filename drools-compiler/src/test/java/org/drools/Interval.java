package org.drools;

public class Interval {
    private long start;
    private long duration;
    
    public Interval(long start,
                    long duration) {
        super();
        this.start = start;
        this.duration = duration;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart( long start ) {
        this.start = start;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration( long duration ) {
        this.duration = duration;
    }
    
    public boolean isAfter( Interval i ) {
        return start > (i.start+i.duration);
    }

}
