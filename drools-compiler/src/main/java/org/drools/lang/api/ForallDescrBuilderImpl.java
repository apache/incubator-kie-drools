/**
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

import org.drools.lang.descr.ForallDescr;

/**
 * An implementation for the CEDescrBuilder
 */
public class ForallDescrBuilderImpl<P extends DescrBuilder< ? >> extends BaseDescrBuilderImpl<ForallDescr>
    implements
    ForallDescrBuilder<P> {

    private P parent;

    public ForallDescrBuilderImpl(P parent) {
        super( new ForallDescr() );
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern( String type ) {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<ForallDescrBuilder<P>>( this,
                                                                                                                 type );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern() {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<ForallDescrBuilder<P>>( this );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    public P end() {
        return parent;
    }

}
