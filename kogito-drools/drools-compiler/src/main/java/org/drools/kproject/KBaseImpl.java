package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.conf.ClockTypeOption;

public class KBaseImpl
        implements
        KBase {
    private String                           namespace;

    private String                           name;
    
    private Set<String>                      includes;

    private List<String>                     files;

    private List<String>                     annotations;

    private AssertBehaviorOption             equalsBehavior;

    private EventProcessingOption            eventProcessingMode;

    private Map<String, KSession>            kSessions;

    private KProjectImpl                     kProject;

    private transient PropertyChangeListener listener;

    private KBaseImpl() {
        this.includes = new HashSet<String>();
        this.files = new ArrayList<String>();
    }

    public KBaseImpl(KProjectImpl kProject,
                     String namespace,
                     String name) {
        this.kProject = kProject;
        this.namespace = namespace;
        this.includes = new HashSet<String>();
        this.name = name;
        this.files = new ArrayList<String>();
        this.kSessions = Collections.emptyMap();
    }

    public KProjectImpl getKProject() {
        return kProject;
    }
    
    public void setKProject(KProject kProject) {
        this.kProject = (KProjectImpl) kProject;
    }    

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getKSessions()
     */
    public Map<String, KSession> getKSessions() {
        return Collections.unmodifiableMap( kSessions );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setKSessions(java.util.Map)
     */
    private void setKSessions(Map<String, KSession> kSessions) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kSessions", this.kSessions, kSessions ) );
            for ( KSession ksession : kSessions.values() ) {
                // make sure the listener is set for each ksession
                ksession.setListener( listener );
            }
        }
        this.kSessions = kSessions;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#addKSession(org.kie.kproject.KSessionImpl)
     */
    public KSession newKSession(String namespace,
                                String name) {
        KSession kSession = new KSessionImpl( this, namespace, name );
        Map<String, KSession> newMap = new HashMap<String, KSession>();
        newMap.putAll( this.kSessions );
        newMap.put( kSession.getQName(), kSession );
        setKSessions( newMap );

        return kSession;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#removeKSession(org.kie.kproject.KSessionImpl)
     */
    public KBase removeKSession(String qName) {
        Map<String, KSession> newMap = new HashMap<String, KSession>();
        newMap.putAll( this.kSessions );
        newMap.remove( qName );
        setKSessions( newMap );
        return this;
    }

    public void moveKSession(String oldQName,
                             String newQName) {
        Map<String, KSession> newMap = new HashMap<String, KSession>();
        newMap.putAll( this.kSessions );
        KSession kSession = newMap.remove( oldQName );
        newMap.put( newQName, kSession );
        setKSessions( newMap );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setListener(java.beans.PropertyChangeListener)
     */
    public KBase setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KSession ksession : kSessions.values() ) {
            // make sure the listener is set for each ksession
            ksession.setListener( listener );
        }
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getNamespace()
     */
    public String getNamespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setNamespace(java.lang.String)
     */
    public KBase setNamespace(String namespace) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "namespace", this.namespace, namespace ) );
        }
        this.namespace = namespace;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setName(java.lang.String)
     */
    public KBase setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getQName()
     */
    public String getQName() {
        return this.namespace + "." + this.name;
    }
        
    public Set<String> getIncludes() {
        return Collections.unmodifiableSet( includes );
    }

    public KBase addInclude(String kBaseQName) {
        this.includes.add( kBaseQName );
        return this;
    }
    
    public KBase removeInclude(String kBaseQName) {
        this.includes.remove( kBaseQName );
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getFiles()
     */
    public List<String> getFiles() {
        return files;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setFiles(java.util.List)
     */
    public KBase setFiles(List<String> files) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "files", this.files, files ) );
        }
        this.files = files;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getEqualsBehavior()
     */
    public AssertBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setEqualsBehavior(org.kie.conf.AssertBehaviorOption)
     */
    public KBase setEqualsBehavior(AssertBehaviorOption equalsBehaviour) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "equalsBehavior", this.equalsBehavior, equalsBehavior ) );
        }
        this.equalsBehavior = equalsBehaviour;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getEventProcessingMode()
     */
    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setEventProcessingMode(org.kie.conf.EventProcessingOption)
     */
    public KBase setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "eventProcessingMode", this.eventProcessingMode, eventProcessingMode ) );
        }
        this.eventProcessingMode = eventProcessingMode;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#setAnnotations(java.util.List)
     */
    public KBase setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KBase#toString()
     */
    @Override
    public String toString() {
        return "KBase [namespace=" + namespace + ", name=" + name + ", files=" + files + ", annotations=" + annotations + ", equalsBehaviour=" + equalsBehavior + ", eventProcessingMode=" + eventProcessingMode + ", ksessions=" + kSessions + "]";
    }

    public static class KBaseConverter extends AbstractXStreamConverter {

        public KBaseConverter() {
            super(KBaseImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KBaseImpl kBase = (KBaseImpl) value;
            writer.addAttribute("name", kBase.getName());
            writer.addAttribute("namespace", kBase.getNamespace());
            if (kBase.getEventProcessingMode() != null) {
                writer.addAttribute("eventProcessingMode", kBase.getEventProcessingMode().getMode());
            }
            if (kBase.getEqualsBehavior() != null) {
                writer.addAttribute("equalsBehavior", kBase.getEqualsBehavior().toString());
            }
            writeList(writer, "files", "file", kBase.getFiles());
            writeList(writer, "includes", "include", kBase.getIncludes());
            writeObjectList(writer, context, "ksessions", "ksession", kBase.getKSessions().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KBaseImpl kBase = new KBaseImpl();
            kBase.setName(reader.getAttribute("name"));
            kBase.setNamespace(reader.getAttribute("namespace"));

            String eventMode = reader.getAttribute("eventProcessingMode");
            if (eventMode != null) {
                kBase.setEventProcessingMode(EventProcessingOption.determineEventProcessingMode(eventMode));
            }
            String equalsBehavior = reader.getAttribute("equalsBehavior");
            if (equalsBehavior != null) {
                kBase.setEqualsBehavior(AssertBehaviorOption.valueOf(equalsBehavior));
            }

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("ksessions".equals(name)) {
                        Map<String, KSession> kSessions = new HashMap<String, KSession>();
                        for (KSessionImpl kSession : readObjectList(reader, context, KSessionImpl.class)) {
                            kSession.setKBase(kBase);
                            kSessions.put( kSession.getQName(), kSession );
                        }
                        kBase.setKSessions(kSessions);
                    } else if ("files".equals(name)) {
                        kBase.setFiles(readList(reader));
                    } else if ("includes".equals(name)) {
                        for (String include : readList(reader)) {
                            kBase.addInclude(include);
                        }
                    }
                }
            });
            return kBase;
        }
    }
}
