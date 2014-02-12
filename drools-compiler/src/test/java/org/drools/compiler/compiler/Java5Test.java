package org.drools.compiler.compiler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.junit.Test;

public class Java5Test extends DroolsTestCase {

    @Test
    public void testJava5Rule() throws Exception {
        final String javaVersion = System.getProperty( "java.specification.version" );
        //do not execute tests under JDK 1.4
        //otherwise the compiled version cannot be interpreted
        if ( javaVersion.equals( "1.4" ) ) {
            System.out.println( "Skipping Java 1.5 tests - current JDK not compatible" );
            return;
        }
        final KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        JavaDialectConfiguration javaConf = ( JavaDialectConfiguration ) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.ECLIPSE );
        javaConf.setJavaLanguageLevel( "1.5" );
        
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl( conf );
        builder.addPackageFromDrl( new InputStreamReader( this.getClass().getResourceAsStream( "java5_rule.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
    }

    @Test
    public void testJava14Defaults() throws Exception {
        final KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        JavaDialectConfiguration javaConf = ( JavaDialectConfiguration ) conf.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl( conf );
        builder.addPackageFromDrl( new InputStreamReader( this.getClass().getResourceAsStream( "java5_rule.drl" ) ) );
        assertTrue( builder.hasErrors() );
    }

}
