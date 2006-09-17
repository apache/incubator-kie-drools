/**
 * 
 */
package org.drools.util;

public class BaseEntry
    implements
    Entry {
    private Entry next;

    public void setNext(Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }
}