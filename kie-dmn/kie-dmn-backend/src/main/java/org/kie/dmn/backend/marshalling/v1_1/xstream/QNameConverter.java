package org.kie.dmn.backend.marshalling.v1_1.xstream;

import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Please note this does not extend the DMNBaseConverter as it just need access to the node value itself.
 */
public class QNameConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( QName.class );
    }

    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
        QName qname = (QName) object;
        if ( !XMLConstants.NULL_NS_URI.equals(qname.getNamespaceURI()) && !XMLConstants.DEFAULT_NS_PREFIX.equals(qname.getPrefix()) ) {
            CustomStaxWriter staxWriter = ((CustomStaxWriter) writer.underlyingWriter());
            try {
                staxWriter.writeNamespace(qname.getPrefix(), qname.getNamespaceURI());
            } catch (XMLStreamException e) {
                // TODO what to do?
                e.printStackTrace();
            }
        }
        writer.setValue(MarshallingUtils.formatQName(qname));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        QName qname = MarshallingUtils.parseQNameString( reader.getValue() );
        Map<String, String> currentNSCtx = ((CustomStaxReader) reader.underlyingReader()).getNsContext();
        String qnameURI = currentNSCtx.get(qname.getPrefix());
        if (qnameURI != null) {
            return new QName(qnameURI, qname.getLocalPart(), qname.getPrefix());
        }
        return qname;
    }

}