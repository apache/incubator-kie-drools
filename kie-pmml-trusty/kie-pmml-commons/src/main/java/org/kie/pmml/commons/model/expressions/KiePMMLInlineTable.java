package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * KiePMML representation of a <b>FieldColumnPair</b>
 */
public class KiePMMLInlineTable extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -8218151315846757917L;
    private final List<KiePMMLRow> rows;

    public KiePMMLInlineTable(String name, List<KiePMMLExtension> extensions, List<KiePMMLRow> rows) {
        super(name, extensions);
        this.rows = rows;
    }

    public List<KiePMMLRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public Optional<Object> evaluate(final Map<String, Object> columnPairsMap, final String outputColumn, final String regexField) {
        return rows.stream()
                .map(row -> row.evaluate(columnPairsMap, outputColumn, regexField))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }

    public void replace(final AtomicReference<String> text,
                        final String inField,
                        final String outField,
                        final String regexField,
                        final boolean isCaseSensitive,
                        final int maxLevenshteinDistance,
                        final boolean tokenize,
                        final String wordSeparatorCharacterRE) {
        rows.forEach(row -> row.replace(text, inField, outField, regexField, isCaseSensitive, maxLevenshteinDistance, tokenize, wordSeparatorCharacterRE));
    }
}
