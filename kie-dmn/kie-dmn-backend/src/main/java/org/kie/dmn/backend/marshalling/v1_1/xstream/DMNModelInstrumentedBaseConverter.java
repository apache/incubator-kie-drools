package org.kie.dmn.backend.marshalling.v1_1.xstream;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DMNModelInstrumentedBaseConverter
        extends DMNBaseConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DMNModelInstrumentedBaseConverter.class);

    public DMNModelInstrumentedBaseConverter(XStream xstream) {
        super( xstream.getMapper() );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        KieDMNModelInstrumentedBase mib = (KieDMNModelInstrumentedBase) parent;

        CustomStaxReader customStaxReader = (CustomStaxReader) reader.underlyingReader();
        
        Map<String, String> currentNSCtx = customStaxReader.getNsContext();
        mib.getNsContext().putAll(currentNSCtx);

        mib.setLocation( customStaxReader.getLocation() );
        
        mib.setAdditionalAttributes( customStaxReader.getAdditionalAttributes() );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        // no call to super as super is abstract method.
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        KieDMNModelInstrumentedBase mib = (KieDMNModelInstrumentedBase) parent;

        CustomStaxWriter staxWriter = ((CustomStaxWriter) writer.underlyingWriter());
        for (Entry<String, String> kv : mib.getNsContext().entrySet()) {
            try {
                if (KieDMNModelInstrumentedBase.URI_DMN.equals(kv.getValue())) {
                    // skip as that is the default namespace xmlns<:prefix>=DMN is handled by the stax driver.
                } else {
                    staxWriter.writeNamespace(kv.getKey(), kv.getValue());
                }
            } catch (Exception e) {
                LOG.warn("The XML driver writer failed to manage writing namespace, namespaces prefixes could be wrong in the resulting file.", e);
            }
        }
        
        for ( Entry<QName, String> kv : mib.getAdditionalAttributes().entrySet() ) {
            staxWriter.addAttribute(kv.getKey().getPrefix() + ":" + kv.getKey().getLocalPart(), kv.getValue());
        }
    }
}
