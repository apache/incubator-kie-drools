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

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.ForallDescrBuilder;
import org.drools.drl.ast.descr.ForallDescr;

/**
 * An implementation for the CEDescrBuilder
 */
public class ForallDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends BaseDescrBuilderImpl<P, ForallDescr>
    implements
    ForallDescrBuilder<P> {

    public ForallDescrBuilderImpl(P parent) {
        super( parent, new ForallDescr() );
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern( String type ) {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this,
                                                                                                                 type );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern() {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    public P end() {
        return parent;
    }

}
