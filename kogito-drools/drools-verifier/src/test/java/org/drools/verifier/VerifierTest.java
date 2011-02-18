/**
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

package org.drools.verifier;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.jar.JarInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ReaderResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class VerifierTest {

    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 6,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 1,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    @Test
    public void testVerifierInvalidDRLs() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        String drl = "This will not work";

        verifier.addResourcesToVerify( new ReaderResource( new StringReader( drl ) ),
                                       ResourceType.DRL );

        assertTrue( verifier.hasErrors() );

        assertEquals( 2,
                      verifier.getErrors().size() );

    }

    @Test
    public void testVerifierNullPackageDescr() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        String drl = "#This will not work";

        verifier.addResourcesToVerify( new ReaderResource( new StringReader( drl ) ),
                                       ResourceType.DRL );

        assertTrue( verifier.hasErrors() );

        assertEquals( 1,
                      verifier.getErrors().size() );

    }

    @Test
    public void testFactTypesFromJar() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        try {

            JarInputStream jar = new JarInputStream( this.getClass().getResourceAsStream( "model.jar" ) );

            verifier.addObjectModel( jar );

        } catch ( IOException e ) {
            fail( e.getMessage() );
        }

        verifier.addResourcesToVerify( new ClassPathResource( "imports.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();

        Collection<ObjectType> objectTypes = result.getVerifierData().getAll( VerifierComponentType.OBJECT_TYPE );

        assertNotNull( objectTypes );
        assertEquals( 3,
                      objectTypes.size() );

        Collection<Field> fields = result.getVerifierData().getAll( VerifierComponentType.FIELD );

        assertNotNull( fields );
        assertEquals( 10,
                      fields.size() );

    }

    @Test
    public void testFactTypesFromJarAndDeclarations() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        try {

            JarInputStream jar = new JarInputStream( this.getClass().getResourceAsStream( "model.jar" ) );

            verifier.addObjectModel( jar );

        } catch ( IOException e ) {
            fail( e.getMessage() );
        }

        verifier.addResourcesToVerify( new ClassPathResource( "importsAndDeclarations.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();

        Collection<ObjectType> objectTypes = result.getVerifierData().getAll( VerifierComponentType.OBJECT_TYPE );

        for ( ObjectType objectType : objectTypes ) {
            if ( objectType.getName().equals( "VoiceCall" ) ) {
                assertEquals( 4,
                              objectType.getMetadata().keySet().size() );
            }
        }

        assertNotNull( objectTypes );
        assertEquals( 4,
                      objectTypes.size() );

        Collection<Field> fields = result.getVerifierData().getAll( VerifierComponentType.FIELD );

        assertNotNull( fields );
        assertEquals( 11,
                      fields.size() );

    }

    @Test
    public void testCustomRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "FindPatterns.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        if ( !works ) {
            for ( VerifierError error : verifier.getErrors() ) {
                System.out.println( error.getMessage() );
            }
            fail( "Could not run verifier" );
        }
        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 6,
                      result.getBySeverity( Severity.NOTE ).size() );

        for ( VerifierMessageBase m : result.getBySeverity( Severity.NOTE ) ) {
            assertEquals( "This pattern was found.",
                          m.getMessage() );
        }
    }
}
