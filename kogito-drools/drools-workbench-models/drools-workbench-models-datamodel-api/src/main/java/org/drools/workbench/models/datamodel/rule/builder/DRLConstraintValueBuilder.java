/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule.builder;

/**
 * A Helper class for building parts of DRL from higher-order representations
 * (i.e. Guided Rule Editor, Guided Template Rule Editor and Guided Decision
 * Table).
 */
public abstract class DRLConstraintValueBuilder {

    public static final String DEFAULT_DIALECT = "mvel";

    public static DRLConstraintValueBuilder getBuilder( String dialect ) {
        if ( DEFAULT_DIALECT.equalsIgnoreCase( dialect ) ) {
            return new MvelDRLConstraintValueBuilder();
        }
        return new JavaDRLConstraintValueBuilder();
    }

    /**
     * Concatenate a String to the provided buffer suitable for the fieldValue
     * and fieldType. Strings and Dates are escaped with double-quotes, whilst
     * Numerics, Booleans, (Java 1.5+) enums and all other fieldTypes are not
     * escaped at all. Guvnor-type enums are really a pick list of Strings and
     * in these cases the underlying fieldType is a String.
     * @param buf
     * @param constraintType
     * @param fieldType
     * @param fieldValue
     */
    public abstract void buildLHSFieldValue( StringBuilder buf,
                                             int constraintType,
                                             String fieldType,
                                             String fieldValue );

    /**
     * Concatenate a String to the provided buffer suitable for the fieldType
     * and fieldValue. Strings are escaped with double-quotes, Dates are wrapped
     * with a call to a pre-constructed SimpleDateFormatter, whilst Numerics,
     * Booleans, (Java 1.5+) enums and all other fieldTypes are not escaped at
     * all. Guvnor-type enums are really a pick list of Strings and in these
     * cases the underlying fieldType is a String.
     * @param buf
     * @param fieldType
     * @param fieldValue
     */
    public abstract void buildRHSFieldValue( StringBuilder buf,
                                             String fieldType,
                                             String fieldValue );
}
