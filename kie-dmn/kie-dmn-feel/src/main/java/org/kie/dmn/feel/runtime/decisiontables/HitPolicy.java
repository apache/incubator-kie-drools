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

package org.kie.dmn.feel.runtime.decisiontables;

public enum HitPolicy {
    UNIQUE( "U", "UNIQUE" ),
    FIRST( "F", "FIRST" ),
    PRIORITY( "P", "PRIORITY" ),
    ANY( "A", "ANY" ),
    COLLECT( "C", "COLLECT" ),
    COLLECT_SUM( "C+", "COLLECT SUM" ),
    COLLECT_COUNT( "C#", "COLLECT COUNT" ),
    COLLECT_MIN( "C<", "COLLECT MIN" ),
    COLLECT_MAX( "C>", "COLLECT MAX" ),
    RULE_ORDER( "R", "RULE ORDER" ),
    OUTPUT_ORDER( "O", "OUTPUT ORDER" );

    private final String shortName;
    private final String longName;

    HitPolicy(final String shortName, final String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public static HitPolicy fromString(String policy) {
        policy = policy.toUpperCase();
        for ( HitPolicy c : HitPolicy.values() ) {
            if ( c.shortName.equals( policy ) || c.longName.equals( policy ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "Unknown hit policy: " + policy );
    }
}
