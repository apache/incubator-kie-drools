/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kproject.models;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.drools.core.util.IoUtils;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class KieModuleModelImpl implements KieModuleModel {

    public static final String KMODULE_FILE_NAME = "kmodule.xml";
    public static final String KMODULE_JAR_PATH = "META-INF/" + KMODULE_FILE_NAME;
    public static final String KMODULE_INFO_JAR_PATH = "META-INF/kmodule.info";
    public static final String KMODULE_SRC_PATH = "src/main/resources/" + KMODULE_JAR_PATH;
    public static final String KMODULE_SPRING_JAR_PATH = "META-INF/kmodule-spring.xml";

    private Map<String, String> confProps = new HashMap<String, String>();
    private Map<String, KieBaseModel> kBases = new HashMap<String, KieBaseModel>();

    public KieModuleModelImpl() { }

    public KieModuleModel setConfigurationProperty(String key, String value) {
        confProps.put(key, value);
        return this;
    }

    public String getConfigurationProperty(String key) {
        return confProps.get(key);
    }

    public Map<String, String> getConfigurationProperties() {
        return confProps;
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

    void changeKBaseName(KieBaseModel kieBase, String oldName, String newName) {
        kBases.remove(oldName);
        kBases.put(newName, kieBase);
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieModuleModel [kbases=" + kBases + "]";
    }

    private static final String KMODULE_XSD =
            "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "         xmlns=\"http://jboss.org/kie/6.0.0/kmodule\""; // missed end >, so we can cater for />

    public String toXML() {
        String xml = MARSHALLER.toXML(this);
        return KMODULE_XSD + xml.substring("<kmodule".length());  // missed end >, so we can cater for />
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
            xStream.registerConverter(new RuleTemplateModelImpl.RuleTemplateConverter());
            xStream.alias("kmodule", KieModuleModelImpl.class);
            xStream.alias("kbase", KieBaseModelImpl.class);
            xStream.alias("ksession", KieSessionModelImpl.class);
            xStream.alias("listener", ListenerModelImpl.class);
            xStream.alias("qualifier", QualifierModelImpl.class);
            xStream.alias("workItemHandler", WorkItemHandlerModelImpl.class);
            xStream.alias("fileLogger", FileLoggerModelImpl.class);
            xStream.alias("ruleTemplate", RuleTemplateModelImpl.class);
            xStream.setClassLoader(KieModuleModelImpl.class.getClassLoader());
        }

        public String toXML(KieModuleModel kieProject) {
            return xStream.toXML(kieProject);
        }

        public KieModuleModel fromXML(InputStream kModuleStream) {
            byte[] bytes = null;
            try {
                bytes = readBytesFromInputStream(kModuleStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            KieModuleValidator.validate(new ByteArrayInputStream(bytes));
            return (KieModuleModel)xStream.fromXML(new ByteArrayInputStream(bytes));
        }

        public KieModuleModel fromXML(java.io.File kModuleFile) {
            KieModuleValidator.validate(kModuleFile);
            return (KieModuleModel)xStream.fromXML(kModuleFile);
        }

        public KieModuleModel fromXML(URL kModuleUrl) {
            KieModuleValidator.validate(kModuleUrl);
            return (KieModuleModel)xStream.fromXML(kModuleUrl);
        }

        public KieModuleModel fromXML(String kModuleString) {
            KieModuleValidator.validate(kModuleString);
            return (KieModuleModel)xStream.fromXML(kModuleString);
        }
    }

    public static class kModuleConverter extends AbstractXStreamConverter {

        public kModuleConverter() {
            super(KieModuleModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieModuleModelImpl kModule = (KieModuleModelImpl) value;
            writePropertyMap(writer, context, "configuration", kModule.confProps);
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
                    } else if ("configuration".equals(name)) {
                        kModule.confProps = readPropertyMap(reader, context);
                    }
                }
            });

            return kModule;
        }
    }

    private static class KieModuleValidator {
        private static final Schema schema = loadSchema();

        private static Schema loadSchema() {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {
                URL url = KieModuleModel.class.getClassLoader().getResource("org/kie/api/kmodule.xsd");
                return factory.newSchema(url);
            } catch (SAXException ex ) {
                throw new RuntimeException( "Unable to load XSD", ex );
            }
        }

        private static void validate(InputStream kModuleStream) {
            validate(new StreamSource(kModuleStream));
        }

        private static void validate(java.io.File kModuleFile) {
            validate(new StreamSource(kModuleFile));
        }

        private static void validate(URL kModuleUrl) {
            try {
                validate(new StreamSource(kModuleUrl.toURI().toString()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        private static void validate(String kModuleString) {
            validate(new StreamSource(new ByteArrayInputStream(kModuleString.getBytes(IoUtils.UTF8_CHARSET))));
        }

        private static void validate(Source source) {
            try {
                schema.newValidator().validate(source);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
