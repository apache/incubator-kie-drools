package org.drools.model.codegen.execmodel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.PackageModel.getPkgUUID;
import static org.drools.util.StringUtils.generateUUID;

public class ModelBuilderImplTest {

    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("group:artifact:version");
    private  static final  KnowledgeBuilderConfigurationImpl CONFIGURATION = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
    private InternalKnowledgePackage internalKnowledgePackage;
    private PackageRegistry packageRegistry;
    private ModelBuilderImpl<PackageSources> modelBuilder;

    @Before
    public void setup() {
        internalKnowledgePackage = CoreComponentFactory.get().createKnowledgePackage("apackage");
        modelBuilder = new ModelBuilderImpl<>(PackageSources::dumpSources, CONFIGURATION, RELEASE_ID, false);
        packageRegistry = new PackageRegistry(ModelBuilderImplTest.class.getClassLoader(), CONFIGURATION, internalKnowledgePackage);
    }

    @Test
    public void getPackageModelWithPkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr packageDescr = getPackageDescr(pkgUUID);
        PackageModel retrieved =  modelBuilder.getPackageModel(packageDescr, packageRegistry, internalKnowledgePackage.getName());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getPackageUUID()).isEqualTo(pkgUUID);
    }

    @Test
    public void getPackageModelWithoutPkgUUID() {
        PackageDescr packageDescr = getPackageDescr(null);
        PackageModel retrieved =  modelBuilder.getPackageModel(packageDescr, packageRegistry, internalKnowledgePackage.getName());
        assertThat(retrieved).isNotNull();
        String expected = getPkgUUID(RELEASE_ID, internalKnowledgePackage.getName());
        assertThat(retrieved.getPackageUUID()).isEqualTo(expected);
    }

    private PackageDescr getPackageDescr(String pkgUUID) {
        PackageDescr packageDescr = new PackageDescr();
        packageDescr.addGlobal(getGlobalDescr());
        if (pkgUUID != null) {
            packageDescr.setPreferredPkgUUID(pkgUUID);
        }

        return packageDescr;
    }

    private GlobalDescr getGlobalDescr() {
        GlobalDescr toReturn = new GlobalDescr();
        toReturn.setType(Double.class.getName());
        toReturn.setIdentifier("DOUBLE_GLOBAL");
        return toReturn;
    }

}