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

package org.kie.dmn.feel.runtime;

import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;

public class FEELExpressionsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // quantified expressions
                { "some price in [ 80, 11, 110 ] satisfies price > 100", Boolean.TRUE },
                { "some price in [ 80, 11, 90 ] satisfies price > 100", Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y", Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 10", Boolean.TRUE },
                { "every price in [ 80, 11, 90 ] satisfies price > 70", Boolean.FALSE },
                { "some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y", Boolean.TRUE },

                // path expressions
                {"{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name", "Doe" },

                // named parameters: in this case foo is null
                {"{ is minor : function( foo, person's age ) foo = null and person's age < 18, bob is minor : is minor( person's age : 16 ) }.bob is minor", Boolean.TRUE },

                // unary test invocation
                {"{ is minor : < 18, bob is minor : is minor(16) }.bob is minor", Boolean.TRUE },

                // negated unary tests
                {"10 in ( not( <5, >=20, =15 ) )", Boolean.TRUE},
                {"10 in ( not( <5, >=20, =10 ) )", Boolean.FALSE},
                {"10 in ( not( <5 ) )", Boolean.TRUE},
                {"10 in ( not( (10..20] ) )", Boolean.TRUE},
                {"10 in ( not( 10 ) )", Boolean.FALSE},
                {"10 in ( not( 5 ) )", Boolean.TRUE},
                {"10 in ( not( 5, (5+5), (20+10) ) )", Boolean.FALSE},
                {"10 in ( not( 5, (20+10) ) )", Boolean.TRUE}
        };
        return Arrays.asList( cases );
    }
}
