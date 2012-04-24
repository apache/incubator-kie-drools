package org.drools.integrationtests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.StringReader;
import java.util.Collection;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.junit.Test;

public class KnowledgeBuilderTest {

    @Test
    public void testAddKPackageSingle() throws Exception {
        String rule = "package org.drools.test\n" +
                      "import org.drools.StockTick\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, kpkgs.size() );

        KnowledgePackage kpkg = kpkgs.iterator().next();

        byte[] skpkg = DroolsStreamUtils.streamOut( kpkg );

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( skpkg ), ResourceType.PKG );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, kpkgs.size() );
        kpkg = kpkgs.iterator().next();
        assertEquals( 1, kpkg.getRules().size() );
    }

    @Test
    public void testAddKPackageCollection() throws Exception {
        String rule = "package org.drools.test\n" +
                      "import org.drools.StockTick\n" +
                      "declare StockTick @role(event) end\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2, kpkgs.size() );

        byte[] skpkg = DroolsStreamUtils.streamOut( kpkgs );

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( skpkg ), ResourceType.PKG );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2, kpkgs.size() );
    }

    @Test
    public void testAddPackageSingle() throws Exception {
        String rule = "package org.drools.test\n" +
                      "import org.drools.StockTick\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        PackageBuilder pkgbuilder = new PackageBuilder();
        pkgbuilder.addPackageFromDrl( new StringReader( rule ) );
        assertFalse( pkgbuilder.getErrors().toString(), pkgbuilder.hasErrors() );

        Package pkg = pkgbuilder.getPackage();

        byte[] spkg = DroolsStreamUtils.streamOut( pkg );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( spkg ), ResourceType.PKG );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, kpkgs.size() );
        KnowledgePackage kpkg = kpkgs.iterator().next();
        assertEquals( 1, kpkg.getRules().size() );
    }

    @Test
    public void testAddPackageArray() throws Exception {
        String rule = "package org.drools.test\n" +
                      "import org.drools.StockTick\n" +
                      "declare StockTick @role(event) end\n" +
                      "rule R1 when\n" +
                      "   StockTick()\n" +
                      "then\n" +
                      "end\n";

        PackageBuilder pkgbuilder = new PackageBuilder();
        pkgbuilder.addPackageFromDrl( new StringReader( rule ) );
        assertFalse( pkgbuilder.getErrors().toString(), pkgbuilder.hasErrors() );

        Package[] pkgs = pkgbuilder.getPackages();

        byte[] spkgs = DroolsStreamUtils.streamOut( pkgs );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( spkgs ), ResourceType.PKG );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2, kpkgs.size() );
    }

}
