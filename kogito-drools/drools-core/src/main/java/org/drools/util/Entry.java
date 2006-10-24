/**
 * 
 */
package org.drools.util;

import java.io.Serializable;

public interface Entry extends Serializable {
    public void setNext(Entry next);

    public Entry getNext();
}