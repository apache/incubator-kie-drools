/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_1;

public enum DecisionTableOrientation {

    RULE_AS_ROW( "Rule-as-Row" ),
    RULE_AS_COLUMN( "Rule-as-Column" ),
    CROSS_TABLE( "CrossTable" );

    private final String value;

    DecisionTableOrientation( String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DecisionTableOrientation fromValue( final String v ) {
        for ( DecisionTableOrientation c : DecisionTableOrientation.values() ) {
            if ( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

}
