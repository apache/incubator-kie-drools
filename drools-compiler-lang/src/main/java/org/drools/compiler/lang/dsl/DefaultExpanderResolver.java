/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.dsl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.ExpanderResolver;

/**
 * The default expander resolver will provide instances of the DefaultExpander.
 * The DefaultExpander uses templates to provide DSL and pseudo
 * natural language support.
 */
public class DefaultExpanderResolver
    implements
    ExpanderResolver {

    private final Map expanders = new HashMap();

    /**
     * Create an empty resolver, which you will then
     * call addExpander multiple times, to map a specific expander
     * with a name that will be found in the drl after the expander keyword.
     */
    public DefaultExpanderResolver() {
    }

    /**
     * This will load up a DSL from the reader specified.
     * This will make the expander available to any parser 
     * regardless of name.
     * 
     * The DSL expander will be the default expander.
     * 
     * This is the constructor most people should use.
     */
    public DefaultExpanderResolver(final Reader reader) throws IOException {
        final DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        if ( file.parseAndLoad( reader ) ) {
            final Expander expander = new DefaultExpander();
            expander.addDSLMapping( file.getMapping() );
            this.expanders.put( "*",
                                expander );
        } else {
            throw new RuntimeException( "Error parsing and loading DSL file." + file.getErrors() );
        }
    }

    /**
     * Add an expander with the given name, which will be used
     * by looking for the "expander" keyword in the DRL.
     * 
     * If a default expander is installed, it will always be returned
     * if none matching the given name can be found.
     * 
     * You don't need to use this unless you have multiple expanders/DSLs
     * involved when compiling multiple rule packages at the same time.
     * If you don't know what that sentence means, you probably don't need to use this method.
     */
    public void addExpander(final String name,
                            final Expander expander) {
        this.expanders.put( name,
                            expander );
    }

    public Expander get(final String name,
                        final String config) {
        if ( this.expanders.containsKey( name ) ) {
            return (Expander) this.expanders.get( name );
        } else {
            final Expander exp = (Expander) this.expanders.get( "*" );
            if ( exp == null ) {
                throw new IllegalArgumentException( "Unable to provide an expander for " + name + " or a default expander." );
            }
            return exp;
        }
    }

}
