/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a {@link XmlAdapter} for mapping Map<String, Object> instances
 * to classes/instances that <b>both</b> JAXB/XML and JSON can deal with.
 * <p>
 * The most important reason for the existence of this class is that it works well
 * with jackson JSON! JaxbMapAdapter, on the other hand, does <b>not</b>!
 */
public class StringKeyObjectValueMapXmlAdapter extends XmlAdapter<StringKeyObjectValueMap, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(StringKeyObjectValueMapXmlAdapter.class);

    @Override
    public StringKeyObjectValueMap marshal(Map<String, Object> map) throws Exception {
        if( map == null ) {
            return null;
        }
        StringKeyObjectValueMap xmlMap = new StringKeyObjectValueMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            StringKeyObjectValueEntry xmlEntry = createJaxbStringObjectMapEntry(value, key);
            xmlMap.addEntry(xmlEntry);
        }
        return xmlMap;
    }

    static StringKeyObjectValueEntry createJaxbStringObjectMapEntry(Object value, String key) {
        byte [] content = null;
        String className = null;
        if( value != null ) {
            className = value.getClass().getName();
            content = serializeObject(value, key);
        }

        return new StringKeyObjectValueEntry(key, className, content);
    }

    public static byte [] serializeObject(Object obj, String key) {
        Class<?> valueClass = obj.getClass();
        if( valueClass.getCanonicalName() == null ) {
            logger.error("Unable to serialize '" + key + "' " +
                    "because serialization of weird classes is not supported: " + valueClass.getName());
            return null;
        }
        if( ! (obj  instanceof Serializable) ) {
            logger.error("Unable to serialize '" + key + "' " +
                    "because " + valueClass.getName() + " is an unserializable class" );
            return null;
        }

        byte [] serializedBytes = null;
        try {
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bais);
            out.writeObject(obj);
            serializedBytes = bais.toByteArray();
        } catch( IOException ioe ) {
            logger.error("Unable to serialize '" + key + "' " + "because of exception: " + ioe.getMessage(), ioe );
            return null;
        }
        return serializedBytes;
    }

    @Override
    public Map<String, Object> unmarshal(StringKeyObjectValueMap xmlMap) {
        if( xmlMap == null ) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for( StringKeyObjectValueEntry xmlEntry : xmlMap.entries ) {
            String key = xmlEntry.getKey();
            Object value = deserializeObject(xmlEntry.getBytes(), xmlEntry.getClassName(), key);
            map.put(key, value);
        }
        return map;
    }

    public static Object deserializeObject(byte [] objBytes, String className, String key) {
        if( objBytes == null || objBytes.length == 0 ) {
            return null;
        }

        try {
            Class.forName(className);
        } catch( ClassNotFoundException cnfe ) {
            logger.error("Unable to deserialize '" + key + "' " + "because " + className + " is not on the classpath.", cnfe);
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
        Object value;
        try {
            ObjectInputStream input = new ObjectInputStream(bais);
            value = input.readObject();
        } catch (IOException ioe) {
            logger.error("Unable to deserialize '" + key + "' because of exception: " + ioe.getMessage(), ioe);
            return null;
        } catch (ClassNotFoundException cnfe) {
            logger.error("Unable to deserialize '" + key + "' because " + className + " is not on the classpath.", cnfe);
            return null;
        }
        return value;
    }

}
