package org.kie.pmml.compiler.commons.factories;

import java.util.UUID;

import org.dmg.pmml.InlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLRowInstanceFactory.getKiePMMLRows;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLInlineTable</code> instance
 * out of <code>InlineTable</code>s
 */
public class KiePMMLInlineTableInstanceFactory {

    private KiePMMLInlineTableInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLInlineTable getKiePMMLInlineTable(final InlineTable inlineTable) {
        return new KiePMMLInlineTable(UUID.randomUUID().toString(),
                                      getKiePMMLExtensions(inlineTable.getExtensions()),
                                      getKiePMMLRows(inlineTable.getRows()));
    }
}
