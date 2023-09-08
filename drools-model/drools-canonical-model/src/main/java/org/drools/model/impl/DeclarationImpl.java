/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.impl;

import org.drools.model.Declaration;
import org.drools.model.DeclarationSource;
import org.drools.model.DomainClassMetadata;
import org.drools.model.Window;

public class DeclarationImpl<T> extends VariableImpl<T> implements Declaration<T> {
    private DeclarationSource source;
    private Window window;
    private DomainClassMetadata metadata;

    public DeclarationImpl(Class<T> type) {
        super(type);
    }

    public DeclarationImpl(Class<T> type, String name) {
        super(type, name);
    }

    @Override
    public DeclarationSource getSource() {
        return source;
    }

    public DeclarationImpl<T> setSource( DeclarationSource source ) {
        this.source = source;
        return this;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    public DeclarationImpl<T> setWindow( Window window ) {
        this.window = window;
        return this;
    }

    @Override
    public DomainClassMetadata getMetadata() {
        return metadata;
    }

    public DeclarationImpl<T> setMetadata( DomainClassMetadata metadata ) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public boolean isEqualTo( ModelComponent var ) {
        if ( !super.isEqualTo( var ) ) return false;
        if ( !(var instanceof DeclarationImpl) ) return false;
        DeclarationImpl decl = (DeclarationImpl) var;
        if ( source != null ? !source.equals( decl.source ) : decl.source != null ) return false;
        return ModelComponent.areEqualInModel(window, decl.window );
    }
}
