package org.drools;

import java.util.Date;

public interface StockTickInterface {

    public abstract String getCompany();

    public abstract void setCompany(String company);

    public abstract double getPrice();

    public abstract void setPrice(double price);

    public abstract long getSeq();

    public abstract void setSeq(long seq);

    public abstract long getTime();

    public abstract void setTime(long time);

    /**
     * @return the duration
     */
    public abstract long getDuration();

    /**
     * @param duration the duration to set
     */
    public abstract void setDuration(long duration);

    public abstract Date getDateTimestamp();

}