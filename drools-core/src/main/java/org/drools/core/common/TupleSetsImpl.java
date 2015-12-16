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

    public T getDeleteFirst() {
        return this.deleteFirst;
    }

    public T getUpdateFirst() {
        return this.updateFirst;
    }

    public T getNormalizedDeleteFirst() {
        return normalizedDeleteFirst;
    }

    public void resetAll() {
        insertFirst = null;
        deleteFirst = null;
        updateFirst = null;
        normalizedDeleteFirst = null;
    }

    public boolean addInsert(T tuple) {
        if (tuple.getStagedType() == Tuple.UPDATE) {
            // do nothing, it's already staged as an update, which means it's already scheduled for eval too.
            return false;
        }

        tuple.setStagedType( Tuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = tuple;
            return true;
        }
        tuple.setStagedNext( insertFirst );
        insertFirst.setStagedPrevious( tuple );
        insertFirst = tuple;
        return false;
    }

    public boolean addDelete(T tuple) {
        switch ( tuple.getStagedType() ) {
            // handle clash with already staged entries
            case Tuple.INSERT:
                removeInsert( tuple );
                return deleteFirst == null;
            case Tuple.UPDATE:
                removeUpdate( tuple );
                break;
        }

        tuple.setStagedType( Tuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = tuple;
            return true;
        }
        tuple.setStagedNext( deleteFirst );
        deleteFirst.setStagedPrevious( tuple );
        deleteFirst = tuple;
        return false;
    }

    public boolean addNormalizedDelete(T tuple) {
        tuple.setStagedType( Tuple.NORMALIZED_DELETE );
        if ( normalizedDeleteFirst == null ) {
            normalizedDeleteFirst = tuple;
            return true;
        }
        tuple.setStagedNext( normalizedDeleteFirst );
        normalizedDeleteFirst.setStagedPrevious( tuple );
        normalizedDeleteFirst = tuple;
        return false;
    }

    public boolean addUpdate(T tuple) {
        if (tuple.getStagedType() != LeftTuple.NONE) {
            // do nothing, it's already staged as insert, which means it's already scheduled for eval too.
            return false;
        }

        tuple.setStagedType( Tuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = tuple;
            return true;
        }
        tuple.setStagedNext( updateFirst );
        updateFirst.setStagedPrevious( tuple );
        updateFirst = tuple;
        return false;
    }

    public void removeInsert(T tuple) {
        tuple.setStagedType( Tuple.NONE );
        if ( tuple == insertFirst ) {
            Tuple next = tuple.getStagedNext();
            if ( next != null ) {
                next.setStagedPrevious( null );
            }
            insertFirst = (T)next;
        } else {
            Tuple next = tuple.getStagedNext();
            Tuple previous = tuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagedPrevious( previous );
            }
            previous.setStagedNext( next );
        }
        tuple.clearStaged();
    }

    public void removeDelete(T tuple) {
        tuple.setStagedType( Tuple.NONE );
        if ( tuple == deleteFirst ) {
            Tuple next = tuple.getStagedNext();
            if ( next != null ) {
                next.setStagedPrevious( null );
            }
            deleteFirst = (T) next;
        } else {
            Tuple next = tuple.getStagedNext();
            Tuple previous = tuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagedPrevious( previous );
            }
            previous.setStagedNext( next );

        }
        tuple.clearStaged();
    }

    public void removeUpdate(Tuple tuple) {
        tuple.setStagedType( Tuple.NONE );
        if ( tuple == updateFirst ) {
            Tuple next = tuple.getStagedNext();
            if ( next != null ) {
                next.setStagedPrevious( null );
            }
            updateFirst = (T)next;
        } else {
            Tuple next = tuple.getStagedNext();
            Tuple previous = tuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagedPrevious( previous );
            }
            previous.setStagedNext( next );
        }
        tuple.clearStaged();
    }

    public void addAllInserts(TupleSets<T> tupleSets) {
        if ( tupleSets.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                insertFirst = tupleSets.getInsertFirst();
            } else {
                Tuple current = insertFirst;
                Tuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                Tuple tuple = tupleSets.getInsertFirst();
                last.setStagedNext( tuple );
                tuple.setStagedPrevious( last );
            }
            ( (TupleSetsImpl) tupleSets ).insertFirst = null;
        }
    }

    public void addAllDeletes(TupleSets<T> tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                deleteFirst = tupleSets.getDeleteFirst();
            } else {
                Tuple current = deleteFirst;
                Tuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                Tuple tuple = tupleSets.getDeleteFirst();
                last.setStagedNext( tuple );
                tuple.setStagedPrevious( last );
            }
            ((TupleSetsImpl) tupleSets).deleteFirst = null;
        }
    }

    public void addAllUpdates(TupleSets<T> tupleSets) {
        if ( tupleSets.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                updateFirst = tupleSets.getUpdateFirst();
            } else {
                Tuple current = updateFirst;
                Tuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                Tuple tuple = tupleSets.getUpdateFirst();
                last.setStagedNext( tuple );
                tuple.setStagedPrevious( last );
            }
            ( (TupleSetsImpl) tupleSets ).updateFirst = null;
        }
    }

    public void addAll(TupleSets<T> source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
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

    private void clear( Tuple tuple ) {
        while ( tuple != null ) {
            Tuple next =  tuple.getStagedNext();
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
        for ( ; tuple != null; tuple = tuple.getStagedNext() ) {
            sbuilder.append( " " ).append( tuple ).append( "\n" );
        }
    }
}
