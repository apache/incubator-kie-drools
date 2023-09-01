package org.kie.dmn.legacy.tests.validation.v1_1;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorInputDataTest extends AbstractValidatorTest {

    @Test
    public void testINPUT_MISSING_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("inputdata/INPUTDATA_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        }
    }

    @Test
    public void testINPUT_MISSING_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("inputdata/INPUTDATA_MISSING_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    public void testINPUT_MISSING_VAR_DefintionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("inputdata/INPUTDATA_MISSING_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "INPUTDATA_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    public void testINPUT_MISMATCH_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("inputdata/INPUTDATA_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
        }
    }

    @Test
    public void testINPUT_MISMATCH_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("inputdata/INPUTDATA_MISMATCH_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    public void testINPUT_MISMATCH_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("inputdata/INPUTDATA_MISMATCH_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "INPUTDATA_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }
}
