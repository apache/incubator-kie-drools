/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.QueryResultsImpl;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.xml.jaxb.util.JaxbListWrapper.JaxbWrapperType;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Do not return an instance of Arrays.asList() -- that implementation is *not*
 * modifiable!
 *
 * 7.0 plans:
 * - move this at least to kie-internal
 * - use JaxbObjectObjectPair instead of JaxbStringObjectPair for maps
 */
@SuppressWarnings("unchecked")
public class JaxbUnknownAdapter extends XmlAdapter<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(JaxbUnknownAdapter.class);

    private static final Object PRESENT = new Object();

    @Override
    public Object marshal( Object o ) throws Exception {
        try {
            return recursiveMarshal(o, new IdentityHashMap<Object, Object>());
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to marshal " + o.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    private Object recursiveMarshal( Object o, Map<Object, Object> seenObjectsMap ) {
        if( o == null ) {
            return o;
        }
        if( seenObjectsMap.put(o, PRESENT) != null ) {
            throw new UnsupportedOperationException("Serialization of recursive data structures is not supported!");
        }
        try {
            if( o instanceof List ) {
                List list = (List) o;
                Object[] serializedArr = convertCollectionToSerializedArray(list, seenObjectsMap);
                return new JaxbListWrapper(serializedArr, JaxbWrapperType.LIST);
            } else if( o instanceof Set ) {
                Set set = (Set) o;
                Object[] serializedArr = convertCollectionToSerializedArray(set, seenObjectsMap);
                return new JaxbListWrapper(serializedArr, JaxbWrapperType.SET);
            } else if( o instanceof Map ) {
                Map<Object, Object> map = (Map<Object, Object>) o;
                List<JaxbStringObjectPair> pairList = new ArrayList<JaxbStringObjectPair>(map.size());
                if( map == null || map.isEmpty() ) {
                    pairList = Collections.EMPTY_LIST;
                }

                for( Entry<Object, Object> entry : map.entrySet() ) {
                    Object key = entry.getKey();
                    if( key != null && !(key instanceof String) ) {
                        throw new UnsupportedOperationException("Only String keys for Map structures are supported [key was a "
                                + key.getClass().getName() + "]");
                    }
                    // There's already a @XmlJavaTypeAdapter(JaxbUnknownAdapter.class) anno on the JaxbStringObjectPair.value field
                    pairList.add(new JaxbStringObjectPair((String) key, entry.getValue()));
                }

                return new JaxbListWrapper(pairList.toArray(new JaxbStringObjectPair[pairList.size()]), JaxbWrapperType.MAP);
            } else if( o.getClass().isArray() ) {
                // convert to serializable types
                int length = Array.getLength(o);
                Object [] serializedArr = new Object[length];
                for( int i = 0; i < length; ++i ) {
                    Object elem = convertObjectToSerializableVariant(Array.get(o, i), seenObjectsMap);
                    serializedArr[i] = elem;
                }

                // convert to JaxbListWrapper
                JaxbListWrapper wrapper =  new JaxbListWrapper(serializedArr, JaxbWrapperType.ARRAY);
                Class componentType = o.getClass().getComponentType();
                String componentTypeName = o.getClass().getComponentType().getCanonicalName();
                if( componentTypeName == null ) {
                   throw new UnsupportedOperationException("Local or anonymous classes are not supported for serialization: " + componentType.getName() );
                }
                wrapper.setComponentType(componentTypeName);
                return wrapper;
            } else {
                return o;
            }
        } finally {
            seenObjectsMap.remove(o);
        }
    }

    private Object[] convertCollectionToSerializedArray( Collection collection, Map<Object, Object> seenObjectsMap ) {
        List<Object> serializedList = new ArrayList<Object>(collection.size());
        for( Object elem : collection ) {
            elem = convertObjectToSerializableVariant(elem, seenObjectsMap);
            serializedList.add(elem);
        }
        return serializedList.toArray(new Object[serializedList.size()]);
    }

    private Object convertObjectToSerializableVariant( Object obj, Map<Object, Object> seenObjectsMap ) {
        if( obj == null ) {
            return null;
        }
        if( obj instanceof QueryResultsImpl ) {
            obj = new FlatQueryResults((QueryResultsImpl) obj);
        } else if( obj instanceof FactHandle ) {
            obj = DisconnectedFactHandle.newFrom((FactHandle) obj);
        } else if( !(obj instanceof JaxbListWrapper) && (obj instanceof Collection || obj instanceof Map) ) {
            obj = recursiveMarshal(obj, seenObjectsMap);
        }
        return obj;
    }

    @Override
    public Object unmarshal( Object o ) throws Exception {
        try {
            return recursiveUnmarhsal(o);
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to *un*marshal " + o.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    public Object recursiveUnmarhsal( Object o ) throws Exception {
        if( o instanceof JaxbListWrapper ) {
            JaxbListWrapper wrapper = (JaxbListWrapper) o;
            Object[] elements = wrapper.getElements();
            int size = 0;
            if( elements != null ) {
                size = elements.length;
            }
            if( wrapper.getType() == null ) {
                List<Object> list = new ArrayList<Object>(size);
                return convertSerializedElementsToCollection(elements, list);
            } else {
                switch ( wrapper.getType() ) {
                case LIST:
                    List<Object> list = new ArrayList<Object>(size);
                    return convertSerializedElementsToCollection(elements, list);
                case SET:
                    Set<Object> set = new HashSet<Object>(size);
                    return convertSerializedElementsToCollection(elements, set);
                case MAP:
                    Map<String, Object> map = new HashMap<String, Object>(size);
                    if( size > 0 ) {
                        for( Object keyValueObj : elements ) {
                            JaxbStringObjectPair keyValue = (JaxbStringObjectPair) keyValueObj;
                            Object key = keyValue.getKey();
                            Object value = convertSerializedObjectToObject(keyValue.getValue());
                            map.put(key.toString(), value);
                        }
                    }
                    return map;
                case ARRAY:
                    Object [] objArr = wrapper.getElements();
                    int length = objArr.length;
                    String componentTypeName = wrapper.getComponentType();
                    Class realArrComponentType = null;
                    realArrComponentType = getClass(componentTypeName);

                    // create and fill array
                    Object realArr = Array.newInstance(realArrComponentType, objArr.length);
                    for( int i = 0; i < length; ++i ) {
                        Array.set(realArr, i, objArr[i]);
                    }
                    return realArr;
                default:
                    throw new IllegalArgumentException("Unknown JAXB collection wrapper type: " + wrapper.getType().toString());
                }
            }
        } else if( o instanceof JaxbStringObjectPair[] ) {
            // backwards compatibile: remove in 7.0.x
            JaxbStringObjectPair[] value = (JaxbStringObjectPair[]) o;
            Map<Object, Object> r = new HashMap<Object, Object>();
            for( JaxbStringObjectPair p : value ) {
                if( p.getValue() instanceof JaxbListWrapper ) {
                    r.put(p.getKey(), new ArrayList(Arrays.asList(((JaxbListWrapper) p.getValue()).getElements())));
                } else {
                    r.put(p.getKey(), p.getValue());
                }
            }
            return r;
        } else {
            return o;
        }
    }

    // idea stolen from org.apache.commons.lang3.ClassUtils
    private static final Map<String, String> classToArrayTypeMap = new HashMap<String, String>();
    static {
        classToArrayTypeMap.put("int", "I");
        classToArrayTypeMap.put("boolean", "Z");
        classToArrayTypeMap.put("float", "F");
        classToArrayTypeMap.put("long", "J");
        classToArrayTypeMap.put("short", "S");
        classToArrayTypeMap.put("byte", "B");
        classToArrayTypeMap.put("double", "D");
        classToArrayTypeMap.put("char", "C");
    }

    private static Class getClass(String className) throws Exception {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = tccl == null ? JaxbUnknownAdapter.class.getClassLoader() : tccl;

        try {
            // String.contains() will be cheaper/faster than Map.contains()
            if( className.contains(".") ) {
                return Class.forName(className,true, loader);
            } else {
                // Thanks, org.apache.commons.lang3.ClassUtils!
                String arrClassName = classToArrayTypeMap.get(className);
                if( arrClassName == null ) {
                    throw new IllegalStateException("Unexpected class type encountered during deserialization: " + arrClassName );
                }
                arrClassName = "[" + arrClassName;
                return Class.forName(arrClassName, true, loader).getComponentType();
            }
        } catch( ClassNotFoundException cnfe ) {
            throw new IllegalStateException("Class '" + className + "' could not be found during deserialization: " + cnfe.getMessage(), cnfe );
        }
    }

    private Collection convertSerializedElementsToCollection( Object[] elements, Collection collection ) throws Exception {
        List<Object> list;
        if( elements == null ) {
            list = Collections.EMPTY_LIST;
        } else {
            list = new ArrayList<Object>(elements.length);
            for( Object elem : elements ) {
                elem = convertSerializedObjectToObject(elem);
                list.add(elem);
            }
        }
        collection.addAll(list);
        return collection;
    }

    private Object convertSerializedObjectToObject( Object element ) throws Exception {
        if( element == null ) {
            return element;
        }
        if( element instanceof JaxbListWrapper ) {
            element = unmarshal(element);
        }
        return element;
    }
}
