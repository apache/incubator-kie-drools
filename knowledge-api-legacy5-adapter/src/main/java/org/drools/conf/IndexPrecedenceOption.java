/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.conf;

/**
 * An Enum for Index Precedence option.
 *
 * drools.indexPrecedence = &lt;pattern|equality&gt;
 *
 * When creating indexes gives precedence to the equality constraints (default)
 * or to the first indexable constraint in the pattern.
 *
 * DEFAULT = equality
 */
public enum IndexPrecedenceOption implements SingleValueKnowledgeBaseOption {

    PATTERN_ORDER("pattern"),
    EQUALITY_PRIORITY("equality");

    /**
     * The property name for the index precedence option
     */
    public static final String PROPERTY_NAME = "drools.indexPrecedence";

    private String             string;

    IndexPrecedenceOption(String mode) {
        this.string = mode;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String getValue() {
        return string;
    }

    public String toString() {
        return "IndexPrecedenceOption( "+string+ " )";
    }

    public String toExternalForm() {
        return this.string;
    }

    public static IndexPrecedenceOption determineIndexPrecedence(String mode) {
        if ( PATTERN_ORDER.getValue().equalsIgnoreCase( mode ) ) {
            return PATTERN_ORDER;
        } else if ( EQUALITY_PRIORITY.getValue().equalsIgnoreCase( mode ) ) {
            return EQUALITY_PRIORITY;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + mode + "' for IndexPrecedence" );
    }

}
