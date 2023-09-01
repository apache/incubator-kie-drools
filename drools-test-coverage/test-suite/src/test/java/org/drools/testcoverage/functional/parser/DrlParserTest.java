package org.drools.testcoverage.functional.parser;

import java.io.File;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;

public class DrlParserTest extends ParserTest {

    public DrlParserTest(final File file, final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(file, kieBaseTestConfiguration);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return getTestParamsFromFiles(getFiles("drl"));
    }

    @Test
    public void testParserSmoke() {
        final Resource fileResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, fileResource);
    }
}
