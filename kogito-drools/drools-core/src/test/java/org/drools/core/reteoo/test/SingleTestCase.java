/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.drools.core.reteoo.test.dsl.NodeTestCase;
import org.drools.core.reteoo.test.dsl.NodeTestDef;
import org.junit.runner.RunWith;

/**
 * A test case suite to manage and run all node test cases
 */
@RunWith(JUnitNodeTestRunner.class)
public class SingleTestCase
    implements
    NodeTestCasesSource {
    
    /* (non-Javadoc)
     * @see org.drools.core.reteoo.test.NodeTestCasesSource#getTestCases()
     */
    public List<NodeTestCase> getTestCases() throws Exception {
        String testName = System.getProperty( "nodeTestName" );
        if( testName == null || testName.trim().length() == 0 ) {
            throw new IllegalArgumentException("No test defined. Please set the system property 'nodeTestName' with the appropriate test name.");
        }
        NodeTestCase result = new NodeTestCase();
        File base = new File( this.getClass().getResource( "." ).toURI() );
        for ( File file : base.listFiles( new FilenameFilter() {
            public boolean accept(File arg0,
                                  String arg1) {
                return arg1.endsWith( ".nodeTestCase" );
            }
        } ) ) {
            InputStream is = null;
            try {
                is = new FileInputStream( file );
                NodeTestCase tcase = ReteDslTestEngine.compile( is );
                if ( tcase.hasErrors() ) {
                    throw new IllegalArgumentException( "Error parsing and loading testcase: " + file.getAbsolutePath() + "\n" + tcase.getErrors().toString() );
                }
                for( NodeTestDef test : tcase.getTests() ) {
                    if( test.getName().equals( testName ) ) {
                        result.setFileName(file.getName());
                        result.setName( tcase.getName() );
                        result.setImports( tcase.getImports() );
                        result.setSetup( tcase.getSetup() );
                        result.addTest( test );
                        result.setTearDown( tcase.getTearDown() );
                        return Collections.singletonList( result );
                    }
                }
            } finally {
                is.close();
            }
        }
        return Collections.singletonList( result );
    }

}
