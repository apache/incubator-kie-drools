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

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELEventListenerTest {

    private static final String LISTENER_OUTPUT = "Listener output";

    private FEEL feel;

    private String testVariable;

    @Before
    public void setup() {
        testVariable = null;
        feel = FEEL.newInstance();
        feel.addListener(event -> testVariable = LISTENER_OUTPUT);
        feel.addListener(System.out::println);
        feel.addListener( (evt) -> { if (evt.getSeverity() == Severity.ERROR) System.err.println(evt); } );
    }

    @Test
    public void testParserError() {
        feel.evaluate( "10 + / 5" );
        assertThat(testVariable).isEqualTo(LISTENER_OUTPUT);
    }
    
    @Test
    public void testSomeBuiltinFunctions() {
        System.out.println( feel.evaluate("append( null, 1, 2 )") );
        assertThat(testVariable).isEqualTo(LISTENER_OUTPUT);
    }
}
