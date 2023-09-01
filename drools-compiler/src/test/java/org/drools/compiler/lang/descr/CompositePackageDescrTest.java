package org.drools.compiler.lang.descr;

import org.drools.io.ByteArrayResource;
import org.drools.drl.ast.descr.PackageDescr;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.util.StringUtils.generateUUID;

public class CompositePackageDescrTest {

    private static final String NAMESPACE = "namespace";
    private  CompositePackageDescr compositePackageDescr;

    @Before
    public void setup() {
        compositePackageDescr = new CompositePackageDescr(new ByteArrayResource(), new PackageDescr(NAMESPACE));
    }

    @Test
    public void addPackageDescrSamePkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr toAdd = new PackageDescr(NAMESPACE);
        toAdd.setPreferredPkgUUID(pkgUUID);
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), toAdd);
        assertThat(compositePackageDescr.getPreferredPkgUUID().isPresent()).isTrue();
        assertThat(compositePackageDescr.getPreferredPkgUUID().get()).isEqualTo(pkgUUID);
        toAdd = new PackageDescr(NAMESPACE);
        toAdd.setPreferredPkgUUID(pkgUUID);
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), toAdd);
        assertThat(compositePackageDescr.getPreferredPkgUUID().get()).isEqualTo(pkgUUID);
    }

    @Test(expected = RuntimeException.class)
    public void addPackageDescrDifferentPkgUUID() {
        String pkgUUID = generateUUID();
        PackageDescr first = new PackageDescr(NAMESPACE);
        first.setPreferredPkgUUID(pkgUUID);
        assertThat(first.getPreferredPkgUUID().isPresent()).isTrue();
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), first);
        assertThat(compositePackageDescr.getPreferredPkgUUID().isPresent()).isTrue();
        assertThat(compositePackageDescr.getPreferredPkgUUID().get()).isEqualTo(pkgUUID);
        pkgUUID = generateUUID();
        PackageDescr second = new PackageDescr(NAMESPACE);
        second.setPreferredPkgUUID(pkgUUID);
        assertThat(second.getPreferredPkgUUID().isPresent()).isTrue();
        assertThat(second.getPreferredPkgUUID().get()).isNotEqualTo(first.getPreferredPkgUUID().get());
        compositePackageDescr.addPackageDescr(new ByteArrayResource(), second);
    }
}