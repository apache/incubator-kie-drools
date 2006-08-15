package org.drools.base.dataproviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.Extractor;
import org.drools.spi.PropagationContext;

public class MethodDataProviderTest extends TestCase {

    private PropagationContext context;
    private ReteooWorkingMemory workingMemory;
    private Map declarations;
    private Map globals;
    
    protected void setUp() {
        context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );
        workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        
        declarations = new HashMap();    
        globals = new HashMap();
    }
    
    public void testWithDeclarationsHelloWorld() throws Exception {
        
        Extractor ex = new ColumnExtractor(new ClassObjectType(TestVariable.class));
        Declaration varDec = new Declaration("var", ex, 0);
        declarations.put("var", varDec);
        
        ex = new ColumnExtractor(new ClassObjectType(String.class));
        Declaration var2Dec = new Declaration("var2", ex, 1);
        declarations.put( "var2", var2Dec );
        
        List args = new ArrayList();
        args.add( new ArgumentValueDescr(ArgumentValueDescr.STRING, "boo") );
        args.add( new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, "42") );
        args.add( new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, "var2") );        
        MethodDataProvider prov = new MethodDataProvider("var", "helloWorld", args, declarations, globals);
        
        TestVariable var = new TestVariable();
        FactHandle varHandle = workingMemory.assertObject( var );
        FactHandle var2Handle = workingMemory.assertObject( "hola" );
        
        ReteTuple tuple = new ReteTuple(new ReteTuple( (DefaultFactHandle) varHandle ), (DefaultFactHandle) var2Handle );
        
        Iterator it = prov.getResults( tuple, workingMemory, context );
        
        Object result = it.next();
        assertEquals("boo42hola", result);
        
        
    }

    public void testWithGlobals() throws Exception {
        globals.put( "foo", TestVariable.class );
        
        Package pkg = new Package("nothing");        
        pkg.addGlobal( "foo", TestVariable.class );
        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkg );
        
        WorkingMemory wm = rb.newWorkingMemory();

        wm.setGlobal( "foo", new TestVariable() );
        
        MethodDataProvider prov = new MethodDataProvider("foo", "otherMethod", new ArrayList(), declarations, globals);

        Iterator it = prov.getResults( null, wm, null );
        assertTrue(it.hasNext());
        assertEquals("boo", it.next());
    }
    
    

}
