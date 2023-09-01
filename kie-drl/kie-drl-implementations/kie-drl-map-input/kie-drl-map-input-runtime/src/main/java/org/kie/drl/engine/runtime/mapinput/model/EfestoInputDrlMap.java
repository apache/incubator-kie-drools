package org.kie.drl.engine.runtime.mapinput.model;

import org.kie.drl.engine.runtime.model.EfestoInputDrl;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;

/**
 * <code>EfestoInputDrl</code> specific for map input usage.
 * Its scope it is to provide a <code>Map</code> with the data needed for the evaluation.
 * To be used, for example, by PMML
 */
public class EfestoInputDrlMap extends EfestoInputDrl<EfestoMapInputDTO> {

    public EfestoInputDrlMap(ModelLocalUriId modelLocalUriId, EfestoMapInputDTO inputData) {
        super(modelLocalUriId, inputData);
    }
}
