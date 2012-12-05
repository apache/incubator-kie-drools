package org.drools.kproject.models;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KieModuleModelImpl implements KieModuleModel {

    public static String KMODULE_JAR_PATH = "META-INF/kmodule.xml";
    public static String KMODULE_SRC_PATH = "src/main/resources/" + KMODULE_JAR_PATH;

    private Map<String, KieBaseModel>  kBases  = new HashMap<String, KieBaseModel>();
    
    public KieModuleModelImpl() {
    }    


    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#addKBase(org.kie.kModule.KieBaseModelImpl)
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
     * @see org.kie.kModule.KieProject#removeKieBaseModel(org.kie.kModule.KieBaseModel)
     */
    public void removeKieBaseModel(String qName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#removeKieBaseModel(org.kie.kModule.KieBaseModel)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        KieBaseModel kieBaseModel = newMap.remove( oldQName );
        newMap.put( newQName, kieBaseModel);
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#getKieBaseModels()
     */
    public Map<String, KieBaseModel> getKieBaseModels() {
        return Collections.unmodifiableMap( kBases );
    }

    public Map<String, KieBaseModel> getRawKieBaseModels() {
        return kBases;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KieBaseModel> kBases) {
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        return problems;
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieProject [kbases=" + kBases + "]";
    }

    public String toXML() {
        return MARSHALLER.toXML(this);
    }

    public static KieModuleModel fromXML(InputStream kModuleStream) {
        return MARSHALLER.fromXML(kModuleStream);
    }

    public static KieModuleModel fromXML(java.io.File kModuleFile) {
        return MARSHALLER.fromXML(kModuleFile);
    }

    public static KieModuleModel fromXML(URL kModuleUrl) {
        return MARSHALLER.fromXML(kModuleUrl);
    }

    public static KieModuleModel fromXML(String kModuleString) {
        return MARSHALLER.fromXML(kModuleString);
    }

    private static final kModuleMarshaller MARSHALLER = new kModuleMarshaller();

    private static class kModuleMarshaller {
        private final XStream xStream = new XStream(new DomDriver());

        private kModuleMarshaller() {
            xStream.registerConverter(new kModuleConverter());
            xStream.registerConverter(new KieBaseModelImpl.KBaseConverter());
            xStream.registerConverter(new KieSessionModelImpl.KSessionConverter());
            xStream.registerConverter(new ListenerModelImpl.ListenerConverter());
            xStream.registerConverter(new QualifierModelImpl.QualifierConverter());
            xStream.registerConverter(new WorkItemHandlerModelImpl.WorkItemHandelerConverter());
            xStream.alias("kmodule", KieModuleModelImpl.class);
            xStream.alias("kbase", KieBaseModelImpl.class);
            xStream.alias("ksession", KieSessionModelImpl.class);
            xStream.alias("listener", ListenerModelImpl.class);
            xStream.alias("qualifier", QualifierModelImpl.class);
            xStream.alias("workItemHandler", WorkItemHandlerModelImpl.class);
        }

        public String toXML(KieModuleModel kieProject) {
            return xStream.toXML(kieProject);
        }

        public KieModuleModel fromXML(InputStream kModuleStream) {
            return (KieModuleModel)xStream.fromXML(kModuleStream);
        }

        public KieModuleModel fromXML(java.io.File kModuleFile) {
            return (KieModuleModel)xStream.fromXML(kModuleFile);
        }

        public KieModuleModel fromXML(URL kModuleUrl) {
            return (KieModuleModel)xStream.fromXML(kModuleUrl);
        }

        public KieModuleModel fromXML(String kModuleString) {
            return (KieModuleModel)xStream.fromXML(kModuleString);
        }
    }

    public static class kModuleConverter extends AbstractXStreamConverter {

        public kModuleConverter() {
            super(KieModuleModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieModuleModelImpl kModule = (KieModuleModelImpl) value;
            for ( KieBaseModel kBaseModule : kModule.getKieBaseModels().values() ) {
                writeObject( writer, context, "kbase", kBaseModule);
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieModuleModelImpl kModule = new KieModuleModelImpl();

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("kbase".equals(name)) {
                        KieBaseModelImpl kBaseModule = readObject( reader, context, KieBaseModelImpl.class );
                        kModule.getRawKieBaseModels().put( kBaseModule.getName(), kBaseModule );
                        kBaseModule.setKModule(kModule);
                    }
                }
            });

            return kModule;
        }
    }
}