package org.kie.dmn.pmml;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.ValidatorUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PMMLValidatorImportTest extends AbstractValidatorTest {

    @Test
    public void testImportPMML() throws IOException {
        // DROOLS-4187 kie-dmn-validation: Incorrect import detection
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                .theseModels(getReader("Invoke_Iris.dmn", PMMLValidatorImportTest.class));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);

    }

    @Test
    public void testImportPMML2() throws IOException {
        // DROOLS-4395 [DMN Designer] Validation fails for included PMML model
        try (Reader defsReader = getReader("KiePMMLScoreCard_wInputType.dmn", DMNRuntimePMMLTest.class);) {
            final Definitions defs = getDefinitions(defsReader,
                    "http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f",
                    "KiePMMLScoreCard");
            DMNValidator.ValidatorBuilder.ValidatorImportReaderResolver resolver = (ns, name, i) -> {
                if (ns.equals(defs.getNamespace()) && name.equals(defs.getName()) && i.equals(defs.getImport().get(0).getLocationURI())) {
                    return getReader("test_scorecard.pmml", DMNRuntimePMMLTest.class);
                } else {
                    return null;
                }
            };
            final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL,
                    Validation.VALIDATE_COMPILATION)
                    .usingImports(resolver)
                    .theseModels(defs);
            assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
        }
    }
}
