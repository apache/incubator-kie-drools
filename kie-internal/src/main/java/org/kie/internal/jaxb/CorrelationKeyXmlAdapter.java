/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.jaxb;

import java.util.ArrayList;
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
        List<String> keys = new ArrayList<String>();
        for( String k : key.split(":") ) {
            keys.add(k);
        }
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
        StringBuffer result = new StringBuffer();
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
