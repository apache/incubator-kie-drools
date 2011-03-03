/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.Restriction;

public class AndCompositeRestriction extends AbstractCompositeRestriction {

    private static final long serialVersionUID = 510l;

    public AndCompositeRestriction() {
    }

    public AndCompositeRestriction(final Restriction[] restriction) {
        super( restriction );
    }

    public boolean isAllowed(final InternalReadAccessor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context ) {
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( !this.restrictions[i].isAllowed( extractor,
                                                  handle,
                                                  workingMemory,
                                                  context ) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        CompositeContextEntry contextEntry = (CompositeContextEntry) context;
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( !this.restrictions[i].isAllowedCachedLeft( contextEntry.contextEntries[i],
                                                            handle ) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        CompositeContextEntry contextEntry = (CompositeContextEntry) context;
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( !this.restrictions[i].isAllowedCachedRight( tuple,
                                                             contextEntry.contextEntries[i] ) ) {
                return false;
            }
        }
        return true;
    }

    public Object clone() {
        Restriction[] clone = new Restriction[ this.restrictions.length ];
        for( int i = 0; i < clone.length; i++ ) {
            clone[i] = (Restriction) this.restrictions[i].clone();
        }
        return new AndCompositeRestriction( clone );
    }

    public Evaluator getEvaluator() {
        // TODO Auto-generated method stub
        return null;
    }

}
