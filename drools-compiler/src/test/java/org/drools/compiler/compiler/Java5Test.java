/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
