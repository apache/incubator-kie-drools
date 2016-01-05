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
 *
 */

package org.drools.core.common;

import org.drools.core.spi.Tuple;

public class TupleSetsImpl<T extends Tuple> implements TupleSets<T> {

    private T insertFirst;
    private T deleteFirst;
    private T updateFirst;
    private T normalizedDeleteFirst;

    public TupleSetsImpl() { }

    TupleSetsImpl( T insertFirst, T updateFirst, T deleteFirst, T normalizedDeleteFirst ) {
        this.insertFirst = insertFirst;
        this.updateFirst = updateFirst;
        this.deleteFirst = deleteFirst;
        this.normalizedDeleteFirst = normalizedDeleteFirst;
    }

    public T getInsertFirst() {
        return this.insertFirst;
    }

    protected void setInsertFirst( T insertFirst ) {
        this.insertFirst = insertFirst;
    }

    public T getDeleteFirst() {
        return this.deleteFirst;
    }

    protected void setDeleteFirst( T deleteFirst ) {
        this.deleteFirst = deleteFirst;
    }

    public T getUpdateFirst() {
        return this.updateFirst;
    }

    protected void setUpdateFirst( T updateFirst ) {
        this.updateFirst = updateFirst;
    }

    public T getNormalizedDeleteFirst() {
        return normalizedDeleteFirst;
    }

    protected void setNormalizedDeleteFirst( T normalizedDeleteFirst ) {
        this.normalizedDeleteFirst = normalizedDeleteFirst;
    }

    public void resetAll() {
        setInsertFirst( null );
        setDeleteFirst( null );
        setUpdateFirst( null );
        setNormalizedDeleteFirst( null );
    }

    public boolean addInsert(T tuple) {
        if ( getStagedType( tuple ) == Tuple.UPDATE) {
            // do nothing, it's already staged as an update, which means it's already scheduled for eval too.
            return false;
        }

        setStagedType( tuple, Tuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = tuple;
            return true;
        }
        setStagedNext( tuple, insertFirst );
        setStagedPrevious( insertFirst, tuple );
        insertFirst = tuple;
        return false;
    }

    public boolean addDelete(T tuple) {
        switch ( getStagedType( tuple ) ) {
            // handle clash with already staged entries
            case Tuple.INSERT:
                removeInsert( tuple );
                return deleteFirst == null;
            case Tuple.UPDATE:
                removeUpdate( tuple );
                break;
        }

        setStagedType( tuple, Tuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = tuple;
            return true;
        }
        setStagedNext( tuple, deleteFirst );
        setStagedPrevious( deleteFirst, tuple );
        deleteFirst = tuple;
        return false;
    }

    public boolean addNormalizedDelete(T tuple) {
        setStagedType( tuple, Tuple.NORMALIZED_DELETE );
        if ( normalizedDeleteFirst == null ) {
            normalizedDeleteFirst = tuple;
            return true;
        }
        setStagedNext( tuple, normalizedDeleteFirst );
        setStagedPrevious( normalizedDeleteFirst, tuple );
        normalizedDeleteFirst = tuple;
        return false;
    }

    public boolean addUpdate(T tuple) {
        if ( getStagedType( tuple ) != Tuple.NONE) {
            // do nothing, it's already staged as insert, which means it's already scheduled for eval too.
            return false;
        }

        setStagedType( tuple, Tuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = tuple;
            return true;
        }
        setStagedNext( tuple, updateFirst );
        setStagedPrevious( updateFirst, tuple );
        updateFirst = tuple;
        return false;
    }

    public void removeInsert(T tuple) {
        setStagedType( tuple, Tuple.NONE );
        if ( tuple == insertFirst ) {
            T next = getStagedNext( tuple );
            if ( next != null ) {
                setStagedPrevious( next, null );
            }
            setInsertFirst( next );
        } else {
            T next = getStagedNext( tuple );
            T previous = getStagedPrevious( tuple );
            if ( next != null ) {
                setStagedPrevious( next, previous );
            }
            setStagedNext( previous, next );
        }
        tuple.clearStaged();
    }

    public void removeDelete(T tuple) {
        setStagedType( tuple, Tuple.NONE );
        if ( tuple == deleteFirst ) {
            T next = getStagedNext( tuple );
            if ( next != null ) {
                setStagedPrevious( next, null );
            }
            deleteFirst = (T) next;
        } else {
            T next = getStagedNext( tuple );
            T previous = getStagedPrevious( tuple );
            if ( next != null ) {
                setStagedPrevious( next, previous );
            }
            setStagedNext( previous, next );

        }
        tuple.clearStaged();
    }

    public void removeUpdate(Tuple tuple) {
        setStagedType( (T) tuple, Tuple.NONE );
        if ( tuple == updateFirst ) {
            T next = getStagedNext( (T) tuple );
            if ( next != null ) {
                setStagedPrevious( next, null );
            }
            updateFirst = next;
        } else {
            T next = getStagedNext( (T) tuple );
            T previous = getStagedPrevious( (T) tuple );
            if ( next != null ) {
                setStagedPrevious( next, previous );
            }
            setStagedNext( previous, next );
        }
        tuple.clearStaged();
    }

    public void addAllInserts(TupleSets<T> tupleSets) {
        if ( tupleSets.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                setInsertFirst( tupleSets.getInsertFirst() );
            } else {
                T current = insertFirst;
                T last = null;
                while ( current != null ) {
                    last = current;
                    current = getStagedNext( current );
                }
                T tuple = tupleSets.getInsertFirst();
                setStagedNext( last, tuple );
                setStagedPrevious( tuple, last );
            }
            ( (TupleSetsImpl) tupleSets ).setInsertFirst( null );
        }
    }

    public void addAllDeletes(TupleSets<T> tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                setDeleteFirst( tupleSets.getDeleteFirst() );
            } else {
                T current = deleteFirst;
                T last = null;
                while ( current != null ) {
                    last = current;
                    current = getStagedNext( current );
                }
                T tuple = tupleSets.getDeleteFirst();
                setStagedNext( last, tuple );
                setStagedPrevious( tuple, last );
            }
            ((TupleSetsImpl) tupleSets).setDeleteFirst( null );
        }
    }

    public void addAllUpdates(TupleSets<T> tupleSets) {
        if ( tupleSets.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                setUpdateFirst( tupleSets.getUpdateFirst() );
            } else {
                T current = updateFirst;
                T last = null;
                while ( current != null ) {
                    last = current;
                    current = getStagedNext( current );
                }
                T tuple = tupleSets.getUpdateFirst();
                setStagedNext( last, tuple );
                setStagedPrevious( tuple, last );
            }
            ( (TupleSetsImpl) tupleSets ).setUpdateFirst( null );
        }
    }

    public void addAll(TupleSets<T> source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

    public void addTo(TupleSets<T> target) {
        target.addAll( this );
    }

    @Override
    public TupleSets<T> takeAll() {
        TupleSets<T> clone = new TupleSetsImpl(insertFirst, updateFirst, deleteFirst, normalizedDeleteFirst);
        resetAll();
        return clone;
    }

    /**
     * clear also ensures all contained LeftTuples are cleared
     * reset does not touch any contained tuples
     */
    public void clear() {
        clear( getInsertFirst() );
        clear( getDeleteFirst() );
        clear( getUpdateFirst() );
        clear( getNormalizedDeleteFirst() );
        resetAll();
    }

    private void clear( T tuple ) {
        while ( tuple != null ) {
            T next = getStagedNext( tuple );
            tuple.clearStaged();
            tuple = next;
        }
    }

    @Override
    public boolean isEmpty() {
        return insertFirst == null && deleteFirst == null && updateFirst == null && normalizedDeleteFirst == null;
    }

    @Override
    public String toStringSizes() {
        return "TupleSets[hasInsert=" + (insertFirst != null) + ", hasDelete=" + (deleteFirst != null) + ", hasUpdate=" + (updateFirst != null) + "]";
    }

    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        appendSet( sbuilder, getInsertFirst() );

        sbuilder.append( "Deleted:\n" );
        appendSet( sbuilder, getDeleteFirst() );

        sbuilder.append( "Updated:\n" );
        appendSet( sbuilder, getUpdateFirst() );

        sbuilder.append( "Normalized Deleted:\n" );
        appendSet( sbuilder, getNormalizedDeleteFirst() );

        return sbuilder.toString();
    }

    private void appendSet( StringBuilder sbuilder, Tuple tuple ) {
        for ( ; tuple != null; tuple = getStagedNext( (T) tuple ) ) {
            sbuilder.append( " " ).append( tuple ).append( "\n" );
        }
    }

    protected T getStagedPrevious( T tuple ) {
        return (T) tuple.getStagedPrevious();
    }

    protected void setStagedPrevious( T tuple, T stagedPrevious ) {
        tuple.setStagedPrevious( stagedPrevious );
    }

    protected T getStagedNext( T tuple ) {
        return tuple.getStagedNext();
    }

    protected void setStagedNext( T tuple, T stagedNext ) {
        tuple.setStagedNext( stagedNext );
    }

    protected void setStagedType( T tuple, short type ) {
        tuple.setStagedType( type );
    }

    protected short getStagedType( T tuple ) {
        return tuple.getStagedType();
    }
}
