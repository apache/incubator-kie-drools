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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class FEELContextsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "{ first name : \"Bob\", birthday : date(\"1978-09-12\"), salutation : \"Hello \"+first name }",
                        new HashMap<String,Object>() {{
                            put( "first name", "Bob" );
                            put( "birthday", LocalDate.of(1978, 9, 12) );
                            put( "salutation", "Hello Bob" );
                        }}, null },
                // nested contexts + qualified name
                { "{ full name : { first name: \"Bob\", last name : \"Doe\" }, birthday : date(\"1978-09-12\"), salutation : \"Hello \"+full name.first name }",
                        new HashMap<String,Object>() {{
                            put( "full name", new HashMap<String,Object>() {{
                                put( "first name", "Bob" );
                                put( "last name", "Doe" );
                            }} );
                            put( "birthday", LocalDate.of(1978, 9, 12) );
                            put( "salutation", "Hello Bob" );
                        }}, null },
                // Example from spec. chapter "10.3.2.7 Ranges"
                { "{ startdate: date(\"1978-09-12\"), enddate: date(\"1978-10-13\"), rangedates: [startdate..enddate] }",
                        new HashMap<String,Object>() {{
                            put( "startdate", LocalDate.of(1978, 9, 12) );
                            put( "enddate", LocalDate.of(1978, 10, 13) );
                            put( "rangedates", new RangeImpl( Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED ) );
                }}, null },

                // testing the non-breakable space character
                {"5\u00A0+\u00A03", BigDecimal.valueOf( 8 ), null },
                {"{ first\u00A0\u00A0name : \"Bob\", salutation : \"Hello \"+first\u00A0\u00A0name+\"!\"}.salutation", "Hello Bob!", null },
                {"{ first\u00A0\u00A0name : \"Bob\", salutation : \"Hello \"+first\u00A0name+\"!\"}.salutation", "Hello Bob!", null },
                {"{ first\u00A0\u00A0name : \"Bob\", salutation : \"Hello \"+first  name+\"!\"}.salutation", "Hello Bob!", null },
                {"{ first name : \"Bob\", salutation : \"Hello \"+first\u00A0name+\"!\"}.salutation", "Hello Bob!", null },
                {"{ \"first name\" : \"Bob\", salutation : \"Hello \"+first name+\"!\"}.salutation", "Hello Bob!", null},
                {"{ \"first name\" : \"Bob\", salutation : \"Hello \"+first\u00A0name+\"!\"}.salutation", "Hello Bob!", null},
                {"{ \"a\" : 1, b : 2, \"c\": a+b}.c", BigDecimal.valueOf( 3 ), null},
                {"[{a: {b: [1]}}, {a: {b: [2.1, 2.2]}}, {a: {b: [3]}}, {a: {b: [4, 5]}}].a.b", Arrays.asList(List.of(new BigDecimal(1)),
                                                                                                             Arrays.asList(new BigDecimal( "2.1" ), new BigDecimal("2.2")),
                                                                                                             List.of(new BigDecimal(3)),
                                                                                                             Arrays.asList(new BigDecimal( 4 ), new BigDecimal( 5 ))), null},
                {"{a:{p:{x:10,y:5}},b : a.p.x*10+a.p.y*10}.b", BigDecimal.valueOf( 150 ), null},
                {"{a:{p:{x:10,y:5}},b : a.p.y < a.p.x and a.p.x > a.p.y }.b", Boolean.TRUE, null},
                {"{a:{p:{x:10,y:5}},b : a.p.y < a.p.x or a.p.y <= a.p.x }.b", Boolean.TRUE, null},
                {"{a: 1, b: 2, a: 3}", null, FEELEvent.Severity.ERROR},
        };
        return addAdditionalParameters(cases, false);
    }
}
