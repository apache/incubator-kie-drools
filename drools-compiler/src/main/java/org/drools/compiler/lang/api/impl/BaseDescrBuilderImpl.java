/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.descr.BaseDescr;

/**
 * A base class for all DescrBuilders
 */
public class BaseDescrBuilderImpl<P extends DescrBuilder<?,?>, T extends BaseDescr>
    implements
    DescrBuilder<P, T> {

    protected T descr;
    protected P parent;

    protected BaseDescrBuilderImpl(final P parent, 
                                   final T descr) {
        this.parent = parent;
        this.descr = descr;
    }

    public DescrBuilder<P, T> startLocation( int line,
                                       int column ) {
        descr.setLocation( line,
                           column );
        return this;
    }

    public DescrBuilder<P, T> endLocation( int line,
                                     int column ) {
        descr.setEndLocation( line,
                              column );
        return this;
    }

    public DescrBuilder<P, T> startCharacter( int offset ) {
        descr.setStartCharacter( offset );
        return this;
    }

    public DescrBuilder<P, T> endCharacter( int offset ) {
        descr.setEndCharacter( offset );
        return this;
    }

    public T getDescr() {
        return descr;
    }
    
    public P end() {
        return parent;
    }

}
