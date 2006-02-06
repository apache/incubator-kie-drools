package org.drools.reteoo;
/*
 * Copyright 2005 JBoss Inc
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


import java.io.Serializable;
import java.util.Arrays;

import org.drools.FactHandle;
import org.drools.rule.Declaration;

/**
 * Specialised array of {@link FactHandle}s intended to be keyed by a
 * {@link Declaration}s index. The list only ever contains as many elements as
 * necessary to hold a handle at the position specified by the largest
 * Declaration index. As a result, the list will neccessarily contain
 * <code>NULL</code> values.
 * 
 * This class exists purely for performance reasons and as such, many
 * assumptions have been made regarding behaviour based on know usage.
 * Therefore, this class should in no way be considered a general purpose data
 * structure. Hence it resides in this package and not a more generic "util"
 * package.
 * 
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
final class FactHandleList
    implements
    Serializable {
    
    /** Empty list for testing purposes only. */
    static final FactHandleList EMPTY_LIST = new FactHandleList();

    /** The list of handles. */
    private final FactHandleImpl[]  handles;

    /** The cached hash code value. */
    private final int           hashCode;

    /**
     * Private constructor for creating the {@link #EMPTY_LIST}.
     */
    private FactHandleList() {
        this.handles = new FactHandleImpl[0];
        this.hashCode = 0;
    }    

    /**
     * Join two lists.
     * 
     * @param left
     *            The left list.
     * @param right
     *            The right list.
     */
    public FactHandleList(FactHandleList left,
                          FactHandleImpl handle) {
        this.handles = new FactHandleImpl[left.handles.length+1];

        System.arraycopy( left.handles,
                          0,
                          this.handles,
                          0,
                          left.handles.length );
        
        this.handles[left.handles.length] = handle;

        this.hashCode = left.hashCode + handle.hashCode(); 
    }

    /**
     * Single value constructor.
     * 
     * @param index
     *            The index at which the handle will be placed.
     * @param handle
     *            The handle to use.
     */
    public FactHandleList(FactHandleImpl handle) {
        this.handles = new FactHandleImpl[] { handle };
        this.hashCode = handle.hashCode();
    }

    /**
     * Obtains the handle at a specified index.
     * 
     * @param index
     *            The position from which the handle should be obtained.
     * @return The handle; or <code>null</code> if no handle exists.
     * @throws ArrayIndexOutOfBoundsException
     *             if <code>index</code> &gt; {@link #size()}.
     */
    public FactHandleImpl get(int index) {
        return this.handles[index];
    }

    /**
     * Determines if the list contains a specified handle.
     * 
     * @param handle
     *            The handle to search for.
     * @return <code>true</code> if the handle is found; otherwise
     *         <code>false</code>
     */
    public boolean contains(FactHandle handle) {
        for ( int i = this.handles.length - 1; i >= 0; i-- ) {
            if ( handle.equals( this.handles[i] ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the list is a super-set of another list.
     * 
     * @param other
     *            The list to be checked.
     * @return <code>true</code> if this list contains all values from the
     *         other list; <code>false</code> otherwise.
     */
    public boolean containsAll(FactHandleList other) {
        if ( other.handles.length > this.handles.length ) {
            return false;
        }

        FactHandle handle;
        for ( int i = other.handles.length - 1; i >= 0; i-- ) {
            handle = other.handles[i];
            if ( handle != null && !handle.equals( this.handles[i] ) ) {
                return false;
            }
        }
        return true;
    }
    
    FactHandle[] getHandles() {
        return this.handles;
    }

    /**
     * Obtains the length of the list.
     * 
     * @return The length of the list, including all <code>null</code> values.
     */
    public int size() {
        return this.handles.length;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        return Arrays.equals( this.handles,
                              ((FactHandleList) object).handles );
    }
}
