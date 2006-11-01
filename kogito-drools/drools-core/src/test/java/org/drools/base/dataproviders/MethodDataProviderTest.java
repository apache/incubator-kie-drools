package org.drools.base.dataproviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.resolvers.DeclarationVariable;
import org.drools.base.resolvers.GlobalVariable;
import org.drools.base.resolvers.LiteralValue;
import org.drools.base.resolvers.ValueHandler;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.Extractor;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class MethodDataProviderTest extends TestCase {

    private PropagationContext  context;
    private ReteooWorkingMemory workingMemory;
    private Map                 declarations;
    private Map                 globals;

    protected void setUp() {
        this.context = new PropagationContextImpl( 0,
                                              PropagationContext.ASSERTION,
                                              null,
                                              null );
        this.workingMemory = new ReteooWorkingMemory( 1,
                                                 (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        this.declarations = new HashMap();
        this.globals = new HashMap();
    }

    public void FIX_ME_testWithDeclarationsHelloWorld() throws Exception {

        Column column = new Column( 0,
                                    new ClassObjectType( Cheese.class ) );

        Extractor ex = new ColumnExtractor( new ClassObjectType( TestVariable.class ) );
        final Declaration varDec = new Declaration( "var",
                                              ex,
                                              column );
        this.declarations.put( "var",
                          varDec );

        column = new Column( 1,
                             new ClassObjectType( Cheese.class ) );
        ex = new ColumnExtractor( new ClassObjectType( String.class ) );
        final Declaration var2Dec = new Declaration( "var2",
                                               ex,
                                               column );
        this.declarations.put( "var2",
                          var2Dec );

        final List args = new ArrayList();
        args.add( new LiteralValue( "boo" ) );
        args.add( new LiteralValue( new Integer( 42 ) ) );
        args.add( new DeclarationVariable( var2Dec ) );

        final MethodInvoker invoker = new MethodInvoker( "helloWorld",
                                                   new DeclarationVariable( varDec ),
                                                   (ValueHandler[]) args.toArray( new ValueHandler[args.size()] ) );

        final MethodDataProvider prov = new MethodDataProvider( invoker );

        final TestVariable var = new TestVariable();
        final FactHandle varHandle = this.workingMemory.assertObject( var );
        final FactHandle var2Handle = this.workingMemory.assertObject( "hola" );

        final Tuple tuple = new ReteTuple( new ReteTuple( (DefaultFactHandle) varHandle ),
                                     (DefaultFactHandle) var2Handle );

        final Iterator it = prov.getResults( tuple,
                                       this.workingMemory,
                                       this.context );

        final Object result = it.next();
        assertEquals( "boo42hola",
                      result );

    }

    public void testWithGlobals() throws Exception {
        this.globals.put( "foo",
                     TestVariable.class );

        final Package pkg = new Package( "nothing" );
        pkg.addGlobal( "foo",
                       TestVariable.class );
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkg );

        final WorkingMemory wm = rb.newWorkingMemory();

        wm.setGlobal( "foo",
                      new TestVariable() );

        final MethodInvoker invoker = new MethodInvoker( "otherMethod",
                                                   new GlobalVariable( "foo",
                                                                       TestVariable.class ),
                                                   new ValueHandler[0] );

        final MethodDataProvider prov = new MethodDataProvider( invoker );

        final Iterator it = prov.getResults( null,
                                       wm,
                                       null );
        assertTrue( it.hasNext() );
        assertEquals( "boo",
                      it.next() );
    }

}
