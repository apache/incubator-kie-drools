package org.drools.compiler.lang.descr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.impl.PackageDescrBuilderImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageDescrTest {

    @Test
    public void createPackageDescrWithTypeDeclarationDescr() throws IOException {
        PackageDescrBuilder builder = PackageDescrBuilderImpl.newPackage();
        builder.newDeclare().type().name("java.lang.String");
        PackageDescr descr = builder.getDescr();
        OutputStream os = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(os);
        descr.writeExternal(oo);
        assertThat(os.toString()).isNotNull();
    }

}