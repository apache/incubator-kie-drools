package org.kie.maven.plugin;

import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidateDMNMojoFlagsTest {

    public static final Logger LOG = LoggerFactory.getLogger(ValidateDMNMojoFlagsTest.class);

    private AbstractDMNValidationAwareMojo mojo;

    @Before
    public void init() {
        mojo = new ValidateDMNMojo();
        mojo.setLog(new SystemStreamLog());
    }

    @Test
    public void testFlagsOK() {
        List<Validation> result = mojo.computeFlagsFromCSVString("VALIDATE_SCHEMA,VALIDATE_MODEL");
        assertThat(result).isNotNull()
                          .hasSize(2)
                          .contains(Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL);
    }

    @Test
    public void testFlagsDisable() {
        List<Validation> result = mojo.computeFlagsFromCSVString("disabled");
        assertThat(result).isNotNull()
                          .hasSize(0);
    }

    @Test
    public void testFlagsUnknown() {
        List<Validation> result = mojo.computeFlagsFromCSVString("VALIDATE_SCHEMA,boh");
        assertThat(result).isNotNull()
                          .hasSize(0);
    }

}
