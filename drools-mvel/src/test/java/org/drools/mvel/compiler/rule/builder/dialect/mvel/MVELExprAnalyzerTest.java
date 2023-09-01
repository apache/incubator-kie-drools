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
package org.drools.mvel.compiler.rule.builder.dialect.mvel;

import org.drools.mvel.builder.MVELExprAnalyzer;
import org.junit.Before;
import org.junit.Test;

public class MVELExprAnalyzerTest {

    private MVELExprAnalyzer analyzer;

    @Before
    public void setUp() throws Exception {
        analyzer = new MVELExprAnalyzer();
    }

    @Test
    public void testGetExpressionIdentifiers() {
//        try {
//            String expression = "order.id == 10";
//            List[] identifiers = analyzer.analyzeExpression( expression, new Set[0] );
//            
//            assertEquals( 1, identifiers.length );
//            assertEquals( 1, identifiers[0].size() );
//            assertEquals( "order", identifiers[0].get( 0 ));
//        } catch ( RecognitionException e ) {
//            e.printStackTrace();
//            fail( "Unexpected exception: "+e.getMessage());
//        }
    }

}
