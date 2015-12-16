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

package org.drools.pmml.pmml_4_2.global;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class HeaderTest extends DroolsAbstractPMMLTest {





    @Test
    public void testPMMLHeader() {
        String source = PMML4Helper.pmmlDefaultPackageName().replace( ".", File.separator ) + File.separator + "test_header.xml";

        boolean header = false;
        boolean timestamp = false;
        boolean appl = false;
        boolean descr = false;
        boolean copyright = false;
        boolean annotation = false;

        PMML4Compiler compiler = new PMML4Compiler();
        compiler.getHelper().setPack( "org.drools.pmml.pmml_4_2.test" );

        String theory = compiler.compile(  source, null );
        BufferedReader reader = new BufferedReader(new StringReader(theory));
        try {
            String line = "";
            while ((line=reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("// Imported PMML Model Theory")) header = true;
                else if (line.startsWith("// Creation timestamp :")) timestamp = line.contains("now");
                else if (line.startsWith("// Description :")) descr = line.contains("test");
                else if (line.startsWith("// Copyright :")) copyright = line.contains("opensource");
                else if (line.startsWith("// Annotation :")) annotation = line.contains("notes here");
                else if (line.startsWith("// Trained with :")) appl = line.contains("handmade");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail();
        }
        assertTrue(header);
        assertTrue(timestamp);
        assertTrue(descr);
        assertTrue(copyright);
        assertTrue(annotation);
        assertTrue(appl);



        KieSession ksession = getSession( theory );
        KiePackage pack = ksession.getKieBase().getKiePackage( "org.drools.pmml.pmml_4_2.test" );
        assertNotNull(pack);

        ksession.dispose();
    }



}