/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.ByteConverter;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.basic.ShortConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.drools.compiler.kproject.models.ChannelModelImpl;
import org.drools.compiler.kproject.models.FileLoggerModelImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.compiler.kproject.models.ListenerModelImpl;
import org.drools.compiler.kproject.models.QualifierModelImpl;
import org.drools.compiler.kproject.models.RuleTemplateModelImpl;
import org.drools.compiler.kproject.models.WorkItemHandlerModelImpl;
import org.drools.core.util.AbstractXStreamConverter;
import org.drools.core.util.IoUtils;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.xml.sax.SAXException;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class KogitoKieModuleMarshaller {

    static final KogitoKieModuleMarshaller MARSHALLER = new KogitoKieModuleMarshaller();

    private final XStream xStream;

    private KogitoKieModuleMarshaller() {
        xStream = new XStream(new DomDriver()) {
            @Override
            protected void setupConverters() {
                registerConverter(new NullConverter(), PRIORITY_VERY_HIGH);
                registerConverter(new IntConverter(), PRIORITY_NORMAL);
                registerConverter(new FloatConverter(), PRIORITY_NORMAL);
                registerConverter(new DoubleConverter(), PRIORITY_NORMAL);
                registerConverter(new LongConverter(), PRIORITY_NORMAL);
                registerConverter(new ShortConverter(), PRIORITY_NORMAL);
                registerConverter(new BooleanConverter(), PRIORITY_NORMAL);
                registerConverter(new ByteConverter(), PRIORITY_NORMAL);
                registerConverter(new StringConverter(), PRIORITY_NORMAL);
                registerConverter(new CollectionConverter(getMapper()), PRIORITY_NORMAL);
                registerConverter(new ReflectionConverter(getMapper(), getReflectionProvider()), PRIORITY_VERY_LOW);
                registerConverter(new KieModuleConverter());
                registerConverter(new KieBaseModelImpl.KBaseConverter());
                registerConverter(new KieSessionModelImpl.KSessionConverter());
                registerConverter(new ListenerModelImpl.ListenerConverter());
                registerConverter(new QualifierModelImpl.QualifierConverter());
                registerConverter(new WorkItemHandlerModelImpl.WorkItemHandelerConverter());
                registerConverter(new ChannelModelImpl.ChannelConverter());
                registerConverter(new RuleTemplateModelImpl.RuleTemplateConverter());
            }
        };
        XStream.setupDefaultSecurity(xStream);
        xStream.addPermission(new AnyTypePermission());
        xStream.alias("kmodule", KieModuleModelImpl.class);
        xStream.alias("kbase", KieBaseModelImpl.class);
        xStream.alias("ksession", KieSessionModelImpl.class);
        xStream.alias("listener", ListenerModelImpl.class);
        xStream.alias("qualifier", QualifierModelImpl.class);
        xStream.alias("workItemHandler", WorkItemHandlerModelImpl.class);
        xStream.alias("channel", ChannelModelImpl.class);
        xStream.alias("fileLogger", FileLoggerModelImpl.class);
        xStream.alias("ruleTemplate", RuleTemplateModelImpl.class);
        xStream.setClassLoader(KieModuleModelImpl.class.getClassLoader());
    }

    public String toXML( KieModuleModel kieProject) {
        return xStream.toXML(kieProject);
    }

    public KieModuleModel fromXML(InputStream kModuleStream) {
        byte[] bytes = null;
        try {
            bytes = readBytesFromInputStream(kModuleStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieModuleValidator.validate(bytes);
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            return (KieModuleModel) xStream.fromXML(input);
        } catch (IOException ex) {
            throw new RuntimeException( "Unable to load KieModuleModel", ex );
        }
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

    public static class KieModuleConverter extends AbstractXStreamConverter {

        public KieModuleConverter() {
            super(KieModuleModelImpl.class);
        }

        public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            org.drools.compiler.builder.impl.KogitoKieModuleModelImpl kModule = ( org.drools.compiler.builder.impl.KogitoKieModuleModelImpl ) value;
            writePropertyMap(writer, context, "configuration", kModule.getConfProps());
            for ( KieBaseModel kBaseModule : kModule.getKieBaseModels().values() ) {
                writeObject( writer, context, "kbase", kBaseModule);
            }
        }

        public Object unmarshal( HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final org.drools.compiler.builder.impl.KogitoKieModuleModelImpl kModule = new org.drools.compiler.builder.impl.KogitoKieModuleModelImpl();

            readNodes(reader, new NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("kbase".equals(name)) {
                        KieBaseModelImpl kBaseModule = readObject( reader, context, KieBaseModelImpl.class );
                        kModule.getRawKieBaseModels().put( kBaseModule.getName(), kBaseModule );
                        kBaseModule.setKModule(kModule);
                    } else if ("configuration".equals(name)) {
                        kModule.setConfProps( readPropertyMap(reader, context) );
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

        private static Schema loadOldSchema() {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {
                URL url = KieModuleModel.class.getClassLoader().getResource("org/kie/api/old-kmodule.xsd");
                return factory.newSchema(url);
            } catch (SAXException ex ) {
                throw new RuntimeException( "Unable to load old XSD", ex );
            }
        }

        private static void validate(byte[] bytes) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                validate(new StreamSource(bais));
            } catch (IOException ex) {
                throw new RuntimeException("Unable to validate byte[]", ex);
            }
        }

        private static void validate(java.io.File kModuleFile) {
            validate(new StreamSource(kModuleFile));
        }

        private static void validate(URL kModuleUrl) {
            String urlString;
            try {
                urlString = kModuleUrl.toURI().toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            validate(new StreamSource(urlString));
        }

        private static void validate(String kModuleString) {
            byte[] bytes = kModuleString.getBytes( IoUtils.UTF8_CHARSET);
            validate(bytes);
        }

        private static void validate( Source source ) {
            try {
                schema.newValidator().validate(source);
            } catch (Exception schemaException) {
                throw new RuntimeException(
                        "XSD validation failed against schema (" + schemaException.getMessage() + ").",
                        schemaException);
            }
        }
    }
}
