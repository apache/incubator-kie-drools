package org.kie.internal.jaxb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.process.CorrelationProperty;

public class CorrelationKeyXmlAdapter extends XmlAdapter<String, CorrelationKey> {

    @Override
    public CorrelationKey unmarshal( String key ) throws Exception {
        return unmarshalCorrelationKey(key);
    }

    /**
     * This method has been made "static"ally available so that
     * non-JAXB code does not have to (unnecesarily) instantiate
     * an instance fo this class.
     *
     * @param key A {@link CorrelationKey}
     * @return A {@link String} equivalent of the key
     */
    public static CorrelationKey unmarshalCorrelationKey( String key ) {
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        if( key == null || key.isEmpty() ) {
           return factory.newCorrelationKey(Collections.EMPTY_LIST);
        }
        List<String> keys = Arrays.asList(key.split(":"));
        return KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(keys);
    }

    @Override
    public String marshal( CorrelationKey key ) throws Exception {
        return CorrelationKeyXmlAdapter.marshalCorrelationKey(key);
    }

    /**
     * This method has been made "static"ally available so that
     * non-JAXB code does not have to (unnecesarily) instantiate
     * an instance fo this class.
     *
     * @param key A {@link CorrelationKey}
     * @return A {@link String} equivalent of the key
     */
    public static String marshalCorrelationKey( CorrelationKey key ) {
        if( key == null ) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        if( ! key.getProperties().isEmpty() ) {
            Iterator<CorrelationProperty<?>> iter = key.getProperties().iterator();
            CorrelationProperty<?> prop = iter.next();
            if( prop != null ) {
                result.append(prop.getValue().toString());
            }
            while( iter.hasNext() ) {
                prop = iter.next();
                if( prop != null ) {
                    result.append(":").append(prop.getValue().toString());
                }
            }
        }
        return result.toString();
    }

}
