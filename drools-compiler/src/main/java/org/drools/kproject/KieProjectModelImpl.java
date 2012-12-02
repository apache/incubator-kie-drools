package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class KieProjectModelImpl implements KieProjectModel {

    public static String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    public static String KPROJECT_SRC_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    private Map<String, KieBaseModel>  kBases;
    
    public KieProjectModelImpl() {
        kBases = Collections.emptyMap();
    }    


    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#addKBase(org.kie.kproject.KieBaseModelImpl)
     */
    public KieBaseModel newKieBaseModel(String name) {
        KieBaseModel kbase = new KieBaseModelImpl(this, name);
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }

    public KieBaseModel newDefaultKieBaseModel() {
        if ( kBases.containsKey(KieBaseModelImpl.DEFAULT_KIEBASE_NAME) ) {
            throw new RuntimeException("This project already contains a default kie base");
        }
        return newKieBaseModel(KieBaseModelImpl.DEFAULT_KIEBASE_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseModel(org.kie.kproject.KieBaseModel)
     */
    public void removeKieBaseModel(String qName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseModel(org.kie.kproject.KieBaseModel)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        KieBaseModel kieBaseModel = newMap.remove( oldQName );
        newMap.put( newQName, kieBaseModel);
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKieBaseModels()
     */
    public Map<String, KieBaseModel> getKieBaseModels() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KieBaseModel> kBases) {
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        return problems;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieProject [kbases=" + kBases + "]";
    }

    public String toXML() {
        return MARSHALLER.toXML(this);
    }

    public static KieProjectModel fromXML(InputStream kProjectStream) {
        return MARSHALLER.fromXML(kProjectStream);
    }

    public static KieProjectModel fromXML(java.io.File kProjectFile) {
        return MARSHALLER.fromXML(kProjectFile);
    }

    public static KieProjectModel fromXML(URL kProjectUrl) {
        return MARSHALLER.fromXML(kProjectUrl);
    }

    public static KieProjectModel fromXML(String kProjectString) {
        return MARSHALLER.fromXML(kProjectString);
    }

    private static final KProjectMarshaller MARSHALLER = new KProjectMarshaller();

    private static class KProjectMarshaller {
        private final XStream xStream = new XStream(new DomDriver());

        private KProjectMarshaller() {
            xStream.registerConverter(new KProjectConverter());
            xStream.registerConverter(new KieBaseModelImpl.KBaseConverter());
            xStream.registerConverter(new KieSessionModelImpl.KSessionConverter());
            xStream.registerConverter(new ListenerModelImpl.ListenerConverter());
            xStream.registerConverter(new QualifierModelImpl.QualifierConverter());
            xStream.registerConverter(new WorkItemHandelerModelImpl.WorkItemHandelerConverter());
            xStream.alias("kproject", KieProjectModelImpl.class);
            xStream.alias("kbase", KieBaseModelImpl.class);
            xStream.alias("ksession", KieSessionModelImpl.class);
            xStream.alias("listener", ListenerModelImpl.class);
            xStream.alias("qualifier", QualifierModelImpl.class);
            xStream.alias("workItemHandeler", WorkItemHandelerModelImpl.class);
        }

        public String toXML(KieProjectModel kieProject) {
            return xStream.toXML(kieProject);
        }

        public KieProjectModel fromXML(InputStream kProjectStream) {
            return (KieProjectModel)xStream.fromXML(kProjectStream);
        }

        public KieProjectModel fromXML(java.io.File kProjectFile) {
            return (KieProjectModel)xStream.fromXML(kProjectFile);
        }

        public KieProjectModel fromXML(URL kProjectUrl) {
            return (KieProjectModel)xStream.fromXML(kProjectUrl);
        }

        public KieProjectModel fromXML(String kProjectString) {
            return (KieProjectModel)xStream.fromXML(kProjectString);
        }
    }

    public static class KProjectConverter extends AbstractXStreamConverter {

        public KProjectConverter() {
            super(KieProjectModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieProjectModelImpl kProject = (KieProjectModelImpl) value;
            writeObjectList(writer, context, "kbases", "kbase", kProject.getKieBaseModels().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieProjectModelImpl kProject = new KieProjectModelImpl();

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("kbases".equals(name)) {
                        Map<String, KieBaseModel> kBases = new HashMap<String, KieBaseModel>();
                        for (KieBaseModelImpl kBase : readObjectList(reader, context, KieBaseModelImpl.class)) {
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