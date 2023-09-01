package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Row;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getRowDataMap;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLRow</code> instance
 * out of <code>Row</code>s
 */
public class KiePMMLRowInstanceFactory {

    private KiePMMLRowInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLRow> getKiePMMLRows(final List<Row> rows) {
        return rows != null ?
                rows.stream().map(KiePMMLRowInstanceFactory::getKiePMMLRow).collect(Collectors.toList()) :
                Collections.emptyList();
    }

    static KiePMMLRow getKiePMMLRow(final Row row) {
        return new KiePMMLRow(getRowDataMap(row));
    }
}
