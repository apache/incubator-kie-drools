/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang.descr;

import org.drools.compiler.lang.descr.ConnectiveDescr;
import org.drools.compiler.lang.descr.ConnectiveDescr.RestrictionConnectiveType;
import org.junit.Test;
import static org.drools.compiler.compiler.xml.rules.DumperTestHelper.assertEqualsIgnoreWhitespace;

public class ConnectiveDescrTest {

    @Test
    public void testBuildExpression() {
        ConnectiveDescr descr1 = new ConnectiveDescr(RestrictionConnectiveType.OR);
        descr1.setPrefix( "name" );
        descr1.add( "== 'darth'" );
        descr1.add( "== 'bobba'" );
        
        StringBuilder sb = new StringBuilder();
        descr1.buildExpression( sb );
        assertEqualsIgnoreWhitespace("name == 'darth' || == 'bobba'", sb.toString());
        
        ConnectiveDescr descr2 = new ConnectiveDescr(RestrictionConnectiveType.AND);
        descr2.setPrefix( "name" );
        descr2.add( "!= 'luke'" );
        sb = new StringBuilder();
        descr2.buildExpression( sb );
        assertEqualsIgnoreWhitespace("name != 'luke'", sb.toString());        
        descr2.add( "!= 'yoda'" );        
        
        ConnectiveDescr descr3 = new ConnectiveDescr(RestrictionConnectiveType.AND);
        descr3.add( descr1 );
        descr3.add( descr2 );
        
        sb = new StringBuilder();
        descr3.buildExpression( sb );
        assertEqualsIgnoreWhitespace("(name == 'darth' || == 'bobba') && (name != 'luke' && != 'yoda')", sb.toString());        
        
        ConnectiveDescr descr4 = new ConnectiveDescr(RestrictionConnectiveType.AND);
        descr4.setPrefix( "age" );
        descr4.add( "!= 33" );
        descr4.add( "!= 34" );  
        
        ConnectiveDescr descr5 = new ConnectiveDescr(RestrictionConnectiveType.OR);
        descr5.add( descr3 );
        descr5.add( descr4 );        
        
        sb = new StringBuilder();
        descr5.buildExpression( sb );
        assertEqualsIgnoreWhitespace("((name == 'darth' || == 'bobba') && (name != 'luke' && != 'yoda')) || (age != 33 && != 34)", sb.toString());        
       
    }    
}
