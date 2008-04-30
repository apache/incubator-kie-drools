/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Jul 5, 2007
 */
package org.drools.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.drools.base.ShadowProxy;

/**
 * A class with simple utility methods for shadowing
 * 
 * @author etirelli
 */
public class ShadowProxyUtils {

    /* Collections.UnmodifiableCollection is a package
     * private class, thus the confusing bit above
     * to get a Class to compare to. */
    private static final Class UNMODIFIABLE_MAP        = Collections.unmodifiableMap( Collections.EMPTY_MAP ).getClass();
    private static final Class UNMODIFIABLE_COLLECTION = Collections.unmodifiableCollection( Collections.EMPTY_LIST ).getClass();

    private ShadowProxyUtils() {
    }

    public static Object cloneObject(Object original) {
        Object clone = null;
        if ( original instanceof Cloneable ) {
            try {
                Method cloneMethod = original.getClass().getMethod( "clone",
                                                                    new Class[0] );
                clone = cloneMethod.invoke( original,
                                            new Object[0] );
            } catch ( Exception e ) {
                /* Failed to clone.  Don't worry about it, and just return
                 * the original object. */
                clone = null;
            }
        }

        if ( clone == null ) {
            try {
                if ( original instanceof Map && 
                     original != Collections.EMPTY_MAP && 
                     !UNMODIFIABLE_MAP.isAssignableFrom( original.getClass() ) ) {
                    
                    /* empty and unmodifiable maps can't (and don't need to) be shadowed */
                    clone = original.getClass().newInstance();
                    ((Map) clone).putAll( (Map) original );
                    
                } else if ( original instanceof Collection && 
                            original != Collections.EMPTY_LIST && 
                            original != Collections.EMPTY_SET && 
                            !UNMODIFIABLE_COLLECTION.isAssignableFrom( original.getClass() ) ) {
                    
                    /* empty and unmodifiable collections can't (and don't need to) be shadowed */
                    clone = original.getClass().newInstance();
                    ((Collection) clone).addAll( (Collection) original );
                    
                } else if ( original.getClass().isArray() ) {
                    clone = cloneArray( original );
                }
                
            } catch ( Exception e ) {
                /* Failed to clone.  Don't worry about it, and just return
                 * the original object. */
                clone = null;
            }
        }

        if ( clone == null ) {
            clone = original;
        }

        return clone;
    }

    public static Object cloneArray(Object original) {
        Object result = null;

        if ( original.getClass().isArray() ) {
            final int arrayLength = Array.getLength( original );

            if ( arrayLength == 0 ) {
                // empty arrays are immutable
                result = original;
            } else {
                final Class componentType = original.getClass().getComponentType();

                // Even though arrays implicitly have a public clone(), it
                // cannot be invoked reflectively, so need to do copy construction:
                result = Array.newInstance( componentType,
                                            arrayLength );

                if ( componentType.isArray() ) {
                    for ( int i = 0; i < arrayLength; i++ ) {
                        Array.set( result,
                                   i,
                                   cloneArray( Array.get( original,
                                                          i ) ) );
                    }
                } else {
                    System.arraycopy( original,
                                      0,
                                      result,
                                      0,
                                      arrayLength );
                }
            }
        }
        return result;
    }
    
    /**
     * Checks if a collection contains a given object taking
     * appropriate care with the presence or not of shadow proxies
     * 
     * @param col
     * @param obj
     * @return
     */
    public static boolean contains( Collection col, Object obj ) {
        if( col == null )
            return false;
        if( obj instanceof ShadowProxy ) {
            // if obj is a shadow proxy, its equals() method
            // implementation will take appropriate care when
            // the collection is checked.
            return col.contains( obj );
        } else {
            // otherwise, iterate over the collection 
            // comparing each element. If the element
            // is a shadow proxy, there is no problem, since
            // its equals() method implementation handles 
            // comparisons with non shadowed objects appropriately
            for( Iterator it = col.iterator(); it.hasNext(); ) {
                Object next = it.next();
                if( next == null && obj == null ||
                    next != null && next.equals( obj ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if an array contains a given object taking
     * appropriate care with the presence or not of shadow proxies
     * 
     * @param col
     * @param obj
     * @return
     */
    public static boolean contains( Object[] array, Object obj ) {
        if( array == null )
            return false;
        if( obj instanceof ShadowProxy ) {
            // if obj is a shadow proxy, its equals() method
            // implementation will take appropriate care when
            // the array is checked.
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == null && obj == null ||
                    obj != null && obj.equals( array[i] )) {
                    return true;
                }
            }
        } else {
            // otherwise, iterate over the array 
            // comparing each element. If the element
            // is a shadow proxy, there is no problem, since
            // its equals() method implementation handles 
            // comparisons with non shadowed objects appropriately
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == null && obj == null ||
                    array[i] != null && array[i].equals( obj )) {
                    return true;
                }
            }
        }
        return false;
    }

}
