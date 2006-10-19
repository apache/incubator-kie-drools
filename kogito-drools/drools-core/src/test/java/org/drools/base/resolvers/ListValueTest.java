package org.drools.base.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

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
import org.drools.spi.Tuple;

public class ListValueTest extends TestCase {

    public void testList() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        final WorkingMemory wm = rb.newWorkingMemory();

        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        final LiteralValue literal = new LiteralValue( "literal" );

        final Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        final DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );

        final GlobalVariable global = new GlobalVariable( "list",
                                                    List.class );

        final LiteralValue literalKey = new LiteralValue( "literalKey" );
        final LiteralValue literalValue = new LiteralValue( "literalValue" );
        final MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey,
                                                                       literalValue );
        final MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair} );

        final List listValueHandlers = new ArrayList();
        listValueHandlers.add( literal );
        listValueHandlers.add( declaration );
        listValueHandlers.add( mapValue );
        listValueHandlers.add( global );

        final ListValue listValue = new ListValue( listValueHandlers );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        final List values = (List) listValue.getValue( tuple,
                                                 wm );
        assertEquals( "literal",
                      values.get( 0 ) );
        assertEquals( stilton,
                      values.get( 1 ) );

        final Map map = (Map) values.get( 2 );
        assertEquals( "literalValue",
                      map.get( "literalKey" ) );

        assertEquals( list,
                      values.get( 3 ) );
    }

    public void testNestedList() {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = rb.newWorkingMemory();

        final LiteralValue literal = new LiteralValue( "literal" );
        final List nestedListValueHandlers = new ArrayList();
        nestedListValueHandlers.add( literal );
        final ListValue nestedListValue = new ListValue( nestedListValueHandlers );

        final List listValueHandlers = new ArrayList();
        listValueHandlers.add( nestedListValue );
        final ListValue listValue = new ListValue( listValueHandlers );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        final List list = (List) listValue.getValue( tuple,
                                               wm );
        assertEquals( 1,
                      list.size() );

        final List nestedList = (List) list.get( 0 );

        assertEquals( 1,
                      nestedList.size() );

        assertEquals( "literal",
                      nestedList.get( 0 ) );

    }
}
