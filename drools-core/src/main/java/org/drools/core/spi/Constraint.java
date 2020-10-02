/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import java.io.Externalizable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.util.bitmask.BitMask;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

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
    Constraint clone();

    /**
     * Returns the type of the constraint, either ALPHA, BETA or UNKNOWN
     *
     * @return
     */
    ConstraintType getType();
    
    /**
     * Returns true in case this constraint is a temporal constraint
     * 
     * @return
     */
    boolean isTemporal();

    default BitMask getListenedPropertyMask( Class modifiedClass, List<String> settableProperties ) {
        return allSetButTraitBitMask();
    }

    default boolean equals(Object object, InternalKnowledgeBase kbase) {
        return this.equals( object );
    }

    default void registerEvaluationContext(BuildContext buildContext) { }

    default Collection<String> getPackageNames() {
        return Collections.emptyList();
    }
    default void addPackageNames(Collection<String> otherPkgs) { }

    /**
     * An enum for Constraint Types
     */
    public static enum ConstraintType {

        UNKNOWN("UNKNOWN"),
        ALPHA("ALPHA"),
        BETA("BETA"),
        XPATH("XPATH");

        private String desc;

        private ConstraintType( String desc ) {
            this.desc = desc;
        }

        public String toString() {
            return "ConstraintType::"+this.desc;
        }
    }
    
}
