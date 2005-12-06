package org.drools.reteoo;

/*
 * $Id: FactHandleList.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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
    Serializable
{
    /** Empty list for testing purposes only. */
    static final FactHandleList EMPTY_LIST = new FactHandleList( );

    /** The list of handles. */
    private final FactHandle[]  handles;

    /** The cached hash code value. */
    private final int           hashCode;

    /**
     * Private constructor for creating the {@link #EMPTY_LIST}.
     */
    private FactHandleList()
    {
        handles = new FactHandleImpl[0];
        hashCode = 0;
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
                          FactHandleList right)
    {
        this.handles = new FactHandle[Math.max( left.handles.length,
                                                right.handles.length )];

        System.arraycopy( left.handles,
                          0,
                          this.handles,
                          0,
                          left.handles.length );

        int hashCode = left.hashCode;
        FactHandle handle;

        for ( int i = right.handles.length - 1; i >= 0; i-- )
        {
            handle = right.handles[i];
            if ( handle != null && this.handles[i] == null )
            {
                this.handles[i] = handle;
                hashCode += handle.hashCode( );
            }
        }

        this.hashCode = hashCode;
    }

    /**
     * Single value constructor.
     * 
     * @param index
     *            The index at which the handle will be placed.
     * @param handle
     *            The handle to use.
     */
    public FactHandleList(int index,
                          FactHandle handle)
    {
        this.handles = new FactHandleImpl[index + 1];
        this.handles[index] = handle;
        this.hashCode = handle.hashCode( );
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
    public FactHandle get(int index)
    {
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
    public boolean contains(FactHandle handle)
    {
        for ( int i = handles.length - 1; i >= 0; i-- )
        {
            if ( handle.equals( handles[i] ) )
            {
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
    public boolean containsAll(FactHandleList other)
    {
        if ( other.handles.length > this.handles.length )
        {
            return false;
        }

        FactHandle handle;
        for ( int i = other.handles.length - 1; i >= 0; i-- )
        {
            handle = other.handles[i];
            if ( handle != null && !handle.equals( this.handles[i] ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtains the length of the list.
     * 
     * @return The length of the list, including all <code>null</code> values.
     */
    public int size()
    {
        return this.handles.length;
    }

    public int hashCode()
    {
        return this.hashCode;
    }

    public boolean equals(Object object)
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null || getClass( ) != object.getClass( ) )
        {
            return false;
        }

        return Arrays.equals( this.handles,
                              ((FactHandleList) object).handles );
    }
}
