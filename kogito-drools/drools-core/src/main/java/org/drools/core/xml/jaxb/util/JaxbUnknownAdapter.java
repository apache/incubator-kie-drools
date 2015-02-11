/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.xml.jaxb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.QueryResultsImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxbUnknownAdapter extends XmlAdapter<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(JaxbUnknownAdapter.class);
    
    private static final boolean ENCODE_STRINGS = Boolean.parseBoolean(System.getProperty("org.kie.xml.encode", "FALSE"));
   
    
    @Override
    public Object marshal(Object o) throws Exception {
        if ( o instanceof String ) {
            return stringToBase64String((String) o);
        } else if ( o instanceof List ) {
            List v = ( List ) o;
            return new JaxbListWrapper( v.toArray( new Object[v.size()]) );
        } else if ( o instanceof Map ){
            Map<Object, Object>  value = ( Map<Object, Object>  ) o;
            if (value == null || value.isEmpty()) {
                return new JaxbStringObjectPair[0];
            }
            
            List<JaxbObjectObjectPair> ret = new ArrayList<JaxbObjectObjectPair>(value.size());
            for (Map.Entry<Object, Object> entry : value.entrySet()) {
                Object obj = entry.getValue();
                Class<? extends Object> vClass = obj.getClass();
                
                if ( obj instanceof QueryResultsImpl) {
                    obj = new FlatQueryResults( (QueryResultsImpl)obj );
                } else if (obj instanceof FactHandle ) {
                    obj = ((InternalFactHandle)obj).toExternalForm();
                } else if (List.class.isAssignableFrom(vClass) && !JaxbListWrapper.class.equals(vClass)) {
                    obj = new JaxbListWrapper( ((List<?>) obj).toArray( new Object[((List<?>) obj).size()]) );;
                }
                ret.add(new JaxbObjectObjectPair((String)entry.getKey(), obj));
            }
            
            return ret.toArray(new JaxbObjectObjectPair[value.size()]);
        } else {
            return o;
        }
    }

    @Override
    public Object unmarshal(Object o) throws Exception {
        if ( o instanceof String ) {
            return base64StringToString((String) o);
        } else if ( o instanceof JaxbListWrapper ) {
            JaxbListWrapper v = ( JaxbListWrapper ) o;
            return Arrays.asList( v.getElements() );
        } else if (o instanceof JaxbObjectObjectPair[] ) {
            JaxbObjectObjectPair[] value = ( JaxbObjectObjectPair[] ) o;
            Map<Object, Object> r = new HashMap<Object, Object>();
            for( JaxbObjectObjectPair p : value ) {
                if ( p.getValue() instanceof JaxbListWrapper) {
                    r.put(p.getKey(), Arrays.asList( ((JaxbListWrapper)p.getValue()).getElements() ) );
                } else {
                    r.put(p.getKey(), p.getValue());
                }
            }
            return r;
        } else {
            return o;
        }
    }

    static String stringToBase64String(String in) { 
        if( ! ENCODE_STRINGS ) { 
            return in;
        }
        logger.debug("Encoding string to base64 [{}]", in);
        byte[] bytes = stringToBytes(in);
        return DatatypeConverter.printBase64Binary(bytes);
    }

    static String base64StringToString(String in) { 
        if( ! ENCODE_STRINGS ) { 
            return in;
        }
        logger.debug("Decoding string from base64 [{}]", in);
        byte [] bytes = DatatypeConverter.parseBase64Binary(in);
        return bytesToString(bytes);
    }
    
    // The following methods bypass issues with string encoding
    
    private static byte[] stringToBytes( String str ) {
        char[] chars = str.toCharArray();
        byte[] b = new byte[chars.length << 1];
        for( int ic = 0; ic < chars.length; ic++ ) {
            int ib = ic << 1;
            b[ib] = (byte) ((chars[ic] & 0xFF00) >> 8);
            b[ib + 1] = (byte) (chars[ic] & 0x00FF);
        }
        return b;
    }

    private static String bytesToString( byte[] bytes ) {
        char[] chars = new char[bytes.length >> 1];
        for( int ic = 0; ic < chars.length; ic++ ) {
            int ib = ic << 1;
            char c = (char) (((bytes[ib] & 0x00FF) << 8) + (bytes[ib + 1] & 0x00FF));
            chars[ic] = c;
        }
        return new String(chars);
    }
}
