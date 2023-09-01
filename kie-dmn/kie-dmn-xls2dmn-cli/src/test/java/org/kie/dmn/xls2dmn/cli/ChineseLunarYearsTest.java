package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.time.LocalDate;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.xls2dmn.cli.TestUtils.getRuntime;

import picocli.CommandLine;

public class ChineseLunarYearsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ChineseLunarYearsTest.class);

    @Test
    public void testCLI() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        final DMNRuntime dmnRuntime = getRuntime(new CommandLine(new App())::execute, tempFile, new String[]{"src/test/resources/ChineseLunarYears.xlsx", tempFile.toString()});
        DMNModel dmnModel = dmnRuntime.getModels().get(0);

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Date", LocalDate.of(2021, 4, 1));
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Chinese Year").getResult()).isEqualTo("Golden Ox");
    }
}