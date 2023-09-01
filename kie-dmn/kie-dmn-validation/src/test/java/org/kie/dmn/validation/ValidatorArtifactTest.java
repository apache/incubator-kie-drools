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

public class ValidatorArtifactTest extends AbstractValidatorTest {

    @Test
    public void testASSOC_REFERENCES_NOT_EMPTY_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "artifact/ASSOC_REFERENCES_NOT_EMPTY.dmn" )) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    public void testASSOC_REFERENCES_NOT_EMPTY_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile( "artifact/ASSOC_REFERENCES_NOT_EMPTY.dmn" ),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    public void testASSOC_REFERENCES_NOT_EMPTY_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions( "artifact/ASSOC_REFERENCES_NOT_EMPTY.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "ASSOC_REFERENCES_NOT_EMPTY"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }
}
