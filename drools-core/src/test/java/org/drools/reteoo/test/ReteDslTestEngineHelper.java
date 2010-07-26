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

package org.drools.reteoo.test;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.drools.reteoo.test.dsl.NodeTestCase;
import org.drools.reteoo.test.dsl.NodeTestCaseResult;


public class ReteDslTestEngineHelper {
    
    public static void executeDsl(String[] fileNames) {
        InputStream[] inputStreams = new InputStream[fileNames.length];
        for ( int i = 0; i < fileNames.length; i++ ) {
            inputStreams[i] = ReteDslTestEngineHelper.class.getResourceAsStream( fileNames[i] );
            TestCase.assertNotNull( fileNames[i], inputStreams[i] );
        }
        executeDsl( fileNames, inputStreams );
    }

    public static void executeDsl(String[] fileNames, InputStream[] inputStreams) {
        InputStream inputStream = inputStreams[0];

        for ( int i = 1; i < inputStreams.length; i++ ) {
            TestCase.assertNotNull( inputStreams[i] );
            inputStream = new SequenceInputStream( inputStream,
                                                   inputStreams[i] );
        }

        executeDsl( Arrays.toString( fileNames ), inputStream );
    }

    public static void executeDsl(String fileName) {
        executeDsl( fileName, ReteDslTestEngineHelper.class.getResourceAsStream( fileName ) );
    }

    public static void executeDsl( String fileName, InputStream inputStream) {
        TestCase.assertNotNull( inputStream );
        try {
            NodeTestCase testCase = ReteDslTestEngine.compile( inputStream );
            ReteDslTestEngine tester = new ReteDslTestEngine();
            NodeTestCaseResult result = tester.run( testCase, null );
            if( result.getTotalTests()-result.getSuccesses() > 0 ) {
                TestCase.fail("Error executing "+fileName+" : \n    "+ result );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            TestCase.fail("Unexpected exception: "+e.getMessage());
        }
    }
    

}
