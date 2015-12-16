/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForallDescr extends BaseDescr
    implements
    ConditionalElementDescr {

    private static final long   serialVersionUID = 510l;

    private static final String BASE_IDENTIFIER  = "$__forallBaseIdentifier";

    private List<BaseDescr>     patterns;

    public ForallDescr() {
        this.patterns = new ArrayList<BaseDescr>( 2 );
    }

    /* (non-Javadoc)
     * @see org.kie.lang.descr.ConditionalElementDescr#addDescr(org.kie.lang.descr.BaseDescr)
     */
    public void addDescr(final BaseDescr baseDescr) {
        // cast to make sure we are adding a pattern descriptor
        this.patterns.add( baseDescr );
    }

    public void insertBeforeLast(final Class<?> clazz,
                                 final BaseDescr baseDescr) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    /* (non-Javadoc)
     * @see org.kie.lang.descr.ConditionalElementDescr#getDescrs()
     */
    public List<BaseDescr> getDescrs() {
        return this.patterns;
    }

    /**
     * Returns the base pattern from the forall CE
     * @return
     */
    public PatternDescr getBasePattern() {
        if ( this.patterns.size() > 1 ) {
            return (PatternDescr) this.patterns.get( 0 );
        } else if ( this.patterns.size() == 1 ) {
            // in case there is only one pattern, we do a rewrite, so:
            // forall( Cheese( type == "stilton" ) )
            // becomes
            // forall( BASE_IDENTIFIER : Cheese() Cheese( this == BASE_IDENTIFIER, type == "stilton" ) )
            PatternDescr original = (PatternDescr) this.patterns.get( 0 );
            PatternDescr base = (PatternDescr) original.clone();
            base.getDescrs().clear();
            base.setIdentifier( BASE_IDENTIFIER );
            return base;
        }
        return null;
    }

    /**
     * Returns the remaining patterns from the forall CE
     * @return
     */
    public List<BaseDescr> getRemainingPatterns() {
        if ( this.patterns.size() > 1 ) {
            return this.patterns.subList( 1,
                                          this.patterns.size() );
        } else if ( this.patterns.size() == 1 ) {
            // in case there is only one pattern, we do a rewrite, so:
            // forall( Cheese( type == "stilton" ) )
            // becomes
            // forall( BASE_IDENTIFIER : Cheese() Cheese( this == BASE_IDENTIFIER, type == "stilton" ) )
            PatternDescr original = (PatternDescr) this.patterns.get( 0 );
            PatternDescr remaining = (PatternDescr) original.clone();
            remaining.addConstraint( new ExprConstraintDescr( "this == " + BASE_IDENTIFIER ) );
            return Collections.singletonList( (BaseDescr)remaining );
        }
        return Collections.emptyList();
    }

    public void addOrMerge(BaseDescr baseDescr) {
        this.patterns.add( baseDescr );
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        return baseDescr == null ? false : patterns.remove(baseDescr);
    }

    @Override
    public String toString() {
        return "forall( "+patterns+" )";
    }
}
