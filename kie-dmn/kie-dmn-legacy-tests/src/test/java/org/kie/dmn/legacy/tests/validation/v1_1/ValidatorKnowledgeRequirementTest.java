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

public class ValidatorKnowledgeRequirementTest extends AbstractValidatorTest {

    @Test
    public void testKNOWREQ_MISSING_BKM_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "knowledgerequirement/KNOWREQ_MISSING_BKM.dmn" )) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    public void testKNOWREQ_MISSING_BKM_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile( "knowledgerequirement/KNOWREQ_MISSING_BKM.dmn" ),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    public void testKNOWREQ_MISSING_BKM_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions( "knowledgerequirement/KNOWREQ_MISSING_BKM.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "KNOWREQ_MISSING_BKM"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    public void testKNOWREQ_REQ_DECISION_NOT_BKM_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "knowledgerequirement/KNOWREQ_REQ_DECISION_NOT_BKM.dmn" )) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    public void testKNOWREQ_REQ_DECISION_NOT_BKM_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile( "knowledgerequirement/KNOWREQ_REQ_DECISION_NOT_BKM.dmn" ),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    public void testKNOWREQ_REQ_DECISION_NOT_BKM_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions( "knowledgerequirement/KNOWREQ_REQ_DECISION_NOT_BKM.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "KNOWREQ_REQ_DECISION_NOT_BKM"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }
}
