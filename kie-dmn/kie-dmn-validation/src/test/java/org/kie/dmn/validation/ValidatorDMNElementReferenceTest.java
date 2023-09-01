package org.kie.dmn.validation;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorDMNElementReferenceTest extends AbstractValidatorTest {

    @Test
    public void testELEMREF_NOHASH_ReaderInput() throws IOException {
        try (final Reader reader = getReader("dmnelementref/ELEMREF_NOHASH.dmn")) {
            final List<DMNMessage> validationMessages = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertValiadationResult(validationMessages);
        }
    }

    @Test
    public void testELEMREF_NOHASH_FileInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getFile("dmnelementref/ELEMREF_NOHASH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    @Test
    public void testELEMREF_NOHASH_DefinitionsInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getDefinitions("dmnelementref/ELEMREF_NOHASH.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "ELEMREF_NOHASH"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    private void assertValiadationResult(List<DMNMessage> validationMessages) {
    	assertThat(validationMessages).as(ValidatorUtil.formatMessages(validationMessages)).hasSize(3);
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_HREF_SYNTAX))).isTrue();
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

}
