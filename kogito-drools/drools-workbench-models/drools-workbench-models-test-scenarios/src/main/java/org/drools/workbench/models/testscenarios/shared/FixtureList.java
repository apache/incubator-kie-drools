/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FixtureList
        implements
        List<Fixture>,
        Fixture {

    private static final long serialVersionUID = 510l;

    private ArrayList<Fixture> list = new ArrayList<Fixture>();

    public FactData getFirstFactData() {
        for ( Fixture fixture : this ) {
            if ( fixture instanceof FactData ) {
                return (FactData) fixture;
            }
        }
        return null;
    }

    public boolean isFieldNameInUse( final String fieldName ) {
        for ( Fixture fixture : this ) {
            if ( fixture instanceof FactData && ( (FactData) fixture ).isFieldNameInUse( fieldName ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains( final Object o ) {
        return list.contains( o );
    }

    @Override
    public Iterator<Fixture> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray( T[] ts ) {
        return list.toArray( ts );
    }

    @Override
    public boolean add( final Fixture fixture ) {
        return list.add( fixture );
    }

    @Override
    public boolean remove( final Object o ) {
        return list.remove( o );
    }

    @Override
    public boolean containsAll( final Collection<?> objects ) {
        return list.containsAll( objects );
    }

    @Override
    public boolean addAll( final Collection<? extends Fixture> fixtures ) {
        return list.addAll( fixtures );
    }

    @Override
    public boolean addAll( final int i,
                           final Collection<? extends Fixture> fixtures ) {
        return list.addAll( i, fixtures );
    }

    @Override
    public boolean removeAll( final Collection<?> objects ) {
        return list.removeAll( objects );
    }

    @Override
    public boolean retainAll( final Collection<?> objects ) {
        return list.retainAll( objects );
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Fixture get( final int i ) {
        return list.get( i );
    }

    @Override
    public Fixture set( final int i,
                        final Fixture fixture ) {
        return list.set( i, fixture );
    }

    @Override
    public void add( final int i,
                     final Fixture fixture ) {
        list.add( i, fixture );
    }

    @Override
    public Fixture remove( final int i ) {
        return list.remove( i );
    }

    @Override
    public int indexOf( final Object o ) {
        return list.indexOf( o );
    }

    @Override
    public int lastIndexOf( final Object o ) {
        return list.lastIndexOf( o );
    }

    @Override
    public ListIterator<Fixture> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Fixture> listIterator( final int i ) {
        return list.listIterator( i );
    }

    @Override
    public List<Fixture> subList( final int i,
                                  final int i1 ) {
        return list.subList( i, i1 );
    }
}
