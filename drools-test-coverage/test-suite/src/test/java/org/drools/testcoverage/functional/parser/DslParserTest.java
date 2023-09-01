package org.drools.testcoverage.functional.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;

public class DslParserTest extends ParserTest {
    private final File dsl;

    public DslParserTest(final File dslr, final File dsl, final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(dslr, kieBaseTestConfiguration);
        this.dsl = dsl;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Set<Object[]> set = new HashSet<>();

        for (File f : getFiles("dsl", "dslr")) {
            final String dslPath = f.getAbsolutePath();
            final File dsl = new File(dslPath.substring(0, dslPath.length() - 1));
            set.add(new Object[] {dsl, f, KieBaseTestConfiguration.CLOUD_EQUALITY});
            set.add(new Object[]{dsl, f, KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN});
        }

        return set;
    }

    @Test
    public void testParserDsl() {
        final Resource dslResource = KieServices.Factory.get().getResources().newFileSystemResource(dsl);
        final Resource dslrResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, dslResource, dslrResource);
    }

    @Test
    public void testParserDsl2() {
        final Resource dslResource = KieServices.Factory.get().getResources().newFileSystemResource(dsl);
        final Resource dslrResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, dslrResource, dslResource);
    }
}
