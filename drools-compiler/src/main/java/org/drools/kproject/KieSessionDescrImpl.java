package org.drools.kproject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseDescr;
import org.kie.builder.KieSessionDescr;
import org.kie.runtime.conf.ClockTypeOption;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class KieSessionDescrImpl
        implements
        KieSessionDescr {
    private String                           name;

    private String                           type;
    private ClockTypeOption                  clockType;

    private List<String>                     annotations;

    private KieBaseDescrImpl kBase;
    
    private transient PropertyChangeListener listener;

    private KieSessionDescrImpl() { }

    public KieSessionDescrImpl(KieBaseDescrImpl kBase, String name) {
        this.kBase = kBase;
        this.name = name;
        this.annotations = new ArrayList<String>();
    }
    
    public KieBaseDescrImpl getKBase() {
        return kBase;
    }
    
    public void setKBase(KieBaseDescr kieBaseDescr) {
        this.kBase = (KieBaseDescrImpl) kieBaseDescr;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#setListener(java.beans.PropertyChangeListener)
     */
    public KieSessionDescr setListener(PropertyChangeListener listener) {
        this.listener = listener;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#setName(java.lang.String)
     */
    public KieSessionDescr setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#setType(java.lang.String)
     */
    public KieSessionDescr setType(String type) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "type", this.type, type ) );
        }
        this.type = type;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#getClockType()
     */
    public ClockTypeOption getClockType() {
        return clockType;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#setClockType(org.kie.runtime.conf.ClockTypeOption)
     */
    public KieSessionDescr setClockType(ClockTypeOption clockType) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "clockType", this.clockType, clockType ) );
        }
        this.clockType = clockType;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionDescr#setAnnotations(java.util.List)
     */
    public KieSessionDescr setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
        return this;
    }

    @Override
    public String toString() {
        return "KieSessionDescr [name=" + name + ", clockType=" + clockType + ", annotations=" + annotations + "]";
    }

    public static class KSessionConverter extends AbstractXStreamConverter {

        public KSessionConverter() {
            super(KieSessionDescrImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieSessionDescrImpl kSession = (KieSessionDescrImpl) value;
            writer.addAttribute("name", kSession.getName());
            writer.addAttribute("type", kSession.getType());
            if (kSession.getClockType() != null) {
                writer.addAttribute("clockType", kSession.getClockType().getClockType());
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            KieSessionDescrImpl kSession = new KieSessionDescrImpl();
            kSession.setName(reader.getAttribute("name"));
            kSession.setType(reader.getAttribute("type"));

            String clockType = reader.getAttribute("clockType");
            if (clockType != null) {
                kSession.setClockType(ClockTypeOption.get(clockType));
            }
            return kSession;
        }
    }
}