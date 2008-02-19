package org.drools.spi;

import java.io.Serializable;

import org.drools.rule.Declaration;

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

public interface Constraint
    extends
    RuleComponent,
    Cloneable {

    /**
     * Returns all the declarations required by the given 
     * constraint implementation.
     * 
     * @return
     */
    Declaration[] getRequiredDeclarations();

    /**
     * A constraint may be required to replace an old
     * declaration object by a new updated one
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
    public Object clone();

    /**
     * Returns the type of the constraint, either ALPHA, BETA or UNKNOWN
     * 
     * @return
     */
    public ConstraintType getType();
    
    /**
     * A java 1.4 type-safe enum
     */
    public static class ConstraintType implements Serializable {
        
        private static final long serialVersionUID = 4865182371013556266L;
        
        public static final ConstraintType UNKNOWN = new ConstraintType(0, "UNKNOWN");
        public static final ConstraintType ALPHA = new ConstraintType(1, "ALPHA");
        public static final ConstraintType BETA = new ConstraintType(2, "BETA");
        
        private final int type; 
        private final String desc;
        
        private ConstraintType( int type, String desc ) {
            this.type = type;
            this.desc = desc;
        }

        /**
         * @inheritDoc
         *
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + type;
            return result;
        }

        /**
         * @inheritDoc
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final ConstraintType other = (ConstraintType) obj;
            if ( type != other.type ) return false;
            return true;
        }
        
        public String toString() {
            return "ConstraintType::"+this.desc;
        }
    }
}