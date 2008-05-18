/**
 *
 */
package org.drools.util;

import java.io.Externalizable;

public interface Entry {
    public void setNext(Entry next);

    public Entry getNext();
}