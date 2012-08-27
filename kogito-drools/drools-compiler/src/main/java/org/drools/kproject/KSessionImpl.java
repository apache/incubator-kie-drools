package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drools.ClockType;
import org.drools.runtime.conf.ClockTypeOption;

public class KSessionImpl
        implements
        KSession {
    private String                           namespace;
    private String                           name;

    private String                           type;
    private ClockTypeOption                  clockType;

    private List<String>                     annotations;

    private KBaseImpl                            kBase;
    
    private transient PropertyChangeListener listener;

    public KSessionImpl(KBaseImpl kBase,
                        String namespace,
                        String name) {
        this.kBase = kBase;
        this.namespace = namespace;
        this.name = name;
        this.annotations = new ArrayList<String>();
    }
    
    public KBaseImpl getKBase() {
        return kBase;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setListener(java.beans.PropertyChangeListener)
     */
    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getNamespace()
     */
    public String getNamespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setNamespace(java.lang.String)
     */
    public void setNamespace(String namespace) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "namespace", this.namespace, namespace ) );
        }
        this.namespace = namespace;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setName(java.lang.String)
     */
    public void setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getQName()
     */
    public String getQName() {
        return this.namespace + "." + this.name;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setType(java.lang.String)
     */
    public void setType(String type) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "type", this.type, type ) );
        }
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getClockType()
     */
    public ClockTypeOption getClockType() {
        return clockType;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setClockType(org.drools.runtime.conf.ClockTypeOption)
     */
    public void setClockType(ClockTypeOption clockType) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "clockType", this.clockType, clockType ) );
        }
        this.clockType = clockType;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KSession#setAnnotations(java.util.List)
     */
    public void setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "KSession [namespace=" + namespace + ", name=" + name + ", clockType=" + clockType + ", annotations=" + annotations + "]";
    }

}
