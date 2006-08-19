package org.drools.leaps;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.common.BetaNodeBinder;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Column;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Tuple;

/**
 * Collection of <code>Column</code> specific constraints
 * 
 * @author Alexander Bagerman
 * 
 */
public class ColumnConstraints implements Serializable {
    private Object                  classType;

    private final FieldConstraint[] alphaConstraints;

    private final boolean           alphaPresent;

    private final BetaNodeBinder    beta;

    private final boolean           betaPresent;

    public ColumnConstraints(final Column column,
                             final List alpha,
                             final BetaNodeBinder beta) {
        this.classType = LeapsBuilder.getLeapsClassType( column.getObjectType() );

        if ( beta != null ) {
            this.beta = beta;
            this.betaPresent = true;
        } else {
            this.beta = null;
            this.betaPresent = false;
        }
        if ( alpha != null && alpha.size() > 0 ) {
            this.alphaConstraints = (FieldConstraint[]) alpha.toArray( new FieldConstraint[0] );
            this.alphaPresent = true;
        } else {
            this.alphaConstraints = null;
            this.alphaPresent = false;
        }
    }

    protected final Object getClassType() {
        return this.classType;
    }

    protected final boolean isAllowed(final InternalFactHandle factHandle,
                                      final Tuple tuple,
                                      final WorkingMemory workingMemory) {
        return this.isAllowedAlpha( factHandle,
                                    tuple,
                                    workingMemory ) && this.isAllowedBeta( factHandle,
                                                                           tuple,
                                                                           workingMemory );
    }

    public final boolean isAllowedAlpha(final InternalFactHandle factHandle,
                                        final Tuple tuple,
                                        final WorkingMemory workingMemory) {
        if ( this.alphaPresent ) {
            for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                // escape immediately if some condition does not match
                if ( !this.alphaConstraints[i].isAllowed( factHandle.getObject(),
                                                          tuple,
                                                          workingMemory ) ) {
                    return false;
                }
            }
        }

        return true;
    }

    protected final boolean isAllowedBeta(final InternalFactHandle factHandle,
                                          final Tuple tuple,
                                          final WorkingMemory workingMemory) {
        if ( this.betaPresent ) {
            return this.beta.isAllowed( factHandle,
                                        tuple,
                                        workingMemory );
        }

        return true;
    }

    protected final boolean isAlphaPresent() {
        return this.alphaPresent;
    }
    
    protected FieldConstraint[] getAlphaContraints() {
        return this.alphaConstraints;
    }
    
    protected FieldConstraint[] getBetaContraints() {
        return this.beta.getConstraints( );
    }
}
