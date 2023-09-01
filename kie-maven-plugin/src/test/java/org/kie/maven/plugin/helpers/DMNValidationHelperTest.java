package org.kie.maven.plugin.helpers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
import org.kie.dmn.validation.DMNValidator;

import static org.assertj.core.api.Assertions.assertThat;
public class DMNValidationHelperTest {

    private final static Log log = new SystemStreamLog();

    @Test
    public void testFlagsOK() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("VALIDATE_SCHEMA,VALIDATE_MODEL", log);
        assertThat(result).isNotNull()
                .hasSize(2)
                .contains(DMNValidator.Validation.VALIDATE_SCHEMA, DMNValidator.Validation.VALIDATE_MODEL);
    }

    @Test
    public void testFlagsDisable() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("disabled", log);
        assertThat(result).isNotNull()
                .hasSize(0);
    }

    @Test
    public void testFlagsUnknown() {
        List<DMNValidator.Validation> result = DMNValidationHelper.computeFlagsFromCSVString("VALIDATE_SCHEMA,boh", log);
        assertThat(result).isNotNull()
                .hasSize(0);
    }
}