package org.kie.declarativetypes;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.rule.TypeMetaInfo;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;

public class JavaBeansEventRoleTest extends CommonTestMethodBase {

    @Test
    public void testFullyQualifiedDeclarativeTypeName() throws Exception {
        String declaration = "package org.drools.compiler;\n" +
                "public class Bean {}";

        String drl = "declare org.drools.compiler.Bean\n" +
                "  @role(event)\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/java/org/drools/compiler/Bean.java", declaration )
                .write( "src/main/resources/bean1.drl", drl );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final KieModule kieModule = kieBuilder.buildAll().getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        final String packageName = "org.drools.compiler";
        final String className = "Bean";
        final Class clazz = kieModuleMetaData.getClass( packageName,
                                                        className );
        final TypeMetaInfo typeMetaInfo = kieModuleMetaData.getTypeMetaInfo( clazz );
        assertTrue( typeMetaInfo.isEvent() );
    }

    @Test
    public void testBeanAndDeclarativeTypeInSamePackage() throws Exception {
        String declaration = "package org.drools.compiler;\n" +
                "public class Bean {}";

        String drl = "package org.drools.compiler;\n" +
                "declare Bean\n" +
                "  @role(event)\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/java/org/drools/compiler/Bean.java", declaration )
                .write( "src/main/resources/bean1.drl", drl );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final KieModule kieModule = kieBuilder.buildAll().getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        final String packageName = "org.drools.compiler";
        final String className = "Bean";
        final Class clazz = kieModuleMetaData.getClass( packageName,
                                                        className );
        final TypeMetaInfo typeMetaInfo = kieModuleMetaData.getTypeMetaInfo( clazz );
        assertTrue( typeMetaInfo.isEvent() );
    }

    @Test
    public void testImportBean() throws Exception {
        String declaration = "package org.drools.compiler;\n" +
                "public class Bean {}";

        String drl = "package some.other.package;\n" +
                "import org.drools.compiler.Bean;\n" +
                "declare Bean\n" +
                "  @role(event)\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/java/org/drools/compiler/Bean.java", declaration )
                .write( "src/main/resources/bean1.drl", drl );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final KieModule kieModule = kieBuilder.buildAll().getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( kieModule );

        final String packageName = "org.drools.compiler";
        final String className = "Bean";
        final Class clazz = kieModuleMetaData.getClass( packageName,
                                                        className );
        final TypeMetaInfo typeMetaInfo = kieModuleMetaData.getTypeMetaInfo( clazz );
        assertTrue( typeMetaInfo.isEvent() );
    }

}