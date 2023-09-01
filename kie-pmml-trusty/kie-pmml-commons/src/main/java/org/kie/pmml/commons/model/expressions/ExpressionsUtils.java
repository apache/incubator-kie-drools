package org.kie.pmml.commons.model.expressions;

import java.util.Optional;
import java.util.stream.Stream;

import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

/**
 * Helper methods for <code>KiePMMLExpression</code>s
 */
public class ExpressionsUtils {

    private ExpressionsUtils() {
        // avoid instantiation
    }

    public static Optional<Object> getFromPossibleSources(final String name, final ProcessingDTO processingDTO) {
        return Stream.of(getFromKiePMMLNameValues(name, processingDTO),
                         getFromDerivedFields(name, processingDTO),
                         getFromOutputFields(name, processingDTO))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }

    public static Optional<Object> getFromKiePMMLNameValues(final String name, final ProcessingDTO processingDTO) {
        return processingDTO.getKiePMMLNameValues()
                .stream()
                .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(name))
                .findFirst()
                .map(KiePMMLNameValue::getValue);
    }

    public static Optional<Object> getFromDerivedFields(final String name, final ProcessingDTO processingDTO) {
        return processingDTO.getDerivedFields()
                .stream()
                .filter(derivedField -> derivedField.getName().equals(name))
                .findFirst()
                .map(derivedField -> derivedField.evaluate(processingDTO));
    }

    public static Optional<Object> getFromOutputFields(final String name,
                                                       final ProcessingDTO processingDTO) {
        return processingDTO.getOutputFields()
                .stream()
                .filter(outputField -> outputField.getName().equals(name))
                .findFirst()
                .map(outputField -> outputField.evaluate(processingDTO));
    }
}
