/*
 * Copyright 2015 JBoss Inc
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

package org.kie.scanner;

import junit.framework.TestCase;
import org.drools.core.rule.TypeMetaInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.Role;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;
import static org.junit.Assert.fail;

public class KieModuleMetaDataTest extends AbstractKieCiTest {

    @Test
    @Ignore
    public void testKieModuleMetaData() throws Exception {
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "org.drools", "drools-core", "5.5.0.Final" );
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( releaseId );
        checkDroolsCoreDep( kieModuleMetaData );
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClass() throws Exception {
        testKieModuleMetaDataInMemory( false );
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemory( true );
    }

    @Test
    public void testKieModuleMetaDataInMemoryUsingPOMWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemoryUsingPOM( true );
    }

    @Test
    public void testKieModuleMetaDataForDependenciesInMemory() throws Exception {
        testKieModuleMetaDataForDependenciesInMemory( false );
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClassDefaultPackage() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "javaDefaultPackage", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/java/test/Bean.java", createJavaSource() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertTrue( messages.isEmpty() );

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        //The call to kieModuleMetaData.getClass() assumes a Java file has an explicit package
        final Class<?> beanClass = kieModuleMetaData.getClass( "", "test.Bean" );
        assertNotNull( beanClass );

        final TypeMetaInfo beanMetaInfo = kieModuleMetaData.getTypeMetaInfo( beanClass );
        assertNotNull( beanMetaInfo );
    }

    @Test
    public void testGetPackageNames() {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/test.drl",
                   "package org.test declare Bean end" );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertTrue( messages.isEmpty() );

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        assertFalse( kieModuleMetaData.getPackages().isEmpty() );
        TestCase.assertTrue( kieModuleMetaData.getPackages().contains( "org.test" ) );
    }

    @Test
    public void testGetRuleNames() {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/test1.drl",
                   "package org.test\n" +
                           "rule A\n" +
                           " when\n" +
                           "then\n" +
                           "end\n" +
                           "rule B\n" +
                           " when\n" +
                           "then\n" +
                           "end\n" );
        kfs.write( "src/main/resources/test2.drl",
                   "package org.test\n" +
                           "rule C\n" +
                           " when\n" +
                           "then\n" +
                           "end\n" );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertTrue( messages.isEmpty() );

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        Collection<String> rules = kieModuleMetaData.getRuleNamesInPackage( "org.test" );
        assertEquals( 3, rules.size() );
        assertTrue( rules.containsAll( asList( "A", "B", "C" ) ) );
    }

    private String createJavaSource() {
        return "package test;\n" +
                "public class Bean {\n" +
                "   private int value;\n" +
                "   public int getValue() {\n" +
                "       return value;\n" +
                "   }\n" +
                "}";
    }

    private void testKieModuleMetaDataInMemory( boolean useTypeDeclaration ) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId( "org.drools", "drools-core", "5.5.0.Final" );
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "metadata-test", "1.0-SNAPSHOT" );

        InternalKieModule kieModule = createKieJarWithClass( ks, releaseId, useTypeDeclaration, 2, 7, dependency );
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );
        checkDroolsCoreDep( kieModuleMetaData );

        Collection<String> testClasses = kieModuleMetaData.getClasses( "org.kie.test" );
        assertEquals( 1, testClasses.size() );
        assertEquals( "Bean", testClasses.iterator().next() );
        Class<?> beanClass = kieModuleMetaData.getClass( "org.kie.test", "Bean" );
        assertNotNull( beanClass.getMethod( "getValue" ) );

        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo( beanClass );
        assertNotNull( beanTypeInfo );

        assertTrue( beanTypeInfo.isEvent() );

        Role role = beanClass.getAnnotation( Role.class );
        assertNotNull( role );
        assertEquals( Role.Type.EVENT, role.value() );

        assertEquals( useTypeDeclaration, beanTypeInfo.isDeclaredType() );
    }

    private void testKieModuleMetaDataInMemoryUsingPOM( boolean useTypeDeclaration ) throws Exception {
        //Build a KieModule jar, deploy it into local Maven repository
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId( "org.drools", "drools-core", "5.5.0.Final" );
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "metadata-test", "1.0-SNAPSHOT" );
        InternalKieModule kieModule = createKieJarWithClass( ks, releaseId, useTypeDeclaration, 2, 7, dependency );
        String pomText = getPom( dependency );
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName( releaseId, null ) + ".pom" );
        try {
            FileOutputStream fos = new FileOutputStream( pomFile );
            fos.write( pomText.getBytes() );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        MavenRepository.getMavenRepository().deployArtifact( releaseId, kieModule, pomFile );

        //Build a second KieModule, depends on the first KieModule jar which we have deployed into Maven
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "metadata-test-using-pom", "1.0-SNAPSHOT" );
        String pomText2 = getPom( releaseId2, releaseId );
        File pomFile2 = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName( releaseId2, null ) + ".pom" );
        try {
            FileOutputStream fos = new FileOutputStream( pomFile2 );
            fos.write( pomText2.getBytes() );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( pomFile2 );
        //checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses( "org.kie.test" );
        assertEquals( 1, testClasses.size() );
        assertEquals( "Bean", testClasses.iterator().next() );
        Class<?> beanClass = kieModuleMetaData.getClass( "org.kie.test", "Bean" );
        assertNotNull( beanClass.getMethod( "getValue" ) );

        if ( useTypeDeclaration ) {
            assertTrue( kieModuleMetaData.getTypeMetaInfo( beanClass ).isEvent() );
        }
    }

    private void checkDroolsCoreDep( KieModuleMetaData kieModuleMetaData ) {
        assertEquals( 17, kieModuleMetaData.getClasses( "org.drools.runtime" ).size() );
        Class<?> statefulKnowledgeSessionClass = kieModuleMetaData.getClass( "org.drools.runtime", "StatefulKnowledgeSession" );
        assertTrue( statefulKnowledgeSessionClass.isInterface() );
        assertEquals( 2, statefulKnowledgeSessionClass.getDeclaredMethods().length );
    }

    private void testKieModuleMetaDataForDependenciesInMemory( boolean useTypeDeclaration ) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId( "org.drools", "drools-core", "5.5.0.Final" );
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "metadata-test", "1.0-SNAPSHOT" );

        InternalKieModule kieModule = createKieJarWithClass( ks, releaseId, useTypeDeclaration, 2, 7, dependency );
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );
        checkDroolsCoreDep( kieModuleMetaData );

        Collection<String> testClasses = kieModuleMetaData.getClasses( "org.drools" );
        assertEquals( 55, testClasses.size() );
        Class<?> beanClass = kieModuleMetaData.getClass( "org.drools", "QueryResult" );
        assertNotNull( beanClass );

        //Classes in dependencies should have TypeMetaInfo
        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo( beanClass );
        assertNotNull( beanTypeInfo );

        if ( useTypeDeclaration ) {
            assertTrue( beanTypeInfo.isEvent() );
        }

        assertEquals( useTypeDeclaration, beanTypeInfo.isDeclaredType() );
    }

    @Test
    @Ignore("https://bugzilla.redhat.com/show_bug.cgi?id=1049674")
    public void testKieMavenPluginEmptyProject() {
        // According to https://bugzilla.redhat.com/show_bug.cgi?id=1049674#c2 the below is the minimal POM required to use KieMavenPlugin.
        // However when we attempt to retrieve meta-data about the classes in the KieModule some are not accessible. IDK whether the minimal
        // POM is correct; or whether KieModuleMetaData needs to ignore certain classes (e.g. if a transient dependency is optional?!?)
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                           + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                           + "<modelVersion>4.0.0</modelVersion>"
                           + "<groupId>org.kie</groupId>"
                           + "<artifactId>plugin-test</artifactId>"
                           + "<version>1.0</version>"
                           + "<packaging>kjar</packaging>"
                           + "<dependencies>"
                           + "<dependency>"
                           + "<groupId>org.drools</groupId>"
                           + "<artifactId>drools-compiler</artifactId>"
                           + "<version>6.1.0-SNAPSHOT</version>"
                           + "</dependency>"
                           + "</dependencies>"
                           + "<build>"
                           + "<plugins>"
                           + "<plugin>"
                           + "<groupId>org.kie</groupId>"
                           + "<artifactId>kie-maven-plugin</artifactId>"
                           + "<version>6.1.0-SNAPSHOT</version>"
                           + "<extensions>true</extensions>"
                           + "</plugin>"
                           + "</plugins>"
                           + "</build>"
                           + "</project>" );

        kfs.write("/src/main/resources/META-INF/kmodule.xml",
                  "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertTrue( messages.isEmpty() );

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        boolean fail = false;
        for ( final String packageName : kieModuleMetaData.getPackages() ) {
            for ( final String className : kieModuleMetaData.getClasses( packageName ) ) {
                try {
                    kieModuleMetaData.getClass( packageName, className );
                } catch ( Throwable e ) {
                    fail = true;
                    System.out.println( e );
                }
            }
        }
        if ( fail ) {
            fail( "See console for details." );
        }
    }

}
