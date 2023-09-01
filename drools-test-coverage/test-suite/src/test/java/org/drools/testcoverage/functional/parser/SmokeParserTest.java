package org.drools.testcoverage.functional.parser;

import java.io.File;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmokeParserTest extends ParserTest {

    private final Logger LOGGER = LoggerFactory.getLogger(SmokeParserTest.class);

    private static int count = 0;

    public SmokeParserTest(final File file, final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(file, kieBaseTestConfiguration);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return getTestParamsFromFiles(getFiles("smoke"));
    }

    @Test
    public void testParserSmoke() {
        LOGGER.warn(count++ + " : " + file.getName());
        final Resource fileResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, fileResource);
        LOGGER.warn("done : " + file.getName());
    }
}
