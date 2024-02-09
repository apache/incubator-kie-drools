/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.api;

public enum HitPolicy {

    UNIQUE( "UNIQUE" ),
    FIRST( "FIRST" ),
    PRIORITY( "PRIORITY" ),
    ANY( "ANY" ),
    COLLECT( "COLLECT" ),
    RULE_ORDER( "RULE ORDER" ),
    OUTPUT_ORDER( "OUTPUT ORDER" );

    private final String value;

    HitPolicy( final String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HitPolicy fromValue( final String v ) {
        for ( HitPolicy c : HitPolicy.values() ) {
            if ( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

    public boolean isMultiHit() {
        switch ( this ) {
            case RULE_ORDER:
            case OUTPUT_ORDER:
            case COLLECT:
                return true;
            default:
                return false;
        }
    }
}
