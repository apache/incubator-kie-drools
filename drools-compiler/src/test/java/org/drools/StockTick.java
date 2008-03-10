package org.drools;

public class StockTick {
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

}
