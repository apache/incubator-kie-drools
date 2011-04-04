package org.drools.lang.descr;

import org.drools.lang.descr.ConnectiveDescr.RestrictionConnectiveType;
import org.junit.Test;
import static org.drools.compiler.xml.rules.DumperTestHelper.assertEqualsIgnoreWhitespace;

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
