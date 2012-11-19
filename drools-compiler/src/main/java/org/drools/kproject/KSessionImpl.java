package org.drools.kproject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.conf.AssertBehaviorOption;
import org.kie.runtime.conf.ClockTypeOption;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class KSessionImpl
        implements
        KSession {
    private String                           namespace;
    private String                           name;

    private String                           type;
    private ClockTypeOption                  clockType;

    private List<String>                     annotations;

    private KBaseImpl                        kBase;
    
    private transient PropertyChangeListener listener;

    private KSessionImpl() { }

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
    
    public void setKBase(KBase kBase) {
        this.kBase = ( KBaseImpl ) kBase;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setListener(java.beans.PropertyChangeListener)
     */
    public KSession setListener(PropertyChangeListener listener) {
        this.listener = listener;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getNamespace()
     */
    public String getNamespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setNamespace(java.lang.String)
     */
    public KSession setNamespace(String namespace) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "namespace", this.namespace, namespace ) );
        }
        this.namespace = namespace;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setName(java.lang.String)
     */
    public KSession setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getQName()
     */
    public String getQName() {
        return this.namespace + "." + this.name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setType(java.lang.String)
     */
    public KSession setType(String type) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "type", this.type, type ) );
        }
        this.type = type;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getClockType()
     */
    public ClockTypeOption getClockType() {
        return clockType;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setClockType(org.kie.runtime.conf.ClockTypeOption)
     */
    public KSession setClockType(ClockTypeOption clockType) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "clockType", this.clockType, clockType ) );
        }
        this.clockType = clockType;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KSession#setAnnotations(java.util.List)
     */
    public KSession setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
        return this;
    }

    @Override
    public String toString() {
        return "KSession [namespace=" + namespace + ", name=" + name + ", clockType=" + clockType + ", annotations=" + annotations + "]";
    }

    public static class KSessionConverter extends AbstractXStreamConverter {

        public KSessionConverter() {
            super(KSessionImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KSessionImpl kSession = (KSessionImpl) value;
            writer.addAttribute("name", kSession.getName());
            writer.addAttribute("namespace", kSession.getNamespace());
            writer.addAttribute("type", kSession.getType());
            if (kSession.getClockType() != null) {
                writer.addAttribute("clockType", kSession.getClockType().getClockType());
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            KSessionImpl kSession = new KSessionImpl();
            kSession.setName(reader.getAttribute("name"));
            kSession.setNamespace(reader.getAttribute("namespace"));
            kSession.setType(reader.getAttribute("type"));

            String clockType = reader.getAttribute("clockType");
            if (clockType != null) {
                kSession.setClockType(ClockTypeOption.get(clockType));
            }
            return kSession;
        }
    }
}