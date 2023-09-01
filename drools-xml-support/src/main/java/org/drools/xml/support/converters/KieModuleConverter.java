package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieBaseModel;

public class KieModuleConverter extends AbstractXStreamConverter {

    public KieModuleConverter() {
        super(KieModuleModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KieModuleModelImpl kModule = (KieModuleModelImpl) value;
        writePropertyMap(writer, context, "configuration", kModule.getConfProps());
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
                    kModule.setConfProps( readPropertyMap(reader, context) );
                }
            }
        });

        return kModule;
    }
}