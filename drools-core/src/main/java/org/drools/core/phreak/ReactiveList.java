/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
*/

package org.drools.core.phreak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;
import org.drools.core.spi.Tuple;

public class ReactiveList<T> extends ReactiveCollection<T, List<T>> implements List<T>{

    public ReactiveList() {
        this(new ArrayList<T>());
    }
    
    public ReactiveList(List<T> wrapped) {
        super(wrapped);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean result = wrapped.addAll(index, c);
        if (result) {
            for ( T element : c ) {
                ReactiveObjectUtil.notifyModification(element, getLeftTuples(), ModificationType.ADD);
                if ( element instanceof ReactiveObject ) {
                    for (Tuple lts : getLeftTuples()) {
                        ((ReactiveObject) element).addLeftTuple(lts);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public T get(int index) {
        return wrapped.get(index);
    }

    @Override
    public T set(int index, T element) {
        T previous = wrapped.set(index, element);
        if ( previous != element ) { // this is indeed intended != to check by reference
            ReactiveObjectUtil.notifyModification(element, getLeftTuples(), ModificationType.ADD);
            if ( element instanceof ReactiveObject ) {
                for (Tuple lts : getLeftTuples()) {
                    ((ReactiveObject) element).addLeftTuple(lts);
                }
            }
            
            if (previous instanceof ReactiveObject) {
                for (Tuple lts : getLeftTuples()) {
                    ((ReactiveObject) previous).removeLeftTuple(lts);
                }
            }
            ReactiveObjectUtil.notifyModification(previous, getLeftTuples(), ModificationType.REMOVE);
        }
        return previous;
    }
    
    @Override
    public void add(int index, T element) {
        wrapped.add(index, element);
        ReactiveObjectUtil.notifyModification(element, getLeftTuples(), ModificationType.ADD);
        if ( element instanceof ReactiveObject ) {
            for (Tuple lts : getLeftTuples()) {
                ((ReactiveObject) element).addLeftTuple(lts);
            }
        }
    }

    @Override
    public T remove(int index) {
        T result = wrapped.remove(index);
        if (result instanceof ReactiveObject) {
            for (Tuple lts : getLeftTuples()) {
                ((ReactiveObject) result).removeLeftTuple(lts);
            }
        }
        ReactiveObjectUtil.notifyModification(result, getLeftTuples(), ModificationType.REMOVE);
        return result;
    }

    @Override
    public int indexOf(Object o) {
        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return wrapped.lastIndexOf(o);
    }
    
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        ReactiveList<T> result = new ReactiveList<T>( wrapped.subList(fromIndex, toIndex) );
        for ( Tuple lts : getLeftTuples() ) {
            result.addLeftTuple( lts );
        }
        return result;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ReactiveListIterator( wrapped.listIterator() );
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ReactiveListIterator( wrapped.listIterator(index) );
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReactiveList[").append(wrapped).append("]");
        return builder.toString();
    }

    private class ReactiveListIterator extends ReactiveIterator<ListIterator<T>> implements ListIterator<T> {

        public ReactiveListIterator(ListIterator<T> wrapped) {
            super(wrapped);
        }

        @Override
        public int nextIndex() {
            return wrapped.nextIndex();
        }

        @Override
        public int previousIndex() {
            return wrapped.previousIndex();
        }
        
        @Override
        public boolean hasPrevious() {
            return wrapped.hasPrevious();
        }

        @Override
        public T previous() {
            last = wrapped.previous();
            return last;
        }

        @Override
        public void set(T e) {
            // As per ListIterator spec, This call can be made only if neither remove nor add have been called after the last call to next or previous:
            if ( last != null ) {
                wrapped.set(e);
                if ( last != e ) { // this is indeed intended != to check by reference
                    ReactiveObjectUtil.notifyModification(e, getLeftTuples(), ModificationType.ADD);
                    if ( e instanceof ReactiveObject ) {
                        for (Tuple lts : getLeftTuples()) {
                            ((ReactiveObject) e).addLeftTuple(lts);
                        }
                    }
                    
                    if (last instanceof ReactiveObject) {
                        for (Tuple lts : getLeftTuples()) {
                            ((ReactiveObject) last).removeLeftTuple(lts);
                        }
                    }
                    ReactiveObjectUtil.notifyModification(last, getLeftTuples(), ModificationType.REMOVE);
                }
                last = e;
            }
        }

        @Override
        public void add(T e) {
            wrapped.add(e);
            // the line above either throws UnsupportedOperationException or follows with:
            ReactiveObjectUtil.notifyModification(e, getLeftTuples(), ModificationType.ADD);
            if ( e instanceof ReactiveObject ) {
                for (Tuple lts : getLeftTuples()) {
                    ((ReactiveObject) e).addLeftTuple(lts);
                }
            }
            last = null;
        }
        
    }
}