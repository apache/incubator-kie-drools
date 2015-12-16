/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.datamodel.auditlog;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * An Audit Log that filters entries added to it depending on the type of
 * entries the Log is configured to receive. Users of this log need therefore
 * not filter which entries should be appended as this is handled by the log
 * itself.
 */
public class AuditLog
        implements
        List<AuditLogEntry> {
    private static final long serialVersionUID = -8133752193392354305L;

    private AuditLogFilter filter;

    //Use a LinkedList so we can quickly insert items at the beginning of the List
    private LinkedList<AuditLogEntry> entries = new LinkedList<AuditLogEntry>();

    public AuditLog() {
    }

    public AuditLog( final AuditLogFilter filter ) {
        this.filter = filter;
    }

    public AuditLogFilter getAuditLogFilter() {
        return this.filter;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean contains( Object o ) {
        return entries.contains( o );
    }

    public Iterator<AuditLogEntry> iterator() {
        return entries.iterator();
    }

    public Object[] toArray() {
        return entries.toArray();
    }

    public <T> T[] toArray( T[] a ) {
        return entries.toArray( a );
    }

    /**
     * Add a new AuditLogEntry at the beginning of the list. This is different
     * behaviour to a regular List but it prevents the need to sort entries in
     * descending order.
     */
    public boolean add( AuditLogEntry e ) {
        if ( filter == null ) {
            throw new IllegalStateException( "AuditLogFilter has not been set. Please set before inserting entries." );
        }
        if ( filter.accept( e ) ) {
            entries.addFirst( e );
            return true;
        }
        return false;
    }

    public boolean remove( Object o ) {
        return entries.remove( o );
    }

    public boolean containsAll( Collection<?> c ) {
        return entries.containsAll( c );
    }

    public void clear() {
        entries.clear();
    }

    public boolean equals( Object o ) {
        return entries.equals( o );
    }

    public int hashCode() {
        return entries.hashCode();
    }

    public AuditLogEntry get( int index ) {
        return entries.get( index );
    }

    public AuditLogEntry remove( int index ) {
        return entries.remove( index );
    }

    public int indexOf( Object o ) {
        return entries.indexOf( o );
    }

    public int lastIndexOf( Object o ) {
        return entries.lastIndexOf( o );
    }

    public ListIterator<AuditLogEntry> listIterator() {
        return entries.listIterator();
    }

    public ListIterator<AuditLogEntry> listIterator( int index ) {
        return entries.listIterator( index );
    }

    public List<AuditLogEntry> subList( int fromIndex,
                                        int toIndex ) {
        return entries.subList( fromIndex,
                                toIndex );
    }

    /**
     * Not supported.
     */
    public boolean addAll( Collection<? extends AuditLogEntry> c ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public boolean addAll( int index,
                           Collection<? extends AuditLogEntry> c ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public boolean removeAll( Collection<?> c ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public boolean retainAll( Collection<?> c ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public AuditLogEntry set( int index,
                              AuditLogEntry element ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public void add( int index,
                     AuditLogEntry element ) {
        throw new UnsupportedOperationException();
    }

}
