/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler;

import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtil {

    public static void assertDrlHasCompilationError( String str, int errorNr ) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        org.kie.api.builder.Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        if ( errorNr > 0 ) {
            assertEquals( errorNr, results.getMessages().size() );
        } else {
            assertTrue( results.getMessages().size() > 0 );
        }
    }
}
