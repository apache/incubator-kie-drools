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

public class MapValueTest extends TestCase {
    public void testFlatMap() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        WorkingMemory wm = rb.newWorkingMemory();

        // Make a literal key/value pair
        LiteralValue literalKey = new LiteralValue( "literalKey1", String.class );
        LiteralValue literalValue = new LiteralValue( "literalValue", String.class );
        MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey,
                                                                       literalValue );

        // Make a declaration/literal key/value pair
        Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );
        MapValue.KeyValuePair declarationLiteralPair = new MapValue.KeyValuePair( declaration,
                                                                                  literalValue );

        // Make a literal/declaration key/value pair
        LiteralValue literalKey2 = new LiteralValue( "literalKey2", String.class );
        MapValue.KeyValuePair literalDeclarationPair = new MapValue.KeyValuePair( literalKey2,
                                                                                  declaration );

        // Make a global/declaration key/value pair
        GlobalVariable global = new GlobalVariable( "list", List.class );
        MapValue.KeyValuePair globalDeclarationPair = new MapValue.KeyValuePair( global,
                                                                                 declaration );

        // Make a literal/global key/value pair
        LiteralValue literalKey3 = new LiteralValue( "literalKey3", String.class );
        MapValue.KeyValuePair LiteralGlobalPair = new MapValue.KeyValuePair( literalKey3,
                                                                             global );

        MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair, globalDeclarationPair, LiteralGlobalPair, declarationLiteralPair, literalDeclarationPair} );

        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        Map map = (Map) mapValue.getValue( tuple,
                                           wm );
        assertEquals( "literalValue",
                      map.get( "literalKey1" ) );
        assertEquals( "literalValue",
                      map.get( stilton ) );
        assertEquals( stilton,
                      map.get( "literalKey2" ) );
        assertEquals( stilton,
                      map.get( "literalKey2" ) );
        assertEquals( stilton,
                      map.get( list ) );
        assertEquals( list,
                      map.get( "literalKey3" ) );
    }

    public void testNestedMap() {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        WorkingMemory wm = rb.newWorkingMemory();

        // Make a literal key/value pair
        LiteralValue literalKey1 = new LiteralValue( "literalKey1", String.class );
        LiteralValue literalValue1 = new LiteralValue( "literalValue1", String.class );
        MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey1,
                                                                       literalValue1 );
        MapValue nestedMapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair} );

        LiteralValue literalKey2 = new LiteralValue( "literalKey2", String.class );
        MapValue.KeyValuePair nestedMapPair = new MapValue.KeyValuePair( literalKey2,
                                                                         nestedMapValue );

        MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{nestedMapPair} );

        Cheese stilton = new Cheese( "stilton",
                                     20 );
        FactHandle stiltonHandle = wm.assertObject( stilton );

        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        Map map = (Map) mapValue.getValue( tuple,
                                           wm );

        Map nestedMap = (Map) map.get( "literalKey2" );
        assertEquals( "literalValue1",
                      nestedMap.get( "literalKey1" ) );
    }
}
