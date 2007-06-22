package org.drools.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class FileScannerTest extends TestCase {

    public void testHasChanged() {
        Map lastMod = new HashMap();
        
        FileScanner scan = new FileScanner();
        assertTrue(scan.hasChanged( "/goo/ber", lastMod, 42 ));

        assertFalse(scan.hasChanged( "/goo/ber", lastMod, 42 ));
        assertTrue(scan.hasChanged( "/goo/baaaa", lastMod, 42 ));

        assertFalse(scan.hasChanged( "/goo/baaaa", lastMod, 42 ));
        assertFalse(scan.hasChanged( "/goo/ber", lastMod, 42 ));

        
        assertTrue(scan.hasChanged( "/goo/ber", lastMod, 400 ));
        assertFalse(scan.hasChanged( "/goo/baaaa", lastMod, 42 ));
        assertTrue(scan.hasChanged( "/goo/baaaa", lastMod, 69 ));
    }
    
    public void testScanAndLoad() throws Exception {
        Package p1 = new Package("p1");
        Package p2 = new Package("p2");
        
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        File p1f = new File(dir, "p1.pkg");
        File p2f = new File(dir, "p2.pkg");
        
        RuleBaseAssemblerTest.writePackage( p1, p1f );
        RuleBaseAssemblerTest.writePackage( p2, p2f);
        
        FileScanner scan = new FileScanner();
        Properties props = new Properties();
        props.setProperty( RuleAgent.FILES, p1f.getPath() + " " + p2f.getPath() );
        scan.configure( props );
        
        RuleBase rb = RuleBaseFactory.newRuleBase();
        scan.updateRuleBase( rb, true );
        
        assertEquals(2, rb.getPackages().length);
        assertEquals("p1", rb.getPackages()[0].getName());
        assertEquals("p2", rb.getPackages()[1].getName());
        
        
        scan.updateRuleBase( rb, true );
        assertEquals(2, rb.getPackages().length);
        assertEquals("p1", rb.getPackages()[0].getName());
        assertEquals("p2", rb.getPackages()[1].getName());

        RuleBaseAssemblerTest.writePackage( p2, p2f );
        scan.updateRuleBase( rb, true );
        assertEquals(2, rb.getPackages().length);
        assertEquals("p1", rb.getPackages()[0].getName());
        assertEquals("p2", rb.getPackages()[1].getName());
        
    }
    
    public void testEmptyList() throws Exception {
        FileScanner scan = new FileScanner();
        RuleBase rb = RuleBaseFactory.newRuleBase();
        scan.updateRuleBase( rb, true );
        assertEquals(0, rb.getPackages().length);
    }
    
    
}
