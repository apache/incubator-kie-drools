/*
 * Copyright 2008 JBoss Inc
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
 *
 * Created on Feb 12, 2008
 */

package org.drools.rule;

import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Constraint;

/**
 * A base class for constraints
 * 
 * @author etirelli
 */
public abstract class MutableTypeConstraint
    implements
    AlphaNodeFieldConstraint,
    BetaNodeFieldConstraint {

    private Constraint.ConstraintType type = Constraint.ConstraintType.UNKNOWN;
    
    public void setType( ConstraintType type ) {
        this.type = type;
    }
    
    public ConstraintType getType() {
        return this.type;
    }

    public abstract Object clone();
}
