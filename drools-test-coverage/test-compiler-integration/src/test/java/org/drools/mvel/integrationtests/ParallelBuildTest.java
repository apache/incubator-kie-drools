package org.drools.mvel.integrationtests;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ParallelBuildTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ParallelBuildTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private final List<Class<?>> classes = Arrays.asList(
            java.util.List.class,
            java.awt.Color.class,
            java.util.concurrent.Callable.class,
            java.util.concurrent.atomic.AtomicBoolean.class,
            java.util.concurrent.locks.Lock.class,
            java.util.zip.ZipFile.class,
            java.awt.color.ColorSpace.class,
            java.awt.font.TextMeasurer.class,
            java.awt.geom.Area.class,
            java.awt.im.InputContext.class,
            java.net.Inet4Address.class,
            java.io.File.class
    );

    @Test
    public void testParallelBuild() {
        StringBuilder sb = new StringBuilder();
        int rc = 0;
        for (Class<?> c : classes) {
            sb.append("rule \"rule_" + rc++ + "\"\n");
            sb.append("  when\n");
            sb.append("    a : " + c.getName() + "()\n");
            sb.append("  then\n");
            sb.append("    System.out.print(\".\");\n");
            sb.append("end\n");
            sb.append("\n");
        }

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, sb.toString());
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

}
