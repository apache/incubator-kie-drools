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
package org.drools.mvel.compiler.lang.descr;

import org.drools.drl.ast.descr.ConnectiveDescr;
import org.drools.drl.ast.descr.ConnectiveDescr.RestrictionConnectiveType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectiveDescrTest {

    @Test
    public void testBuildExpression() {
        ConnectiveDescr descr1 = new ConnectiveDescr(RestrictionConnectiveType.OR);
        descr1.setPrefix( "name" );
        descr1.add( "== 'darth'" );
        descr1.add( "== 'bobba'" );
        
        StringBuilder sb = new StringBuilder();
        descr1.buildExpression( sb );
        assertThat(sb.toString()).isEqualToIgnoringWhitespace("name == 'darth' || == 'bobba'");
        
        ConnectiveDescr descr2 = new ConnectiveDescr( RestrictionConnectiveType.AND);
        descr2.setPrefix( "name" );
        descr2.add( "!= 'luke'" );
        sb = new StringBuilder();
        descr2.buildExpression( sb );
        assertThat(sb.toString()).isEqualToIgnoringWhitespace("name != 'luke'");
        descr2.add( "!= 'yoda'" );        
        
        ConnectiveDescr descr3 = new ConnectiveDescr(RestrictionConnectiveType.AND);
        descr3.add( descr1 );
        descr3.add( descr2 );
        
        sb = new StringBuilder();
        descr3.buildExpression( sb );
        assertThat(sb.toString()).isEqualToIgnoringWhitespace("(name == 'darth' || == 'bobba') && (name != 'luke' && != 'yoda')");
        
        ConnectiveDescr descr4 = new ConnectiveDescr(RestrictionConnectiveType.AND);
        descr4.setPrefix( "age" );
        descr4.add( "!= 33" );
        descr4.add( "!= 34" );  
        
        ConnectiveDescr descr5 = new ConnectiveDescr(RestrictionConnectiveType.OR);
        descr5.add( descr3 );
        descr5.add( descr4 );        
        
        sb = new StringBuilder();
        descr5.buildExpression( sb );
        assertThat(sb.toString()).isEqualToIgnoringWhitespace("((name == 'darth' || == 'bobba') && (name != 'luke' && != 'yoda')) || (age != 33 && != 34)");
       
    }    
}
