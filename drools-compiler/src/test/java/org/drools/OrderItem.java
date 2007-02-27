/*
 * Copyright 2006 JBoss Inc
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

/**
 * @author etirelli
 *
 */
public class OrderItem {
    private int seq;
    private Order order;
    
    public OrderItem() {
        this( null, 0 );
    }
    
    public OrderItem( Order order, int seq ) {
        this.order = order;
        this.seq = seq;
    }
    /**
     * @return the order
     */
    public Order getOrder() {
        return order;
    }
    /**
     * @param order the order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }
    /**
     * @return the seq
     */
    public int getSeq() {
        return seq;
    }
    /**
     * @param seq the seq to set
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((order == null) ? 0 : order.hashCode());
        result = PRIME * result + seq;
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final OrderItem other = (OrderItem) obj;
        if ( order == null ) {
            if ( other.order != null ) return false;
        } else if ( !order.equals( other.order ) ) return false;
        if ( seq != other.seq ) return false;
        return true;
    }
    
    

}
