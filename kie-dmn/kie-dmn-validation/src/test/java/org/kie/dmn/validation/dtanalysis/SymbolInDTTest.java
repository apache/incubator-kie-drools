package org.kie.dmn.validation.dtanalysis;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class SymbolInDTTest extends AbstractDTAnalysisTest {

    @Test
    public void testSymbolMyThreshold() {
        List<DMNMessage> validate = validator.validate(getReader("SymbolInDT.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(messageForSymbol("my threshold"))).as("It should contain DMNMessage for symbol not supported in input").isTrue();

        DTAnalysis analysis1 = getAnalysis(validate, "_50D70081-079A-40DA-BA9C-B1173F0D2831");
        assertThat(analysis1.isError()).isTrue();
    }

    private Predicate<? super DMNMessage> messageForSymbol(String symbolName) {
        return p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("symbol reference: '"+symbolName+"'.");
    }
    
    @Test
    public void testSymbolLastDateOfWork() {
        List<DMNMessage> validate = validator.validate(getReader("SymbolInDT2.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(messageForSymbol("Last Date of Work"))).as("It should contain DMNMessage for symbol not supported in input").isTrue();

        DTAnalysis analysis1 = getAnalysis(validate, "_50D70081-079A-40DA-BA9C-B1173F0D2831");
        assertThat(analysis1.isError()).isTrue();
    }
}
