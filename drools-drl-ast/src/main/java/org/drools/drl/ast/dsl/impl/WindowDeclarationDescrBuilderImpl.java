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
package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.WindowDeclarationDescrBuilder;
import org.drools.drl.ast.descr.WindowDeclarationDescr;

public class WindowDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, WindowDeclarationDescr>
    implements
    WindowDeclarationDescrBuilder {

    protected WindowDeclarationDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent,
               new WindowDeclarationDescr() );
    }

    public WindowDeclarationDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public AnnotationDescrBuilder<WindowDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<WindowDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this,
                                                                                                                                          name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern( String type ) {
        PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern = new PatternDescrBuilderImpl<>( this,
                                                                                                                                 type );
        descr.setPattern( pattern.getDescr() );
        return pattern;
    }

    public PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern() {
        PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern = new PatternDescrBuilderImpl<>( this );
        descr.setPattern( pattern.getDescr() );
        return pattern;
    }

}
