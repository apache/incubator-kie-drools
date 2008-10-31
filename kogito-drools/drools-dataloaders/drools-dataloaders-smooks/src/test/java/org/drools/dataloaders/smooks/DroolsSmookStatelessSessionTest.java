package org.drools.dataloaders.smooks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.PackageBuilder;
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;

public class DroolsSmookStatelessSessionTest extends TestCase {
    public void testSmooksNestedIterable() throws Exception {
        PackageBuilder pkgBuilder = new PackageBuilder();       
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( DroolsSmookStatelessSessionTest.class.getResourceAsStream( "test_SmooksNestedIterable.drl" ) ) );

        assertFalse( pkgBuilder.hasErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );

        StatelessSession session = ruleBase.newStatelessSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );

        DroolsSmooksConfiguration conf = new DroolsSmooksConfiguration( "root",
                                                                        "children" );
        //
        DroolsSmooksStatelessSession dataLoader = new DroolsSmooksStatelessSession( session,
                                           smooks,
                                           conf );
        dataLoader.executeFilter( new StreamSource( getClass().getResourceAsStream( "SmooksNestedIterable.xml") ) );
        //
        
        assertEquals(2, list.size());
        
        assertEquals( "example.OrderItem", list.get( 0 ).getClass().getName() );
        assertEquals( "example.OrderItem", list.get( 1 ).getClass().getName() );
        
        assertNotSame( list.get( 0 ), list.get( 1 ) );
    }    
    
    public void testSmooksDirectRoot() throws Exception {
        PackageBuilder pkgBuilder = new PackageBuilder();       
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( DroolsSmookStatelessSessionTest.class.getResourceAsStream( "test_SmooksDirectRoot.drl" ) ) );

        assertFalse( pkgBuilder.hasErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );

        StatelessSession session = ruleBase.newStatelessSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );        

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );


        DroolsSmooksConfiguration conf = new DroolsSmooksConfiguration( "orderItem", null );
        //
        DroolsSmooksStatelessSession dataLoader = new DroolsSmooksStatelessSession( session,
                                           smooks,
                                           conf );
        dataLoader.executeFilter( new StreamSource( getClass().getResourceAsStream( "SmooksDirectRoot.xml") ) );
        
        assertEquals(1, list.size());
        
        assertEquals( "example.OrderItem", list.get( 0 ).getClass().getName() );
    }
    
    
    

    private static byte[] readInputMessage(InputStream stream) {
        try {
            return StreamUtils.readStream( stream );
        } catch ( IOException e ) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }
}
