/**
 *
 */
package org.drools.util;

import java.io.Externalizable;

public interface Entry
    extends
    Externalizable {
    public void setNext(Entry next);

    public Entry getNext();
}