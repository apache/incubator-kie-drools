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

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;

public class FEELEventListenerTest {

    private FEEL feel;

    @Before
    public void setup() {
        feel = FEEL.newInstance();
        feel.addListener( event -> System.out.println( event ) );
        feel.addListener( (evt) -> { if (evt.getSeverity() == Severity.ERROR) System.err.println(evt); } );
    }

    @Test
    public void testParserError() {
        feel.evaluate( "10 + / 5" );

    }
    
    @Test
    public void testSomeBuiltinFunctions() {
        System.out.println( feel.evaluate("append( null, 1, 2 )") );
    }
}
