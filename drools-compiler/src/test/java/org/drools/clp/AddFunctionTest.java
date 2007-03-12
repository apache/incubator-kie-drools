package org.drools.clp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.ObjectType;

import junit.framework.TestCase;

public class AddFunctionTest extends TestCase {
    public void testAdd() {
        ValueHandler val1 = new ObjectLiteralValue(new BigDecimal( 10 ));
        ValueHandler val2 = new ObjectLiteralValue(new BigDecimal( 10 ));
        AddFunction add = new AddFunction( new ValueHandler[] { val1, val2 } );

        assertEquals( new BigDecimal( 20 ), add.getBigDecimalValue( null ) );        
    }
    
    public void testNestedAdd() {
        
        ValueHandler val1 = new ObjectLiteralValue(new BigDecimal( 10 ));
        ValueHandler val2 = new ObjectLiteralValue(new BigDecimal( 10 ));
        ValueHandler val3 = new ObjectLiteralValue(new BigDecimal( 10 ));
        
        AddFunction add1 = new AddFunction( new ValueHandler[] { val1, val2 } );        
        AddFunction add2 = new AddFunction( new ValueHandler[] { val3, add1 } );

        assertEquals( new BigDecimal( 30 ), add2.getBigDecimalValue( null ) );                  
    }
    
    public void testNestedAddWithVars() {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = ( InternalWorkingMemory ) ruleBase.newWorkingMemory();
        
        InternalFactHandle factHandle = ( InternalFactHandle ) workingMemory.assertObject( new BigDecimal( 10 ) );
        ReteTuple tuple = new ReteTuple( factHandle );
        
        ObjectType objectType = new ClassObjectType( BigDecimal.class );
        Column column0 = new Column(0, objectType);
        Column column1 = new Column(1, objectType);
        ColumnExtractor extractor = new ColumnExtractor( objectType );
        
        Map variables = new HashMap();
        variables.put( "pd", new CLPPreviousDeclarationVariable( new Declaration("pd", extractor, column0) ) );
        variables.put( "ld", new CLPLocalDeclarationVariable( new Declaration("ld", extractor, column1) ) );
        
        ValueHandler val1 = new TempTokenVariable("pd");                
        ValueHandler val2 = new LocalVariableValue("lv", 0);                  
        ValueHandler val3 = new TempTokenVariable("ld");
        
        ExecutionContext context = new ExecutionContext(workingMemory, tuple, new BigDecimal( 10 ), 1 );
        context.setLocalVariable( 0, new BigDecimal( 10 ) );
        
        AddFunction add1 = new AddFunction( new ValueHandler[] { val1, val2 } );        
        AddFunction add2 = new AddFunction( new ValueHandler[] { val3, add1 } );                
        
        add2.replaceTempTokens( variables );

        assertEquals( new BigDecimal( 30 ), add2.getBigDecimalValue( context ) );                  
    }    
}
