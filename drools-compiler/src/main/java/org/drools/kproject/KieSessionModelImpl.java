package org.drools.kproject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.ListenerModel;
import org.kie.builder.WorkItemHandelerModel;
import org.kie.runtime.conf.ClockTypeOption;

import java.util.ArrayList;
import java.util.List;

public class KieSessionModelImpl
        implements
        KieSessionModel {
    private String                           name;

    private String                           type;
    private ClockTypeOption                  clockType;

    private KieBaseModelImpl kBase;

    private final List<ListenerModel> listeners = new ArrayList<ListenerModel>();
    private final List<WorkItemHandelerModel> wihs = new ArrayList<WorkItemHandelerModel>();

    private KieSessionModelImpl() { }

    public KieSessionModelImpl(KieBaseModelImpl kBase, String name) {
        this.kBase = kBase;
        this.name = name;
    }
    
    public KieBaseModelImpl getKieBaseModel() {
        return kBase;
    }
    
    public void setKBase(KieBaseModel kieBaseModel) {
        this.kBase = (KieBaseModelImpl) kieBaseModel;
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
        this.clockType = clockType;
        return this;
    }

    public ListenerModel newListenerModel(String type) {
        ListenerModelImpl listenerModel = new ListenerModelImpl(this, type);
        listeners.add(listenerModel);
        return listenerModel;
    }

    public List<ListenerModel> getListenerModels() {
        return listeners;
    }

    private void addListenerModel(ListenerModel listener) {
        listeners.add(listener);
    }

    public WorkItemHandelerModel newWorkItemHandelerModel(String type) {
        WorkItemHandelerModelImpl wihModel = new WorkItemHandelerModelImpl(this, type);
        wihs.add(wihModel);
        return wihModel;
    }

    public List<WorkItemHandelerModel> getWorkItemHandelerModels() {
        return wihs;
    }

    private void addWorkItemHandelerModel(WorkItemHandelerModel wih) {
        wihs.add(wih);
    }

    @Override
    public String toString() {
        return "KieSessionModel [name=" + name + ", clockType=" + clockType + "]";
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
            for (ListenerModel listener : kSession.getListenerModels()) {
                writeObject(writer, context, "listener", listener);
            }
            for (WorkItemHandelerModel wih : kSession.getWorkItemHandelerModels()) {
                writeObject(writer, context, "workItemHandeler", wih);
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieSessionModelImpl kSession = new KieSessionModelImpl();
            kSession.setName(reader.getAttribute("name"));
            kSession.setType(reader.getAttribute("type"));

            String clockType = reader.getAttribute("clockType");
            if (clockType != null) {
                kSession.setClockType(ClockTypeOption.get(clockType));
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "listener".equals( name ) ) {
                        ListenerModelImpl listener = readObject(reader, context, ListenerModelImpl.class);
                        listener.setKSession( kSession );
                        kSession.addListenerModel(listener);
                    } else if ( "workItemHandeler".equals( name ) ) {
                        WorkItemHandelerModelImpl wih = readObject(reader, context, WorkItemHandelerModelImpl.class);
                        wih.setKSession( kSession );
                        kSession.addWorkItemHandelerModel(wih);
                    }
                }
            } );
            return kSession;
        }
    }
}