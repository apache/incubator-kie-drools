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

tree grammar NodeTestDSLTree;

options{
    tokenVocab=NodeTestDSL;
    ASTLabelType=CommonTree;
    output=AST;
}

@header {
    package org.kie.reteoo.test.parser;

    import org.kie.reteoo.test.dsl.*;

}

@members {
    NodeTestDSLFactory factory = new NodeTestDSLFactory();
    NodeTestCase testCase = null;

    public NodeTestCase getTestCase() {
        return testCase;
    }
}

compilation_unit
    :	 ^(VT_TEST_CASE test_case_statement import_statement* setup? teardown? test*)
    ;

test_case_statement
    : 	^(VK_TEST_CASE name=STRING)
        { testCase = factory.createTestCase( $name.text ); }
    ;

import_statement
    :	^(VK_IMPORT clazz=VT_QUALIFIED_ID)
            { testCase.addImport( $clazz.text ); }
    ;

setup
    :	^(VK_SETUP { factory.createSetup(); } step*)
    ;

teardown
    :	^(VK_TEARDOWN { factory.createTearDown(); } step*)
    ;

test	
    :	^(VK_TEST name=STRING { factory.createTest( $VK_TEST, $name ); } step*)
    ;

step
    :	^(ID p+=params+ { factory.createStep( $ID, $p ); })
    ;

params
    :	^(VT_PARAMS param_chunk+ )
    ;

param_chunk
    :	VT_CHUNK
    ;

