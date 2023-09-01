package org.kie.drl.engine.runtime.mapinput.model;

import java.util.Map;

import org.kie.drl.engine.runtime.model.EfestoOutputDrl;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * <code>EfestoOutputDrl</code> specific for map input usage.
 * Its scope it is to return a <code>Map</code> with the result of the evaluation.
 * To be used, for example, by PMML
 */
public class EfestoOutputDrlMap extends EfestoOutputDrl<Map<String, Object>> {

    public EfestoOutputDrlMap(ModelLocalUriId modelLocalUriId, Map<String, Object> inputData) {
        super(modelLocalUriId, inputData);
    }
}
