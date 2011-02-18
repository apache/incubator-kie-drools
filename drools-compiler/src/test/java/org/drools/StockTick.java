/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import java.io.Serializable;
import java.util.Date;

public class StockTick implements Serializable, StockTickInterface {
    private static final long serialVersionUID = 510l;
    
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

    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getCompany()
     */
    public String getCompany() {
        return company;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#setCompany(java.lang.String)
     */
    public void setCompany(String company) {
        this.company = company;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getPrice()
     */
    public double getPrice() {
        return price;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#setPrice(double)
     */
    public void setPrice(double price) {
        this.price = price;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getSeq()
     */
    public long getSeq() {
        return seq;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#setSeq(long)
     */
    public void setSeq(long seq) {
        this.seq = seq;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getTime()
     */
    public long getTime() {
        return time;
    }
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#setTime(long)
     */
    public void setTime(long time) {
        this.time = time;
    }

    public String toString() {
        return "StockTick( "+this.seq+" : " +this.company +" : "+ this.price +" )";
    }

    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getDuration()
     */
    public long getDuration() {
        return duration;
    }

    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#setDuration(long)
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    /* (non-Javadoc)
     * @see org.drools.StockTickInterface#getDateTimestamp()
     */
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
