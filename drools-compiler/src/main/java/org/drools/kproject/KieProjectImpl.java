package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseDescr;
import org.kie.builder.KieProject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KieProjectImpl implements KieProject {

    private GAV groupArtifactVersion;
    
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KieBaseDescr>  kBases;
    
    private  transient PropertyChangeListener listener;
    
    public KieProjectImpl() {
        kBases = Collections.emptyMap();
    }    

    public GAV getGroupArtifactVersion() {
        return groupArtifactVersion;
    }

    public void setGroupArtifactVersion(GAV groupArtifactVersion) {
        this.groupArtifactVersion = groupArtifactVersion;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setListener(java.beans.PropertyChangeListener)
     */
    public KieProject setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KieBaseDescr kbase : kBases.values() ) {
            // make sure the listener is set for each kbase
            kbase.setListener( listener );
        }
        return this;
    }



    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKProjectPath()
     */
    public String getKProjectPath() {
        return kProjectPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKProjectPath(java.lang.String)
     */
    public KieProject setKProjectPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new java.beans.PropertyChangeEvent( this, "kProjectPath", this.kProjectPath, kProjectPath ) );
        }
        this.kProjectPath = kprojectPath;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKBasesPath()
     */
    public String getKBasesPath() {
        return kBasesPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKBasesPath(java.lang.String)
     */
    public KieProject setKBasesPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBasesPath", this.kBasesPath, kBasesPath ) );     
        }
        this.kBasesPath = kprojectPath;
        return this;
    }  
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#addKBase(org.kie.kproject.KieBaseDescrImpl)
     */
    public KieBaseDescr newKieBaseDescr(String name) {
        KieBaseDescr kbase = new KieBaseDescrImpl(this, name);
        Map<String, KieBaseDescr> newMap = new HashMap<String, KieBaseDescr>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseDescr(org.kie.kproject.KieBaseDescr)
     */
    public void removeKieBaseDescr(String qName) {
        Map<String, KieBaseDescr> newMap = new HashMap<String, KieBaseDescr>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseDescr(org.kie.kproject.KieBaseDescr)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KieBaseDescr> newMap = new HashMap<String, KieBaseDescr>();
        newMap.putAll( this.kBases );
        KieBaseDescr kieBaseDescr = newMap.remove( oldQName );
        newMap.put( newQName, kieBaseDescr);
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKieBaseDescrs()
     */
    public Map<String, KieBaseDescr> getKieBaseDescrs() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KieBaseDescr> kBases) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBases", this.kBases, kBases ) );
            
            for ( KieBaseDescr kbase : kBases.values() ) {
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
     * @see org.kie.kproject.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }

    public String toXML() {
        return MARSHALLER.toXML(this);
    }

    public static KieProject fromXML(InputStream kProjectStream) {
        return MARSHALLER.fromXML(kProjectStream);
    }

    public static KieProject fromXML(java.io.File kProjectFile) {
        return MARSHALLER.fromXML(kProjectFile);
    }

    public static KieProject fromXML(URL kProjectUrl) {
        return MARSHALLER.fromXML(kProjectUrl);
    }

    private static final KProjectMarshaller MARSHALLER = new KProjectMarshaller();

    private static class KProjectMarshaller {
        private final XStream xStream = new XStream(new DomDriver());

        private KProjectMarshaller() {
            xStream.registerConverter(new KProjectConverter());
            xStream.registerConverter(new KieBaseDescrImpl.KBaseConverter());
            xStream.registerConverter(new KieSessionDescrImpl.KSessionConverter());
            xStream.alias("kproject", KieProjectImpl.class);
            xStream.alias("kbase", KieBaseDescrImpl.class);
            xStream.alias("ksession", KieSessionDescrImpl.class);
        }

        public String toXML(KieProject kieProject) {
            return xStream.toXML(kieProject);
        }

        public KieProject fromXML(InputStream kProjectStream) {
            return (KieProject)xStream.fromXML(kProjectStream);
        }

        public KieProject fromXML(java.io.File kProjectFile) {
            return (KieProject)xStream.fromXML(kProjectFile);
        }

        public KieProject fromXML(URL kProjectUrl) {
            return (KieProject)xStream.fromXML(kProjectUrl);
        }
    }

    public static class KProjectConverter extends AbstractXStreamConverter {

        public KProjectConverter() {
            super(KieProjectImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieProjectImpl kProject = (KieProjectImpl) value;
            writeAttribute(writer, "kBasesPath", kProject.getKBasesPath());
            writeAttribute(writer, "kProjectPath", kProject.getKProjectPath());
            writeObject(writer, context, "groupArtifactVersion", kProject.getGroupArtifactVersion());
            writeObjectList(writer, context, "kbases", "kbase", kProject.getKieBaseDescrs().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieProjectImpl kProject = new KieProjectImpl();
            kProject.setKBasesPath(reader.getAttribute("kBasesPath"));
            kProject.setKProjectPath(reader.getAttribute("kProjectPath"));

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("groupArtifactVersion".equals(name)) {
                        kProject.setGroupArtifactVersion((GroupArtifactVersion) context.convertAnother(reader.getValue(), GroupArtifactVersion.class));
                    } else if ("kbases".equals(name)) {
                        Map<String, KieBaseDescr> kBases = new HashMap<String, KieBaseDescr>();
                        for (KieBaseDescrImpl kBase : readObjectList(reader, context, KieBaseDescrImpl.class)) {
                            kBase.setKProject(kProject);
                            kBases.put(kBase.getName(), kBase);
                        }
                        kProject.setKBases(kBases);
                    }
                }
            });

            return kProject;
        }
    }
}