/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.beliefsystem.defeasible;

public enum DefeasibilityStatus {
    DEFINITELY( "strict" ),
    DEFEASIBLY( "defeasibly" ),
    DEFEATEDLY( "defeater" ),
    UNDECIDABLY( "nil" );

    private String id;

    DefeasibilityStatus( String id ) {
        this.id = id;
    }

    public String getValue() {
        return id;
    }

    public static DefeasibilityStatus resolve( Object value ) {
        if ( value == null ) {
            return null;
        } else if ( DEFINITELY.id.equals( value ) ) {
            return DEFINITELY;
        } else if ( DEFEASIBLY.id.equals( value ) ) {
            return DEFEASIBLY;
        }  else if ( DEFEATEDLY.id.equals( value ) ) {
            return DEFEATEDLY;
        }
        return null;
    }
}
