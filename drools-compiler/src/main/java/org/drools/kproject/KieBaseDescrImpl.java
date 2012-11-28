package org.drools.kproject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.drools.core.util.Predicate;
import org.kie.builder.KieBaseDescr;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionDescr;
import org.kie.builder.ResourceType;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.core.util.IoUtils.recursiveListFile;

public class KieBaseDescrImpl
        implements
        KieBaseDescr {
    private String                           name;
    
    private Set<String>                      includes;

    private List<String>                     annotations;

    private AssertBehaviorOption             equalsBehavior;

    private EventProcessingOption            eventProcessingMode;

    private Map<String, KieSessionDescr>            kSessions;

    private KieProjectImpl kProject;

    private transient PropertyChangeListener listener;

    private KieBaseDescrImpl() {
        this.includes = new HashSet<String>();
    }

    public KieBaseDescrImpl(KieProjectImpl kProject, String name) {
        this.kProject = kProject;
        this.includes = new HashSet<String>();
        this.name = name;
        this.kSessions = Collections.emptyMap();
    }

    public KieProjectImpl getKProject() {
        return kProject;
    }
    
    public void setKProject(KieProject kieProject) {
        this.kProject = (KieProjectImpl) kieProject;
    }    

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getKieSessionDescrs()
     */
    public Map<String, KieSessionDescr> getKieSessionDescrs() {
        return Collections.unmodifiableMap( kSessions );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setKSessions(java.util.Map)
     */
    private void setKSessions(Map<String, KieSessionDescr> kSessions) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kSessions", this.kSessions, kSessions ) );
            for ( KieSessionDescr ksession : kSessions.values() ) {
                // make sure the listener is set for each ksession
                ksession.setListener( listener );
            }
        }
        this.kSessions = kSessions;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#addKSession(org.kie.kproject.KieSessionDescrImpl)
     */
    public KieSessionDescr newKieSessionDescr(String name) {
        KieSessionDescr kieSessionDescr = new KieSessionDescrImpl( this, name );
        Map<String, KieSessionDescr> newMap = new HashMap<String, KieSessionDescr>();
        newMap.putAll( this.kSessions );
        newMap.put( kieSessionDescr.getName(), kieSessionDescr);
        setKSessions( newMap );

        return kieSessionDescr;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#removeKieSessionDescr(org.kie.kproject.KieSessionDescrImpl)
     */
    public KieBaseDescr removeKieSessionDescr(String qName) {
        Map<String, KieSessionDescr> newMap = new HashMap<String, KieSessionDescr>();
        newMap.putAll( this.kSessions );
        newMap.remove( qName );
        setKSessions( newMap );
        return this;
    }

    public void moveKSession(String oldQName,
                             String newQName) {
        Map<String, KieSessionDescr> newMap = new HashMap<String, KieSessionDescr>();
        newMap.putAll( this.kSessions );
        KieSessionDescr kieSessionDescr = newMap.remove( oldQName );
        newMap.put( newQName, kieSessionDescr);
        setKSessions( newMap );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setListener(java.beans.PropertyChangeListener)
     */
    public KieBaseDescr setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KieSessionDescr ksession : kSessions.values() ) {
            // make sure the listener is set for each ksession
            ksession.setListener( listener );
        }
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setName(java.lang.String)
     */
    public KieBaseDescr setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
        return this;
    }

    public Set<String> getIncludes() {
        return Collections.unmodifiableSet( includes );
    }

    public KieBaseDescr addInclude(String kBaseQName) {
        this.includes.add( kBaseQName );
        return this;
    }
    
    public KieBaseDescr removeInclude(String kBaseQName) {
        this.includes.remove( kBaseQName );
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getEqualsBehavior()
     */
    public AssertBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setEqualsBehavior(org.kie.conf.AssertBehaviorOption)
     */
    public KieBaseDescr setEqualsBehavior(AssertBehaviorOption equalsBehaviour) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "equalsBehavior", this.equalsBehavior, equalsBehavior ) );
        }
        this.equalsBehavior = equalsBehaviour;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getEventProcessingMode()
     */
    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setEventProcessingMode(org.kie.conf.EventProcessingOption)
     */
    public KieBaseDescr setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "eventProcessingMode", this.eventProcessingMode, eventProcessingMode ) );
        }
        this.eventProcessingMode = eventProcessingMode;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#getAnnotations()
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#setAnnotations(java.util.List)
     */
    public KieBaseDescr setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieBaseDescr#toString()
     */
    @Override
    public String toString() {
        return "KieBaseDescr [name=" + name + ", annotations=" + annotations + ", equalsBehaviour=" + equalsBehavior + ", eventProcessingMode=" + eventProcessingMode + ", ksessions=" + kSessions + "]";
    }

    public static List<String> getFiles(String kBaseName, ZipFile zipFile) {
        List<String> files = new ArrayList<String>();
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String fileName = zipEntry.getName();
            if (filterFileInKBase(kBaseName, fileName)) {
                files.add(fileName);
            }
        }
        return files;
    }

    public static List<String> getFiles(String kBaseName, java.io.File root) {
        String prefix = "";
        java.io.File kBaseRoot = null;
        if (root.getName().equals(kBaseName)) {
            kBaseRoot = root;
        } else {
            for (java.io.File child : root.listFiles()) {
                if (child.getName().equals(kBaseName)) {
                    kBaseRoot = child;
                    break;
                }
            }
            prefix = kBaseName + "/";
        }

        if (kBaseRoot == null) {
            throw new RuntimeException("Unable to find KieBaseDescr " + kBaseName + " in " + root);
        }

        return recursiveListFile(kBaseRoot, prefix, new Predicate<java.io.File>() {
            public boolean apply(java.io.File file) {
                String fileName = file.getName();
                return fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) || 
                       fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() );
            }
        });
    }

    private static boolean filterFileInKBase(String kBaseQName, String fileName) {
        return fileName.startsWith(kBaseQName) && (fileName.endsWith(ResourceType.DRL.getDefaultExtension()) || fileName.endsWith(ResourceType.BPMN2.getDefaultExtension()));
    }

    public static class KBaseConverter extends AbstractXStreamConverter {

        public KBaseConverter() {
            super(KieBaseDescrImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieBaseDescrImpl kBase = (KieBaseDescrImpl) value;
            writer.addAttribute("name", kBase.getName());
            if (kBase.getEventProcessingMode() != null) {
                writer.addAttribute("eventProcessingMode", kBase.getEventProcessingMode().getMode());
            }
            if (kBase.getEqualsBehavior() != null) {
                writer.addAttribute("equalsBehavior", kBase.getEqualsBehavior().toString());
            }
            // writeList(writer, "files", "file", kBase.getFiles());
            writeList(writer, "includes", "include", kBase.getIncludes());
            writeObjectList(writer, context, "ksessions", "ksession", kBase.getKieSessionDescrs().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieBaseDescrImpl kBase = new KieBaseDescrImpl();
            kBase.setName(reader.getAttribute("name"));

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
                        Map<String, KieSessionDescr> kSessions = new HashMap<String, KieSessionDescr>();
                        for (KieSessionDescrImpl kSession : readObjectList(reader, context, KieSessionDescrImpl.class)) {
                            kSession.setKBase(kBase);
                            kSessions.put( kSession.getName(), kSession );
                        }
                        kBase.setKSessions(kSessions);
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
