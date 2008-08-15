package org.drools.dataloaders.smooks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;

public class DroolsSmookStatefulSessionTest extends TestCase {
    public void testDirectRoot() throws Exception {
        PackageBuilder pkgBuilder = new PackageBuilder();       
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( DroolsSmookStatefulSessionTest.class.getResourceAsStream( "test_SmooksDirectRoot.drl" ) ) );

        assertFalse( pkgBuilder.hasErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );        

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );


        DroolsSmooksConfiguration conf = new DroolsSmooksConfiguration( "orderItem", null );
        //
        DroolsSmooksStatefulSession dataLoader = new DroolsSmooksStatefulSession( session,
                                           smooks,
                                           conf );
        Map<FactHandle, Object> handles = dataLoader.insertFilter( new StreamSource( getClass().getResourceAsStream( "SmooksDirectRoot.xml") ) );
        //
        session.fireAllRules();
        
        assertEquals(1, handles.size() );
        assertEquals(1, list.size());
        
        assertEquals( "example.OrderItem", list.get( 0 ).getClass().getName() );
    }    
    
    public void testNestedIterable() throws Exception {
        PackageBuilder pkgBuilder = new PackageBuilder();       
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( DroolsSmookStatefulSessionTest.class.getResourceAsStream( "test_SmooksNestedIterable.drl" ) ) );

        assertFalse( pkgBuilder.hasErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );

        DroolsSmooksConfiguration conf = new DroolsSmooksConfiguration( "root",
                                                                        "children" );
        //
        DroolsSmooksStatefulSession dataLoader = new DroolsSmooksStatefulSession( session,
                                           smooks,
                                           conf );
        Map<FactHandle, Object> handles = dataLoader.insertFilter( new StreamSource( getClass().getResourceAsStream( "SmooksNestedIterable.xml") ) );
        //
        session.fireAllRules();
        
        assertEquals(2, handles.size() );
        assertEquals(2, list.size());
        
        assertEquals( "example.OrderItem", list.get( 0 ).getClass().getName() );
        assertEquals( "example.OrderItem", list.get( 1 ).getClass().getName() );
        
        assertNotSame( list.get( 0 ), list.get( 1 ) );
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
