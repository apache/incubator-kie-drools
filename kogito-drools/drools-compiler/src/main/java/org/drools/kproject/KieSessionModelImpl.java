package org.drools.kproject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.conf.ClockTypeOption;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class KieSessionModelImpl
        implements
        KieSessionModel {
    private String                           name;

    private String                           type;
    private ClockTypeOption                  clockType;

    private List<String>                     annotations;

    private KieBaseModelImpl kBase;
    
    private transient PropertyChangeListener listener;

    private KieSessionModelImpl() { }

    public KieSessionModelImpl(KieBaseModelImpl kBase, String name) {
        this.kBase = kBase;
        this.name = name;
        this.annotations = new ArrayList<String>();
    }
    
    public KieBaseModelImpl getKBase() {
        return kBase;
    }
    
    public void setKBase(KieBaseModel kieBaseModel) {
        this.kBase = (KieBaseModelImpl) kieBaseModel;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setListener(java.beans.PropertyChangeListener)
     */
    public KieSessionModel setListener(PropertyChangeListener listener) {
        this.listener = listener;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setName(java.lang.String)
     */
    public KieSessionModel setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setType(java.lang.String)
     */
    public KieSessionModel setType(String type) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "type", this.type, type ) );
        }
        this.type = type;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getClockType()
     */
    public ClockTypeOption getClockType() {
        return clockType;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setClockType(org.kie.runtime.conf.ClockTypeOption)
     */
    public KieSessionModel setClockType(ClockTypeOption clockType) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "clockType", this.clockType, clockType ) );
        }
        this.clockType = clockType;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setAnnotations(java.util.List)
     */
    public KieSessionModel setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
        return this;
    }

    @Override
    public String toString() {
        return "KieSessionModel [name=" + name + ", clockType=" + clockType + ", annotations=" + annotations + "]";
    }

    public static class KSessionConverter extends AbstractXStreamConverter {

        public KSessionConverter() {
            super(KieSessionModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieSessionModelImpl kSession = (KieSessionModelImpl) value;
            writer.addAttribute("name", kSession.getName());
            writer.addAttribute("type", kSession.getType());
            if (kSession.getClockType() != null) {
                writer.addAttribute("clockType", kSession.getClockType().getClockType());
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            KieSessionModelImpl kSession = new KieSessionModelImpl();
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