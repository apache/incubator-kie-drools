/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Context {

    private Map<String, Object> entries = new HashMap<>(  );

    public Map<String, Object> getEntries() {
        return entries;
    }

    public Object addEntry( String name, Object value ) {
        return this.entries.put( name, value );
    }

    public Object getEntry( String name ) {
        return this.entries.get( name );
    }

    @Override
    public String toString() {
        return entries.entrySet()
                .stream()
                .map( e -> e.getKey() + ": "+ e.getValue() )
                .collect( Collectors.joining(",\n    ", "{\n    ", "\n}\n") );
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Context) ) return false;

        Context context = (Context) o;

        return entries != null ? entries.equals( context.entries ) : context.entries == null;

    }

    @Override
    public int hashCode() {
        return entries != null ? entries.hashCode() : 0;
    }
}
