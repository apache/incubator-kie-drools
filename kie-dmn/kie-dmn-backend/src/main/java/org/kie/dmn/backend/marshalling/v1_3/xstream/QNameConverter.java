package org.kie.dmn.backend.marshalling.v1_3.xstream;

import javax.xml.namespace.QName;

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
        // DMN v1.2 semantic always local part.
        QName qname = (QName) object;
        writer.setValue(qname.getLocalPart());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        // DMN v1.2 semantic always local part.
        QName qname = new QName(reader.getValue());
        return qname;
    }

}