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
        setNextTuple( tuple, insertFirst );
        setPreviousTuple( insertFirst, tuple );
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
        setNextTuple( tuple, deleteFirst );
        setPreviousTuple( deleteFirst, tuple );
        deleteFirst = tuple;
        return false;
    }

    public boolean addNormalizedDelete(T tuple) {
        setStagedType( tuple, Tuple.NORMALIZED_DELETE );
        if ( normalizedDeleteFirst == null ) {
            normalizedDeleteFirst = tuple;
            return true;
        }
        setNextTuple( tuple, normalizedDeleteFirst );
        setPreviousTuple( normalizedDeleteFirst, tuple );
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
        setNextTuple( tuple, updateFirst );
        setPreviousTuple( updateFirst, tuple );
        updateFirst = tuple;
        return false;
    }

    public void removeInsert(T tuple) {
        setStagedType( tuple, Tuple.NONE );
        if ( tuple == insertFirst ) {
            T next = getNextTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            setInsertFirst( next );
        } else {
            T next = getNextTuple( tuple );
            T previous = getPreviousTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
        }
        tuple.clearStaged();
    }

    public void removeDelete(T tuple) {
        setStagedType( tuple, Tuple.NONE );
        if ( tuple == deleteFirst ) {
            T next = getNextTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            deleteFirst = (T) next;
        } else {
            T next = getNextTuple( tuple );
            T previous = getPreviousTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );

        }
        tuple.clearStaged();
    }

    public void removeUpdate(Tuple tuple) {
        setStagedType( (T) tuple, Tuple.NONE );
        if ( tuple == updateFirst ) {
            T next = getNextTuple( (T) tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            updateFirst = next;
        } else {
            T next = getNextTuple( (T) tuple );
            T previous = getPreviousTuple( (T) tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
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
                    current = getNextTuple( current );
                }
                T tuple = tupleSets.getInsertFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
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
                    current = getNextTuple( current );
                }
                T tuple = tupleSets.getDeleteFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
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
                    current = getNextTuple( current );
                }
                T tuple = tupleSets.getUpdateFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
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

    public void clear() {
        clear( getInsertFirst() );
        clear( getDeleteFirst() );
        clear( getUpdateFirst() );
        clear( getNormalizedDeleteFirst() );
        resetAll();
    }

    private void clear( T tuple ) {
        while ( tuple != null ) {
            T next = getNextTuple( tuple );
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
        for ( ; tuple != null; tuple = getNextTuple( (T) tuple ) ) {
            sbuilder.append( " " ).append( tuple ).append( "\n" );
        }
    }

    protected T getPreviousTuple( T tuple ) {
        return (T) tuple.getStagedPrevious();
    }

    protected void setPreviousTuple( T tuple, T stagedPrevious ) {
        tuple.setStagedPrevious( stagedPrevious );
    }

    protected T getNextTuple( T tuple ) {
        return tuple.getStagedNext();
    }

    protected void setNextTuple( T tuple, T stagedNext ) {
        tuple.setStagedNext( stagedNext );
    }

    protected void setStagedType( T tuple, short type ) {
        tuple.setStagedType( type );
    }

    protected short getStagedType( T tuple ) {
        return tuple.getStagedType();
    }
}
