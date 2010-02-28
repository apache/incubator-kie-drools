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
