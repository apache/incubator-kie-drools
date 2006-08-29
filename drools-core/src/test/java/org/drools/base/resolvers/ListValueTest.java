package org.drools.base.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class ListValueTest extends TestCase {

    public void testList() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        WorkingMemory wm = rb.newWorkingMemory();
        
        List list = new ArrayList();
        wm.setGlobal( "list",
                      list );        
        
        LiteralValue literal = new LiteralValue( "literal", String.class );
        
        Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );
        
        GlobalVariable global = new GlobalVariable( "list", List.class );
        
        LiteralValue literalKey = new LiteralValue( "literalKey", String.class );
        LiteralValue literalValue = new LiteralValue( "literalValue", String.class );
        MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey,
                                                                       literalValue );
        MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair} );
        
        List listValueHandlers = new ArrayList();
        listValueHandlers.add( literal );
        listValueHandlers.add( declaration );
        listValueHandlers.add( mapValue );
        listValueHandlers.add( global );
        
        ListValue listValue = new ListValue( listValueHandlers );
        
        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        
        List values = ( List ) listValue.getValue( tuple, wm );
        assertEquals( "literal", values.get( 0 ) );
        assertEquals( stilton, values.get( 1 ) );

        Map map = ( Map ) values.get( 2 );
        assertEquals( "literalValue", map.get( "literalKey" ) );
        
        assertEquals( list, values.get( 3 ) );                
    }
    
    public void testNestedList() {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        WorkingMemory wm = rb.newWorkingMemory();
        
        LiteralValue literal = new LiteralValue( "literal", String.class );
        List nestedListValueHandlers = new ArrayList();
        nestedListValueHandlers.add(  literal );
        ListValue nestedListValue = new ListValue( nestedListValueHandlers );
        
        List listValueHandlers = new ArrayList();
        listValueHandlers.add(  nestedListValue );
        ListValue listValue = new ListValue( listValueHandlers );
        
        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );
        
        List list = ( List ) listValue.getValue( tuple, wm );
        assertEquals( 1, list.size() );
        
        List nestedList = ( List ) list.get( 0 );
        
        assertEquals( 1, nestedList.size() );
        
        assertEquals( "literal", nestedList.get( 0 ) );
        
    }
}
