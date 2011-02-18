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

package org.drools.core.util;

import java.lang.reflect.Array;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Taken from commons lang
 * 
 * <p>Operations on arrays, primitive arrays (like <code>int[]</code>) and
 * primitive wrapper arrays (like <code>Integer[]</code>).</p>
 * 
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code>
 * array input. However, an Object array that contains a <code>null</code>
 * element may throw an exception. Each method documents its behaviour.</p>
 *
 * @author Stephen Colebourne
 * @author Moritz Petersen
 * @author <a href="mailto:fredrik@westermarck.com">Fredrik Westermarck</a>
 * @author Nikolay Metchev
 * @author Matthew Hawthorne
 * @author Tim O'Brien
 * @author Pete Gieser
 * @author Gary Gregory
 * @author <a href="mailto:equinus100@hotmail.com">Ashwin S</a>
 * @author Maarten Coene
 * @since 2.0
 * @version $Id$
 */
public class ArrayUtils {
    // Taken from commons ArrayUtils

    public static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>Checks if the object is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     * 
     * @param array  the array to search through
     * @param objectToFind  the object to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(Object[] array,
                                   Object objectToFind) {
        return indexOf( array,
                        objectToFind ) != INDEX_NOT_FOUND;
    }

    // IndexOf search
    // ----------------------------------------------------------------------

    // Object IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given object in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     * 
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @return the index of the object within the array, 
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(Object[] array,
                              Object objectToFind) {
        return indexOf( array,
                        objectToFind,
                        0 );
    }

    /**
     * <p>Finds the index of the given object in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     * 
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @param startIndex  the index to start searching at
     * @return the index of the object within the array starting at the index,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(Object[] array,
                              Object objectToFind,
                              int startIndex) {
        if ( array == null ) {
            return INDEX_NOT_FOUND;
        }
        if ( startIndex < 0 ) {
            startIndex = 0;
        }
        if ( objectToFind == null ) {
            for ( int i = startIndex; i < array.length; i++ ) {
                if ( array[i] == null ) {
                    return i;
                }
            }
        } else {
            for ( int i = startIndex; i < array.length; i++ ) {
                if ( objectToFind.equals( array[i] ) ) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    public static int hashCode(Object[] array) {
        final int PRIME = 31;
        if ( array == null ) return 0;
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    public static Object[] copyOf(Object[] original,
                                  int newLength,
                                  Class newType) {
        Object[] arr = (newType == Object[].class) ? new Object[newLength] : (Object[]) Array.newInstance( newType.getComponentType(),
                                                                                                           newLength );
        int len = (original.length < newLength ? original.length : newLength);
        System.arraycopy( original,
                          0,
                          arr,
                          0,
                          len );
        return arr;
    }

    /**
     * @since 1.5
     */
    public static boolean deepEquals(Object[] a1,
                                     Object[] a2) {
        if ( a1 == a2 ) return true;
        if ( a1 == null || a2 == null ) return false;
        int len = a1.length;
        if ( len != a2.length ) return false;
        for ( int i = 0; i < len; i++ ) {
            Object e1 = a1[i];
            Object e2 = a2[i];
            if ( e1 == e2 ) continue;
            if ( e1 == null ) return false;
            boolean eq = (e1.getClass() != e2.getClass() || !e1.getClass().isArray()  || !e2.getClass().isArray() ) ? e1.equals( e2 ) : (e1 instanceof Object[] && e2 instanceof Object[]) ? deepEquals( (Object[]) e1,
                                                                                                                                                                          (Object[]) e2 ) : (e1 instanceof byte[] && e2 instanceof byte[]) ? equals( (byte[]) e1,
                                                                                                                                                                                                                                                     (byte[]) e2 ) : (e1 instanceof short[] && e2 instanceof short[]) ? equals( (short[]) e1,
                                                                                                                                                                                                                                                                                                                                (short[]) e2 ) : (e1 instanceof int[] && e2 instanceof int[]) ? equals( (int[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                        (int[]) e2 ) : (e1 instanceof long[] && e2 instanceof long[]) ? equals( (long[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                (long[]) e2 ) : (e1 instanceof char[] && e2 instanceof char[]) ? equals( (char[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         (char[]) e2 ) : (e1 instanceof boolean[] && e2 instanceof boolean[]) ? equals( (boolean[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        (boolean[]) e2 ) : (e1 instanceof float[] && e2 instanceof float[]) ? equals( (float[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      (float[]) e2 ) : (e1 instanceof double[] && e2 instanceof double[]) ? equals( (double[]) e1,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    (double[]) e2 ) : e1.equals( e2 );

            if ( !eq ) return false;
        }
        return true;
    }

    // Equality Testing

    public static boolean equals(long[] a,
                                 long[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(int[] a,
                                 int[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(short[] a,
                                 short a2[]) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(char[] a,
                                 char[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(byte[] a,
                                 byte[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(boolean[] a,
                                 boolean[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(double[] a,
                                 double[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(float[] a,
                                 float[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

    public static boolean equals(Object[] a,
                                 Object[] a2) {
        return java.util.Arrays.equals( a,
                                        a2 );
    }

}
