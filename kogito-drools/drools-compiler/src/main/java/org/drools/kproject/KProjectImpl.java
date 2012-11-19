package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KProjectImpl implements KProject {

    private GroupArtifactVersion groupArtifactVersion;
    
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KBase>  kBases;
    
    private  transient PropertyChangeListener listener;
    
    public KProjectImpl() {
        kBases = Collections.emptyMap();
    }    

    public GroupArtifactVersion getGroupArtifactVersion() {
        return groupArtifactVersion;
    }

    public void setGroupArtifactVersion(GroupArtifactVersion groupArtifactVersion) {
        this.groupArtifactVersion = groupArtifactVersion;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setListener(java.beans.PropertyChangeListener)
     */
    public KProject setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KBase kbase : kBases.values() ) {
            // make sure the listener is set for each kbase
            kbase.setListener( listener );
        }
        return this;
    }



    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKProjectPath()
     */
    public String getKProjectPath() {
        return kProjectPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKProjectPath(java.lang.String)
     */
    public KProject setKProjectPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new java.beans.PropertyChangeEvent( this, "kProjectPath", this.kProjectPath, kProjectPath ) );
        }
        this.kProjectPath = kprojectPath;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKBasesPath()
     */
    public String getKBasesPath() {
        return kBasesPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKBasesPath(java.lang.String)
     */
    public KProject setKBasesPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBasesPath", this.kBasesPath, kBasesPath ) );     
        }
        this.kBasesPath = kprojectPath;
        return this;
    }  
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#addKBase(org.kie.kproject.KBaseImpl)
     */
    public KBase newKBase(String namespace,
                         String name) {
        KBase kbase = new KBaseImpl(this, namespace, name);
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getQName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#removeKBase(org.kie.kproject.KBase)
     */
    public void removeKBase(String qName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#removeKBase(org.kie.kproject.KBase)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        KBase kBase = newMap.remove( oldQName );
        newMap.put( newQName, kBase );
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKBases()
     */
    public Map<String, KBase> getKBases() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KBase> kBases) {        
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBases", this.kBases, kBases ) );
            
            for ( KBase kbase : kBases.values() ) {
                // make sure the listener is set for each kbase
                kbase.setListener( listener );
            }
        }
        
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        if ( kProjectPath == null) {
            problems.add( "A path to the kproject.properties file must be specified" );
        }
//
//        // check valid kbase relative paths
//        for ( Entry<String, String> entry : kbasePaths.entrySet() ) {
//
//        }

        // validate valid kbases
        //for ( Entry<String, >)

        return problems;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#toString()
     */
    @Override
    public String toString() {
        return "KProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }

    public String toXML() {
        return MARSHALLER.toXML(this);
    }

    public static KProject fromXML(InputStream kProjectStream) {
        return MARSHALLER.fromXML(kProjectStream);
    }

    public static KProject fromXML(java.io.File kProjectFile) {
        return MARSHALLER.fromXML(kProjectFile);
    }

    public static KProject fromXML(URL kProjectUrl) {
        return MARSHALLER.fromXML(kProjectUrl);
    }

    private static final KProjectMarshaller MARSHALLER = new KProjectMarshaller();

    private static class KProjectMarshaller {
        private final XStream xStream = new XStream(new DomDriver());

        private KProjectMarshaller() {
            xStream.registerConverter(new KProjectConverter());
            xStream.registerConverter(new KBaseImpl.KBaseConverter());
            xStream.registerConverter(new KSessionImpl.KSessionConverter());
            xStream.alias("kproject", KProjectImpl.class);
            xStream.alias("kbase", KBaseImpl.class);
            xStream.alias("ksession", KSessionImpl.class);
        }

        public String toXML(KProject kProject) {
            return xStream.toXML(kProject);
        }

        public KProject fromXML(InputStream kProjectStream) {
            return (KProject)xStream.fromXML(kProjectStream);
        }

        public KProject fromXML(java.io.File kProjectFile) {
            return (KProject)xStream.fromXML(kProjectFile);
        }

        public KProject fromXML(URL kProjectUrl) {
            return (KProject)xStream.fromXML(kProjectUrl);
        }
    }

    public static class KProjectConverter extends AbstractXStreamConverter {

        public KProjectConverter() {
            super(KProjectImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KProjectImpl kProject = (KProjectImpl) value;
            writeAttribute(writer, "kBasesPath", kProject.getKBasesPath());
            writeAttribute(writer, "kProjectPath", kProject.getKProjectPath());
            writeObject(writer, context, "groupArtifactVersion", kProject.getGroupArtifactVersion());
            writeObjectList(writer, context, "kbases", "kbase", kProject.getKBases().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KProjectImpl kProject = new KProjectImpl();
            kProject.setKBasesPath(reader.getAttribute("kBasesPath"));
            kProject.setKProjectPath(reader.getAttribute("kProjectPath"));

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("groupArtifactVersion".equals(name)) {
                        kProject.setGroupArtifactVersion((GroupArtifactVersion) context.convertAnother(reader.getValue(), GroupArtifactVersion.class));
                    } else if ("kbases".equals(name)) {
                        Map<String, KBase> kBases = new HashMap<String, KBase>();
                        for (KBaseImpl kBase : readObjectList(reader, context, KBaseImpl.class)) {
                            kBase.setKProject(kProject);
                            kBases.put(kBase.getQName(), kBase);
                        }
                        kProject.setKBases(kBases);
                    }
                }
            });

            return kProject;
        }
    }
}