package org.drools;

import java.io.Serializable;
import java.util.Date;

public class StockTick implements Serializable {
    private static final long serialVersionUID = -1702366432018395425L;
    
    private long seq;
    private String company;
    private double price;
    private long time;
    private long duration;
    
    public StockTick() {
    }

    public StockTick(long seq,
                     String company,
                     double price,
                     long time) {
        super();
        this.seq = seq;
        this.company = company;
        this.price = price;
        this.time = time;
    }

    public StockTick(long seq,
                     String company,
                     double price,
                     long time, 
                     long duration ) {
        super();
        this.seq = seq;
        this.company = company;
        this.price = price;
        this.time = time;
        this.duration = duration;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public long getSeq() {
        return seq;
    }
    public void setSeq(long seq) {
        this.seq = seq;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public String toString() {
        return "StockTick( "+this.seq+" : " +this.company +" : "+ this.price +" )";
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
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public Date getDateTimestamp() {
        return new Date( this.time );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result + (int) (seq ^ (seq >>> 32));
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        StockTick other = (StockTick) obj;
        if ( company == null ) {
            if ( other.company != null ) return false;
        } else if ( !company.equals( other.company ) ) return false;
        if ( seq != other.seq ) return false;
        if ( time != other.time ) return false;
        return true;
    }
    

}
