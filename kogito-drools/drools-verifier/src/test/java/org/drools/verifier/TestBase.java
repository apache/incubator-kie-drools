package org.drools.verifier;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.visitor.PackageDescrVisitor;
import org.junit.Before;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.jar.JarInputStream;

public class TestBase {

    protected VerifierData verifierData;
    protected PackageDescrVisitor packageDescrVisitor;

    @Before
    public void setUp() throws Exception {
        verifierData = VerifierReportFactory.newVerifierData();
        packageDescrVisitor = new PackageDescrVisitor(verifierData,
                Collections.<JarInputStream>emptyList());
    }

    protected PackageDescr getPackageDescr(InputStream resourceAsStream) throws DroolsParserException {
        Reader drlReader = new InputStreamReader(resourceAsStream);
        return new DrlParser().parse(drlReader);
    }
}
