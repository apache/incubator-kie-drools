/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.rule.Package;

public class FileScannerTest {

    @Test
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
    
    @Test
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
        PackageProvider.applyChanges( rb, true, scan.loadPackageChanges().getChangedPackages(), new MockListener() );
        
        
        
        assertEquals(2, rb.getPackages().length);
        assertTrue("p1".equals(rb.getPackages()[0].getName()) || "p1".equals(rb.getPackages()[1].getName()));
        assertTrue("p2".equals(rb.getPackages()[0].getName()) || "p2".equals(rb.getPackages()[1].getName()));

        PackageProvider.applyChanges( rb, true, scan.loadPackageChanges().getChangedPackages(), new MockListener() );
        assertEquals(2, rb.getPackages().length);
        assertTrue("p1".equals(rb.getPackages()[0].getName()) || "p1".equals(rb.getPackages()[1].getName()));
        assertTrue("p2".equals(rb.getPackages()[0].getName()) || "p2".equals(rb.getPackages()[1].getName()));

        RuleBaseAssemblerTest.writePackage( p2, p2f );
        PackageProvider.applyChanges( rb, true, scan.loadPackageChanges().getChangedPackages(), new MockListener() );


        assertEquals(2, rb.getPackages().length);
        assertTrue("p1".equals(rb.getPackages()[0].getName()) || "p1".equals(rb.getPackages()[1].getName()));
        assertTrue("p2".equals(rb.getPackages()[0].getName()) || "p2".equals(rb.getPackages()[1].getName()));
        
    }
    
    @Test
    public void testEmptyList() throws Exception {
        FileScanner scan = new FileScanner();
        RuleBase rb = RuleBaseFactory.newRuleBase();
        
        PackageProvider.applyChanges( rb, true, scan.loadPackageChanges().getChangedPackages(), new MockListener() );

        assertEquals(0, rb.getPackages().length);
    }
    
    @Test
    public void testFileChanges() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        File t = new File(dir, "x.bar");
        
        Package x = new Package("x");
        RuleBaseAssemblerTest.writePackage( x, t );
        
        FileScanner scan = new FileScanner();
        Map updates = new HashMap();
        assertTrue(scan.hasChanged( "x", updates, t.lastModified() ));
        assertFalse(scan.hasChanged( "x", updates, t.lastModified() ));
        
    }

    
    
}
