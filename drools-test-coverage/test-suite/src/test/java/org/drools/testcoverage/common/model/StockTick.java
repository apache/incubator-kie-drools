package org.drools.testcoverage.common.model;

import java.io.Serializable;
import java.util.Date;

public class StockTick implements Serializable {

    private static final long serialVersionUID = 510L;

    private long seq;
    private String company;
    private double price;
    private long time;
    private long duration;

    public StockTick() {
    }

    public StockTick(final long seq,
                     final String company,
                     final double price) {
        this(seq, company, price, System.currentTimeMillis());
    }

    public StockTick(final long seq,
                     final String company,
                     final double price,
                     final long time) {
        super();
        this.seq = seq;
        this.company = company;
        this.price = price;
        this.time = time;
    }

    public StockTick(final long seq,
                     final String company,
                     final double price,
                     final long time,
                     final long duration) {
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
    public void setCompany(final String company) {
        this.company = company;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(final double price) {
        this.price = price;
    }

    public long getSeq() {
        return seq;
    }
    public void setSeq(final long seq) {
        this.seq = seq;
    }

    public long getTime() {
        return time;
    }
    public void setTime(final long time) {
        this.time = time;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public Date getDateTimestamp() {
        return new Date(this.time);
    }

    public String toString() {
        return "StockTick( " + this.seq + " : " + this.company + " : " + this.price + " )";
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StockTick other = (StockTick) obj;
        if (company == null) {
            if (other.company != null) {
                return false;
            }
        } else if (!company.equals(other.company)) {
            return false;
        }
        if (seq != other.seq) {
            return false;
        }
        return time == other.time;
    }
}
