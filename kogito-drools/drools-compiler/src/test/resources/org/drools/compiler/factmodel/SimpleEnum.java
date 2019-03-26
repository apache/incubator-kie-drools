/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.factmodel;


import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public enum SimpleEnum {


    MERCURY ( (Double) MVEL.eval("3.303e+23"), (Double) MVEL.eval("2.4397e6"), (String) MVEL.eval("Mercury") ),
    VENUS   ( 4.869e+24, 6.0518e6, "Venus" ),
    EARTH   ( 5.976e+24, 6.37814e6, "Earth" ),
    MARS    ( 6.421e+23, 3.3972e6, "Mars" ),
    JUPITER ( 1.9e+27,   7.1492e7, "Jupiter" ),
    SATURN  ( 5.688e+26, 6.0268e7, "Saturn" ),
    URANUS  ( 8.686e+25, 2.5559e7, "Uranus" ),
    NEPTUNE ( 1.024e+26, 2.4746e7, "Neptune" );


    private double mass;
    private double radius;
    private String name;

    private SimpleEnum( double m, double r, String n ) {

        mass = m;
        radius = r;
        name = n;
    }

}
