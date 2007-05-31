package org.drools.compiler;

import java.util.Properties;

import junit.framework.TestCase;

public class PackageBuilderConfigurationTest extends TestCase {

    public void testSystemProperties() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        assertEquals( PackageBuilderConfiguration.ECLIPSE,
                      cfg.getCompiler() );

        System.setProperty( "drools.compiler",
                            "JANINO" );
        cfg = new PackageBuilderConfiguration();
        assertEquals( PackageBuilderConfiguration.JANINO,
                      cfg.getCompiler() );

        final PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration();
        assertEquals( cfg.getCompiler(),
                      cfg2.getCompiler() );

        System.setProperty( "drools.compiler",
                            "ECLIPSE" );
        cfg = new PackageBuilderConfiguration();
        assertEquals( PackageBuilderConfiguration.ECLIPSE,
                      cfg.getCompiler() );

        cfg2.setCompiler( PackageBuilderConfiguration.ECLIPSE );
        assertEquals( PackageBuilderConfiguration.ECLIPSE,
                      cfg2.getCompiler() );

        cfg2.setCompiler( PackageBuilderConfiguration.JANINO );
        assertEquals( PackageBuilderConfiguration.JANINO,
                      cfg2.getCompiler() );

        final PackageBuilderConfiguration cfg3 = new PackageBuilderConfiguration();

        assertEquals( cfg.getCompiler(),
                      cfg3.getCompiler() );

    }

    public void testProgrammaticProperties() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        assertEquals( "java",
                      cfg.getDefaultDialect() );

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "tea" );
        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration( properties );
        assertEquals( "tea",
                      cfg1.getDefaultDialect() );

        final PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration(properties);
        assertEquals( cfg1.getDefaultDialect(),
                      cfg2.getDefaultDialect() );

        properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "coke" );
        PackageBuilderConfiguration cfg3 = new PackageBuilderConfiguration( properties );
        assertEquals( "coke",
                      cfg3.getDefaultDialect() );

        cfg2.setDefaultDialect( "orange" );
        assertEquals( "orange",
                      cfg2.getDefaultDialect() );

        cfg2.setDefaultDialect( "lemonade" );
        assertEquals( "lemonade",
                      cfg2.getDefaultDialect() );

        final PackageBuilderConfiguration cfg4 = new PackageBuilderConfiguration();

        assertEquals( cfg.getDefaultDialect(),
                      cfg4.getDefaultDialect() );
    }
}
