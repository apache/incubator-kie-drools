/**
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.manners;

public class Hobby {
    public static final String   stringH1     = "h1";
    public static final String   stringH2     = "h2";
    public static final String   stringH3     = "h3";
    public static final String   stringH4     = "h4";
    public static final String   stringH5     = "h5";

    public static final String[] hobbyStrings = new String[]{Hobby.stringH1, Hobby.stringH2, Hobby.stringH3, Hobby.stringH4, Hobby.stringH5};

    public static final Hobby    H1           = new Hobby( 1 );
    public static final Hobby    H2           = new Hobby( 2 );
    public static final Hobby    H3           = new Hobby( 3 );
    public static final Hobby    H4           = new Hobby( 4 );
    public static final Hobby    H5           = new Hobby( 5 );

    private String               hobbyStr;
    private int                  hobbyIndex;

    public Hobby() {
    }

    private Hobby(final int hobby) {
        this.hobbyIndex = hobby - 1;
        this.hobbyStr = Hobby.hobbyStrings[this.hobbyIndex];
    }

    public String getHobby() {
        return this.hobbyStr;
    }

    public final static Hobby resolve(final String hobby) {
        if ( Hobby.stringH1.equals( hobby ) ) {
            return Hobby.H1;
        } else if ( Hobby.stringH2.equals( hobby ) ) {
            return Hobby.H2;
        } else if ( Hobby.stringH3.equals( hobby ) ) {
            return Hobby.H3;
        } else if ( Hobby.stringH4.equals( hobby ) ) {
            return Hobby.H4;
        } else if ( Hobby.stringH5.equals( hobby ) ) {
            return Hobby.H5;
        } else {
            throw new RuntimeException( "Hobby '" + hobby + "' does not exist for Hobby Enum" );
        }
    }

    public String toString() {
        return getHobby();
    }

    public boolean equals(final Object object) {
        return (this == object);
    }

    public int hashCode() {
        return this.hobbyIndex;
    }

}