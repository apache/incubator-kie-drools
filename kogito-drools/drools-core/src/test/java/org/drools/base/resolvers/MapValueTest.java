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

public class MapValueTest extends TestCase {
    public void testFlatMap() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final Package pkg = new Package( "org.test" );
        pkg.addGlobal( "list",
                       List.class );
        rb.addPackage( pkg );
        final WorkingMemory wm = rb.newWorkingMemory();

        // Make a literal key/value pair
        final LiteralValue literalKey = new LiteralValue( "literalKey1" );
        final LiteralValue literalValue = new LiteralValue( "literalValue" );
        final MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey,
                                                                       literalValue );

        // Make a declaration/literal key/value pair
        final Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ),
                                    "stilton" );
        final DeclarationVariable declaration = new DeclarationVariable( column.getDeclaration() );
        final MapValue.KeyValuePair declarationLiteralPair = new MapValue.KeyValuePair( declaration,
                                                                                  literalValue );

        // Make a literal/declaration key/value pair
        final LiteralValue literalKey2 = new LiteralValue( "literalKey2" );
        final MapValue.KeyValuePair literalDeclarationPair = new MapValue.KeyValuePair( literalKey2,
                                                                                  declaration );

        // Make a global/declaration key/value pair
        final GlobalVariable global = new GlobalVariable( "list",
                                                    List.class );
        final MapValue.KeyValuePair globalDeclarationPair = new MapValue.KeyValuePair( global,
                                                                                 declaration );

        // Make a literal/global key/value pair
        final LiteralValue literalKey3 = new LiteralValue( "literalKey3" );
        final MapValue.KeyValuePair LiteralGlobalPair = new MapValue.KeyValuePair( literalKey3,
                                                                             global );

        final MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair, globalDeclarationPair, LiteralGlobalPair, declarationLiteralPair, literalDeclarationPair} );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        final Map map = (Map) mapValue.getValue( tuple,
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
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = rb.newWorkingMemory();

        // Make a literal key/value pair
        final LiteralValue literalKey1 = new LiteralValue( "literalKey1" );
        final LiteralValue literalValue1 = new LiteralValue( "literalValue1" );
        final MapValue.KeyValuePair literalPair = new MapValue.KeyValuePair( literalKey1,
                                                                       literalValue1 );
        final MapValue nestedMapValue = new MapValue( new MapValue.KeyValuePair[]{literalPair} );

        final LiteralValue literalKey2 = new LiteralValue( "literalKey2" );
        final MapValue.KeyValuePair nestedMapPair = new MapValue.KeyValuePair( literalKey2,
                                                                         nestedMapValue );

        final MapValue mapValue = new MapValue( new MapValue.KeyValuePair[]{nestedMapPair} );

        final Cheese stilton = new Cheese( "stilton",
                                     20 );
        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final Tuple tuple = new ReteTuple( (DefaultFactHandle) stiltonHandle );

        final Map map = (Map) mapValue.getValue( tuple,
                                           wm );

        final Map nestedMap = (Map) map.get( "literalKey2" );
        assertEquals( "literalValue1",
                      nestedMap.get( "literalKey1" ) );
    }
}
