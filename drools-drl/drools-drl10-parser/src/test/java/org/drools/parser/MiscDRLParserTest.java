package org.drools.parser;

import junit.framework.TestCase;
import org.drools.drl.ast.descr.PackageDescr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * This test class is being ported from org.drools.mvel.compiler.lang.RuleParserTest
 */
public class MiscDRLParserTest extends TestCase {

    private DRLParserWrapper parser;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        parser = new DRLParserWrapper();
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertEquals("foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithErrorNode() throws Exception {
        final String source = "package 12 foo.bar.baz";
        final PackageDescr pkg = parser.parse(source);
        assertTrue(parser.hasErrors());
        // getText() combines an ErrorNode "12" so the result is different from DRL6Parser.
        assertEquals("12foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithAllErrorNode() throws Exception {
        final String source = "package 12 12312 231";
        final PackageDescr pkg = parser.parse(source);
        assertTrue(parser.hasErrors());
        // NPE inside DRLVisitorImpl.visitIdentifier(). So pkg is null. Different from DRL6Parser.
        assertNull(pkg);
    }
}
