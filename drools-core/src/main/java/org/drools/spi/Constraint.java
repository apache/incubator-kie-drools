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

package org.drools.spi;

import java.io.Externalizable;

import org.drools.rule.Declaration;

public interface Constraint
    extends
    RuleComponent,
    Externalizable,
    Cloneable {

    /**
     * Returns all the declarations required by the given
     * constraint implementation.
     *
     * @return
     */
    Declaration[] getRequiredDeclarations();

    /**
     * When a rule contains multiple logical branches, i.e., makes 
     * use of 'OR' CE, it is required to clone patterns and declarations
     * for each logical branch. Since this is done at ReteOO build
     * type, when constraints were already created, eventually
     * some constraints need to update their references to the
     * declarations.
     *
     * @param oldDecl
     * @param newDecl
     */
    void replaceDeclaration(Declaration oldDecl,
                            Declaration newDecl);

    /**
     * Clones the constraint
     * @return
     */
    public Constraint clone();

    /**
     * Returns the type of the constraint, either ALPHA, BETA or UNKNOWN
     *
     * @return
     */
    public ConstraintType getType();
    
    /**
     * Returns true in case this constraint is a temporal constraint
     * 
     * @return
     */
    public boolean isTemporal();
    
    /**
     * An enum for Constraint Types
     */
    public static enum ConstraintType {

        UNKNOWN("UNKNOWN"),
        ALPHA("ALPHA"),
        BETA("BETA");

        private String desc;

        private ConstraintType( String desc ) {
            this.desc = desc;
        }

        public String toString() {
            return "ConstraintType::"+this.desc;
        }
    }
    
}
