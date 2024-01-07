/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.common;

import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.Tuple;

public class TupleSetsImpl implements TupleSets {

    private TupleImpl insertFirst;
    private TupleImpl deleteFirst;
    private TupleImpl updateFirst;
    private TupleImpl normalizedDeleteFirst;

    private int insertSize;

    public TupleSetsImpl() { }

    TupleSetsImpl(TupleImpl insertFirst, TupleImpl updateFirst, TupleImpl deleteFirst, TupleImpl normalizedDeleteFirst, int insertSize) {
        this.insertFirst = insertFirst;
        this.updateFirst = updateFirst;
        this.deleteFirst = deleteFirst;
        this.normalizedDeleteFirst = normalizedDeleteFirst;
        this.insertSize = insertSize;
    }

    public int getInsertSize() {
        return insertSize;
    }

    public TupleImpl getInsertFirst() {
        return this.insertFirst;
    }

    protected void setInsertFirst( TupleImpl insertFirst) {
        this.insertFirst = insertFirst;
    }

    public TupleImpl getDeleteFirst() {
        return this.deleteFirst;
    }

    protected void setDeleteFirst( TupleImpl deleteFirst) {
        this.deleteFirst = deleteFirst;
    }

    public TupleImpl getUpdateFirst() {
        return this.updateFirst;
    }

    protected void setUpdateFirst( TupleImpl updateFirst) {
        this.updateFirst = updateFirst;
    }

    public TupleImpl getNormalizedDeleteFirst() {
        return normalizedDeleteFirst;
    }

    protected void setNormalizedDeleteFirst( TupleImpl normalizedDeleteFirst) {
        this.normalizedDeleteFirst = normalizedDeleteFirst;
    }

    public void resetAll() {
        setInsertFirst( null );
        setDeleteFirst( null );
        setUpdateFirst( null );
        setNormalizedDeleteFirst( null );
        insertSize = 0;
    }

    public boolean addInsert(TupleImpl tuple) {
        if ( getStagedType( tuple ) == Tuple.UPDATE) {
            // do nothing, it's already staged as an update, which means it's already scheduled for eval too.
            return false;
        }

        setStagedType( tuple, Tuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = tuple;
            insertSize = 1;
            return true;
        }
        setNextTuple( tuple, insertFirst );
        setPreviousTuple( insertFirst, tuple );
        insertFirst = tuple;
        insertSize++;
        return false;
    }

    public boolean addDelete(TupleImpl tuple) {
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

    public boolean addNormalizedDelete(TupleImpl tuple) {
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

    public boolean addUpdate(TupleImpl tuple) {
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

    public void removeInsert(TupleImpl tuple) {
        if ( tuple == insertFirst ) {
            TupleImpl next = getNextTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            setInsertFirst( next );
        } else {
            TupleImpl next     = getNextTuple(tuple);
            TupleImpl previous = getPreviousTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
        }
        tuple.clearStaged();
        insertSize--;
    }

    public void removeDelete(TupleImpl tuple) {
        if ( tuple == deleteFirst ) {
            TupleImpl next = getNextTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            deleteFirst = next;
        } else {
            TupleImpl next     = getNextTuple(tuple);
            TupleImpl previous = getPreviousTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );

        }
        tuple.clearStaged();
    }

    public void removeUpdate(TupleImpl tuple) {
        if ( tuple == updateFirst ) {
            TupleImpl next = getNextTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            updateFirst = next;
        } else {
            TupleImpl next     = getNextTuple(tuple);
            TupleImpl previous = getPreviousTuple(tuple);
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
        }
        tuple.clearStaged();
    }

    private void addAllInserts(TupleSets tupleSets) {
        if ( tupleSets.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                setInsertFirst( tupleSets.getInsertFirst() );
                insertSize = tupleSets.getInsertSize();
            } else {
                TupleImpl current = insertFirst;
                TupleImpl last    = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                TupleImpl tuple = tupleSets.getInsertFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
                insertSize = insertSize + tupleSets.getInsertSize();
            }
            ( (TupleSetsImpl) tupleSets ).setInsertFirst( null );
        }
    }

    private void addAllDeletes(TupleSets tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                setDeleteFirst( tupleSets.getDeleteFirst() );
            } else {
                TupleImpl current = deleteFirst;
                TupleImpl last    = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                TupleImpl tuple = tupleSets.getDeleteFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
            }
            ((TupleSetsImpl) tupleSets).setDeleteFirst( null );
        }
    }

    private void addAllUpdates(TupleSets tupleSets) {
        if ( tupleSets.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                setUpdateFirst( tupleSets.getUpdateFirst() );
            } else {
                TupleImpl current = updateFirst;
                TupleImpl last    = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                TupleImpl tuple = tupleSets.getUpdateFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
            }
            ( (TupleSetsImpl) tupleSets ).setUpdateFirst( null );
        }
    }

    public void addAll(TupleSets source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

    public void addTo(TupleSets target) {
        target.addAll( this );
    }

    @Override
    public TupleSets takeAll() {
        TupleSets clone = new TupleSetsImpl(insertFirst, updateFirst, deleteFirst, normalizedDeleteFirst, insertSize);
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

    private void clear( TupleImpl tuple) {
        while ( tuple != null ) {
            TupleImpl next = getNextTuple(tuple);
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

    private void appendSet( StringBuilder sbuilder, TupleImpl tuple) {
        for ( ; tuple != null; tuple = getNextTuple( tuple ) ) {
            sbuilder.append( " " ).append( tuple ).append( "\n" );
        }
    }

    protected TupleImpl getPreviousTuple(TupleImpl tuple) {
        return tuple.getStagedPrevious();
    }

    protected void setPreviousTuple(TupleImpl tuple, TupleImpl stagedPrevious) {
        tuple.setStagedPrevious( stagedPrevious );
    }

    protected TupleImpl getNextTuple(TupleImpl tuple) {
        return tuple.getStagedNext();
    }

    protected void setNextTuple(TupleImpl tuple, TupleImpl stagedNext) {
        tuple.setStagedNext( stagedNext );
    }

    protected void setStagedType(TupleImpl tuple, short type) {
        tuple.setStagedType( type );
    }

    protected short getStagedType( TupleImpl tuple) {
        return tuple.getStagedType();
    }
}
