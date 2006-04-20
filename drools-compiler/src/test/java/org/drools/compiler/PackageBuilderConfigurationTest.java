package org.drools.compiler;

import junit.framework.TestCase;

public class PackageBuilderConfigurationTest extends TestCase {

    public void testSystemProperty() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        assertEquals(cfg.getCompiler(), PackageBuilderConfiguration.getDefaultCompiler());
        
        System.setProperty( "drools.compiler", "JANINO" );
        assertEquals(PackageBuilderConfiguration.JANINO, PackageBuilderConfiguration.getDefaultCompiler());
        
        PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration();
        assertEquals(cfg.getCompiler(), cfg2.getCompiler());

        System.setProperty( "drools.compiler", "ECLIPSE" );
        assertEquals(PackageBuilderConfiguration.ECLIPSE, PackageBuilderConfiguration.getDefaultCompiler());
        
        cfg2.setCompiler( PackageBuilderConfiguration.ECLIPSE );
        assertEquals(PackageBuilderConfiguration.ECLIPSE, cfg2.getCompiler());
        
        cfg2.setCompiler( PackageBuilderConfiguration.JANINO );
        assertEquals(PackageBuilderConfiguration.JANINO, cfg2.getCompiler());
        
        PackageBuilderConfiguration cfg3 = new PackageBuilderConfiguration();
        
        assertEquals(cfg.getCompiler(), cfg3.getCompiler());
        
    }
    
}
