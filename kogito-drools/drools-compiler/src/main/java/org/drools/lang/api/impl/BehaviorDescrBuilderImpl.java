/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import java.util.List;

import org.drools.lang.descr.BehaviorDescr;

/**
 * A descr builder implementation for pattern behaviors
 */
public class BehaviorDescrBuilderImpl<P extends PatternDescrBuilder<?>> extends BaseDescrBuilderImpl<BehaviorDescr>
    implements
    BehaviorDescrBuilder<P> {

    private P parent;

    protected BehaviorDescrBuilderImpl(P parent) {
        super( new BehaviorDescr() );
        this.parent = parent;
        this.parent.getDescr().addBehavior( descr );
    }

    public BehaviorDescrBuilder<P> type( String type,
                                         String subtype ) {
        descr.setType( type );
        descr.setSubType( subtype );
        return this;
    }

    public BehaviorDescrBuilder<P> parameters( List<String> params ) {
        descr.setParameters( params );
        return this;
    }

    public P end() {
        return parent;
    }
}
