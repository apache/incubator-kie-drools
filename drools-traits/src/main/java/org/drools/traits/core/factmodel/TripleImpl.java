package org.drools.traits.core.factmodel;

import org.drools.core.util.Entry;

public class TripleImpl extends AbstractTriple {
    private Entry next;
    
    private Object instance;
    private Object property;
    private Object value;

    protected TripleImpl(Object instance,
                  String property,
                  Object value) {
        super();
        this.instance = instance;
        this.property = property;
        this.value = value;
    }

    protected TripleImpl(Object instance,
                  Object property,
                  Object value) {
        super();
        this.instance = instance;
        this.property = property;
        this.value = value;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getInstance()
     */
    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getProperty()
     */
    public Object getProperty() {
        return property;
    }

    public void setProperty(Object property) {
        this.property = property;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getValue()
     */
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "Triple [instance=" + instance + ", property=" + property + ", value=" + value + "]";
    }
 
}
