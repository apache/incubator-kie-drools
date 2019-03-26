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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FixturesMap
        implements Fixture,
                   Map<String, FixtureList> {

    private HashMap<String, FixtureList> map = new HashMap<String, FixtureList>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey( final Object o ) {
        return map.containsKey( o );
    }

    @Override
    public boolean containsValue( final Object o ) {
        return map.containsValue( o );
    }

    @Override
    public FixtureList get( Object o ) {
        return map.get( o );
    }

    @Override
    public FixtureList put( final String s,
                            final FixtureList fixtures ) {
        return map.put( s, fixtures );
    }

    @Override
    public FixtureList remove( Object o ) {
        return map.remove( o );
    }

    @Override
    public void putAll( final Map<? extends String, ? extends FixtureList> map ) {
        for ( String key : map.keySet() ) {
            this.map.put( key, map.get( key ) );
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<FixtureList> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, FixtureList>> entrySet() {
        return map.entrySet();
    }
}
