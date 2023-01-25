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

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.Tuple;

public class LeftTupleSetsImpl implements LeftTupleSets {

    private LeftTuple insertFirst;
    private LeftTuple deleteFirst;
    private LeftTuple updateFirst;
    private LeftTuple normalizedDeleteFirst;

    private int insertSize;

    public LeftTupleSetsImpl() { }

    LeftTupleSetsImpl(LeftTuple insertFirst, LeftTuple updateFirst, LeftTuple deleteFirst, LeftTuple normalizedDeleteFirst, int insertSize ) {
        this.insertFirst = insertFirst;
        this.updateFirst = updateFirst;
        this.deleteFirst = deleteFirst;
        this.normalizedDeleteFirst = normalizedDeleteFirst;
        this.insertSize = insertSize;
    }

    public int getInsertSize() {
        return insertSize;
    }

    public LeftTuple getInsertFirst() {
        return this.insertFirst;
    }

    protected void setInsertFirst( LeftTuple insertFirst ) {
        this.insertFirst = insertFirst;
    }

    public LeftTuple getDeleteFirst() {
        return this.deleteFirst;
    }

    protected void setDeleteFirst( LeftTuple deleteFirst ) {
        this.deleteFirst = deleteFirst;
    }

    public LeftTuple getUpdateFirst() {
        return this.updateFirst;
    }

    protected void setUpdateFirst( LeftTuple updateFirst ) {
        this.updateFirst = updateFirst;
    }

    public LeftTuple getNormalizedDeleteFirst() {
        return normalizedDeleteFirst;
    }

    protected void setNormalizedDeleteFirst( LeftTuple normalizedDeleteFirst ) {
        this.normalizedDeleteFirst = normalizedDeleteFirst;
    }

    public void resetAll() {
        setInsertFirst( null );
        setDeleteFirst( null );
        setUpdateFirst( null );
        setNormalizedDeleteFirst( null );
        insertSize = 0;
    }

    public boolean addInsert(LeftTuple tuple) {
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

    public boolean addDelete(LeftTuple tuple) {
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

    public boolean addNormalizedDelete(LeftTuple tuple) {
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

    public boolean addUpdate(LeftTuple tuple) {
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

    public void removeInsert(LeftTuple tuple) {
        if ( tuple == insertFirst ) {
            LeftTuple next = getNextTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            setInsertFirst( next );
        } else {
            LeftTuple next = getNextTuple( tuple );
            LeftTuple previous = getPreviousTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
        }
        tuple.clearStaged();
        insertSize--;
    }

    public void removeDelete(LeftTuple tuple) {
        if ( tuple == deleteFirst ) {
            LeftTuple next = getNextTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            deleteFirst = next;
        } else {
            LeftTuple next = getNextTuple( tuple );
            LeftTuple previous = getPreviousTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );

        }
        tuple.clearStaged();
    }

    public void removeUpdate(LeftTuple tuple) {
        if ( tuple == updateFirst ) {
            LeftTuple next = getNextTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, null );
            }
            updateFirst = next;
        } else {
            LeftTuple next = getNextTuple( tuple );
            LeftTuple previous = getPreviousTuple( tuple );
            if ( next != null ) {
                setPreviousTuple( next, previous );
            }
            setNextTuple( previous, next );
        }
        tuple.clearStaged();
    }

    private void addAllInserts(TupleSets<LeftTuple> tupleSets) {
        if ( tupleSets.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                setInsertFirst( tupleSets.getInsertFirst() );
                insertSize = tupleSets.getInsertSize();
            } else {
                LeftTuple current = insertFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                LeftTuple tuple = tupleSets.getInsertFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
                insertSize = insertSize + tupleSets.getInsertSize();
            }
            ( (LeftTupleSetsImpl) tupleSets ).setInsertFirst( null );
        }
    }

    private void addAllDeletes(TupleSets<LeftTuple> tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                setDeleteFirst( tupleSets.getDeleteFirst() );
            } else {
                LeftTuple current = deleteFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                LeftTuple tuple = tupleSets.getDeleteFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
            }
            ((LeftTupleSetsImpl) tupleSets).setDeleteFirst( null );
        }
    }

    private void addAllUpdates(TupleSets<LeftTuple> tupleSets) {
        if ( tupleSets.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                setUpdateFirst( tupleSets.getUpdateFirst() );
            } else {
                LeftTuple current = updateFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = getNextTuple( current );
                }
                LeftTuple tuple = tupleSets.getUpdateFirst();
                setNextTuple( last, tuple );
                setPreviousTuple( tuple, last );
            }
            ( (LeftTupleSetsImpl) tupleSets ).setUpdateFirst( null );
        }
    }

    public void addAll(TupleSets<LeftTuple> source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

    public void addTo(TupleSets<LeftTuple> target) {
        target.addAll( this );
    }

    @Override
    public LeftTupleSets takeAll() {
        LeftTupleSets clone = new LeftTupleSetsImpl(insertFirst, updateFirst, deleteFirst, normalizedDeleteFirst, insertSize);
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

    private void clear( LeftTuple tuple ) {
        while ( tuple != null ) {
            LeftTuple next = getNextTuple( tuple );
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

    private void appendSet( StringBuilder sbuilder, LeftTuple tuple ) {
        for ( ; tuple != null; tuple = getNextTuple( tuple ) ) {
            sbuilder.append( " " ).append( tuple ).append( "\n" );
        }
    }

    protected LeftTuple getPreviousTuple( LeftTuple tuple ) {
        return tuple.getStagedPrevious();
    }

    protected void setPreviousTuple( LeftTuple tuple, LeftTuple stagedPrevious ) {
        tuple.setStagedPrevious( stagedPrevious );
    }

    protected LeftTuple getNextTuple( LeftTuple tuple ) {
        return tuple.getStagedNext();
    }

    protected void setNextTuple( LeftTuple tuple, LeftTuple stagedNext ) {
        tuple.setStagedNext( stagedNext );
    }

    protected void setStagedType( LeftTuple tuple, short type ) {
        tuple.setStagedType( type );
    }

    protected short getStagedType( LeftTuple tuple ) {
        return tuple.getStagedType();
    }
}
