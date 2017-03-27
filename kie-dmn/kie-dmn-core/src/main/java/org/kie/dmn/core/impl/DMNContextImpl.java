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

package org.kie.dmn.core.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;

public class DMNContextImpl
        implements DMNContext {
    private static final String DEFAULT_IDENT = "    ";

    private Map<String, Object> entries    = new LinkedHashMap<String, Object>();

    public DMNContextImpl() {
    }

    public DMNContextImpl(Map<String, Object> entries) {
        this.entries = entries;
    }

    @Override
    public Object set(String name, Object value) {
        return entries.put( name, value );
    }

    @Override
    public Object get(String name) {
        return entries.get( name );
    }

    @Override
    public Map<String, Object> getAll() {
        return entries;
    }

    @Override
    public boolean isDefined(String name) {
        return entries.containsKey( name );
    }

    @Override
    public DMNContext clone() {
        return new DMNContextImpl( new LinkedHashMap<>( entries ) );
    }

    @Override
    public String toString() {
        return printContext( entries, "" );
    }

    private String printContext(Map<String, Object> context, String ident ) {
        StringBuilder builder = new StringBuilder(  );
        builder.append( "{\n" );
        for( Map.Entry e : context.entrySet() ) {
            builder.append( ident )
                    .append( DEFAULT_IDENT )
                    .append( e.getKey() )
                    .append( ": " );
            if( e.getValue() instanceof Map ) {
                builder.append( printContext( (Map<String, Object>) e.getValue(), ident + DEFAULT_IDENT ) );
            } else {
                builder.append( e.getValue() )
                        .append( "\n" );
            }
        }
        builder.append( ident+"}\n" );
        return builder.toString();
    }

}
