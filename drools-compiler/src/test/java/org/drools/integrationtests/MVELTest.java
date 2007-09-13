package org.drools.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.evaluators.DateFactory;
import org.drools.common.DroolsObjectInputStream;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.mvel.MVEL;

public class MVELTest extends TestCase {
    public void testHelloWorld() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_mvel.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        // Bellow lines are a way to make sure serialization is fine
        // start of serialization block
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream obj = new ObjectOutputStream( out );
        obj.writeObject( ruleBase );
        obj.close();

        byte[] buf = out.toByteArray();

        DroolsObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( buf ) );
        ruleBase = (RuleBase) in.readObject();
        // end of serialization block

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );


        Cheese c = new Cheese("stilton", 10) ;
        workingMemory.insert( c);
        workingMemory.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( new Integer(30), list.get(0));
        assertEquals( new Integer(22), list.get(1));

        assertEquals( "hello world", list2.get(0));

        Date dt = DateFactory.parseDate( "10-Jul-1974" );
        assertEquals(dt, c.getUsedBy());


    }

    public Object compiledExecute(String ex) {
        Serializable compiled = MVEL.compileExpression(ex);
        return MVEL.executeExpression(compiled, new Object(), new HashMap());
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        System.out.println( builder.getErrors() );

        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        // load up the rulebase
        return ruleBase;
    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }
}
