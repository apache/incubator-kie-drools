/*
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

package org.drools.reteoo.test.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class to store the results of the execution of a NodeTestCase
 * 
 */
public class NodeTestCaseResult {
    
    public static enum Result {
        NOT_EXECUTED, SUCCESS, FAILURE, ERROR;
    }

    private NodeTestCase testCase;
    private List<NodeTestResult> results;
    private int totalTests = 0;
    private int executed = 0;
    private int successes = 0;
    private int failures = 0;
    private int errors = 0;
    
    public NodeTestCaseResult(NodeTestCase testCase) {
        super();
        this.testCase = testCase;
        this.totalTests = testCase.getTests().size();
        this.results = new ArrayList<NodeTestResult>(this.totalTests);
    }

    public void add(NodeTestResult testResult) {
        executed++;
        switch( testResult.result ) {
            case SUCCESS : 
                successes++;
                break;
            case FAILURE : 
                failures++;
                break;
            case ERROR : 
                errors++;
                break;
        }
        results.add( testResult );
    }
    
    public NodeTestCase getTestCase() {
        return testCase;
    }

    public List<NodeTestResult> getResults() {
        return results;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getExecuted() {
        return executed;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getFailures() {
        return failures;
    }

    public int getErrors() {
        return errors;
    }
    
    public String toString() {
        String toString = "TestCase: '"+testCase.getName()+"' total="+totalTests+" executed="+executed+" successes="+successes+" failures="+failures+" errors="+errors;
        if( totalTests - successes > 0 ) {
            for( NodeTestResult result : this.results ) {
                if( result.result != Result.SUCCESS ) {
                    toString+="\n     "+result;
                }
            }
        }
        return toString;
    }

    public static class NodeTestResult {
        public NodeTestDef test;
        public Result result;
        public Map<String, Object> context;
        public List<String> errorMsgs;
        
        public NodeTestResult(NodeTestDef test,
                              Result result,
                              Map<String, Object> context,
                              List<String> errorMsgs) {
            super();
            this.test = test;
            this.result = result;
            this.context = context;
            this.errorMsgs = errorMsgs;
        }
        
        public String getMessages() {
            return "["+result+"] Test '"+test.getName()+"' returned messages: "+errorMsgs.toString();
        }
        
        public String toString() {
            return getMessages();
        }
    }
    
}
