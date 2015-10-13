/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.xml.jaxb.util.JaxbListWrapper.JaxbWrapperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxbListAdapter extends XmlAdapter<JaxbListWrapper, Collection> {

    protected static Logger logger = LoggerFactory.getLogger(JaxbListAdapter.class);

    private final static JaxbUnknownAdapter unknownAdapter = new JaxbUnknownAdapter();

    @Override
    public JaxbListWrapper marshal( Collection v ) throws Exception {
        try {
            if( v == null ) {
                return new JaxbListWrapper(new Object[0]);
            } else if( v instanceof List ) {
                Object [] marshalledArr = marshalUnknownCollection(v);
                return new JaxbListWrapper(marshalledArr, JaxbWrapperType.LIST);
            } else if( v instanceof Set ) {
                Object [] marshalledArr = marshalUnknownCollection(v);
                return new JaxbListWrapper(marshalledArr, JaxbWrapperType.SET);
            } else {
                throw new UnsupportedOperationException("Unsupported collection type: " + v.getClass());
            }
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to marshal " + v.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    private static Object [] marshalUnknownCollection(Collection objColl) throws Exception {
        Object [] wrapperArr = new Object[objColl.size()];
        int i = 0;
        for( Object obj : objColl ) {
            wrapperArr[i++] = unknownAdapter.marshal(obj);
        }
        return wrapperArr;
    }

    @Override
    public Collection unmarshal( JaxbListWrapper v ) throws Exception {
        try {
            if( v.getType() == null ) {
                // the Arrays.asList() impl is non-modifiable
                return new ArrayList(Arrays.asList(v.getElements()));
            } else {
                switch ( v.getType() ) {
                case LIST:
                    return new ArrayList(unmarshalWrappedArray(v.getElements()));
                case SET:
                    return new HashSet(unmarshalWrappedArray(v.getElements()));
                default:
                    throw new UnsupportedOperationException("Unsupported collection type: " + v.getType());
                }
            }
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to *un*marshal " + v.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    private static Collection unmarshalWrappedArray(Object [] objArr) throws Exception {
        if( objArr == null ) {
            return Collections.EMPTY_LIST;
        }
        List<Object> list = new ArrayList<Object>(objArr.length);
        for( Object obj : objArr ) {
            list.add(unknownAdapter.unmarshal(obj));
        }
        return list;
    }

}
