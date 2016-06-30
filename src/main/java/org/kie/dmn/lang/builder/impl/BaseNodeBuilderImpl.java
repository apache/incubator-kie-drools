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

package org.kie.dmn.lang.builder.impl;

import org.kie.dmn.lang.ast.BaseNode;
import org.kie.dmn.lang.builder.ASTBuilder;

/**
 * A base class for all NodeBuilders
 */
public class BaseNodeBuilderImpl<P extends ASTBuilder<?,? extends BaseNode>, T extends BaseNode>
        implements
        ASTBuilder<P, T> {

    protected T descr;
    protected P parent;

    protected BaseNodeBuilderImpl(
            final P parent,
            final T descr) {
        this.parent = parent;
        this.descr = descr;
    }

    public ASTBuilder<P, T> startLocation( int line,
                                             int column ) {
        descr.setStartLine( line );
        descr.setStartColumn( column );
        return this;
    }

    public ASTBuilder<P, T> endLocation( int line,
                                           int column ) {
        descr.setEndLine( line );
        descr.setEndColumn( column );
        return this;
    }

    public ASTBuilder<P, T> startCharacter( int offset ) {
        descr.setStartChar( offset );
        return this;
    }

    public ASTBuilder<P, T> endCharacter( int offset ) {
        descr.setEndChar( offset );
        return this;
    }

    public T getDescr() {
        return descr;
    }

    public P end() {
        return parent;
    }

}
