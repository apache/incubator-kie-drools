package org.kie.scanner;

import org.drools.core.rule.TypeDeclaration;
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
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

public class KieModuleMetaDataTest extends AbstractKieCiTest {

    @Test @Ignore
    public void testKieModuleMetaData() throws Exception {
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
        checkDroolsCoreDep(kieModuleMetaData);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClass() throws Exception {
        testKieModuleMetaDataInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemory(true);
    }

    @Test
    public void testKieModuleMetaDataInMemoryUsingPOMWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemoryUsingPOM(true);
    }

    @Test
    public void testKieModuleMetaDataForDependenciesInMemory() throws Exception {
        testKieModuleMetaDataForDependenciesInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClassDefaultPackage() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "javaDefaultPackage", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/java/Bean.java", createJavaSource() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertTrue( messages.isEmpty() );

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);

        //The call to kieModuleMetaData.getClass() assumes a Java file has an explicit package
        final Class<?> beanClass = kieModuleMetaData.getClass("", "Bean");
        assertNotNull( beanClass );

        final TypeMetaInfo beanMetaInfo = kieModuleMetaData.getTypeMetaInfo( beanClass );
        assertNotNull( beanMetaInfo );
    }

    private String createJavaSource() {
        return "public class Bean {\n" +
                "   private int value;\n" +
                "   public int getValue() {\n" +
                "       return value;\n" +
                "   }\n" +
                "}";
    }

    private void testKieModuleMetaDataInMemory(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");

        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, dependency);
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.kie.test");
        assertEquals(1, testClasses.size());
        assertEquals("Bean", testClasses.iterator().next());
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.test", "Bean");
        assertNotNull(beanClass.getMethod("getValue"));

        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo( beanClass );
        assertNotNull( beanTypeInfo );

        assertTrue(beanTypeInfo.isEvent());

        Role role = beanClass.getAnnotation(Role.class);
        assertNotNull(role);
        assertEquals(Role.Type.EVENT, role.value());

        assertEquals(useTypeDeclaration, beanTypeInfo.isDeclaredType());
    }

    private void testKieModuleMetaDataInMemoryUsingPOM(boolean useTypeDeclaration) throws Exception {
        //Build a KieModule jar, deploy it into local Maven repository
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");
        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, dependency);
        String pomText = getPom(dependency);
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().deployArtifact(releaseId, kieModule, pomFile);

        //Build a second KieModule, depends on the first KieModule jar which we have deployed into Maven
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "metadata-test-using-pom", "1.0-SNAPSHOT");
        String pomText2 = getPom(releaseId2, releaseId);
        File pomFile2 = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName(releaseId2, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile2);
            fos.write(pomText2.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(pomFile2);
        //checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.kie.test");
        assertEquals(1, testClasses.size());
        assertEquals("Bean", testClasses.iterator().next());
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.test", "Bean");
        assertNotNull(beanClass.getMethod("getValue"));

        if (useTypeDeclaration) {
            assertTrue(kieModuleMetaData.getTypeMetaInfo(beanClass).isEvent());
        }
    }

    private void checkDroolsCoreDep(KieModuleMetaData kieModuleMetaData) {
        assertEquals(17, kieModuleMetaData.getClasses("org.drools.runtime").size());
        Class<?> statefulKnowledgeSessionClass = kieModuleMetaData.getClass("org.drools.runtime", "StatefulKnowledgeSession");
        assertTrue(statefulKnowledgeSessionClass.isInterface());
        assertEquals(2, statefulKnowledgeSessionClass.getDeclaredMethods().length);
    }

    private void testKieModuleMetaDataForDependenciesInMemory(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");

        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, dependency);
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.drools");
        assertEquals(55, testClasses.size());
        Class<?> beanClass = kieModuleMetaData.getClass("org.drools", "QueryResult");
        assertNotNull( beanClass );

        //Classes in dependencies should have TypeMetaInfo
        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo( beanClass);
        assertNotNull( beanTypeInfo );

        if (useTypeDeclaration) {
            assertTrue(beanTypeInfo.isEvent());
        }

        assertEquals(useTypeDeclaration, beanTypeInfo.isDeclaredType());
    }
}
