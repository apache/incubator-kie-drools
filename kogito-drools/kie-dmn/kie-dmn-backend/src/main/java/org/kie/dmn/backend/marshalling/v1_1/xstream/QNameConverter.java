package org.kie.dmn.backend.marshalling.v1_1.xstream;

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
        writer.setValue(MarshallingUtils.formatQName((QName) object));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return MarshallingUtils.parseQNameString( reader.getValue() );
    }

}