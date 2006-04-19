package org.drools.xml;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.lang.descr.PackageDescr;

public class PackageReaderTest extends TestCase {
    public void testParsePackageName() throws Exception {
        PackageReader packageReader = new PackageReader( );
        packageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParsePackageName.xml" ) ) );
        PackageDescr packageDescr = packageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals("com.sample", packageDescr.getName() );
    }
    

    public void testParseImport() throws Exception {
        PackageReader packageReader = new PackageReader( );
        packageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseImport.xml" ) ) );
        PackageDescr packageDescr = packageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals("com.sample", packageDescr.getName() );
        
        List imports = packageDescr.getImports();
        assertEquals( 2, imports.size() );
        assertEquals("java.util.HashMap", imports.get( 0 ) );
        assertEquals("org.drools.*", imports.get( 1 ) );
    }
    
    public void testParseGlobal() throws Exception {
        PackageReader packageReader = new PackageReader( );
        packageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseGlobal.xml" ) ) );
        PackageDescr packageDescr = packageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals("com.sample", packageDescr.getName() );
        
        List imports = packageDescr.getImports();
        assertEquals( 2, imports.size() );
        assertEquals("java.util.HashMap", imports.get( 0 ) );
        assertEquals("org.drools.*", imports.get( 1 ) );
        
        Map globals = packageDescr.getGlobals();
        assertEquals( 2, globals.size() );
        assertEquals("com.sample.X", globals.get( "x" ) );
        assertEquals("com.sample.Yada", globals.get( "yada" ) );        
    }    
    
}
